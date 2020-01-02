package com.wjs.expr.test;

import com.wjs.expr.ExprEvalService;
import com.wjs.expr.ExprExprService;
import com.wjs.expr.ExprGrammarService;
import com.wjs.expr.bean.Expr;
import com.wjs.expr.eval.AviatorEval;
import com.wjs.expr.eval.NutzEval;
import com.wjs.expr.eval.PredicateEval;
import com.wjs.expr.eval.QLExprEval;
import com.wjs.expr.exprNative.SqlExprNativeService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wjs
 * @date 2019-12-31 22:27
 **/
public class ExprTest {

    ExprGrammarService exprGrammarService = new ExprGrammarService();
    ExprEvalService exprEvalService = new ExprEvalService();
    PredicateEval predicateEval = new AviatorEval();
    SqlExprNativeService sqlExprNativeService = new SqlExprNativeService();
    ExprExprService exprExprService = new ExprExprService();

    @Before
    public void init(){
        exprGrammarService.exprNativeService = sqlExprNativeService;
        exprEvalService.exprGrammarService = exprGrammarService;
        exprExprService.predicateEval = predicateEval;
        exprExprService.exprNativeService = sqlExprNativeService;
        exprEvalService.exprExprService = exprExprService;
    }

    @Test
    public void testExpr(){
        String text = "select concat(year,\"-\",month,\"-\",day) as ddate,count(1) num\n" +
                "from hive.woe.l_activity_taskcomplete_log\n" +
                "where concat(year,month,day) between \"20190321\" and \"20191231\"\n" +
                "and activityid=30015\n" +
                "group by ddate\n" +
                "as e1;\n" +
                "#if ads=sdf #then\n" +
                "select concat(year,\"-\",month,\"-\",day) as ddate,count(1) num  #if 1=1 #then dsdds #else sayhello #end\n" +
                "#elif ds=dffff adn #then;\n" +
                "from hive.woe.l_activity_taskcomplete_log\n" +
                "#else\n" +
                "where concat(year,month,day) between \"20190321\" and \"20191231\"\n" +
                "and activityid=30015\n" +
                "#end\n" +
                "group by ddate\n" +
                "as e2;";


        List<Expr> list = exprGrammarService.parse(text);
        System.out.println(list);
    }

    @Test
    public void test1(){
        String sql = "#if 'and'='1' Or(1=1)\n" +
                "\tand '2'<'1'\n" +
                "#then\n" +
                "  select concat(year,\"-\",month,\"-\",day) as ddate,count(1) num\n" +
                "  from hive.woe.l_activity_taskcomplete_log\n" +
                "\t#if 2>1 #then\n" +
                "\t\t2.1\n" +
                "\t\t#if 2<1 #then\n" +
                "\t\t\t2.1.1\n" +
                "\t\t#elif 2==2 #then\n" +
                "\t\t\t2.1.2\n" +
                "\t\t#else\n" +
                "\t\t\t2.1.3\n" +
                "\t\t#end\n" +
                "\t\t2.2\n" +
                "\t#else\n" +
                "\t\t333\n" +
                "\t#end\n" +
                "  where concat(year,month,day) between \"20190321\" and \"20191231\"\n" +
                "#else\n" +
                "   and activityid=30015\n" +
                "#end\n" +
                "NNNNNN\n" +
                "#if 1==1 #then\n" +
                "  select concat(year,\"-\",month,\"-\",day) as ddate,count(1) num\n" +
                "  from hive.woe.l_activity_taskcomplete_log\n" +
                "\t#if 2>1 #then\n" +
                "\t\t2.1\n" +
                "\t\t#if 2<1 #then\n" +
                "\t\t\t2.1.1\n" +
                "\t\t#elif 2==2 #then\n" +
                "\t\t\t2.1.2\n" +
                "\t\t#else\n" +
                "\t\t\t2.1.3\n" +
                "\t\t#end\n" +
                "\t\t2.2\n" +
                "\t#else\n" +
                "\t\t333\n" +
                "\t#end\n" +
                "  where concat(year,month,day) between \"20190321\" and \"20191231\"\n" +
                "#else\n" +
                "   and activityid=30015\n" +
                "#end;";

        String rs = exprEvalService.eval(sql, new HashMap<>());
        System.out.println(rs);
        Assert.assertTrue(("\n" +
                "   and activityid=30015\n" +
                "\n" +
                "NNNNNN\n" +
                "\n" +
                "  select concat(year,\"-\",month,\"-\",day) as ddate,count(1) num\n" +
                "  from hive.woe.l_activity_taskcomplete_log\n" +
                "\t\n" +
                "\t\t2.1\n" +
                "\t\t\n" +
                "\t\t\t2.1.2\n" +
                "\t\t\n" +
                "\t\t2.2\n" +
                "\t\n" +
                "  where concat(year,month,day) between \"20190321\" and \"20191231\"\n" +
                ";").equals(rs));
    }

    @Test
    public void testExample(){
        Map<String, Object> params = new HashMap<>();
        params.put("woe", true);
        String sql =
                "SELECT concat(year,\"-\",month,\"-\",day) as ddate,count(1) num\n" +
                        "FROM #if woe #then hive.woe.l_activity_taskcomplete_log #else hive.boe.l_activity_taskcomplete_log #end\n" +
                        "WHERE concat(year,month,day) between \"20190321\" and \"20191231\"\n" +
                        "#if 1=1 #then \n" +
                        "\t1.0\n" +
                        "\t#if 2>1 #then\n" +
                        "\t\t2.1\n" +
                        "\t\t#if 2<1 #then\n" +
                        "\t\t\t2.1.1\n" +
                        "\t\t#elif 2==2 #then\n" +
                        "\t\t\t2.1.2\n" +
                        "\t\t#else\n" +
                        "\t\t\t2.1.3\n" +
                        "\t\t#end\n" +
                        "\t\t2.2\n" +
                        "\t#else\n" +
                        "\t\t333\n" +
                        "\t#end\n" +
                        "  \t1.1\n" +
                        "#else\n" +
                        "   1.2\n" +
                        "#end;\n" +
                        "\tand activityid=30015 as e1;";
        String rs = exprEvalService.eval(sql, params);
        System.out.println(rs);
    }

}
