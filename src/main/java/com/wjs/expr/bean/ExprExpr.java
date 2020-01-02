package com.wjs.expr.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 包含断言条件的表达式
 * if - elif
 *
 * @author wjs
 * @date 2019-12-31 20:23
 **/
@Getter
@Setter
public class ExprExpr extends BodyExpr {

    public Integer exprStartCol;

    public Integer exprStopCol;

    private Map<String, Object> params;

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
     * @return
     */
    public String getExprText(){
        return text.substring(exprStartCol, exprStopCol);
    }
}
