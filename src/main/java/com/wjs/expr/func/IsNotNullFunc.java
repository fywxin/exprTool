package com.wjs.expr.func;

import com.googlecode.aviator.runtime.type.AviatorBoolean;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.util.Map;

/**
 * @author wjs
 * @date 2020-01-15 15:56
 **/
public class IsNotNullFunc extends AbstractExprFunc {

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
        try {
            return AviatorBoolean.valueOf(arg1.getValue(env) != null);
        }catch (Exception e){
            return AviatorBoolean.FALSE;
        }
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
        Object object = env.get(getString(arg1));
        if (object == null){
            return AviatorBoolean.FALSE;
        }


        try {
            return AviatorBoolean.valueOf(arg1.getValue(env) != null);
        }catch (Exception e){
            return AviatorBoolean.FALSE;
        }
    }

    @Override
    public String name() {
        return "isNotNull";
    }
}
