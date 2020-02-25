package com.wjs.expr.exprNative;

/**
 * 判断字符是否为分隔符
 * @author wjs
 * @date 2020-02-23 23:44
 **/
@FunctionalInterface
public interface CharSpitPredicate {

    /**
     * 是否分割符号
     * @param c
     * @return
     */
    boolean isSplitChar(Character c);
}
