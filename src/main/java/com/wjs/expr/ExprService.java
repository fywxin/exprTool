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
        List<Expr> exprList = this.parse(text, params);
        return eval(text, exprList);
    }

    public List<Expr> parse(String text, Map<String, Object> params){
        List<Expr> exprList = this.exprGrammarService.parse(text, true);
        this.attachExprParams(exprList, params);
        return exprList;
    }

    public String eval(String text, List<Expr> exprList){
        if (exprList.isEmpty()){
            return text;
        }
        StringBuilder sb = new StringBuilder(text.length());
        this.eval(text, this.exprGrammarService.root(exprList), sb, 0, text.length());
        return sb.toString();
    }

    /**
     * 绑定执行条件参数
     */
    private void attachExprParams(List<Expr> exprList, Map<String, Object> params){
        exprList.forEach(x -> {
            x.ifExpr.setParams(params);
            x.elifExprList.forEach(y -> y.setParams(params));
        });
    }

    private void eval(String text, List<Expr> flatExprList, StringBuilder sb, int start, int stop){
        if (flatExprList.isEmpty()){
            sb.append(text);
            return ;
        }
        for (Expr expr : flatExprList){
            sb.append(text, start, expr.startCol);
            IfExpr ifExpr = expr.ifExpr;

            boolean match = false;
            //执行If表达式
            if (predicate(ifExpr)){
                this.out(text, ifExpr, sb);
                match = true;
            //执行elseIf表达式
            }else if(!expr.elifExprList.isEmpty()){
                for (ElifExpr elifExpr : expr.elifExprList){
                    if (predicate(elifExpr)){
                        this.out(text, elifExpr, sb);
                        match = true;
                    }
                }
            //如上皆不满足，则使用else值
            }

            if (!match && expr.elseExpr.isPresent()){
                this.out(text, expr.elseExpr.get(), sb);
            }
            start = expr.stopCol;
        }
        if (start < stop){
            sb.append(text, start, stop);
        }
    }

    private void out(String text, BodyExpr bodyExpr, StringBuilder sb){
        List<Expr> child = bodyExpr.getChildExprList();
        if (child.isEmpty()){
            sb.append(text, bodyExpr.bodyStartCol, bodyExpr.bodyStopCol);
        }else{
            eval(text, child, sb, bodyExpr.bodyStartCol, bodyExpr.bodyStopCol);
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
