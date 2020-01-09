package com.wjs.expr.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wjs
 * @date 2020-01-09 09:43
 **/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Exprs {

    public List<Expr> exprList;

    public List<FuncExpr> funcExprList;

    public List<SectionExpr> sectionExprList;


    /**
     * 顶层根表达式列表
     * @return
     */
    public List<Expr> rootExprList(){
        return exprList.stream().filter(x -> x.parent == null).collect(Collectors.toList());
    }

    /**
     * 绑定执行条件参数
     */
    public void attachExprParams(Map<String, Object> params){
        if (isEmpty()){
            return;
        }
        exprList.forEach(x -> {
            x.ifExpr.setParams(params);
            x.elifExprList.forEach(y -> y.setParams(params));
        });
        funcExprList.forEach(x -> x.setParams(params));
        sectionExprList.forEach(x -> x.setParams(params));
    }

    /**
     * 是否没有 Aviator 标识内容
     * @return
     */
    public boolean isEmpty(){
        return (exprList == null || exprList.isEmpty())
                && (funcExprList == null || funcExprList.isEmpty())
                && (sectionExprList == null || sectionExprList.isEmpty());
    }

    /**
     * 获取范围内的包含的 自定义函数与段落
     * @param startCol
     * @param stopCol
     * @return
     */
    public List<SectionExpr> innerSectionAndFunc(int startCol, int stopCol) {
        List<SectionExpr> innerFuncExprList = this.innerSection(funcExprList, startCol, stopCol);
        List<SectionExpr> innerSectionExprList = this.innerSection(sectionExprList, startCol, stopCol);
        innerSectionExprList.addAll(innerFuncExprList);
        Collections.sort(innerSectionExprList);
        return innerSectionExprList;
    }

    private List<SectionExpr> innerSection(List<? extends SectionExpr> list, int startCol, int stopCol){
        return list.stream().filter(x -> x.startCol >= startCol && x.stopCol <= stopCol).collect(Collectors.toList());
    }
}
