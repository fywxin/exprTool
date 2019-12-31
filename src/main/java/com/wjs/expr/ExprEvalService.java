package com.wjs.expr;

import com.googlecode.aviator.AviatorEvaluator;
import com.wjs.expr.bean.*;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wjs
 * @date 2019-12-31 20:05
 **/
@Slf4j
public class ExprEvalService {

    public ExprService exprService;

    public String eval(String text){
        List<Expr> exprList = this.exprService.parse(text);
        StringBuilder sb = new StringBuilder(text.length());
        this.eval(text, this.exprService.root(exprList), sb, 0, text.length());
        return sb.toString();
    }

    private void eval(String text, List<Expr> flatExprList, StringBuilder sb, int start, int stop){
        if (flatExprList.isEmpty()){
            sb.append(text);
            return ;
        }
        for (Expr expr : flatExprList){
            sb.append(text, start, expr.startCol);
            IfExpr ifExpr = expr.ifExpr;
            if (eval(ifExpr)){
                this.out(text, ifExpr, sb);
            }else if(!expr.elifExprList.isEmpty()){
                for (ElifExpr elifExpr : expr.elifExprList){
                    if (eval(elifExpr)){
                        this.out(text, elifExpr, sb);
                        break;
                    }
                }
            }else if (expr.elseExpr.isPresent()){
                this.out(text, expr.elseExpr.get(), sb);
            }
            start = expr.stopCol;
        }
        if (start < stop){
            sb.append(text, start, stop);
        }
    }

    private void out(String text, BodyExpr bodyExpr, StringBuilder sb){
        List<Expr> child = bodyExpr.getChildExprList();
        if (child.isEmpty()){
            sb.append(text, bodyExpr.bodyStartCol, bodyExpr.bodyStopCol);
        }else{
            eval(text, child, sb, bodyExpr.bodyStartCol, bodyExpr.bodyStopCol);
        }
    }

    /**
     * @param exprExpr
     * @return
     */
    private boolean eval(ExprExpr exprExpr){
        Map<String, Object> env = new HashMap<String, Object>();

        Boolean rs = (Boolean) AviatorEvaluator.execute(exprExpr.getExprText(), env);
        log.info(exprExpr.getExprText()+" = "+rs);
        return rs;
    }

}
