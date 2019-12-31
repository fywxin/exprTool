package com.wjs.expr.bean;

import com.wjs.expr.exprNative.ExprNativeService;
import lombok.Getter;
import lombok.Setter;

/**
 * @author wjs
 * @date 2019-12-31 20:23
 **/
@Getter
@Setter
public class ExprExpr extends BodyExpr {

    public Integer exprStartCol;

    public Integer exprStopCol;

    public ExprNativeService exprNativeService;

    public ExprExpr(String text, Integer startLine, Integer startCol, Integer exprStartCol) {
        super(text, startLine, startCol);
        this.exprStartCol = exprStartCol;
    }

    @Override
    public String toString() {
        return getClass().getName()+"{" +
                "\nelif: [" + getText().substring(exprStartCol, exprStopCol) +
                "]\nbody: [" + getText().substring(getBodyStartCol(), getBodyStopCol()) +
                "]\n}";
    }

    /**
     * TODO 修复 = 与 and or
     * @return
     */
    public String getExprText(){
        return exprNativeService.exprNative(text.substring(exprStartCol, exprStopCol));
    }
}
