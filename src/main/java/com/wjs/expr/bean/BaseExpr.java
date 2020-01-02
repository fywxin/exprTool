package com.wjs.expr.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 表达式模型基类
 * @author wjs
 * @date 2019-12-31 17:45
 **/
@Getter
@Setter
public class BaseExpr {

    //原始表达式语句
    public String text;

    public Integer startLine;

    public Integer stopLine;

    public Integer startCol;

    public Integer stopCol;

    //嵌套子表达式
    private List<Expr> childExprList = new ArrayList<>();

    public BaseExpr(String text) {
        this.text = text;
    }

    public boolean done(){
        return stopLine != null;
    }

    /**
     * 本对象是否为 other 的父类
     * @param other
     * @return
     */
    public boolean contain(BaseExpr other){
        return this.startCol < other.startCol && this.stopCol > other.stopCol;
    }

    @Override
    public String toString() {
        return text.substring(startCol, stopCol);
    }
}
