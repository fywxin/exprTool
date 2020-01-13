package com.wjs.expr.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * 表达式模型基类
 * @author wjs
 * @date 2019-12-31 17:45
 **/
@Getter
@Setter
public class BaseExpr implements Comparable<BaseExpr> {
    public static final Character GRAMMAR = '$';

    public static final String IF = "if";
    public static final String THEN = "then";
    public static final String ELSE = "else";
    public static final String ELIF = "elif";
    public static final String ENDIF = "end";

    public static final String FOR = "for";
    public static final String ENDFOR = "endfor";

    public static final String _IF = GRAMMAR + IF;
    public static final String _THEN = GRAMMAR + THEN;
    public static final String _ELSE = GRAMMAR + ELSE;
    public static final String _ELIF = GRAMMAR + ELIF;
    public static final String _ENDIF = GRAMMAR + ENDIF;

    public static final String _FOR = GRAMMAR + FOR;
    public static final String _ENDFOR  = GRAMMAR + ENDFOR;

    //原始表达式语句
    public String text;

    public Integer startLine;

    public Integer stopLine;

    public Integer startCol;

    public Integer stopCol;

    //是否自动适配补充完成的
    public boolean autoComplete = false;

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
        return this.startCol <= other.startCol && this.stopCol >= other.stopCol;
    }

    public boolean in(int startCol, int stopCol){
        return this.startCol >= startCol && this.stopCol <= stopCol;
    }

    public boolean in(BaseExpr other){
        return this.startCol >= other.startCol && this.stopCol <= other.stopCol;
    }

    @Override
    public String toString() {
        return text.substring(startCol, stopCol);
    }

    @Override
    public int compareTo(BaseExpr other) {
        return this.startCol - other.startCol;
    }
}
