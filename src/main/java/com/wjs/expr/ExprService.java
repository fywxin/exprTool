package com.wjs.expr;

import com.wjs.expr.bean.*;
import com.wjs.expr.commons.ReflectionUtil;
import com.wjs.expr.eval.ExprEval;
import com.wjs.expr.listener.IForInListener;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 表达式执行求解器
 * @author wjs
 * @date 2019-12-31 20:05
 **/
@Slf4j
public class ExprService {

    public ExprGrammarService exprGrammarService;

    public ExprEvalService exprEvalService;

    public List<IForInListener> forInListenerList = new ArrayList<>();

    public static final Integer MAX_LOOP = 1000;

    /**
     * 入口函数，表达式解析执行
     * @param text 表达式
     * @param params 表达式变量值
     * @return
     */
    public String eval(String text, Map<String, Object> params){
        ExprTree exprTree = this.parse(text);
        return eval(exprTree, params);
    }

    public ExprTree parse(String text){
        return this.exprGrammarService.parse(text, true);
    }

    public String eval(ExprTree exprTree, Map<String, Object> params){
        exprTree.attachParams(params);
        return eval(exprTree, exprTree.startCol, exprTree.stopCol-1);
    }

    public String eval(ExprTree exprTree, int startCol, int stopCol){
        if (exprTree.isEmpty()){
            return exprTree.text.substring(startCol, stopCol+1);
        }
        StringBuilder sb = new StringBuilder(stopCol-startCol);
        this.evalExpr(exprTree, sb, startCol, stopCol);
        return sb.toString();
    }

    private void evalExpr(ExprTree exprTree, StringBuilder sb, int start, int stop){
        BaseExpr baseExpr = null;
        ExprTree subExprTree = null;
        exprTree.start();
        while ((baseExpr = exprTree.walk()) != null){
            evalSection(exprTree, sb, start, this.skipGrammarLineBeforeIndex(exprTree.text, baseExpr.startCol-1));
            if (baseExpr instanceof ForExpr){
                ForExpr forExpr = (ForExpr) baseExpr;
                subExprTree = exprTree.getSubExprTree(forExpr, forExpr.bodyStartCol, forExpr.bodyStopCol);
                this.evalFor(subExprTree, sb, forExpr);
            }else{
                BinaryExpr binaryExpr = (BinaryExpr)baseExpr;
                IfExpr ifExpr = binaryExpr.ifExpr;

                boolean match = false;
                //执行If表达式
                if (predicate(ifExpr, exprTree.getParams())){
                    subExprTree = exprTree.getSubExprTree(ifExpr, ifExpr.bodyStartCol, ifExpr.bodyStopCol);
                    this.out(ifExpr, subExprTree, sb);
                    match = true;
                //执行elseIf表达式
                }else if(!binaryExpr.elifExprList.isEmpty()){
                    for (ElifExpr elifExpr : binaryExpr.elifExprList){
                        if (predicate(elifExpr, exprTree.getParams())){
                            subExprTree = exprTree.getSubExprTree(elifExpr, elifExpr.bodyStartCol, elifExpr.bodyStopCol);
                            this.out(elifExpr, subExprTree, sb);
                            match = true;
                        }
                    }
                }

                //如上皆不满足，则使用else值
                if (!match && binaryExpr.elseExpr.isPresent()){
                    ElseExpr elseExpr = binaryExpr.elseExpr.get();
                    subExprTree = exprTree.getSubExprTree(elseExpr, elseExpr.bodyStartCol, elseExpr.bodyStopCol);
                    this.out(elseExpr, subExprTree, sb);
                }
            }

            //start = baseExpr.stopCol+1;
//            if (!exprTree.hasNext()) {
//                start = baseExpr.stopCol+1;
//            }else{
                start = skipGrammarLineAfterIndex(exprTree.getText(), baseExpr.stopCol + 1, stop);
//            }
        }
        if (start <= stop){
            evalSection(exprTree, sb, start, stop);
        }
    }



    /**
     * for表达式支持
     * @param subExprTree
     * @param sb
     * @param forExpr
     */
    public void evalFor(ExprTree subExprTree, StringBuilder sb, ForExpr forExpr) {
        Map<String, Object> params = subExprTree.getParams();
        ExprEval exprEval = this.exprEvalService.exprEval;
        int start = skipGrammarLineAfterIndex(subExprTree.getText(), forExpr.bodyStartCol, forExpr.bodyStopCol);
        int stop = this.skipGrammarLineBeforeIndex(subExprTree.getText(), forExpr.bodyStopCol);
        if (forExpr.getForEnum() == ForExpr.ForEnum.TERNARY_MODE){
            String key = forExpr.getTernaryVar().getFirst();
            Object oldVal = params.get(key);
            try {
                int loop = 0;
                if (forExpr.getTernaryVar().getSecond() == null){
                    params.put(key, params.get(forExpr.getTernaryVar().getThird()));
                }else{
                    params.put(key, forExpr.getTernaryVar().getSecond());
                }
                while (exprEval.eval(forExpr.getTernaryPredicate(), params)) {
                    sb.append(eval(subExprTree, start, stop));
                    params.put(key, exprEval.call(forExpr.getTernaryOpt(), params));
                    loop ++;
                    if (loop > MAX_LOOP){
                        throw new ExprException("第["+forExpr.getStartLine()+"]行"+BaseExpr._FOR + "表达式["+forExpr.getForText()+"]执行次数超过最大阀值["+MAX_LOOP+"]");
                    }
                }
            }finally {
                params.put(key, oldVal);
            }
        }else{
            Object target = null;
            String inTmp = forExpr.getInState().getSecond();
            if (inTmp.contains(".")){
                List<String> frames = Arrays.asList(inTmp.split("\\."));
                target = params.get(frames.get(0));
                for (int i=1; i<frames.size(); i++){
                    if (target != null) {
                        target = ReflectionUtil.getPojoFieldValue(target, frames.get(i));
                    }
                }
            }else{
                target = params.get(forExpr.getInState().getSecond());
            }

            String key = forExpr.getInState().getFirst();
            Object oldVal = params.get(key);
            String key_index = key+"_INDEX";
            String key_FIRST = key+"_FIRST";
            String key_LAST = key+"_LAST";
            int i = 0;
            try {
                IForInListener.callBefore(forInListenerList, subExprTree, forExpr);
                if (target instanceof Collection) {
                    Collection cc = (Collection) target;
                    if (cc.size() > MAX_LOOP){
                        throw new ExprException("第["+forExpr.getStartLine()+"]行"+BaseExpr._FOR + "表达式["+forExpr.getForText()+"]数据源["+forExpr.getInState().getSecond()+"]记录数超过["+MAX_LOOP+"]");
                    }
                    Iterator iterator = cc.iterator();
                    while (iterator.hasNext()){
                        Object obj = iterator.next();
                        params.put(key, obj);
                        params.put(key_index, i);
                        params.put(key_FIRST, i == 0);
                        params.put(key_LAST, i == cc.size()-1);
                        sb.append(eval(subExprTree, start, stop));
                        IForInListener.callIn(forInListenerList, subExprTree, forExpr, key, obj, i);
                        i++;
                    }
                }else if (target instanceof Map){
                    Map<?, ?> map = (Map) target;
                    for (Map.Entry<?, ?> entry : map.entrySet()){
                        params.put(key, entry);
                        params.put(key_index, i);
                        params.put(key_FIRST, i == 0);
                        params.put(key_LAST, i == map.size()-1);
                        sb.append(eval(subExprTree, start, stop));
                        IForInListener.callIn(forInListenerList, subExprTree, forExpr, key, entry, i);
                        i++;
                    }
                }
                IForInListener.callAfter(forInListenerList, subExprTree, forExpr);
            }finally {
                params.put(key, oldVal);
                params.remove(key_index);
                params.remove(key_FIRST);
                params.remove(key_LAST);
            }
        }
    }

    private void out(BodyExpr bodyExpr, ExprTree exprTree, StringBuilder sb){
        //处理多余表达式换行
        int start = skipGrammarLineAfterIndex(exprTree.text, bodyExpr.bodyStartCol, bodyExpr.bodyStopCol);
        int stop = skipGrammarLineBeforeIndex(exprTree.text, bodyExpr.bodyStopCol);
        if (exprTree.getTopExprList().isEmpty()){
            evalSection(exprTree, sb, start, stop);
        }else{
            evalExpr(exprTree, sb, start, stop);
        }
    }

    /**
     * 表达式片段(自定义函数) 求值
     * @param exprTree
     * @param sb
     * @param startCol
     * @param stopCol
     */
    public void evalSection(ExprTree exprTree, StringBuilder sb, int startCol, int stopCol){
        String text = exprTree.text;
        List<SectionExpr> innerSectionExprList = exprTree.innerSectionAndFunc(startCol, stopCol);
        if (innerSectionExprList.isEmpty()){
            if (stopCol == text.length()){
                sb.append(text, startCol, stopCol);
            }else{
                sb.append(text, startCol, stopCol+1);
            }
            return ;
        }
        int start = startCol;
        for (SectionExpr sectionExpr : innerSectionExprList){
            sb.append(text, start, sectionExpr.startCol);
            sb.append(this.exprEvalService.eval(sectionExpr, exprTree.getParams()));
            start = sectionExpr.stopCol + 1;
        }
        if (start <= stopCol){
            if (stopCol == text.length()){
                sb.append(text, start, stopCol);
            }else{
                sb.append(text, start, stopCol+1);
            }
        }
    }

    /**
     * 表达式断言求值
     * @param exprExpr
     * @return
     */
    private boolean predicate(ExprExpr exprExpr, Map<String, Object> params){
        try {
            return exprEvalService.eval(exprExpr, params);
        }catch (Exception e){
            log.error("表达式计算异常: ["+exprExpr.getExprText()+"].("+params+")", e);
            throw new ExprException(e);
        }
    }

    /**
     * 跳过语法换行位置适配
     *  1. bodyStartCol 往后跳
     *  2. endif endfor 往后跳
     *
     * @param text
     * @param startCol  原始开始位置
     * @param stopCol 边界
     * @return
     */
    private Integer skipGrammarLineAfterIndex(String text, int startCol, int stopCol){
        int start = startCol;
        while (start <= stopCol && (text.charAt(start) == ' ' || text.charAt(start) == '\t')){
            start++;
        }
//        if (start < stopCol && text.charAt(start) == '\n'){
//            return start+1;
//        }
//        return startCol;

        //TODO 空格也删除
        if (start <= stopCol && text.charAt(start) == '\n'){
            start++;
        }
        return start;
    }

    private Integer skipGrammarLineBeforeIndex(String text, int stopCol){
        if (stopCol >= text.length()){
            return stopCol;
        }
        int pre = stopCol;
        while (pre>0 && (text.charAt(pre) == ' ' || text.charAt(pre) == '\t')){
            pre--;
        }
        if (pre>=0 && text.charAt(pre) == '\n'){
            return pre;
        }
        return stopCol;
    }

    //---------------------------------------------------------------

    public static void main(String[] args) {
        String a ="0123456";
        StringBuilder sb = new StringBuilder();
        sb.append(a, 1,3);
        System.out.println(sb.toString());
        System.out.println(a.substring(1,3));
    }
}
