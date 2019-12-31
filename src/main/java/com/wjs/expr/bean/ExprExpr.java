package com.wjs.expr.bean;

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
        StringBuilder sb = new StringBuilder();
        String tmp = text.substring(exprStartCol, exprStopCol);
        Character c = null;
        for (int i=0; i<tmp.length()-1; i++){
            c = tmp.charAt(i);
            if (c == '\n'){
                sb.append(' ');
                continue;
            }
            if (c == '='){
                sb.append(c);
                sb.append("=");
                if (tmp.charAt(i+1) == '='){
                    i++;
                }
                continue;
            }
            sb.append(c);
        }
        c = tmp.charAt(tmp.length()-1);
        if (c != '\n'){
            sb.append(c);
        }
        System.out.println(">>> "+sb.toString());
        return sb.toString();
    }
}
