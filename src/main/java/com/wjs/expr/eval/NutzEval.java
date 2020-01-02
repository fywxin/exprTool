package com.wjs.expr.eval;


import java.util.Map;

import org.nutz.el.*;
import org.nutz.lang.util.Context;
import org.nutz.lang.util.SimpleContext;

/**
 * https://github.com/nutzam/nutz/blob/master/src/org/nutz/el/El.java
 *
 * @author wjs
 * @date 2020-01-02 14:18
 **/
public class NutzEval implements PredicateEval {

    @Override
    public boolean eval(String expr, Map<String, Object> params) {
        Context context = new SimpleContext(params);
        return (Boolean) El.eval(context, expr);
    }
}