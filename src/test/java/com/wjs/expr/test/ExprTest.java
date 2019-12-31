package com.wjs.expr.test;

import com.wjs.expr.ExprEvalService;
import com.wjs.expr.ExprService;
import com.wjs.expr.bean.Expr;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * @author wjs
 * @date 2019-12-31 22:27
 **/
public class ExprTest {

    ExprService exprService = new ExprService();
    ExprEvalService exprEvalService = new ExprEvalService();

    @Before
    public void init(){
        exprEvalService.exprService = exprService;
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


        List<Expr> list = exprService.parse(text);
        System.out.println(list);
    }

    @Test
    public void test1(){
        String sql = "#if 1=1\n" +
                "\t&& 2<1\n" +
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
                "#end;\n" +
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


        System.out.println(exprEvalService.eval(sql));
    }
}
