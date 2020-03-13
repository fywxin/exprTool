package com.wjs.expr;

/**
 * 是否直接结果可变, 如果条件触发了变更，则需要重新执行一遍模板引擎
 * @author wjs
 * @date 2020-03-13 14:18
 **/
public interface ExprMutable {

    /**
     * 是否发生了变更
     * @param args
     * @return
     */
    boolean mutable(Object[] args);
}
