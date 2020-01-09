package com.wjs.expr.test;

import com.wjs.expr.ExprGrammarService;
import com.wjs.expr.ExprManager;
import com.wjs.expr.func.ColValueFunc;
import com.wjs.expr.func.IfNullFunc;
import com.wjs.expr.func.StrFunc;
import com.wjs.expr.func.TestFunc;
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
    }
}
