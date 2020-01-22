package com.wjs.expr.test;

import com.wjs.expr.bean.ExprTree;
import org.apache.commons.collections.map.HashedMap;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author wjs
 * @date 2020-01-22 15:33
 **/
public class SubExprTest extends BaseTest {
    String sql = "var autoFill=true;\n" +
            "\n" +
            "SELECT IFNULL(uid,'') AS uid, IFNULL(item_id,'') AS item_id FROM hive.sultans.j_add_item_log  WHERE 1=1  AND item_id  in ('421901')  AND  concat(year,'-', month, '-', day) BETWEEN '2020-01-20' AND '2020-01-23'  AS e1;\n" +
            "\n" +
            "$if 100 < 500 $then\n" +
            "\tSELECT IFNULL(uid,'') AS uid, IFNULL(type,'') AS type \n" +
            "\tFROM hive.sultans.j_add_item_log \n" +
            "\tWHERE 1=1  AND  concat(year,'-', month, '-', day) BETWEEN '2020-01-20' AND '2020-01-23' \n" +
            "\tand uid in(<!e1.uid!>)  \n" +
            "\tAS e2;\n" +
            "$else\n" +
            "\tSELECT IFNULL(e2.uid,'') AS uid, IFNULL(e2.type,'') AS type \n" +
            "\tFROM e1 AS e1 inner join hive.sultans.j_add_item_log e2 on e1.uid = e2.uid \n" +
            "\tWHERE 1=1  AND  concat(year,'-', month, '-', day) BETWEEN '2020-01-20' AND '2020-01-23'  \n" +
            "\tAS e2;\n" +
            "$endif\n" +
            "SELECT DISTINCT(typeid) as value, CONCAT(name) as label from mysql.inf.if_conf_type where   (   `table` = 'l_add_item_log'  ) and (   `column` = 'type'  )  as c_4246624664;";

    String sql2 = "var autoFill=true;\n" +
            "\n" +
            "SELECT IFNULL(uid,'') AS uid, IFNULL(item_id,'') AS item_id FROM hive.sultans.j_add_item_log  WHERE 1=1  AND item_id  in ('421901')  AND  concat(year,'-', month, '-', day) BETWEEN '2020-01-20' AND '2020-01-23'  AS e1;\n" +
            "\n" +
            "$if 100 < 500 $then\n" +
            "\tSELECT IFNULL(uid,'') AS uid, IFNULL(type,'') AS type \n" +
            "\tFROM hive.sultans.j_add_item_log \n" +
            "\tWHERE 1=1  AND  concat(year,'-', month, '-', day) BETWEEN '2020-01-20' AND '2020-01-23' \n" +
            "\tand uid in(<!e1.uid!>)  \n" +
            "\tAS e2;\n" +
            "$else\n" +
            "\tSELECT IFNULL(e2.uid,'') AS uid, IFNULL(e2.type,'') AS type \n" +
            "\tFROM e1 AS e1 inner join hive.sultans.j_add_item_log e2 on e1.uid = e2.uid \n" +
            "\tWHERE 1=1  AND  concat(year,'-', month, '-', day) BETWEEN '2020-01-20' AND '2020-01-23'  \n" +
            "\tAS e2;\n";

    @Test
    public void test1(){
        String subSql = "\tSELECT IFNULL(uid,'') AS uid, IFNULL(type,'') AS type \n" +
                "\tFROM hive.sultans.j_add_item_log \n" +
                "\tWHERE 1=1  AND  concat(year,'-', month, '-', day) BETWEEN '2020-01-20' AND '2020-01-23' \n" +
                "\tand uid in(<!e1.uid!>)  \n" +
                "\tAS e2;";

        ExprTree exprTree = this.exprService.parse(sql);

        int start = sql.indexOf(subSql);
        int stop = start+subSql.length();

        ExprTree subExprTree = exprTree.getSubExprTree(null, start, stop);
        System.out.println(subExprTree);
    }

    @Test
    public void test2(){
        String subSql = "$if 100 < 500 $then\n" +
                "\tSELECT IFNULL(uid,'') AS uid, IFNULL(type,'') AS type \n" +
                "\tFROM hive.sultans.j_add_item_log \n" +
                "\tWHERE 1=1  AND  concat(year,'-', month, '-', day) BETWEEN '2020-01-20' AND '2020-01-23' \n" +
                "\tand uid in(<!e1.uid!>)  \n" +
                "\tAS e2;\n" +
                "$else\n" +
                "\tSELECT IFNULL(e2.uid,'') AS uid, IFNULL(e2.type,'') AS type \n" +
                "\tFROM e1 AS e1 inner join hive.sultans.j_add_item_log e2 on e1.uid = e2.uid \n" +
                "\tWHERE 1=1  AND  concat(year,'-', month, '-', day) BETWEEN '2020-01-20' AND '2020-01-23'  \n" +
                "\tAS e2;\n" +
                "$endif\n" ;

        ExprTree exprTree = this.exprService.parse(sql);

        int start = sql.indexOf(subSql);
        int stop = start+subSql.length();

        ExprTree subExprTree = exprTree.getSubExprTree(null, start, stop);
        System.out.println(subExprTree);
    }

    @Test
    public void test3(){
        String subSql = "$if 100 < 500 $then\n" +
                "\tSELECT IFNULL(uid,'') AS uid, IFNULL(type,'') AS type \n" +
                "\tFROM hive.sultans.j_add_item_log \n" +
                "\tWHERE 1=1  AND  concat(year,'-', month, '-', day) BETWEEN '2020-01-20' AND '2020-01-23' \n" +
                "\tand uid in(<!e1.uid!>)  \n" +
                "\tAS e2;\n" +
                "$else\n" +
                "\tSELECT IFNULL(e2.uid,'') AS uid, IFNULL(e2.type,'') AS type \n" +
                "\tFROM e1 AS e1 inner join hive.sultans.j_add_item_log e2 on e1.uid = e2.uid \n" +
                "\tWHERE 1=1  AND  concat(year,'-', month, '-', day) BETWEEN '2020-01-20' AND '2020-01-23'  \n" +
                "\tAS e2;\n";

        ExprTree exprTree = this.exprService.parse(sql);

        int start = sql2.indexOf(subSql);
        int stop = start+subSql.length();

        ExprTree subExprTree = exprTree.getSubExprTree(null, start, stop);
        System.out.println(subExprTree);
    }


    @Test
    public void test4(){
        String subSql = "$if 100 < 500 $then\n" +
                "\tSELECT IFNULL(uid,'') AS uid, IFNULL(type,'') AS type \n" +
                "\tFROM hive.sultans.j_add_item_log \n" +
                "\tWHERE 1=1  AND  concat(year,'-', month, '-', day) BETWEEN '2020-01-20' AND '2020-01-23' \n" +
                "\tand uid in(<!e1.uid!>)  \n" +
                "\tAS e2;\n" +
                "$else\n" +
                "\tSELECT IFNULL(e2.uid,'') AS uid, IFNULL(e2.type,'') AS type \n" +
                "\tFROM e1 AS e1 inner join hive.sultans.j_add_item_log e2 on e1.uid = e2.uid \n" +
                "\tWHERE 1=1  AND  concat(year,'-', month, '-', day) BETWEEN '2020-01-20' AND '2020-01-23'  \n" +
                "\tAS e2;\n";

        ExprTree exprTree = this.exprService.parse(sql2);

        int start = sql2.indexOf(subSql);
        int stop = start+subSql.length();

        ExprTree subExprTree = exprTree.getSubExprTree(null, start, stop);
        String rs = this.exprService.eval(subExprTree, new HashedMap());
        System.out.println(rs);
        Assert.assertTrue(("\tSELECT IFNULL(uid,'') AS uid, IFNULL(type,'') AS type \n" +
                "\tFROM hive.sultans.j_add_item_log \n" +
                "\tWHERE 1=1  AND  concat(year,'-', month, '-', day) BETWEEN '2020-01-20' AND '2020-01-23' \n" +
                "\tand uid in(<!e1.uid!>)  \n" +
                "\tAS e2;").equals(rs));
    }

}
