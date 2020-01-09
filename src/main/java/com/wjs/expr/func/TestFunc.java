package com.wjs.expr.func;

import com.googlecode.aviator.runtime.type.AviatorBoolean;
import com.googlecode.aviator.runtime.type.AviatorJavaType;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorType;
import com.wjs.expr.ExprException;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author wjs
 * @date 2020-01-06 17:02
 **/
@Slf4j
public class TestFunc extends AbstractExprFunc {

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3) {
        if (arg1.getAviatorType() != AviatorType.JavaType || arg2.getAviatorType() != AviatorType.String || arg3.getAviatorType() != AviatorType.Long) {
            throw new ExprException("Func colValue({stepId}, '{colName}', Number) param type error, Use colValue(e1, 'id', 1)");
        }
        if (arg1.getAviatorType() == AviatorType.JavaType){
            AviatorJavaType obj = (AviatorJavaType)arg1;
            String value = obj.getName();
            System.out.println("value: "+value);
            return AviatorBoolean.valueOf(value.equals(env.get("test")));
        }
        System.out.println("---------");
        return AviatorBoolean.FALSE;
    }

    @Override
    public String name() {
        return "test";
    }
}
