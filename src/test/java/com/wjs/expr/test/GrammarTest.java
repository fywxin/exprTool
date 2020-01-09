package com.wjs.expr.test;

import com.wjs.expr.ExprException;
import com.wjs.expr.bean.Exprs;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author wjs
 * @date 2020-01-03 09:23
 **/
public class GrammarTest extends BaseTest {

    @Test
    public void testExpr(){
        String text = "select concat(year,\"-\",month,\"-\",day) as ddate,count(1) num\n" +
                "from hive.woe.l_activity_taskcomplete_log\n" +
                "where concat(year,month,day) between \"20190321\" and \"20191231\"\n" +
                "and activityid=30015\n" +
                "group by ddate\n" +
                "as e1;\n" +
                "$if ads=sdf $then\n" +
                "   select concat(year,\"-\",month,\"-\",day) as ddate,count(1) num  $if 1=1 $then dsdds $else sayhello $end\n" +
                "$elif ds=dffff adn $then;\n" +
                "   from hive.woe.l_activity_taskcomplete_log\n" +
                "$else\n" +
                "   where concat(year,month,day) between \"20190321\" and \"20191231\"\n" +
                "   and activityid=30015\n" +
                "$end\n" +
                "group by ddate\n" +
                "as e2;";

        Exprs exprs = exprGrammarService.parse(text, false);
        Assert.assertTrue(exprs.exprList.size() == 2);
    }

    @Test
    public void testExprAutoComplete(){
        String text = "select concat(year,\"-\",month,\"-\",day) as ddate,count(1) num\n" +
                "from hive.woe.l_activity_taskcomplete_log\n" +
                "where concat(year,month,day) between \"20190321\" and \"20191231\"\n" +
                "and activityid=30015\n" +
                "group by ddate\n" +
                "as e1;\n" +
                "$if ads=sdf $then\n" +
                "   select concat(year,\"-\",month,\"-\",day) as ddate,count(1) num  $if 1=1 $then dsdds $else sayhello $end\n";

        Exprs exprs = exprGrammarService.parse(text, true);
        Assert.assertTrue(exprs.exprList.size() == 2);
    }

    @Test
    public void testExprNotIf(){
        String text = "select concat(year,\"-\",month,\"-\",day) as ddate,count(1) num\n" +
                "from hive.woe.l_activity_taskcomplete_log\n" +
                "where concat(year,month,day) between \"20190321\" and \"20191231\"\n" +
                "and activityid=30015\n" +
                "group by ddate\n" +
                "as e1;\n" +
                "$if ads=sdf $then\n" +
                "   select concat(year,\"-\",month,\"-\",day) as ddate,count(1) num  1=1 $then dsdds $else sayhello $end\n";

        Assert.assertThrows(ExprException.class, () -> exprGrammarService.parse(text, true));
    }
}
