package com.wjs.expr.test;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.AviatorEvaluatorInstance;
import com.googlecode.aviator.exception.ExpressionRuntimeException;
import com.googlecode.aviator.exception.ExpressionSyntaxErrorException;
import com.wjs.expr.ExprException;
import com.wjs.expr.exprNative.SqlExprNativeService;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wjs
 * @date 2020-01-02 15:42
 **/
public class AviatorTest extends BaseTest {

    AviatorEvaluatorInstance aviator = exprService.aviatorEval.getAviator();
    SqlExprNativeService sqlExprNativeService = exprService.sqlExprNativeService;


    private void run(String... exprList){
        for (String expr : exprList){
            Object rs = aviator.execute(sqlExprNativeService.exprNative(expr));
            System.out.println(expr+" = "+rs);
        }
    }

    private void run(Map<String, Object> map, String... exprList){
        for (String expr : exprList){
            Object rs = aviator.execute(sqlExprNativeService.exprNative(expr), map);
            System.out.println(expr+" = "+rs);
        }
    }

    @Test
    public void testEmptyStr() {
        run("''==''");
    }

    @Test
    public void toStringFunc(){
        //run("str(null) == '5'");
        run("str(5) == '5'");
        //run("str(5) == 5");
        run("str('5') == '5'");
    }

    @Test
    public void testLine(){
        Assert.assertThrows(ExpressionSyntaxErrorException.class, () -> run("\"1\\n2\"!=\"1\\n2\""));
        Assert.assertThrows(ExprException.class, () ->run ("'1\n3'!='2'"));
    }

    @Test
    public void testStringEq(){
        run("'and'=='and'",
                "'and'==\"and\"",
                "\"and\"==\"and\"");
    }

    @Test
    public void testTypeMissMatch(){
        Assert.assertThrows(ExpressionRuntimeException.class, ()->run("'1'=1"));
    }

    @Test
    public void testAndOr(){
        run("'and'=\"and\" or 1='1'",
                "'and'=\"and\" and 1=1",
                "\"and\"==\"and\" or 1==1 && 3>1");
    }

    @Test
    public void testObjectProperties(){
        User user = new User("zs", 13);
        Map<String, Object> tmp = new HashMap<>();
        tmp.put("man", true);

        Map<String, Object> map = new HashMap<>();
        map.put("user", user);
        map.put("tmp", tmp);

        List<String> exprList = new ArrayList<>();
        run(map, "'zs'=user.name",
                "user.age>14",
                "tmp.man");
    }

    @Test
    public void testFunction(){
        run("StringUtils.isNotBlank('zs')");
    }

    @Test
    public void testUserTestFunc(){
        Map<String, Object> map = new HashMap<>();
        map.put("test", "test");
        run(map, "$test(ls)",
                "$test(zs) && 1==1",
                "$TEST(zw)",
                "$test('33')",
                "$test(zs, '33')"
                );
    }

    @Test
    public void testColValueFunc(){
        Map<String, Object> map = new HashMap<>();
        map.put("test", "test");
        run(map, "$colValue(e1, 'id', 3)");
    }

    @Test
    public void testLoopFunc(){
        Map<String, Object> map = new HashMap<>();
        map.put("test", "test");
        run(map, "$colValue(e1, $str('id'), 3)");
    }

    @Test
    public void testOpt(){
        List<String> exprList = new ArrayList<>();
        exprList.add("'and'=='and' and 1=1");
        exprList.add("'and'=='and' and 1=1 && 2==2");
        for (String expr : exprList){
            Object rs = aviator.execute(expr);
            System.out.println(expr+" = "+rs);
        }
    }

    @Test
    public void testAviatorExample(){

        System.out.println(AviatorEvaluator.execute("a=1;a+1"));
        Map<String, Object> env = new HashMap<String, Object>();
        env.put("x", 1);
        env.put("y", 2);
        System.out.println(AviatorEvaluator.execute("y+1;x+1", env));

        //AviatorEvaluator.setOption(Options.TRACE_EVAL, true);
        //字符串
        System.out.println(AviatorEvaluator.execute(" 'a\"b' "));
        System.out.println(AviatorEvaluator.execute(" \"a\'b\" "));
        System.out.println(AviatorEvaluator.execute(" 'hello '+3 "));
        System.out.println(AviatorEvaluator.execute(" 'hello '+ unkno2w "));

        //三元计算表达式
        env = new HashMap<String, Object>();
        env.put("a", 1);
        String result = (String) AviatorEvaluator.execute("a>0? 'yes':'no'", env);
        System.out.println(result);


        //数组访问
        int[] a = new int[10];
        for (int i = 0; i < 10; i++) {
            a[i] = i;
        }
        env = new HashMap<String, Object>();
        env.put("a", a);

        System.out.println(AviatorEvaluator.execute("a[1] + 100", env));
        System.out.println(AviatorEvaluator.execute("'a[1]=' + a[1]", env));
        System.out.println(AviatorEvaluator.execute("count(a)", env));
        System.out.println(AviatorEvaluator.execute("reduce(a,+,0)", env));
        System.out.println(AviatorEvaluator.execute("seq.every(a,seq.gt(0))", env));
        System.out.println(AviatorEvaluator.execute("seq.every(a,seq.and(seq.ge(0), seq.lt(10)))", env));
        System.out.println(AviatorEvaluator.execute("seq.not_any(a,seq.and(seq.ge(0), seq.lt(10)))", env));
        System.out.println(AviatorEvaluator.execute("seq.not_any(a,seq.and(seq.lt(0), seq.ge(10)))", env));
        System.out.println(AviatorEvaluator.execute("seq.some(a,seq.eq(3))", env));

        //Nil
        System.out.println(AviatorEvaluator.execute("nil == nil"));
        System.out.println(AviatorEvaluator.execute(" 3> nil"));
        System.out.println(AviatorEvaluator.execute(" ' '>nil "));
        System.out.println(AviatorEvaluator.execute(" a==nil "));
        System.out.println(AviatorEvaluator.execute(" 1!=nil "));
        System.out.println(AviatorEvaluator.execute(" nil<='hello' "));

        //lambda
        String exp = "a=1; b = lambda(x) -> a+ x end ; a=4 ; b(5)";
        System.out.println(AviatorEvaluator.execute(exp)); // output 6

        env = new HashMap<String, Object>();
        env.put("x", 1);
        env.put("y", 2);
        env.put("z", 3);
        User user = new User();
        user.setName("dsdas");
        env.put("user", user);

        AviatorEvaluator.defineFunction("test","lambda(x) -> lambda(y) -> lambda(z) -> x + y + z end end end");
        System.out.println(AviatorEvaluator.execute("test(4)(5)(6)", env)); // output 15

        env.put("a", 4);
        System.out.println(AviatorEvaluator.execute("test(4)(5)(6) + a", env)); // output 19

        //System.out.println(AviatorEvaluator.execute("a++", env));
        System.out.println(AviatorEvaluator.execute("user.age==nil", env));
        System.out.println(AviatorEvaluator.execute("user1==nil", env));
        //System.out.println(AviatorEvaluator.execute("user1.age==nil", env));
        System.out.println(AviatorEvaluator.execute("string.length(user.name)", env));
    }

}
