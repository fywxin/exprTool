package com.wjs.expr;

import com.wjs.expr.bean.ExprExpr;
import com.wjs.expr.bean.FuncExpr;
import com.wjs.expr.eval.ExprEval;
import com.wjs.expr.exprNative.ExprNativeService;
import lombok.extern.slf4j.Slf4j;

/**
 * 表达式断言求知执行器
 * @author wjs
 * @date 2020-01-02 10:22
 **/
@Slf4j
public class ExprEvalService {

    public ExprNativeService exprNativeService;

    public ExprEval exprEval;

    public boolean eval(ExprExpr exprExpr){
        String expr = exprNativeService.exprNative(exprExpr.getExprText());
        Boolean rs = exprEval.eval(expr, exprExpr.getParams());
        log.info("{}({}) = {}", expr, exprExpr.getParams(), rs);
        return rs;
    }

    public String eval(FuncExpr funcExpr){
        String expr = funcExpr.getFuncText();
        Object rs = exprEval.call(expr, funcExpr.getParams());
        if (rs == null){
            return "null";
        }
        if (rs instanceof Number){
            return String.valueOf(rs);
        }
        if (rs instanceof String){
            return (String)rs;
        }
        if (rs instanceof Boolean){
            return ((Boolean)rs).toString();
        }
        return rs.toString();
    }
}
