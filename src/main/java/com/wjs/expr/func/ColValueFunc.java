package com.wjs.expr.func;

import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.*;
import com.wjs.expr.ExprException;
import com.wjs.expr.bean.BaseExpr;

import java.util.Map;

/**
 * @author wjs
 * @date 2020-01-06 18:13
 **/
public class ColValueFunc extends AbstractFunction {

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3) {
        if (arg1.getAviatorType() != AviatorType.JavaType || arg2.getAviatorType() != AviatorType.String || arg3.getAviatorType() != AviatorType.Long) {
            throw new ExprException("Func colValue({stepId}, '{colName}', Number) param type error, Use colValue(e1, 'id', 1)");
        }
        AviatorJavaType obj = (AviatorJavaType)arg1;
        String stepId = obj.getName().trim();
        AviatorString str = (AviatorString)arg2;
        String col = str.getLexeme();
        AviatorLong line = (AviatorLong)arg3;
        long num = line.longValue();

        String val = getName()+"("+stepId+", '"+col+"', "+num+")";
        System.out.println(val);
        return new AviatorString(val);
    }

    @Override
    public String getName() {
        return BaseExpr.GRAMMAR + "colValue";
    }


}
