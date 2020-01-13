package com.wjs.expr;

import com.wjs.expr.bean.*;
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

    public static final Integer MAX_LOOP = 10000;

    /**
     * 入口函数，表达式解析执行
     * @param text 表达式
     * @param params 表达式变量值
     * @return
     */
    public String eval(String text, Map<String, Object> params){
        ExprTree exprTree = this.parse(text, params);
        return eval(text, exprTree, 0, text.length());
    }

    public ExprTree parse(String text, Map<String, Object> params){
        ExprTree exprTree = this.exprGrammarService.parse(text, true);
        exprTree.attachParams(params);
        return exprTree;
    }

    public String eval(String text, ExprTree exprTree, int startCol, int stopCol){
        if (exprTree.isEmpty()){
            return text.substring(startCol, stopCol);
        }
        StringBuilder sb = new StringBuilder(stopCol-startCol);
        this.evalExpr(text, exprTree, sb, startCol, stopCol);
        return sb.toString();
    }

    private void evalExpr(String text, ExprTree exprTree, StringBuilder sb, int start, int stop){
        BaseExpr baseExpr = null;
        ExprTree subExprTree = null;
        exprTree.start();
        while ((baseExpr = exprTree.walk()) != null){
            evalSection(text, exprTree, start, baseExpr.startCol, sb);
            if (baseExpr instanceof ForExpr){
                ForExpr forExpr = (ForExpr) baseExpr;
                subExprTree = exprTree.getSubExprTree(forExpr, forExpr.bodyStartCol, forExpr.bodyStopCol);
                this.evalFor(text, subExprTree, sb, forExpr);
            }else{
                BinaryExpr binaryExpr = (BinaryExpr)baseExpr;
                IfExpr ifExpr = binaryExpr.ifExpr;

                boolean match = false;
                //执行If表达式
                if (predicate(ifExpr, exprTree.getParams())){
                    subExprTree = exprTree.getSubExprTree(ifExpr, ifExpr.bodyStartCol, ifExpr.bodyStopCol);
                    this.out(text, ifExpr, subExprTree, sb);
                    match = true;
                //执行elseIf表达式
                }else if(!binaryExpr.elifExprList.isEmpty()){
                    for (ElifExpr elifExpr : binaryExpr.elifExprList){
                        if (predicate(elifExpr, exprTree.getParams())){
                            subExprTree = exprTree.getSubExprTree(elifExpr, elifExpr.bodyStartCol, elifExpr.bodyStopCol);
                            this.out(text, elifExpr, subExprTree, sb);
                            match = true;
                        }
                    }
                }

                //如上皆不满足，则使用else值
                if (!match && binaryExpr.elseExpr.isPresent()){
                    ElseExpr elseExpr = binaryExpr.elseExpr.get();
                    subExprTree = exprTree.getSubExprTree(elseExpr, elseExpr.bodyStartCol, elseExpr.bodyStopCol);
                    this.out(text, elseExpr, subExprTree, sb);
                }
            }

            start = baseExpr.stopCol;
        }
        if (start < stop){
            evalSection(text, exprTree, start, stop, sb);
        }
    }

    /**
     * for表达式支持
     * @param text
     * @param subExprTree
     * @param sb
     * @param forExpr
     */
    public void evalFor(String text, ExprTree subExprTree, StringBuilder sb, ForExpr forExpr) {
        Map<String, Object> params = subExprTree.getParams();
        ExprEval exprEval = this.exprEvalService.exprEval;
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
                    sb.append(eval(text, subExprTree, forExpr.bodyStartCol, forExpr.bodyStopCol));
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
            Object target = params.get(forExpr.getInState().getSecond());
            String key = forExpr.getInState().getFirst();
            Object oldVal = params.get(key);
            String key_index = key+"_INDEX";
            String key_FIRST = key+"_FIRST";
            String key_LAST = key+"_LAST";
            int i = 0;
            try {
                IForInListener.callBefore(forInListenerList, subExprTree, forExpr);
                if (target instanceof List) {
                    List list = (List) target;
                    for (Object obj : list) {
                        params.put(key, obj);
                        params.put(key_index, i);
                        params.put(key_FIRST, i == 0);
                        params.put(key_LAST, i == list.size()-1);
                        sb.append(eval(text, subExprTree, forExpr.bodyStartCol, forExpr.bodyStopCol));
                        IForInListener.callIn(forInListenerList, subExprTree, forExpr, key, obj, i);
                        i++;
                    }
                }else if (target instanceof Set){
                    Set set = (Set) target;
                    Iterator iterator = set.iterator();
                    while (iterator.hasNext()){
                        Object obj = iterator.next();
                        params.put(key, obj);
                        params.put(key_index, i);
                        params.put(key_FIRST, i == 0);
                        params.put(key_LAST, i == set.size()-1);
                        sb.append(eval(text, subExprTree, forExpr.bodyStartCol, forExpr.bodyStopCol));
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
                        sb.append(eval(text, subExprTree, forExpr.bodyStartCol, forExpr.bodyStopCol));
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

    private void out(String text, BodyExpr bodyExpr, ExprTree exprTree, StringBuilder sb){
        if (exprTree.getTopExprList().isEmpty()){
            evalSection(text, exprTree, bodyExpr.bodyStartCol, bodyExpr.bodyStopCol, sb);
        }else{
            evalExpr(text, exprTree, sb, bodyExpr.bodyStartCol, bodyExpr.bodyStopCol);
        }
    }

    /**
     * 表达式片段(自定义函数) 求值
     * @param text
     * @param exprTree
     * @param startCol
     * @param stopCol
     * @param sb
     */
    public void evalSection(String text, ExprTree exprTree, int startCol, int stopCol, StringBuilder sb){
        List<SectionExpr> innerSectionExprList = exprTree.innerSectionAndFunc(startCol, stopCol);
        if (innerSectionExprList.isEmpty()){
            sb.append(text, startCol, stopCol);
            return ;
        }
        int start = startCol;
        for (SectionExpr sectionExpr : innerSectionExprList){
            sb.append(text, start, sectionExpr.startCol);
            sb.append(this.exprEvalService.eval(sectionExpr, exprTree.getParams()));
            start = sectionExpr.stopCol;
        }
        if (start < stopCol){
            sb.append(text, start, stopCol);
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

    //---------------------------------------------------------------

}
