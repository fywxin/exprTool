package com.wjs.expr.test;

import com.wjs.expr.exprNative.SqlExprNativeService;
import org.junit.Test;
import org.nutz.el.El;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wjs
 * @date 2020-01-02 15:51
 **/
public class NutzElTests {

    SqlExprNativeService sqlExprNativeService = new SqlExprNativeService();

    @Test
    public void testStringEq2(){
        List<String> exprList = new ArrayList<>();
        exprList.add("'and'=='and'");
        exprList.add("\"and\"==\"and\" and 1==1 && 3>1");
        exprList.add("'and'=\"and\" and 1='1'");

        for (String expr : exprList){
            Object rs = El.eval(sqlExprNativeService.exprNative(expr));
            System.out.println(expr+" = "+rs);
        }
    }
}
