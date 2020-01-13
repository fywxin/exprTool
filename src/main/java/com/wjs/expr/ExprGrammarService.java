package com.wjs.expr;

import com.wjs.expr.bean.*;
import com.wjs.expr.exprNative.ExprNativeService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 表达式语法解析器
 * @author wjs
 * @date 2019-12-31 17:51
 **/
public class ExprGrammarService {

    public ExprNativeService exprNativeService;

    /**
     * 语法解析提取
     * case when then else end 支持
     * @param text
     * @return
     */
    public ExprTree parse(String text, boolean autoComplete) {
        Character c = null;
        Character n = null;
        String frame = null;
        String cmd = null;
        int line = 0;

        List<BinaryExpr> list= new ArrayList<>();
        List<FuncExpr> funcExprList = new ArrayList<>();
        List<SectionExpr> sectionExprList = new ArrayList<>();
        List<ForExpr> forExprList = new ArrayList<>();

        //待解析的表达式栈
        List<BinaryExpr> ifStack= new ArrayList<>();
        List<ForExpr> forStack= new ArrayList<>();
        BinaryExpr binaryExpr = null;

        for (int i=0; i<text.length(); i++) {
            c = text.charAt(i);
            //段落表达式解析 <$ 段落 $>
            if (c == SectionExpr.SECTION_OPEN){
                if (i < text.length()-1 && text.charAt(i+1) == BaseExpr.GRAMMAR){
                    int startCol = i;
                    int startLine = line;
                    try {
                        i = i + 2;
                        c = text.charAt(i);
                        n = text.charAt(i+1);
                        if (c == '\n'){
                            line++;
                        }
                        while (!(c == BaseExpr.GRAMMAR && n == SectionExpr.SECTION_CLOSE)){
                            if (c == SectionExpr.SECTION_OPEN && n == BaseExpr.GRAMMAR){
                                throw new ExprException("第["+(startLine+1)+" | "+(line+1)+"]行, 区间["+startCol+"-"+(i+2)+"] 片段表达式不支持循环嵌套:\n"+text.substring(startCol, i+2));
                            }
                            i++;
                            c = text.charAt(i);
                            n = text.charAt(i+1);
                            if (c == '\n'){
                                line++;
                            }
                        }
                        i++;
                        sectionExprList.add(new SectionExpr(text, startLine, line, startCol, i+1));
                        continue;
                    }catch (Exception e){
                        if (e instanceof ExprException){
                            throw e;
                        }
                        throw new ExprException("第["+(startLine+1)+" - "+(line+1)+"]行, 区间["+startCol+"-"+i+"] 片段表达式解析异常:\n"+text.substring(startCol, i-1), e);
                    }
                }
            }

            if (c == '\n'){
                line++;
                continue;
            }

            if (c == BaseExpr.GRAMMAR){
                i++;
                frame = getWord(text, i);
                cmd = frame.toLowerCase();
                switch (cmd){
                    case BaseExpr.IF:
                        binaryExpr = new BinaryExpr(new IfExpr(text, line, i - 1, i + BaseExpr.IF.length()));
                        //父表达式 -> 栈上最近一个未完成的表达式
                        for (int j=ifStack.size()-1; j>=0; j--){
                            if (!ifStack.get(j).isOk()){
                                binaryExpr.parentBinary = ifStack.get(j);
                                break;
                            }
                        }
                        ifStack.add(binaryExpr);
                        break;
                    case BaseExpr.THEN:
                        this.checkIf(ifStack, line);
                        binaryExpr = ifStack.get(ifStack.size()-1);
                        //if
                        if (binaryExpr.elifExprList.isEmpty()){
                            IfExpr ifExpr = binaryExpr.ifExpr;
                            if (ifExpr.getExprStopCol() == null){
                                ifExpr.setExprStopCol(i-1);
                                ifExpr.setBodyStartCol(i+BaseExpr.THEN.length());
                            }else{
                                throw new ExprException("第["+line+"]行的 "+BaseExpr._THEN+" 缺少匹配的 "+BaseExpr._IF);
                            }
                        //elif
                        } else {
                            ElifExpr elifExpr = binaryExpr.lastElifExpr();
                            if (elifExpr.getExprStopCol() == null){
                                elifExpr.setExprStopCol(i-1);
                                elifExpr.setBodyStartCol(i+BaseExpr.THEN.length());
                            }else{
                                throw new ExprException("第["+line+"]行的 "+BaseExpr._THEN+" 缺少匹配的 "+BaseExpr._ELIF);
                            }
                        }
                        break;
                    case BaseExpr.ELIF:
                        this.checkIf(ifStack, line);
                        binaryExpr = ifStack.get(ifStack.size()-1);
                        if (binaryExpr.elifExprList.isEmpty()){
                            endIfExpr(binaryExpr, line, i);
                        } else {
                            endElIfExpr(binaryExpr, line, i);
                        }
                        binaryExpr.addElifExpr(new ElifExpr(text, line, i-1, i+BaseExpr.ELIF.length()));
                        break;
                    case BaseExpr.ELSE:
                        this.checkIf(ifStack, line);
                        binaryExpr = ifStack.get(ifStack.size()-1);
                        if (binaryExpr.elifExprList.isEmpty()){
                            endIfExpr(binaryExpr, line, i);
                        } else {
                            endElIfExpr(binaryExpr, line, i);
                        }

                        binaryExpr.setElseExpr(Optional.of(new ElseExpr(text, line, i-1)));
                        break;
                    case BaseExpr.ENDIF:
                        this.checkIf(ifStack, line);
                        //表达式解析完成，出栈
                        binaryExpr = ifStack.remove(ifStack.size()-1).finish(line, i+BaseExpr.ENDIF.length());
                        if(binaryExpr.elseExpr.isPresent()){
                            endElse(binaryExpr, line, i);
                        }else if (binaryExpr.elifExprList.isEmpty()){
                            endIfExpr(binaryExpr, line, i);
                        } else{
                            endElIfExpr(binaryExpr, line, i);
                        }
                        //for未完成
                        if (!forStack.isEmpty()){
                            binaryExpr.setParentFor(forStack.get(forStack.size()-1));
                        }

                        list.add(binaryExpr);
                        break;
                    case BaseExpr.FOR:
                        int start = i-1;
                        int loop = 1;
                        i = i+BaseExpr.FOR.length();
                        n = text.charAt(i);
                        if (n != '(') {
                            while (i < text.length() - 1) {
                                i++;
                                n = text.charAt(i);
                                if (n == '('){
                                    break;
                                }
                                if (n != ' ' && n != '\t') {
                                    throw new ExprException("第[" + (line+1) + "]行 " + BaseExpr._FOR + " 区间[" + start + " - " + i + "] 表达式格式错误: "+text.substring(start, i+1));
                                }
                            }
                        }
                        i++;
                        while (loop > 0 && i < text.length()-1){
                            n = text.charAt(i);
                            i++;
                            if (n == '('){
                                loop++;
                            }
                            if (n == ')'){
                                loop--;
                            }
                            if (n == '\n'){
                                throw new ExprException("第["+(line+1)+"]行 "+BaseExpr._FOR+" 区间["+start+" - "+i+"] 不支持换行: "+text.substring(start, i+1));
                            }
                        }
                        if (loop > 0){
                            throw new ExprException("第["+(line+1)+"]行 "+BaseExpr._FOR+" 区间["+start+" - "+i+"] 找不到循环表达式结束右括号 ) : "+text.substring(start, i+1));
                        }
                        forStack.add(new ForExpr(text, line, start, i+1));
                        break;
                    case BaseExpr.ENDFOR:
                        if (forStack.isEmpty()){
                            throw new ExprException("第["+(line+1)+"]行 "+BaseExpr._ENDFOR+" 找不到匹配的 "+BaseExpr._FOR);
                        }
                        ForExpr forExpr = forStack.remove(forStack.size()-1).finish(line, i+BaseExpr.ENDFOR.length(), i-1);
                        //for 是IF的子节点
                        if (!ifStack.isEmpty()){
                            forExpr.setParent(ifStack.get(ifStack.size()-1));
                        }
                        forExprList.add(forExpr);
                        break;
                    default:
                        //自定义函数 -> 函数嵌套
                        if (FuncExpr.support(BaseExpr.GRAMMAR + frame)){
                            int j = i + frame.length();
                            while (text.charAt(j) == ' '){
                                j++;
                            }
                            if (text.charAt(j) == '('){
                                int stopLine = line;
                                loop=1;
                                while (loop > 0){
                                    j++;
                                    Character cc = text.charAt(j);
                                    if (cc == '('){
                                        loop++;
                                    }
                                    if (cc == ')'){
                                        loop--;
                                    }
                                    if (cc == '\n'){
                                        stopLine++;
                                    }
                                }
                                funcExprList.add(new FuncExpr(text, line, stopLine, i-1, j+1));
                                i = j;
                            }
                        }
                        break;
                }
            }
        }

        if (!forStack.isEmpty()){
            throw new ExprException("缺失第[" + (forStack.get(0).getStartLine() + 1) + "]行 "+BaseExpr._FOR+" 对应的 "+BaseExpr._ENDFOR+", 未闭合错误");
        }

        if (autoComplete){
            while (!ifStack.isEmpty()){
                int i = text.length();
                binaryExpr = ifStack.remove(ifStack.size()-1).finish(line, i);
                if(binaryExpr.elseExpr.isPresent()){
                    endElse(binaryExpr, line, i+1);
                    ElseExpr elseExpr = binaryExpr.elseExpr.get();
                    elseExpr.setStopCol(i);
                    elseExpr.autoComplete = true;
                }else if (binaryExpr.elifExprList.isEmpty()){
                    endIfExpr(binaryExpr, line, i+1);
                    binaryExpr.ifExpr.autoComplete = true;
                } else{
                    endElIfExpr(binaryExpr, line, i+1);
                    binaryExpr.lastElifExpr().autoComplete = true;
                }
                binaryExpr.autoComplete = true;
                list.add(binaryExpr);
            }
        }else {
            if (!ifStack.isEmpty()) {
                throw new ExprException("缺失第[" + (ifStack.get(0).getStartLine() + 1) + "]行 "+BaseExpr._IF+" 对应的 "+BaseExpr._ENDIF+", 未闭合错误");
            }
        }
        return new ExprTree(list, funcExprList, sectionExprList, forExprList, 0, text.length(), null);
    }

    private void checkIf(List<BinaryExpr> ifStack, int line){
        if (ifStack.isEmpty() || ifStack.get(ifStack.size()-1).ifExpr == null) {
            throw new ExprException("第["+line+"]行之上, 缺少 "+BaseExpr._IF+" 关键字");
        }
    }

    private void endIfExpr(BinaryExpr binaryExpr, Integer line, int i) {
        IfExpr ifExpr = binaryExpr.ifExpr;
        if (ifExpr.getBodyStartCol() == null){
            throw new ExprException("第["+(ifExpr.getStartLine()+1)+"]行 "+BaseExpr._IF+" 缺少 "+BaseExpr._THEN+" 关键字");
        }
        ifExpr.setStopLine(line);
        ifExpr.setBodyStopCol(i-1);
        ifExpr.setStopCol(i-1);
    }

    private void endElIfExpr(BinaryExpr binaryExpr, Integer line, int i){
        ElifExpr elifExpr = binaryExpr.lastElifExpr();
        if (elifExpr.getBodyStartCol() == null){
            throw new ExprException("第["+(elifExpr.getStartLine()+1)+"]行 "+BaseExpr._ELIF+" 缺少 "+BaseExpr._THEN+" 关键字");
        }
        elifExpr.setStopLine(line);
        elifExpr.setBodyStopCol(i-1);
        elifExpr.setStopCol(i-1);
    }

    private void endElse(BinaryExpr binaryExpr, Integer line, int i) {
        ElseExpr elseExpr = binaryExpr.elseExpr.get();
        elseExpr.setStopLine(line);
        elseExpr.setBodyStopCol(i-1);
        elseExpr.setStopCol(i+BaseExpr.ENDIF.length());
    }

    /**
     * @param text
     * @param start
     * @return
     */
    public String getWord(String text, int start){
        int end = getWordEnd(text, start);
        if (start == end){
            return String.valueOf(text.charAt(start));
        }
        return text.substring(start, end);
    }

    private Integer getWordEnd(String text, int start){
        int index = start;
        char c = text.charAt(index);
        if (isSplitChar(c)){
            return index;
        }
        while (!isSplitChar(c)){
            index++;
            if (index > text.length()-1){
                return index;
            }
            c = text.charAt(index);
        }
        return index;
    }

    /**
     * 是否为分词
     * @param c
     * @return
     */
    public boolean isSplitChar(Character c){
        return exprNativeService.isSplitChar(c);
    }

}
