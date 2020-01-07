package com.wjs.expr.test;

import com.wjs.expr.ExprEvalService;
import com.wjs.expr.ExprGrammarService;
import com.wjs.expr.ExprService;
import com.wjs.expr.eval.AviatorEval;
import com.wjs.expr.eval.ExprEval;
import com.wjs.expr.exprNative.ExprNativeService;
import com.wjs.expr.exprNative.SqlExprNativeService;
import org.junit.Before;

/**
 * @author wjs
 * @date 2020-01-07 09:39
 **/
public class BaseTest {

    public ExprGrammarService exprGrammarService = new ExprGrammarService();
    public ExprService exprService = new ExprService();
    public ExprEval exprEval = new AviatorEval();
    public ExprNativeService exprNativeService = new SqlExprNativeService();
    public ExprEvalService exprEvalService = new ExprEvalService();

    @Before
    public void init(){
        exprGrammarService.exprNativeService = exprNativeService;
        exprService.exprGrammarService = exprGrammarService;
        exprEvalService.exprEval = exprEval;
        exprEvalService.exprNativeService = exprNativeService;
        exprService.exprEvalService = exprEvalService;
    }
}
