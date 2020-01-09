package com.wjs.expr.eval;

import com.wjs.expr.ExprFunction;

import java.util.Map;

/**
 * 断言执行
 * @author wjs
 * @date 2020-01-02 10:08
 **/
public interface ExprEval {

    boolean eval(String expr, Map<String, Object> params);

    Object call(String expr, Map<String, Object> params);

    /**
     * 函数注册
     * @param func
     */
    void registerFunc(ExprFunction func);
}
