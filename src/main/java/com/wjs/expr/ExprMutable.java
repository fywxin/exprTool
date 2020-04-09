package com.wjs.expr;

import com.wjs.expr.bean.FuncExpr;

/**
 * 是否直接结果可变, 如果条件触发了变更，则需要重新执行一遍模板引擎
 * @author wjs
 * @date 2020-03-13 14:18
 **/
public interface ExprMutable {

    /**
     * 决定因子
     * @param funcExpr
     * @return
     */
    String mutableFactor(FuncExpr funcExpr);
}
