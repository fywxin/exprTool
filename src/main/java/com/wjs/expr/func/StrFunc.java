package com.wjs.expr.func;

import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorNil;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorString;
import com.wjs.expr.bean.BaseExpr;

import java.util.Map;

/**
 * @author wjs
 * @date 2020-01-07 09:04
 **/
public class StrFunc extends AbstractFunction {

    @Override
    public AviatorObject call(Map<String, Object> env) {

        return AviatorNil.NIL;
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
        AviatorString str = (AviatorString)arg1;
        String col = str.getLexeme();
        System.out.println(getName()+"('"+col+"') = Arg_"+ col);
        return new AviatorString("Arg_"+col);
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
        AviatorString str1 = (AviatorString)arg1;
        String col1 = str1.getLexeme();

        AviatorString str2 = (AviatorString)arg2;
        String col2 = str2.getLexeme();

        System.out.println(getName()+"('"+col1+"', '"+col2+"') = Arg_"+ col1+"_"+col2);
        return new AviatorString("Arg_"+ col1+"_"+col2);
    }

    @Override
    public String getName() {
        return BaseExpr.GRAMMAR + "str";
    }
}
