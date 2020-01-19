package com.wjs.expr.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * @author wjs
 * @date 2019-12-31 17:44
 **/
@Getter
@Setter
public class IfExpr extends ExprExpr {

    public IfExpr(String text, Line startLine, Integer startCol, Integer exprStartCol) {
        super(text, startLine, startCol, exprStartCol);
    }

}
