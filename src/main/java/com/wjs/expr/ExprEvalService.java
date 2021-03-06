package com.wjs.expr;

import com.wjs.expr.bean.ExprExpr;
import com.wjs.expr.bean.FuncExpr;
import com.wjs.expr.bean.SectionExpr;
import com.wjs.expr.eval.ExprEval;
import com.wjs.expr.exprNative.ExprNativeService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static com.wjs.expr.bean.BaseExpr.INNER_SYMBOL;
import static com.wjs.expr.bean.BaseExpr.NOT_NULL;

/**
 * 表达式断言求值执行器
 * @author wjs
 * @date 2020-01-02 10:22
 **/
@Setter
@Slf4j
public class ExprEvalService {

    public ExprNativeService exprNativeService;

    public ExprEval exprEval;

    public boolean eval(ExprExpr exprExpr, Map<String, Object> params){
        Boolean rs = null;
        String exprText = exprExpr.getExprText().trim();
        //可能是内部方法，使用内部方法执行器
        if (exprText.startsWith(INNER_SYMBOL)){
            int start = exprText.indexOf('(');
            int end = exprText.lastIndexOf(')');
            if (start < end && start != -1){
                String funcName = exprText.substring(0, start);
                ExprFunction exprFunction = ExprFunction.get(funcName);
                if (exprFunction != null && exprFunction instanceof ExprInnerFunction){
                    String paramText = exprText.substring(start+1, end).trim();
                    rs = (Boolean) this.evalInnerFunc((ExprInnerFunction)exprFunction, params, paramText, Boolean.class);
                    log.debug("Inner func: {}({}) = {}", funcName, paramText, rs);
                    return rs;
                }
            }
        }
        String expr = exprNativeService.exprNative(exprExpr.getExprText());
        rs = exprEval.eval(expr, params);
        log.debug("Expr {}({}) = {}", expr, params, rs);
        return rs;
    }

    public String eval(SectionExpr sectionExpr, Map<String, Object> params){
        Object rs = null;
        if (sectionExpr instanceof FuncExpr){
            rs = this.evalFunc((FuncExpr)sectionExpr, params);
        }else{
            rs = this.evalSection(sectionExpr, params);
        }

        if (rs == null) {
            return "null";
        }
        if (rs instanceof Number) {
            return String.valueOf(rs);
        }
        if (rs instanceof String) {
            return (String) rs;
        }
        if (rs instanceof Boolean) {
            return ((Boolean) rs).toString();
        }
        return rs.toString();

    }

    private Object evalSection(SectionExpr sectionExpr, Map<String, Object> params){
        String expr = sectionExpr.getSectionText();
        if (expr == null || expr.trim().length() == 0){
            return sectionExpr.defaultValueText();
        }
        try {
            Object rs = exprEval.call(expr, params);
            if (rs == null && sectionExpr.hasDefaultValue()){
                return sectionExpr.defaultValueText();
            }
            return rs;
        }catch (Exception e){
            if (sectionExpr.hasDefaultValue()){
                return sectionExpr.defaultValueText();
            }
            throw e;
        }
    }

    private Object evalFunc(FuncExpr funcExpr, Map<String, Object> params){
        ExprFunction exprFunction = ExprFunction.get(funcExpr.getFuncName());
        //表达式解析方法
        if (exprFunction == null || !(exprFunction instanceof ExprInnerFunction)){
            return exprEval.call(funcExpr.getSectionText(), params);
        //内置快速方法
        }else {
            return this.evalInnerFunc((ExprInnerFunction)exprFunction, params, funcExpr.getFuncParamStr(), Object.class);
        }
    }

    private Object evalInnerFunc(ExprInnerFunction exprNativeFunc, Map<String, Object> params, String paramStr, Class clazz){
        Character c = null;
        for (int i=0; i<paramStr.length()-1; i++){
            c = paramStr.charAt(i);
            if (c == NOT_NULL && paramStr.charAt(i+1) == NOT_NULL){
                try {
                    Object rs = exprNativeFunc.invoke(params, paramStr.substring(0, i));
                    if (rs == null){
                        return castValue(paramStr.substring(i+2), clazz);
                    }
                    return rs;
                }catch (Exception e){
                    return castValue(paramStr.substring(i+2), clazz);
                }
            }
        }
        return exprNativeFunc.invoke(params, paramStr);
    }

    public Object castValue(String rs, Class clazz){
        if (clazz == Boolean.class){
            return "true".equalsIgnoreCase(rs) || "1".equals(rs);
        }
        return rs;
    }
}
