package com.wjs.expr.func;

import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorNil;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.wjs.expr.bean.BaseExpr;

import java.util.Map;

/**
 * @author wjs
 * @date 2020-01-07 11:25
 **/
public class IfNullFunc extends AbstractFunction {

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
        if (arg1 instanceof AviatorNil){
            if (arg1 == AviatorNil.NIL){
                return arg2;
            }
        }
        return arg1;
    }

    @Override
    public String getName() {
        return BaseExpr.GRAMMAR + "ifnull";
    }
}
