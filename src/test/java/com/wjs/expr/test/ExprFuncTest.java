package com.wjs.expr.test;

import org.junit.Assert;
import org.junit.Test;
import org.nutz.dao.sql.Sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
                "1.1 $ifNull($str(),'Null'), $ifNull($str('val'),'Null')\n" +
                "   1.1.0 $if b=1 $then 1.1.1$colValue(cc,$str('a'),1)$str('b', 'c')1.1.2 $endif 1.1.3\n" +
                "$elif a=2 $then\n" +
                "1.2\n" +
                "$elif a=3 $then\n" +
                "1.3\n" +
                "$else\n" +
                "1.4\n" +
                "$endif\n" +
                "test";
        Map<String, Object> map = new HashMap<>();
        map.put("a", 1);
        map.put("b", 1);
        String rs = this.exprService.eval(sql, map);
        System.out.println(rs);
        Assert.assertTrue(("select 1 from \n" +
                "\n" +
                "1.1 Null, Arg_val\n" +
                "   1.1.0  1.1.1$colValue[cc_Arg_a_1]Arg_b_c1.1.2  1.1.3\n" +
                "\n" +
                "test").equals(rs));
    }

    @Test
    public void test2(){
        Map<String, Object> map = new HashMap<>();
        map.put("a", 1);
        User user = new User();
        user.setName("test");
        map.put("user", user);



        String sql = "constant:$isNull(1) xx:$isNull(xx) user:$isNull(user) name:$isNull(user.name) age:$isNull(user.age) none:$isNull(user.none)  over <$ user.name $>";
        String rs = this.exprService.eval(sql, map);
        System.out.println(rs);
        Assert.assertTrue("constant:false xx:true user:false name:false age:true none:true  over test".equals(rs));

        List<String> tags = new ArrayList<>();
        tags.add("tag1");
        tags.add("tag2");
        user.setTags(tags);
        map.put("i",1);

        List<User> child = new ArrayList<>();
        User u1 = new User("zs", 1);
        User u2 = new User("ls", 2);
        child.add(u1);
        child.add(u2);
        user.setChild(child);

        sql = "<$ LIST.size(user.tags)-1 $> <$ user.tags[i] $> <$ user.child[i] $> <$ user.child[i] $>";
        rs = this.exprService.eval(sql, map);
        System.out.println(rs);
    }

    @Test
    public void testInnerFunc(){
        Map<String, Object> map = new HashMap<>();
        map.put("a", 1);
        String sql ="a $if $_isNull(a) $then null $else a $endif b $_isNull(a)";
        String rs = this.exprService.eval(sql, map);
        System.out.println(rs);
        Assert.assertTrue("a  null  b true".equals(rs));
    }
}
