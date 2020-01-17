package com.wjs.expr.func;

import com.googlecode.aviator.runtime.type.AviatorBoolean;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.util.Map;

/**
 * @author wjs
 * @date 2020-01-15 16:39
 **/
public class IsEmptyStrFunc extends AbstractExprFunc {

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {

        try {
            Object val = arg1.getValue(env);
            if (val == null){
                return AviatorBoolean.TRUE;
            }
            String str = val.toString();
            if (str.trim().length() == 0){
                return AviatorBoolean.TRUE;
            }
            return AviatorBoolean.FALSE;
        }catch (Exception e){
            return AviatorBoolean.TRUE;
        }
    }

    @Override
    public String name() {
        return "isEmptyStr";
    }
}
