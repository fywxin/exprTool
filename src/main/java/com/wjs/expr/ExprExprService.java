package com.wjs.expr;

import com.wjs.expr.bean.ExprExpr;
import com.wjs.expr.eval.PredicateEval;
import com.wjs.expr.exprNative.ExprNativeService;
import lombok.extern.slf4j.Slf4j;

/**
 * 表达式断言求知执行器
 * @author wjs
 * @date 2020-01-02 10:22
 **/
@Slf4j
public class ExprExprService {

    public ExprNativeService exprNativeService;

    public PredicateEval predicateEval;

    public boolean eval(ExprExpr exprExpr){
        String expr = exprNativeService.exprNative(exprExpr.getExprText());
        Boolean rs = predicateEval.eval(expr, exprExpr.getParams());
        log.info("{}({}) = {}", expr, exprExpr.getParams(), rs);
        return rs;
    }
}
