package com.wjs.expr.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * for 循环支持  $for(i=0; i<xx.size; i++) xxx $endfor
 *
 * @author wjs
 * @date 2020-01-09 16:51
 **/
@Setter
@Getter
public class ForExpr extends BodyExpr {

    public ForExpr(String text, Integer startLine, Integer startCol, Integer bodyStopCol) {
        super(text, startLine, startCol);
        this.bodyStartCol = bodyStopCol;
    }

    public ForExpr finish(Integer stopLine, Integer stopCol) {
        this.setStopLine(stopLine);
        this.setStopCol(stopCol);
        return this;
    }

}
