package com.wjs.expr.bean;

import com.wjs.expr.commons.Tuple3;
import lombok.Getter;
import lombok.Setter;

/**
 * @author wjs
 * @date 2019-12-31 17:45
 **/
@Getter
@Setter
public class ElseExpr extends BodyExpr{

    public ElseExpr(String text, Tuple3<Integer, Integer, Integer> startLine, Integer startCol) {
        super(text, startLine, startCol);
        this.setBodyStartCol(startCol+ELSE.length()+1);
    }

    @Override
    public String toString() {
        return "ElseExpr{body: ["+getText().substring(getBodyStartCol(), getBodyStopCol())+"]}";
    }
}
