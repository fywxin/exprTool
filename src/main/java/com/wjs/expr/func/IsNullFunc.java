package com.wjs.expr.func;

import com.googlecode.aviator.runtime.type.AviatorBoolean;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.util.Map;

/**
 * @author wjs
 * @date 2020-01-15 14:50
 **/
public class IsNullFunc extends AbstractExprFunc {

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
        try {
            return AviatorBoolean.valueOf(arg1.getValue(env) == null);
        }catch (Exception e){
            return AviatorBoolean.TRUE;
        }
    }

    @Override
    public String name() {
        return "isNull";
    }
}
