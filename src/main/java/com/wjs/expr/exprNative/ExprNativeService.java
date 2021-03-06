package com.wjs.expr.exprNative;

import java.util.function.Predicate;

/**
 * 表达式 处理 方言
 * @author wjs
 * @date 2019-12-31 22:35
 **/
public interface ExprNativeService extends Predicate<Character> {

    /**
     * 将方言表达式修改为需要的格式
     * @param expr
     * @return
     */
    String exprNative(String expr);

}
