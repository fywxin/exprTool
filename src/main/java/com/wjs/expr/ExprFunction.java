package com.wjs.expr;

import com.wjs.expr.bean.BaseExpr;
import org.apache.commons.collections.map.HashedMap;

import java.util.Map;

/**
 * @Author wjs
 * @Date 2020-01-09 15:11
 **/
public interface ExprFunction {

    Map<String, ExprFunction> funcCache = new HashedMap();

    static void register(ExprFunction exprFunction){
        funcCache.put(exprFunction.funcName(), exprFunction);
    }

    static boolean support(String funcName){
        return funcCache.containsKey(funcName);
    }

    static ExprFunction get(String funcName){
        return funcCache.get(funcName);
    }

    String name();

    default String funcName(){
        return BaseExpr.GRAMMAR + name();
    }
}
