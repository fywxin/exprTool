package com.wjs.expr.func;

import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.wjs.expr.ExprFunction;

/**
 * @author wjs
 * @date 2020-01-09 15:21
 **/
public abstract class AbstractExprFunc extends AbstractFunction implements ExprFunction {

    @Override
    public String getName() {
        return funcName();
    }
}
