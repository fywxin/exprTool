package com.wjs.expr.eval;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.AviatorEvaluatorInstance;
import com.googlecode.aviator.Options;

import java.util.Map;

/**
 * 基于 Aviator 的表达式执行器
 * @author wjs
 * @date 2020-01-02 10:13
 **/
public class AviatorEval implements PredicateEval {

    AviatorEvaluatorInstance aviatorEvaluatorInstance = AviatorEvaluator.getInstance();

    {
        aviatorEvaluatorInstance.setOption(Options.OPTIMIZE_LEVEL, AviatorEvaluator.COMPILE);
        aviatorEvaluatorInstance.setOption(Options.USE_USER_ENV_AS_TOP_ENV_DIRECTLY, false);
    }

    @Override
    public boolean eval(String expr, Map<String, Object> params) {
        return (Boolean) aviatorEvaluatorInstance.execute(expr, params, false);
    }
}
