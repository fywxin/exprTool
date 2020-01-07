package com.wjs.expr.eval;

import org.mvel2.templates.TemplateRuntime;

import java.util.Map;

/**
 * http://mvel.documentnode.com/#simple-property-expression
 * @author wjs
 * @date 2020-01-02 14:39
 **/
public class MvelEval implements ExprEval {

    @Override
    public boolean eval(String expr, Map<String, Object> params) {
        System.out.println(expr);
         Object rs = TemplateRuntime.eval(expr, params);
        System.out.println(rs);
        return (Boolean)rs;
    }

    @Override
    public Object call(String expr, Map<String, Object> params) {
        return null;
    }
}
