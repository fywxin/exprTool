package com.wjs.expr.eval;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.AviatorEvaluatorInstance;
import com.googlecode.aviator.Options;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.wjs.expr.ExprFunction;
import com.wjs.expr.bean.FuncExpr;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 基于 Aviator 的表达式执行器
 * @author wjs
 * @date 2020-01-02 10:13
 **/
@Slf4j
public class AviatorEval implements ExprEval {

    public static AviatorEvaluatorInstance aviator = AviatorEvaluator.getInstance();;

    static {
        aviator.setOption(Options.OPTIMIZE_LEVEL, AviatorEvaluator.COMPILE);
        aviator.setOption(Options.USE_USER_ENV_AS_TOP_ENV_DIRECTLY, false);
    }

    @Override
    public boolean eval(String expr, Map<String, Object> params) {
        return (Boolean) aviator.execute(expr, params, false);
    }

    @Override
    public Object call(String expr, Map<String, Object> params) {
        return aviator.execute(expr, params, true);
    }

    @Override
    public void registerFunc(ExprFunction func) {
        if (func instanceof AbstractFunction){
            AbstractFunction afunc = (AbstractFunction)func;
            aviator.addFunction(afunc);
            FuncExpr.add(func);
        }
    }

    public AviatorEvaluatorInstance getAviator() {
        return aviator;
    }
}
