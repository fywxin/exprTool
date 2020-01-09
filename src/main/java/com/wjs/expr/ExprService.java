package com.wjs.expr;

import com.wjs.expr.bean.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * 表达式执行求解器
 * @author wjs
 * @date 2019-12-31 20:05
 **/
@Slf4j
public class ExprService {

    public ExprGrammarService exprGrammarService;

    public ExprEvalService exprEvalService;

    /**
     * 入口函数，表达式解析执行
     * @param text 表达式
     * @param params 表达式变量值
     * @return
     */
    public String eval(String text, Map<String, Object> params){
        Exprs exprs = this.parse(text, params);
        return eval(text, exprs);
    }

    public Exprs parse(String text, Map<String, Object> params){
        Exprs exprs = this.exprGrammarService.parse(text, true);
        exprs.attachExprParams(params);
        return exprs;
    }

    public String eval(String text, Exprs exprs){
        if (exprs.isEmpty()){
            return text;
        }
        StringBuilder sb = new StringBuilder(text.length());
        this.evalExpr(text, exprs.rootExprList(), exprs, sb, 0, text.length());
        return sb.toString();
    }

    private void evalExpr(String text, List<Expr> flatExprList, Exprs exprs, StringBuilder sb, int start, int stop){
        for (Expr expr : flatExprList){
            evalSection(text, exprs, start, expr.startCol, sb);
            IfExpr ifExpr = expr.ifExpr;

            boolean match = false;
            //执行If表达式
            if (predicate(ifExpr)){
                this.out(text, ifExpr, exprs, sb);
                match = true;
            //执行elseIf表达式
            }else if(!expr.elifExprList.isEmpty()){
                for (ElifExpr elifExpr : expr.elifExprList){
                    if (predicate(elifExpr)){
                        this.out(text, elifExpr, exprs, sb);
                        match = true;
                    }
                }
            //如上皆不满足，则使用else值
            }

            if (!match && expr.elseExpr.isPresent()){
                this.out(text, expr.elseExpr.get(), exprs, sb);
            }
            start = expr.stopCol;
        }
        if (start < stop){
            //sb.append(text, start, stop);
            evalSection(text, exprs, start, stop, sb);
        }
    }

    private void out(String text, BodyExpr bodyExpr, Exprs exprs, StringBuilder sb){
        List<Expr> child = bodyExpr.getChildExprList();
        if (child.isEmpty()){
            //sb.append(text, bodyExpr.bodyStartCol, bodyExpr.bodyStopCol);
            evalSection(text, exprs, bodyExpr.bodyStartCol, bodyExpr.bodyStopCol, sb);
        }else{
            evalExpr(text, child, exprs, sb, bodyExpr.bodyStartCol, bodyExpr.bodyStopCol);
        }
    }

    /**
     * 表达式片段(自定义函数) 求值
     * @param text
     * @param exprs
     * @param startCol
     * @param stopCol
     * @param sb
     */
    public void evalSection(String text, Exprs exprs, int startCol, int stopCol, StringBuilder sb){
        List<SectionExpr> innerSectionExprList = exprs.innerSectionAndFunc(startCol, stopCol);
        if (innerSectionExprList.isEmpty()){
            sb.append(text, startCol, stopCol);
            return ;
        }
        int start = startCol;
        for (SectionExpr sectionExpr : innerSectionExprList){
            sb.append(text, start, sectionExpr.startCol);
            sb.append(this.exprEvalService.eval(sectionExpr));
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
    private boolean predicate(ExprExpr exprExpr){
        try {
            return exprEvalService.eval(exprExpr);
        }catch (Exception e){
            log.error("表达式计算异常: ["+exprExpr.getExprText()+"].("+exprExpr.getParams()+")", e);
            throw new ExprException(e);
        }
    }

}
