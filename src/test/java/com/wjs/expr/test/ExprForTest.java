package com.wjs.expr.test;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;

/**
 * @author wjs
 * @date 2020-01-09 18:16
 **/
public class ExprForTest extends BaseTest {


    @Test
    public void test1Thin(){
        Map<String, Object> params = new HashMap<>();
        params.put("woe", true);

        String sql = "d\n" +
                "$for (i=0;i<2;i++)\n" +
                "x\n" +
                "$endfor\n" +
                "e";

        System.out.println(sql+"\n");

        String rs = exprService.eval(sql, params);
        System.out.println(rs);
        Assert.assertTrue(("d\n" +
                "x\n" +
                "x\n" +
                "e").equals(rs));
    }

    @Test
    public void testFor(){
        Map<String, Object> params = new HashMap<>();
        params.put("k", 5);
        String sql = "$for(i=1; i<k;i++)\n" +
                "SELECT * FROM mysql.inf.if_user_basic LIMIT <$i$> as e<$i$>;\n" +
                "$endfor";
        String rs = exprService.eval(sql, params);
        System.out.println(rs);
    }

    @Test
    public void test2(){
        Map<String, Object> params = new HashMap<>();
        params.put("woe", true);

        String sql = "1\n" +
                "$for(i=0;i<3;i++)\n" +
                "xx\n" +
                "$endfor\n" +
                "2\n" +
                "$if woe $then\n" +
                "woe\n" +
                "$else\n" +
                "sd\n" +
                "$endif\n" +
                "3";

        String rs = exprService.eval(sql, params);
        System.out.println(rs);
        Assert.assertTrue(("1\n" +
                "xx\n" +
                "xx\n" +
                "xx\n" +
                "2\n" +
                "woe\n" +
                "3").equals(rs));
    }

    @Test
    public void test3(){
        Map<String, Object> params = new HashMap<>();
        params.put("woe", true);

        String sql = "1\n" +
                "$for(i=0;i<2;i++)\n" +
                "   1.1\n" +
                "   $if 1==1 $then\n" +
                "       1.1.<$i$>\n" +
                "   $endif\n" +
                "   xx\n" +
                "$endfor\n" +
                "2\n" +
                "$if woe $then\n" +
                "   woe\n" +
                "$else\n" +
                "   sd\n" +
                "$endif\n" +
                "3";
        String rs = exprService.eval(sql, params);
        System.out.println(rs);
        Assert.assertTrue(("1\n" +
                "   1.1\n" +
                "       1.1.0\n" +
                "   xx\n" +
                "   1.1\n" +
                "       1.1.1\n" +
                "   xx\n" +
                "2\n" +
                "   woe\n" +
                "3").equals(rs));
    }

    @Test
    public void test4(){
        Map<String, Object> params = new HashMap<>();
        params.put("woe", true);

        String sql = "1\n" +
                "$for(i=0;i<1;i++)\n" +
                "   1.1\n" +
                "   $if 1==1 $then\n" +
                "       1.1.i\n" +
                "       $if 1==2 $then\n" +
                "           1.1.i.1\n" +
                "       $else\n" +
                "           1.1.i.2\n" +
                "       $endif\n" +
                "       1.2.i\n" +
                "   $endif\n" +
                "   xx\n" +
                "$endfor\n" +
                "2\n" +
                "$if woe $then\n" +
                "   woe\n" +
                "$else\n" +
                "   sd\n" +
                "$endif\n" +
                "3";
        String rs = exprService.eval(sql, params);
        System.out.println(rs);
        Assert.assertTrue(("1\n" +
                "   1.1\n" +
                "       1.1.i\n" +
                "           1.1.i.2\n" +
                "       1.2.i\n" +
                "   xx\n" +
                "2\n" +
                "   woe\n" +
                "3").equals(rs));
    }

    @Test
    public void test5(){
        Map<String, Object> params = new HashMap<>();
        params.put("woe", true);

        String sql = "1\n" +
                "$for(i=0;i<2;i++)\n" +
                "   1.1\n" +
                "   $if 1==1 $then\n" +
                "       1.1.<$i$>\n" +
                "       $if i==1 $then\n" +
                "           1.1.<$i$>.1\n" +
                "       $else\n" +
                "           1.1.<$i$>.2\n" +
                "       $endif\n" +
                "       1.2.<$i$>\n" +
                "   $endif\n" +
                "   xx\n" +
                "$endfor\n" +
                "2\n\n" +
                "$if woe $then\n" +
                "   woe1\n" +
                "   $for(i=0; i<3; i++)\n" +
                "       woe2\n" +
                "   $endfor\n" +
                "   woe3\n" +
                "$else\n" +
                "   sd1\n" +
                "   $for(i=0; i<3; i++)\n" +
                "       sd2\n" +
                "   $endfor\n" +
                "   sd3" +
                "$endif\n" +
                "3";
        String rs = exprService.eval(sql, params);
        System.out.println(rs);
        Assert.assertTrue(("1\n" +
                "   1.1\n" +
                "       1.1.0\n" +
                "           1.1.0.2\n" +
                "       1.2.0\n" +
                "   xx\n" +
                "   1.1\n" +
                "       1.1.1\n" +
                "           1.1.1.1\n" +
                "       1.2.1\n" +
                "   xx\n" +
                "2\n" +
                "\n" +
                "   woe1\n" +
                "       woe2\n" +
                "       woe2\n" +
                "       woe2\n" +
                "   woe3\n" +
                "3").equals(rs));
    }

    @Test
    public void test6(){
        Map<String, Object> params = new HashMap<>();
        params.put("arr", Arrays.asList("a", "b", "c"));

        String sql = "dd\n" +
                "$for(i=0;i<LIST.size(arr);i++)\n" +
                "<$LIST.get(arr,i)$>\n" +
                "$endfor\n" +
                "ee";
        String rs = exprService.eval(sql, params);
        System.out.println(rs);
        Assert.assertTrue(("dd\n" +
                "a\n" +
                "b\n" +
                "c\n" +
                "ee").equals(rs));
    }

    @Test
    public void test7(){
        Map<String, Object> params = new HashMap<>();
        Set<String> set = new HashSet<>();
        set.add("a1");
        set.add("a2");
        set.add("a3");
        params.put("arr", set);

        String sql = "dd\n" +
                "$for(i=0;i<SET.size(arr);i++)\n" +
                "<$SET.remove(arr,i)$>\n" +
                "$endfor\n" +
                "ee";
        String rs = exprService.eval(sql, params);
        System.out.println(rs);
        Assert.assertTrue(("dd\n" +
                "false\n" +
                "false\n" +
                "false\n" +
                "ee").equals(rs));
    }

    @Test
    public void test8(){
        Map<String, Object> params = new HashMap<>();
        Map<String,String> map = new HashMap<>();
        map.put("a", "A");
        map.put("b", "B");
        params.put("map", map);

        String sql = "dd\n" +
                "$for(e : map)\n" +
                "<$e.key$> - <$e.value$> - <$e_INDEX$> - <$e_FIRST$> - <$e_LAST$>\n" +
                "$endfor\n" +
                "ee";
        String rs = exprService.eval(sql, params);
        System.out.println(rs);
        Assert.assertTrue(("dd\n" +
                "a - A - 0 - true - false\n" +
                "b - B - 1 - false - true\n" +
                "ee").equals(rs));
    }

    @Test
    public void test9(){
        Map<String, Object> params = new HashMap<>();
        params.put("woe", true);

        String sql = "d\n" +
                "$for(i=0;i<2;i++)\n" +
                "   1\n" +
                "   $if(i==0) $then\n" +
                "       $for(j=0;j<3;j++)\n" +
                "           x<$i$>_<$j$>\n" +
                "       $endfor\n" +
                "   $endif\n" +
                "   2\n" +
                "$endfor\n" +
                "e";
        System.out.println(sql+"\n\n");
        String rs = exprService.eval(sql, params);
        System.out.println(rs);
        Assert.assertTrue(("d\n" +
                "   1\n" +
                "           x0_0\n" +
                "           x0_1\n" +
                "           x0_2\n" +
                "   2\n" +
                "   1\n" +
                "   2\n" +
                "e").equals(rs));
    }

    @Test
    public void test10(){
        Map<String, Object> params = new HashMap<>();
        params.put("woe", true);

        String sql = "dd\n" +
                "$for(i=0;i<2;i++)\n" +
                "   $for(j=i;j<3;j++)\n" +
                "       x<$i$>_<$j$>\n" +
                "   $endfor\n" +
                "$endfor\n" +
                "ee";
        String rs = exprService.eval(sql, params);
        System.out.println(rs);
        Assert.assertTrue(("dd\n" +
                "       x0_0\n" +
                "       x0_1\n" +
                "       x0_2\n" +
                "       x1_1\n" +
                "       x1_2\n" +
                "ee").equals(rs));
    }
}
