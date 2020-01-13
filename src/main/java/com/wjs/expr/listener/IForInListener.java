package com.wjs.expr.listener;

import com.wjs.expr.bean.ExprTree;
import com.wjs.expr.bean.ForExpr;

import java.util.List;

/**
 * @author wjs
 * @date 2020-01-13 16:31
 **/
public interface IForInListener {

    void before(ExprTree exprTree, ForExpr forExpr);

    void in(ExprTree exprTree, ForExpr forExpr, String key, Object val, int index);

    void after(ExprTree exprTree, ForExpr forExpr);

    static void callBefore(List<IForInListener> forInListenerList, ExprTree exprTree, ForExpr forExpr){
        for (IForInListener listener : forInListenerList){
            listener.before(exprTree, forExpr);
        }
    }

    static void callIn(List<IForInListener> forInListenerList, ExprTree exprTree, ForExpr forExpr, String key, Object val, int index){
        for (IForInListener listener : forInListenerList){
            listener.in(exprTree, forExpr, key, val, index);
        }
    }

    static void callAfter(List<IForInListener> forInListenerList, ExprTree exprTree, ForExpr forExpr){
        for (IForInListener listener : forInListenerList){
            listener.after(exprTree, forExpr);
        }
    }
}
