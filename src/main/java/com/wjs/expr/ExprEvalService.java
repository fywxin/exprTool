package com.wjs.expr;

import com.wjs.expr.bean.ExprExpr;
import com.wjs.expr.bean.SectionExpr;
import com.wjs.expr.eval.ExprEval;
import com.wjs.expr.exprNative.ExprNativeService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 表达式断言求知执行器
 * @author wjs
 * @date 2020-01-02 10:22
 **/
@Setter
@Slf4j
public class ExprEvalService {

    public ExprNativeService exprNativeService;

    public ExprEval exprEval;

    public boolean eval(ExprExpr exprExpr, Map<String, Object> params){
        String expr = exprNativeService.exprNative(exprExpr.getExprText());
        Boolean rs = exprEval.eval(expr, params);
        log.info("{}({}) = {}", expr, params, rs);
        return rs;
    }

    public String eval(SectionExpr sectionExpr, Map<String, Object> params){
        String expr = sectionExpr.getSectionText();
        if (expr == null || expr.trim().length() == 0){
            return "";
        }
        Object rs = exprEval.call(expr, params);
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
