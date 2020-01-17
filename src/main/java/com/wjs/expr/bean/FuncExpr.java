package com.wjs.expr.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * 用户自定义方法混入
 * @author wjs
 * @date 2020-01-06 19:10
 **/
@Getter
@Setter
public class FuncExpr extends SectionExpr {

    //是否稳幂等, 不随执行次数与上线文而改变
    public boolean idempotent = false;

    public FuncExpr(String text) {
        super(text);
    }

    public FuncExpr(String text, int startLine, int stopLine, int startCol, int stopCol) {
        super(text, startLine, stopLine, startCol, stopCol);
    }

    @Override
    public String getSectionText(){
        return text.substring(startCol, stopCol);
    }

    /**
     * 返回方法名称
     * @return
     */
    public String getFuncName(){
        return text.substring(startCol, text.indexOf('(', startCol)).trim();
    }

    /**
     * 返回方法参数内容
     * @return
     */
    public String getFuncParamStr(){
        return text.substring(text.indexOf('(', startCol)+1, stopCol-1).trim();
    }
}
