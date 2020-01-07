package com.wjs.expr.test;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * 函数方法测试
 * @author wjs
 * @date 2020-01-07 09:38
 **/
public class ExprFuncTest extends BaseTest {

    @Test
    public void test1(){
        String sql="select 1 from \n" +
                "$if a=1 $then\n" +
                "1.1 $ifnull($str(),'Null'), $ifnull($str('val'),'Null')\n" +
                "   1.1.0 $if b=1 $then 1.1.1$colValue(cc,$str('a'),1)$str('b', 'c')1.1.2 $end 1.1.3\n" +
                "$elif a=2 $then\n" +
                "1.2\n" +
                "$elif a=3 $then\n" +
                "1.3\n" +
                "$else\n" +
                "1.4\n" +
                "$end\n" +
                "test";
        Map<String, Object> map = new HashMap<>();
        map.put("a", 1);
        map.put("b", 1);
        String rs = this.exprService.eval(sql, map);
        System.out.println(rs);
        Assert.assertTrue(("select 1 from \n\n1.1 Null, Arg_val\n" +
                "   1.1.0  1.1.1$colValue(cc,Arg_a,1)Arg_b_c1.1.2  1.1.3\n" +
                "\n" +
                "test").equals(rs));
    }
}
