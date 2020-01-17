package com.wjs.expr.func;

import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.*;
import com.wjs.expr.ExprException;
import com.wjs.expr.ExprFunction;

import java.util.Map;

/**
 * @author wjs
 * @date 2020-01-09 15:21
 **/
public abstract class AbstractExprFunc extends AbstractFunction implements ExprFunction {

    @Override
    public String getName() {
        return funcName();
    }

    /**
     * javaType 时， 从env上下文中取值
     * @param env
     * @param arg
     * @return
     */
    public Object getProperty(Map<String, Object> env, AviatorJavaType arg){
        return arg.getValue(env);
    }

    public String getString(AviatorObject arg){
        switch (arg.getAviatorType()){
            case String: return ((AviatorString)arg).getLexeme();
            case JavaType: return ((AviatorJavaType) arg).getName();
            case Long: return String.valueOf(((AviatorLong)arg).longValue());
            case Double: return String.valueOf(((AviatorDouble)arg).doubleValue());
            case BigInt: return String.valueOf(((AviatorBigInt)arg).longValue());
            case Nil:return null;
            case Boolean:return String.valueOf(((AviatorBoolean)arg).booleanValue(null));
            default: throw new ExprException("UnSupport type arg： "+arg);
        }
    }

    public Boolean getBoolean(AviatorObject arg) {
        String key = null;
        switch (arg.getAviatorType()) {
            case String:
                key = ((AviatorString) arg).getLexeme();
                return "1".equals(key) || "true".equals(key.toLowerCase());
            case JavaType:
                key = ((AviatorJavaType) arg).getName();
                return "1".equals(key) || "true".equals(key.toLowerCase());
            case Long:
                return Long.valueOf(((AviatorLong) arg).longValue()).intValue() == 1;
            case Double:
                return false;
            case BigInt:
                return ((AviatorBigInt) arg).toBigInt().intValue() == 1;
            case Nil:
                return false;
            case Boolean:
                return arg.booleanValue(null);
            default:
                throw new ExprException("UnSupport type arg： " + arg);
        }
    }

    public Integer getInteger(AviatorObject arg) {
        switch (arg.getAviatorType()) {
            case String:
                return Integer.parseInt(((AviatorString) arg).getLexeme());
            case JavaType:
                String key = ((AviatorJavaType) arg).getName();
                throw new ExprException("Inter value but get javaType: "+key);
            case Long:
                return Long.valueOf(((AviatorLong) arg).longValue()).intValue();
            case Double:
                return ((AviatorDouble) arg).toBigInt().intValue();
            case BigInt:
                return ((AviatorBigInt) arg).toBigInt().intValue();
            case Nil:
                return null;
            case Boolean:
                return arg.booleanValue(null) ? 1 : 0;
            default:
                throw new ExprException("UnSupport type arg： " + arg);
        }
    }

    public Long getLong(AviatorObject arg) {
        switch (arg.getAviatorType()) {
            case String:
                return Long.parseLong(((AviatorString) arg).getLexeme());
            case JavaType:
                String key = ((AviatorJavaType) arg).getName();
                throw new ExprException("Long value but get javaType: "+key);
            case Long:
                return ((AviatorLong) arg).longValue();
            case Double:
                return ((AviatorDouble) arg).longValue();
            case BigInt:
                return ((AviatorBigInt) arg).longValue();
            case Nil:
                return null;
            case Boolean:
                return arg.booleanValue(null) ? 1L : 0L;
            default:
                throw new ExprException("UnSupport type arg： " + arg);
        }
    }

    public Double getDouble(AviatorObject arg) {
        switch (arg.getAviatorType()) {
            case String:
                return Double.valueOf(((AviatorString) arg).getLexeme());
            case JavaType:
                String key = ((AviatorJavaType) arg).getName();
                throw new ExprException("Double value but get javaType: "+key);
            case Long:
                return ((AviatorLong) arg).doubleValue();
            case Double:
                return ((AviatorDouble) arg).doubleValue();
            case BigInt:
                return ((AviatorBigInt) arg).doubleValue();
            case Nil:
                return null;
            case Boolean:
                return arg.booleanValue(null) ? 1d : 0d;
            default:
                throw new ExprException("UnSupport type arg： " + arg);
        }
    }

    public AviatorObject value(Object obj){
        if (obj == null){
            return AviatorNil.NIL;
        }
        if (obj instanceof String){
            return new AviatorString((String)obj);
        }
        if (obj instanceof Number){
            return AviatorNumber.valueOf(obj);
        }
        if (obj instanceof Boolean){
            return AviatorBoolean.valueOf((Boolean)obj);
        }
        throw new ExprException("UnSupport type object: "+obj);
    }
}
