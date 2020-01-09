package com.wjs.expr;

import com.wjs.expr.bean.BaseExpr;

/**
 * @Author wjs
 * @Date 2020-01-09 15:11
 **/
public interface ExprFunction {

    String name();

    default String funcName(){
        return BaseExpr.GRAMMAR + name();
    }
}
