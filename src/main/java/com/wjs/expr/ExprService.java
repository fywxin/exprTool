package com.wjs.expr;

import com.wjs.expr.bean.*;
import com.wjs.expr.common.Tuple2;
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
        Tuple2<List<Expr>, List<FuncExpr>> tuple2 = this.parse(text, params);
        return eval(text, tuple2);
    }

    public Tuple2<List<Expr>, List<FuncExpr>> parse(String text, Map<String, Object> params){
        Tuple2<List<Expr>, List<FuncExpr>> tuple2 = this.exprGrammarService.parse(text, true);
        this.attachExprParams(tuple2, params);
        return tuple2;
    }

    public String eval(String text, Tuple2<List<Expr>, List<FuncExpr>> tuple2){
        if (tuple2.getFirst().isEmpty()){
            return evalFuncExpr(text, tuple2.getSecond());
        }
        StringBuilder sb = new StringBuilder(text.length());
        this.eval(text, this.exprGrammarService.root(tuple2.getFirst()), tuple2.getSecond(), sb, 0, text.length());
        return sb.toString();
    }



    /**
     * 绑定执行条件参数
     */
    private void attachExprParams(Tuple2<List<Expr>, List<FuncExpr>> tuple2, Map<String, Object> params){
        tuple2.getFirst().forEach(x -> {
            x.ifExpr.setParams(params);
            x.elifExprList.forEach(y -> y.setParams(params));
        });
        tuple2.getSecond().forEach(x -> x.setParams(params));
    }

    private void eval(String text, List<Expr> flatExprList, List<FuncExpr> funcExprList, StringBuilder sb, int start, int stop){
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
                this.out(text, ifExpr, funcExprList, sb);
                match = true;
            //执行elseIf表达式
            }else if(!expr.elifExprList.isEmpty()){
                for (ElifExpr elifExpr : expr.elifExprList){
                    if (predicate(elifExpr)){
                        this.out(text, elifExpr, funcExprList, sb);
                        match = true;
                    }
                }
            //如上皆不满足，则使用else值
            }

            if (!match && expr.elseExpr.isPresent()){
                this.out(text, expr.elseExpr.get(), funcExprList, sb);
            }
            start = expr.stopCol;
        }
        if (start < stop){
            sb.append(text, start, stop);
        }
    }

    private void out(String text, BodyExpr bodyExpr, List<FuncExpr> funcExprList,StringBuilder sb){
        List<Expr> child = bodyExpr.getChildExprList();
        if (child.isEmpty()){
            sb.append(text, bodyExpr.bodyStartCol, bodyExpr.bodyStopCol);
        }else{
            eval(text, child, funcExprList, sb, bodyExpr.bodyStartCol, bodyExpr.bodyStopCol);
        }
    }

    public String evalFuncExpr(String text, List<FuncExpr> funcExprList){
        return null;
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
