package com.wjs.expr;

import com.wjs.expr.bean.BaseExpr;

import java.util.Map;

/**
 * 内置快速执行方法，绕过表达式解析引擎，加快执行速度
 * 内置方法 $_ 开头
 * @author wjs
 * @date 2020-01-17 09:15
 **/
public interface ExprInnerFunction extends ExprFunction {

    Object invoke(Map<String, Object> env, String paramStr);

    String Name();

    @Override
    default String name(){
        return BaseExpr.INNNER + Name();
    }
}
