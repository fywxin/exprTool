package com.wjs.expr;

import com.wjs.expr.eval.AviatorEval;
import com.wjs.expr.exprNative.SqlExprNativeService;
import com.wjs.expr.listener.IForInListener;
import lombok.Getter;
import lombok.Setter;

/**
 * @author wjs
 * @date 2020-01-09 15:48
 **/
@Getter
@Setter
public class ExprManager extends ExprService {

    public AviatorEval aviatorEval;

    public SqlExprNativeService sqlExprNativeService;

    public ExprManager(){
        aviatorEval = new AviatorEval();
        sqlExprNativeService = new SqlExprNativeService();

        exprGrammarService = new ExprGrammarService();
        exprGrammarService.charSpitService = sqlExprNativeService;

        exprEvalService = new ExprEvalService();
        exprEvalService.exprEval = aviatorEval;
        exprEvalService.exprNativeService = sqlExprNativeService;
    }

    public ExprManager registerFunc(ExprFunction func){
        aviatorEval.registerFunc(func);
        ExprFunction.register(func);
        return this;
    }

    public ExprManager addForInListener(IForInListener listener){
        forInListenerList.add(listener);
        return this;
    }

    public static class ExprServiceHolder{
        private static ExprManager INSTANCE = new ExprManager();

        public static ExprManager getInstance() {
            return INSTANCE;
        }
    }
}
