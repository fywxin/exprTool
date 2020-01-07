package com.wjs.expr.test;

import com.googlecode.aviator.AviatorEvaluatorInstance;
import com.googlecode.aviator.exception.ExpressionRuntimeException;
import com.googlecode.aviator.exception.ExpressionSyntaxErrorException;
import com.wjs.expr.ExprException;
import com.wjs.expr.eval.AviatorEval;
import com.wjs.expr.exprNative.SqlExprNativeService;
import lombok.Getter;
import lombok.Setter;
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
public class AviatorTest {

    AviatorEvaluatorInstance aviatorEvaluatorInstance = AviatorEval.aviatorEvaluatorInstance;
    SqlExprNativeService sqlExprNativeService = new SqlExprNativeService();


    private void run(String... exprList){
        for (String expr : exprList){
            Object rs = aviatorEvaluatorInstance.execute(sqlExprNativeService.exprNative(expr));
            System.out.println(expr+" = "+rs);
        }
    }

    private void run(Map<String, Object> map, String... exprList){
        for (String expr : exprList){
            Object rs = aviatorEvaluatorInstance.execute(sqlExprNativeService.exprNative(expr), map);
            System.out.println(expr+" = "+rs);
        }
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
            Object rs = aviatorEvaluatorInstance.execute(expr);
            System.out.println(expr+" = "+rs);
        }
    }

    @Getter
    @Setter
    public static class User{

        public String name;

        private Integer age;

        public User(String name, Integer age) {
            this.name = name;
            this.age = age;
        }
    }
}
