package com.wjs.expr.eval;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.AviatorEvaluatorInstance;
import com.googlecode.aviator.Options;
import com.wjs.expr.bean.FuncExpr;
import com.wjs.expr.func.ColValueFunc;
import com.wjs.expr.func.IfNullFunc;
import com.wjs.expr.func.StrFunc;
import com.wjs.expr.func.TestFunc;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 基于 Aviator 的表达式执行器
 * @author wjs
 * @date 2020-01-02 10:13
 **/
@Slf4j
public class AviatorEval implements ExprEval {

    public static AviatorEvaluatorInstance aviatorEvaluatorInstance = AviatorEvaluator.getInstance();

    static {
        aviatorEvaluatorInstance.setOption(Options.OPTIMIZE_LEVEL, AviatorEvaluator.COMPILE);
        aviatorEvaluatorInstance.setOption(Options.USE_USER_ENV_AS_TOP_ENV_DIRECTLY, false);
        TestFunc testFunc = new TestFunc();
        ColValueFunc colValueFunc = new ColValueFunc();
        StrFunc strFunc = new StrFunc();
        IfNullFunc ifNullFunc = new IfNullFunc();
        FuncExpr.add(testFunc);
        FuncExpr.add(colValueFunc);
        FuncExpr.add(strFunc);
        FuncExpr.add(ifNullFunc);

        aviatorEvaluatorInstance.addFunction(testFunc);
        aviatorEvaluatorInstance.addFunction(colValueFunc);
        aviatorEvaluatorInstance.addFunction(strFunc);
        aviatorEvaluatorInstance.addFunction(ifNullFunc);
        //TODO 添加日期自定义方法
    }

    @Override
    public boolean eval(String expr, Map<String, Object> params) {
        return (Boolean) aviatorEvaluatorInstance.execute(expr, params, false);
    }

    @Override
    public Object call(String expr, Map<String, Object> params) {
        System.out.println(expr);
        return aviatorEvaluatorInstance.execute(expr, params, true);
    }
}
