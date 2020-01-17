package com.wjs.expr.test;

import com.wjs.expr.ExprException;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wjs
 * @date 2020-01-09 11:12
 **/
public class ExprSectionTest extends BaseTest {

    @Test
    public void test1(){
        String sql = "aa <$ 1 $>ff";
        Map<String, Object> map = new HashMap<>();
        map.put("a", 1);
        String rs = this.exprService.eval(sql, map);
        System.out.println(rs);
        Assert.assertTrue("aa 1ff".equals(rs));

    }

    @Test
    public void test2(){
        String sql = "aa <$ a+1 $>ff";
        Map<String, Object> map = new HashMap<>();
        map.put("a", 1);
        String rs = this.exprService.eval(sql, map);
        System.out.println(rs);
        Assert.assertTrue("aa 2ff".equals(rs));

    }

    @Test
    public void testWrap(){
        String sql = "aa <$ 3+<$a+1$> $>ff";
        Map<String, Object> map = new HashMap<>();
        map.put("a", 1);
        Assert.assertThrows(ExprException.class, () -> this.exprService.eval(sql, map));
    }

    @Test
    public void testCrossLine(){
        String sql = "aa <$ a+\n1 $>ff";
        Map<String, Object> map = new HashMap<>();
        map.put("a", 1);
        Assert.assertThrows(ExprException.class, () -> this.exprService.eval(sql, map));
    }

    @Test
    public void testFuncAndSection1() {
        String sql = "aa$str('1') <$ a+1 $>ff";
        Map<String, Object> map = new HashMap<>();
        map.put("a", 1);
        String rs = this.exprService.eval(sql, map);
        System.out.println(rs);
        Assert.assertTrue("aaArg_1 2ff".equals(rs));
    }

    @Test
    public void testFuncAndSection2() {
        String sql = "aa <$ $str('1')+1 $>ff";
        Map<String, Object> map = new HashMap<>();
        map.put("a", 1);
        String rs = this.exprService.eval(sql, map);
        System.out.println(rs);
        Assert.assertTrue("aa Arg_11ff".equals(rs));
    }

    @Test
    public void testFuncAndSection3() {
        String sql = "aa $str('0')<$ $str('1')+'|' $>ff $str('3')";
        Map<String, Object> map = new HashMap<>();
        map.put("a", 1);
        String rs = this.exprService.eval(sql, map);
        System.out.println(rs);
        Assert.assertTrue("aa Arg_0Arg_1|ff Arg_3".equals(rs));
    }

    @Test
    public void testFuncAndSection4() {
        String sql = "aa $str('0')<$ $str('1')+'|' $>ff $str('3')<$$>";
        Map<String, Object> map = new HashMap<>();
        map.put("a", 1);

        String rs = this.exprService.eval(sql, map);
        System.out.println(rs);
        Assert.assertTrue("aa Arg_0Arg_1|ff Arg_3".equals(rs));
    }

    @Test
    public void testFuncAndSection5() {
        String sql = "aa $str('0')<$ $str('1')+'|' $>f$str('3')f $str('4')<$$>";
        Map<String, Object> map = new HashMap<>();
        map.put("a", 1);

        String rs = this.exprService.eval(sql, map);
        System.out.println(rs);
        Assert.assertTrue("aa Arg_0Arg_1|fArg_3f Arg_4".equals(rs));
    }

    @Test
    public void testFuncAndSection6() {
        String sql = "aa $str('0')<$ $str('1')+'|' $>ff $str('3') <$";
        Map<String, Object> map = new HashMap<>();
        map.put("a", 1);

        Assert.assertThrows(ExprException.class, () -> this.exprService.eval(sql, map));
    }

    @Test
    public void testAll() {
        Map<String, Object> map = new HashMap<>();
        map.put("a", 1);
        String sql="select 1 from \n" +
                "$if a=1 $then\n" +
                "1.1 $ifNull($str(),'Null'), $ifNull($str('val'),'Null')\n" +
                "   1.1.0 $if b=1 $then 1.1.1$colValue(cc,$str('a'),1)$str('b', 'c')1.1.2 $endif 1.1.3\n" +
                "   <$ 'section'+a$> $str('1.1.4')\n"+
                "$elif a=2 $then\n" +
                "1.2\n" +
                "$elif a=3 $then\n" +
                "1.3\n" +
                "$else\n" +
                "1.4 <$ 'section'+a$> $str('1.4.1')\n" +
                "$endif\n" +
                "test";
        String rs = this.exprService.eval(sql, map);
        System.out.println(rs);
        Assert.assertTrue(("select 1 from \n" +
                "\n" +
                "1.1 Null, Arg_val\n" +
                "   1.1.0  1.1.3\n" +
                "   section1 Arg_1.1.4\n" +
                "\n" +
                "test").equals(rs));
    }

    @Test
    public void testDefaultVal1(){
        Map<String, Object> map = new HashMap<>();
        map.put("a", 1);
        String sql = "aa <$ b!!BB $> cc";
        String rs = this.exprService.eval(sql, map);
        System.out.println(rs);
        Assert.assertTrue("aa BB cc".equals(rs));
    }

    @Test
    public void testDefaultVal2(){
        Map<String, Object> map = new HashMap<>();
        map.put("a", 1);
        String sql = "aa <$ b !! BB $>cc aa <$ b !!' BB '$>cc <$ a !!' BB '$>";
        String rs = this.exprService.eval(sql, map);
        System.out.println(rs);
        Assert.assertTrue("aa BBcc aa  BB cc 1".equals(rs));
    }
}
