package com.wjs.expr.bean;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 表达式树
 * @author wjs
 * @date 2020-01-09 09:43
 **/
@Getter
@Setter
@NoArgsConstructor
public class ExprTree {

    public List<BinaryExpr> binaryExprList;

    public List<FuncExpr> funcExprList;

    public List<SectionExpr> sectionExprList;

    public List<ForExpr> forExprList;

    //----------------父树节点------------------
    public Integer startCol;

    public Integer stopCol;

    public BaseExpr parent;

    //----------------树节点循环信息-----------------
    private List<BaseExpr> topExprList;

    private int walkIndex = 0;

    public Map<String, Object> params;



    public ExprTree(List<BinaryExpr> binaryExprList, List<FuncExpr> funcExprList, List<SectionExpr> sectionExprList, List<ForExpr> forExprList, Integer startCol, Integer stopCol, BaseExpr parent) {
        this.binaryExprList = binaryExprList;
        this.funcExprList = funcExprList;
        this.sectionExprList = sectionExprList;
        this.forExprList = forExprList;
        this.startCol = startCol;
        this.stopCol = stopCol;
        this.parent = parent;
    }

    /**
     * 获取次级树节点
     * @param baseExpr
     * @param startCol
     * @param stopCol
     * @return
     */
    public ExprTree getSubExprTree(BaseExpr baseExpr, int startCol, int stopCol){
        ExprTree subExprTree = new ExprTree();
        subExprTree.setBinaryExprList(this.binaryExprList.stream().filter(x -> x.in(startCol, stopCol)).collect(Collectors.toList()));
        subExprTree.setFuncExprList(this.funcExprList.stream().filter(x -> x.in(startCol, stopCol)).collect(Collectors.toList()));
        subExprTree.setSectionExprList(this.sectionExprList.stream().filter(x -> x.in(startCol, stopCol)).collect(Collectors.toList()));
        subExprTree.setForExprList(this.forExprList.stream().filter(x -> x.in(startCol, stopCol)).collect(Collectors.toList()));
        subExprTree.setStartCol(startCol);
        subExprTree.setStopCol(stopCol);
        subExprTree.setParent(baseExpr);
        subExprTree.setParams(this.params);
        return subExprTree;
    }

    /**
     * 获取树 Top第一层级子节点
     * @return
     */
    public List<BaseExpr> getTopExprList(){
        if (topExprList == null){
            topExprList = new ArrayList<>();
            List<BaseExpr> list = new ArrayList<>();
            list.addAll(binaryExprList);
            list.addAll(forExprList);

            for (BaseExpr baseExpr : list){
                if (list.stream().noneMatch(x -> x.contain(baseExpr) && x != baseExpr)){
                    topExprList.add(baseExpr);
                }
            }
            Collections.sort(topExprList);
        }
        return topExprList;
    }

    /**
     * 准备开始遍历 top 层子节点
     */
    public void start(){
        walkIndex = 0;
    }

    /**
     * 遍历 top 层子节点
     * @return
     */
    public BaseExpr walk(){
        //遍历到尾部
        if (walkIndex == getTopExprList().size()){
            return null;
        }
        BaseExpr baseExpr = getTopExprList().get(walkIndex);
        walkIndex++;
        return baseExpr;
    }

    /**
     * 绑定执行条件参数
     */
    public void attachParams(Map<String, Object> params){
        this.params = params;
    }

    /**
     * 是否没有 Aviator 标识内容
     * @return
     */
    public boolean isEmpty(){
        return (binaryExprList == null || binaryExprList.isEmpty())
                && (funcExprList == null || funcExprList.isEmpty())
                && (sectionExprList == null || sectionExprList.isEmpty())
                && (forExprList == null || forExprList.isEmpty());
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

    /**
     * 重置缓存对象
     */
    public void reset(){
        topExprList = null;
        walkIndex = 0;
        params = null;
    }
}
