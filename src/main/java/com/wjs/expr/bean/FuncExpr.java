package com.wjs.expr.bean;

import com.googlecode.aviator.runtime.function.AbstractFunction;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 用户自定义方法混入
 * @author wjs
 * @date 2020-01-06 19:10
 **/
@Getter
@Setter
public class FuncExpr extends BaseExpr {

    public static final Set<String> funcSet = new HashSet<>();

    private Map<String, Object> params;

    //是否稳幂等, 不随执行次数与上线文而改变
    public boolean idempotent = false;

    public FuncExpr(String text) {
        super(text);
    }

    public FuncExpr(String text, int startLine, int stopLine, int startCol, int stopCol) {
        super(text);
        this.startLine = startLine;
        this.stopLine = stopLine;
        this.startCol = startCol;
        this.stopCol = stopCol;
    }

    public String getFuncText(){
        return text.substring(startCol, stopCol);
    }

    public static boolean support(String funcName) {
        return funcSet.contains(funcName);
    }

    public static void add(AbstractFunction func) {
        funcSet.add(func.getName());
    }
}
