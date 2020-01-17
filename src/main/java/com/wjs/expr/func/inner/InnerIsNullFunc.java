package com.wjs.expr.func.inner;

import com.wjs.expr.ExprInnerFunction;

import java.util.Map;

/**
 * @author wjs
 * @date 2020-01-17 11:02
 **/
public class InnerIsNullFunc implements ExprInnerFunction {
    @Override
    public Object invoke(Map<String, Object> env, String paramStr) {

        return env.containsKey(paramStr);
    }

    @Override
    public String Name() {
        return "isNull";
    }
}
