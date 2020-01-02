package com.wjs.expr.eval;

import java.util.Map;

/**
 * 断言执行
 * @author wjs
 * @date 2020-01-02 10:08
 **/
public interface PredicateEval {

    boolean eval(String expr, Map<String, Object> params);
}
