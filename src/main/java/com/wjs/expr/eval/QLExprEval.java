package com.wjs.expr.eval;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.wjs.expr.ExprException;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * https://github.com/alibaba/QLExpress
 * @author wjs
 * @date 2020-01-02 14:33
 **/
@Slf4j
public class QLExprEval implements ExprEval {

    ExpressRunner runner = new ExpressRunner();

    @Override
    public boolean eval(String expr, Map<String, Object> params) {
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        context.putAll(params);
        try {
            return (Boolean) runner.execute(expr, context, null, false, false);
        } catch (Exception e) {
            log.error("表达式["+expr+"]执行异常", e);
            throw new ExprException(e);
        }
    }

    @Override
    public Object call(String expr, Map<String, Object> params) {
        return null;
    }
}
