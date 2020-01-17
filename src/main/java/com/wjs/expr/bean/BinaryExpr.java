package com.wjs.expr.bean;

import com.wjs.expr.commons.Tuple3;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * expr: IF condition=str THEN body (ELIF condition=str THEN body)* (ELSE body)? END;
 *
 * @author wjs
 * @date 2019-12-31 17:47
 **/
@Getter
@Setter
public class BinaryExpr extends BaseExpr {

    public IfExpr ifExpr;

    public List<ElifExpr> elifExprList = new ArrayList<>();

    public Optional<ElseExpr> elseExpr = Optional.empty();

    public BinaryExpr parentBinary;

    public ForExpr parentFor;

    public boolean ok = false;



    public BinaryExpr(IfExpr ifExpr) {
        super(ifExpr.getText());
        this.ifExpr = ifExpr;
        this.setStartLine(ifExpr.startLine);
        this.setStartCol(ifExpr.startCol);
    }

    public BinaryExpr finish(Tuple3<Integer, Integer, Integer> stopLine, Integer stopCol) {
        this.setStopLine(stopLine);
        this.setStopCol(stopCol);
        this.ok = true;
        return this;
    }

    public ElifExpr lastElifExpr(){
        return elifExprList.get(elifExprList.size()-1);
    }

    public void addElifExpr(ElifExpr elifExpr) {
        elifExprList.add(elifExpr);
    }

    @Override
    public boolean done(){
        if (!ok) {
            return false;
        }
        if (!ifExpr.done()){
            return false;
        }
        if (elifExprList.stream().anyMatch(x -> !x.done())){
            return false;
        }
        if (elseExpr.isPresent() && !elseExpr.get().done()){
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Expr{" +
                "ifExpr=" + ifExpr +
                ", elifExprList=" + elifExprList +
                ", elseExpr=" + elseExpr +
                ", parent=" + parentBinary +
                ", ok=" + ok +
                '}';
    }
}
