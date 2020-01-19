package com.wjs.expr.bean;

import com.wjs.expr.ExprException;
import com.wjs.expr.commons.Tuple2;
import com.wjs.expr.commons.Tuple3;
import lombok.Getter;
import lombok.Setter;

/**
 * for 循环支持
 *  1. $for(i=0; i<xx.size; i++) xxx $endfor
 *  2. $for(xx : yy) $endfor
 *
 * @author wjs
 * @date 2020-01-09 16:51
 **/
@Setter
@Getter
public class ForExpr extends BodyExpr {

    public BinaryExpr parent;

    //----------for表达式内容-----------
    private String forText;

    private ForEnum forEnum;

    private Tuple3<String, Integer, String> ternaryVar;

    private String ternaryPredicate;

    private String ternaryOpt;

    public Tuple2<String, String> inState;

    public ForExpr(String text, Line startLine, Integer startCol, Integer bodyStartCol) {
        super(text, startLine, startCol);
        this.bodyStartCol = bodyStartCol;
        //this.bodyStartCol = passBlank(text, bodyStartCol);
        int left = text.indexOf('(', startCol);
        int right = this.bodyStartCol+1;
        Character c = text.charAt(right);
        while (c != ')' && right > left){
            right--;
            c = text.charAt(right);
        }
        if (left > right || left > bodyStartCol || right > bodyStartCol || c != ')'){
            throw new ExprException("第["+startLine+"]行, 区间["+startCol+" - "+bodyStartCol+"] 的"+_FOR+" 表达式括号闭合错误");
        }

        this.forText = text.substring(left+1, right);
        try {
            this.forEnum = !forText.contains(";") && forText.contains(":") ? ForEnum.IN_MODE : ForEnum.TERNARY_MODE;
            String[] arr = null;
            if (forEnum == ForEnum.IN_MODE){
                arr = this.forText.split(":");
                this.inState = new Tuple2<>(arr[0].trim(), arr[1].trim());
            }else{
                arr = this.forText.split(";");
                this.ternaryPredicate = arr[1].trim();
                this.ternaryOpt = arr[2].trim().replaceAll("\\+\\+", "+1").replaceAll("--", "-1");
                arr = arr[0].split("=");
                try {
                    this.ternaryVar = new Tuple3<>(arr[0].trim(), Integer.parseInt(arr[1].trim()), null);
                }catch (Exception e){
                    this.ternaryVar = new Tuple3<>(arr[0].trim(), null, arr[1].trim());
                }
            }
        }catch (Exception e) {
            throw new ExprException("第["+startLine+"]行, 区间["+startCol+" - "+bodyStartCol+"] 的"+_FOR+" 表达式["+this.forText+"]格式错误", e);
        }
    }

    public ForExpr finish(Line stopLine, Integer stopCol, Integer bodyStopCol) {
        this.setStopLine(stopLine);
        this.setStopCol(stopCol);
        this.setBodyStopCol(bodyStopCol);
        return this;
    }

    public enum ForEnum{
        IN_MODE,  TERNARY_MODE
    }

}
