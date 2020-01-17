package com.wjs.expr.func;

import com.googlecode.aviator.runtime.type.AviatorBoolean;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.util.Map;

/**
 * @author wjs
 * @date 2020-01-16 10:25
 **/
public class IsNotEmptyStrFunc extends AbstractExprFunc {

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {

        try {
            Object val = arg1.getValue(env);
            if (val == null){
                return AviatorBoolean.FALSE;
            }
            String str = val.toString();
            if (str.trim().length() == 0){
                return AviatorBoolean.FALSE;
            }
            return AviatorBoolean.TRUE;
        }catch (Exception e){
            return AviatorBoolean.FALSE;
        }
    }

    @Override
    public String name() {
        return "isNotEmptyStr";
    }
}