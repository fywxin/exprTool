package com.wjs.expr.test;

import com.wjs.expr.ExprGrammarService;
import com.wjs.expr.ExprManager;
import com.wjs.expr.func.*;
import com.wjs.expr.func.inner.InnerIsNullFunc;
import org.junit.Before;

/**
 * @author wjs
 * @date 2020-01-07 09:39
 **/
public class BaseTest {

    public ExprManager exprService = ExprManager.ExprServiceHolder.getInstance();

    ExprGrammarService exprGrammarService = exprService.exprGrammarService;

    @Before
    public void init(){
        exprService.registerFunc(new StrFunc());
        exprService.registerFunc(new IfNullFunc());
        exprService.registerFunc(new TestFunc());
        exprService.registerFunc(new ColValueFunc());
        exprService.registerFunc(new IsNullFunc());
        exprService.registerFunc(new IsNotNullFunc());
        exprService.registerFunc(new InnerIsNullFunc());
    }
}
