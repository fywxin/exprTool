package com.wjs.expr;

import com.wjs.expr.bean.*;
import com.wjs.expr.exprNative.ExprNativeService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 表达式语法解析器
 * @author wjs
 * @date 2019-12-31 17:51
 **/
public class ExprGrammarService {

    public ExprNativeService exprNativeService;

    /**
     * 顶层根表达式列表
     * @param list
     * @return
     */
    public List<Expr> root(List<Expr> list){
        return list.stream().filter(x -> x.parent == null).collect(Collectors.toList());
    }

    /**
     * 语法解析提取
     * case when then else end 支持
     * @param text
     * @return
     */
    public List<Expr> parse(String text, boolean autoComplete) {
        Character c = null;
        String cmd = null;
        int line = 0;

        List<Expr> list= new ArrayList<>();

        //待解析的表达式栈
        List<Expr> stack= new ArrayList<>();
        Expr expr = null;

        for (int i=0; i<text.length(); i++) {
            c = text.charAt(i);
            if (c == '\n'){
                line++;
                continue;
            }
            if (c == BaseExpr.GRAMMAR){
                i++;
                cmd = getWord(text, i).toLowerCase();
                switch (cmd){
                    case BaseExpr.IF:
                        expr = new Expr(new IfExpr(text, line, i - 1, i + BaseExpr.IF.length()));
                        //父表达式 -> 栈上最近一个未完成的表达式
                        for (int j=stack.size()-1; j>=0; j--){
                            if (!stack.get(j).isOk()){
                                expr.parent = stack.get(j);
                                break;
                            }
                        }
                        stack.add(expr);
                        break;
                    case BaseExpr.THEN:
                        expr = stack.get(stack.size()-1);
                        //if
                        if (expr.elifExprList.isEmpty()){
                            IfExpr ifExpr = expr.ifExpr;
                            ifExpr.setExprStopCol(i-1);
                            ifExpr.setBodyStartCol(i+BaseExpr.THEN.length());
                        //elif
                        } else {
                            ElifExpr elifExpr = expr.lastElifExpr();
                            elifExpr.setExprStopCol(i-1);
                            elifExpr.setBodyStartCol(i+BaseExpr.THEN.length());
                        }
                        break;
                    case BaseExpr.ELIF:
                        expr = stack.get(stack.size()-1);
                        if (expr.elifExprList.isEmpty()){
                            endIfExpr(expr, line, i);
                        } else {
                            endElIfExpr(expr, line, i);
                        }
                        expr.addElifExpr(new ElifExpr(text, line, i-1, i+BaseExpr.ELIF.length()));
                        break;
                    case BaseExpr.ELSE:
                        expr = stack.get(stack.size()-1);
                        if (expr.elifExprList.isEmpty()){
                            endIfExpr(expr, line, i);
                        } else {
                            endElIfExpr(expr, line, i);
                        }

                        expr.setElseExpr(Optional.of(new ElseExpr(text, line, i-1)));
                        break;
                    case BaseExpr.END:
                        //表达式解析完成，出栈
                        expr = stack.remove(stack.size()-1).finish(line, i+BaseExpr.END.length());
                        if(expr.elseExpr.isPresent()){
                            endElse(expr, line, i);
                        }else if (expr.elifExprList.isEmpty()){
                            endIfExpr(expr, line, i);
                        } else{
                            endElIfExpr(expr, line, i);
                        }
                        list.add(expr);
                        break;
                    default:
                        break;
                }
            }
        }

        if (autoComplete){
            while (!stack.isEmpty()){
                int i = text.length();
                expr = stack.remove(stack.size()-1).finish(line, i);
                if(expr.elseExpr.isPresent()){
                    endElse(expr, line, i+1);
                    ElseExpr elseExpr = expr.elseExpr.get();
                    elseExpr.setStopCol(i);
                    elseExpr.autoComplete = true;
                }else if (expr.elifExprList.isEmpty()){
                    endIfExpr(expr, line, i+1);
                    expr.ifExpr.autoComplete = true;
                } else{
                    endElIfExpr(expr, line, i+1);
                    expr.lastElifExpr().autoComplete = true;
                }
                expr.autoComplete = true;
                list.add(expr);
            }
        }else {
            if (!stack.isEmpty()) {
                throw new ExprException("缺失第[" + (stack.get(0).getStartLine() + 1) + "]行 "+BaseExpr._IF+" 对应的 "+BaseExpr._END+", 未闭合错误");
            }
        }
        this.tree(list);
        return list;
    }

    //TODO 性能优化，减去无必要的循环
    private void tree(List<Expr> list){
        for (Expr expr : list){
            List<Expr> child = list.stream().filter(x -> x.parent == expr).collect(Collectors.toList());
            if (!child.isEmpty()){
                expr.ifExpr.setChildExprList(child.stream().filter(x -> expr.ifExpr.contain(x)).collect(Collectors.toList()));
                for (ElifExpr elifExpr : expr.elifExprList) {
                    elifExpr.setChildExprList(child.stream().filter(x -> child.contains(x)).collect(Collectors.toList()));
                }
                if (expr.elseExpr.isPresent()){
                    expr.elseExpr.get().setChildExprList(child.stream().filter(x -> expr.elseExpr.get().contain(x)).collect(Collectors.toList()));
                }
            }
        }
    }

    private void endIfExpr(Expr expr, Integer line, int i) {
        IfExpr ifExpr = expr.ifExpr;
        if (ifExpr.getBodyStartCol() == null){
            throw new ExprException("第["+(ifExpr.getStartLine()+1)+"]行 "+BaseExpr._IF+" 缺少 "+BaseExpr._THEN+" 关键字");
        }
        ifExpr.setStopLine(line);
        ifExpr.setBodyStopCol(i-1);
        ifExpr.setStopCol(i-1);
    }

    private void endElIfExpr(Expr expr, Integer line, int i){
        ElifExpr elifExpr = expr.lastElifExpr();
        if (elifExpr.getBodyStartCol() == null){
            throw new ExprException("第["+(elifExpr.getStartLine()+1)+"]行 "+BaseExpr._ELIF+" 缺少 "+BaseExpr._THEN+" 关键字");
        }
        elifExpr.setStopLine(line);
        elifExpr.setBodyStopCol(i-1);
        elifExpr.setStopCol(i-1);
    }

    private void endElse(Expr expr, Integer line, int i) {
        ElseExpr elseExpr = expr.elseExpr.get();
        elseExpr.setStopLine(line);
        elseExpr.setBodyStopCol(i-1);
        elseExpr.setStopCol(i+BaseExpr.END.length());
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
