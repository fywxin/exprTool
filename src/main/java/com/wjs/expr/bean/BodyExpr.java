package com.wjs.expr.bean;

import com.wjs.expr.commons.Tuple3;
import lombok.Getter;
import lombok.Setter;

/**
 * 表达式内容体
 * @author wjs
 * @date 2019-12-31 18:22
 **/
@Setter
@Getter
public class BodyExpr extends BaseExpr {

    public Integer bodyStartCol;

    public Integer bodyStopCol;

    public BodyExpr(String text, Tuple3<Integer, Integer, Integer> startLine, Integer startCol) {
        super(text);
        this.setStartLine(startLine);
        this.setStartCol(startCol);
    }
}
