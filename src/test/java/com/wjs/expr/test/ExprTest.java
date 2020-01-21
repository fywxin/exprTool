package com.wjs.expr.test;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wjs
 * @date 2019-12-31 22:27
 **/
public class ExprTest extends BaseTest {

    @Test
    public void test1(){
        String sql = "$if 'and'='1' Or(1=1)\n" +
                "\tand '2'<'1'\n" +
                "$then\n" +
                "  select concat(year,\"-\",month,\"-\",day) as ddate,count(1) num\n" +
                "  from hive.woe.l_activity_taskcomplete_log\n" +
                "\t$if 2>1 $then\n" +
                "\t\t2.1\n" +
                "\t\t$if 2<1 $then\n" +
                "\t\t\t2.1.1\n" +
                "\t\t$elseif 2==2 $then\n" +
                "\t\t\t2.1.2\n" +
                "\t\t$else\n" +
                "\t\t\t2.1.3\n" +
                "\t\t$endif\n" +
                "\t\t2.2\n" +
                "\t$else\n" +
                "\t\t333\n" +
                "\t$endif\n" +
                "  where concat(year,month,day) between \"20190321\" and \"20191231\"\n" +
                "$else\n" +
                "   and activityid=30015\n" +
                "$endif\n" +
                "NNNNNN\n" +
                "$if 1==1 $then\n" +
                "  select concat(year,\"-\",month,\"-\",day) as ddate,count(1) num\n" +
                "  from hive.woe.l_activity_taskcomplete_log\n" +
                "\t$if 2>1 $then\n" +
                "\t\t2.1\n" +
                "\t\t$if 2<1 $then\n" +
                "\t\t\t2.1.1\n" +
                "\t\t$elseif 2==2 $then\n" +
                "\t\t\t2.1.2\n" +
                "\t\t$else\n" +
                "\t\t\t2.1.3\n" +
                "\t\t$endif\n" +
                "\t\t2.2\n" +
                "\t$else\n" +
                "\t\t333\n" +
                "\t$endif\n" +
                "  where concat(year,month,day) between \"20190321\" and \"20191231\"\n" +
                "$else\n" +
                "   and activityid=30015\n" +
                "$endif;";

        String rs = exprService.eval(sql, new HashMap<>());
        System.out.println(rs);
        Assert.assertTrue(("   and activityid=30015\n" +
                "NNNNNN\n" +
                "  select concat(year,\"-\",month,\"-\",day) as ddate,count(1) num\n" +
                "  from hive.woe.l_activity_taskcomplete_log\n" +
                "\t\t2.1\n" +
                "\t\t\t2.1.2\n" +
                "\t\t2.2\n" +
                "  where concat(year,month,day) between \"20190321\" and \"20191231\"\n" +
                ";").equals(rs));
    }

    @Test
    public void testExample(){
        Map<String, Object> params = new HashMap<>();
        params.put("woe", true);
        String sql =
                "SELECT concat(year,\"-\",month,\"-\",day) as ddate,count(1) num\n" +
                        "FROM $if woe $then hive.woe.l_activity_taskcomplete_log $else hive.boe.l_activity_taskcomplete_log $endif\n" +
                        "WHERE concat(year,month,day) between \"20190321\" and \"20191231\"\n" +
                        "$if 1=1 $then \n" +
                        "\t1.0\n" +
                        "\t$if 2>1 $then\n" +
                        "\t\t2.1\n" +
                        "\t\t$if 2<1 $then\n" +
                        "\t\t\t2.1.1\n" +
                        "\t\t$elseif 2!=2 $then\n" +
                        "\t\t\t2.1.2\n" +
                        "\t\t$elseif 2>2 $then\n" +
                        "\t\t\t2.1.3\n" +
                        "\t\t$else\n" +
                        "\t\t\t2.1.4\n" +
                        "\t\t\t$if 1==1 $then 2.1.4.1 $else 2.1.4.2 $endif 2.1.4.3 $if 1>1 $then 2.1.4.4 $else 2.1.4.5 $endif\n" +
                        "\t\t\t2.1.5\n" +
                        "\t\t$endif\n" +
                        "\t\t2.2\n" +
                        "\t$else\n" +
                        "\t\t2.3\n" +
                        "\t$endif\n" +
                        "  \t1.1\n" +
                        "$else\n" +
                        "   1.2\n" +
                        "$end;\n" +
                        "\tand activityid=30015 as e1;";
        String rs = exprService.eval(sql, params);
        System.out.println(rs);
        Assert.assertTrue(("SELECT concat(year,\"-\",month,\"-\",day) as ddate,count(1) num\n" +
                "FROM hive.woe.l_activity_taskcomplete_log WHERE concat(year,month,day) between \"20190321\" and \"20191231\"\n" +
                "\t1.0\n" +
                "\t\t2.1\n" +
                "\t\t\t2.1.4\n" +
                "2.1.4.1 2.1.4.3 2.1.4.5 \t\t\t2.1.5\n" +
                "\t\t2.2\n" +
                "  \t1.1\n").equals(rs));
    }

    @Test
    public void testAutoComplete() {
        Map<String, Object> params = new HashMap<>();
        params.put("woe", true);
        String sql =
                "SELECT concat(year,\"-\",month,\"-\",day) as ddate,count(1) num\n" +
                        "FROM $if woe $then hive.woe.l_activity_taskcomplete_log $else hive.boe.l_activity_taskcomplete_log $endif\n" +
                        "WHERE concat(year,month,day) between \"20190321\" and \"20191231\"\n" +
                        "$if 1=1 $then \n" +
                        "\t1.0\n" +
                        "\t$if 2>1 $then\n" +
                        "\t\t2.1\n" +
                        "\t\t$IF 2<1 $then\n" +
                        "\t\t\t2.1.1\n" +
                        "\t\t$elseif 2!=2 $then\n" +
                        "\t\t\t2.1.2\n" +
                        "\t\t$elseif 2>2 $then\n" +
                        "\t\t\t2.1.3\n" +
                        "\t\t$else\n" +
                        "\t\t\t2.1.4\n" +
                        "\t\t\t$if 1==1 $then 2.1.4.1 $else 2.1.4.2 $endif 2.1.4.3 $if 1>1 $then 2.1.4.4";
        String rs = exprService.eval(sql, params);
        System.out.println(rs);
        Assert.assertTrue(("SELECT concat(year,\"-\",month,\"-\",day) as ddate,count(1) num\n" +
                "FROM hive.woe.l_activity_taskcomplete_log WHERE concat(year,month,day) between \"20190321\" and \"20191231\"\n" +
                "\t1.0\n" +
                "\t\t2.1\n" +
                "\t\t\t2.1.4\n" +
                "2.1.4.1 2.1.4.3 ").equals(rs));
    }

    @Test
    public void test3(){
        String sql ="var b=0,a=0;\n" +
                "\n" +
                "$if b=0 $then\n" +
                "SELECT uid, $if b=0 $then ltid, $elseif b>=2 $then citylvl, $else kid, $endif level from mysql.inf.if_user_basic_u WHERE uid = 12807099 as ceshi1;\n" +
                "SELECT * FROM mysql.inf.conf_mf_source LIMIT 10 as e4;\n" +
                "$endif\n" +
                "\n" +
                "\n" +
                "$if b>0 $then \n" +
                "select * from mysql.inf.conf_mf_source LIMIT 10 as e5;\n" +
                "$end";
        Map<String, Object> map = new HashMap<>();
        map.put("b", 0);
        map.put("a", 0);
        String rs = exprService.eval(sql, map);
        System.out.println(rs);
        Assert.assertTrue(("var b=0,a=0;\n" +
                "\n" +
                "SELECT uid, ltid, level from mysql.inf.if_user_basic_u WHERE uid = 12807099 as ceshi1;\n" +
                "SELECT * FROM mysql.inf.conf_mf_source LIMIT 10 as e4;\n\n\n").equals(rs));
    }
}
