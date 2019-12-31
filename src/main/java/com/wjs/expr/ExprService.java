package com.wjs.expr;



import com.wjs.expr.bean.ElifExpr;
import com.wjs.expr.bean.ElseExpr;
import com.wjs.expr.bean.Expr;
import com.wjs.expr.bean.IfExpr;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author wjs
 * @date 2019-12-31 17:51
 **/
public class ExprService {

    public List<Expr> root(List<Expr> list){
        return list.stream().filter(x -> x.parent == null).collect(Collectors.toList());
    }

    //TODO 性能优化，减去无必要的循环
    private void tree(List<Expr> list){
        for (Expr expr : list){
            List<Expr> child = list.stream().filter(x -> x.parent == expr).collect(Collectors.toList());
            if (!child.isEmpty()){
                expr.ifExpr.setChildExprList(child.stream().filter(x -> expr.ifExpr.contain(x)).collect(Collectors.toList()));
                for (ElifExpr elifExpr : expr.elifExprList) {
                    elifExpr.setChildExprList(child.stream().filter(x -> child.contains(x)).collect(Collectors.toList()));
                }
                if (expr.elseExpr.isPresent()){
                    expr.elseExpr.get().setChildExprList(child.stream().filter(x -> expr.elseExpr.get().contain(x)).collect(Collectors.toList()));
                }
            }
        }
    }

    public List<Expr> parse(String text) {
        Character c = null;
        String cmd = null;
        int line = 0;

        List<Expr> list= new ArrayList<>();

        List<Expr> stack= new ArrayList<>();
        Expr expr = null;

        for (int i=0; i<text.length(); i++) {
            c = text.charAt(i);
            if (c == '\n'){
                line++;
                continue;
            }
            if (c == '#'){
                i++;
                cmd = getWord(text, i).toLowerCase();
                switch (cmd){
                    case "if":
                        expr = new Expr(new IfExpr(text, line, i - 1, i + 2));
                        for (int j=stack.size()-1; j>=0; j--){
                            if (!stack.get(j).isOk()){
                                expr.parent = stack.get(j);
                                break;
                            }
                        }
                        stack.add(expr);
                        break;
                    case "then":
                        expr = stack.get(stack.size()-1);
                        //if
                        if (expr.elifExprList.isEmpty()){
                            IfExpr ifExpr = expr.ifExpr;
                            ifExpr.setExprStopCol(i-1);
                            ifExpr.setBodyStartCol(i+4);
                        //elif
                        } else {
                            ElifExpr elifExpr = expr.lastElifExpr();
                            elifExpr.setExprStopCol(i-1);
                            elifExpr.setBodyStartCol(i+4);
                        }
                        break;
                    case "elif":
                        expr = stack.get(stack.size()-1);
                        if (expr.elifExprList.isEmpty()){
                            endIfExpr(expr, line, i);
                        } else {
                            endElIfExpr(expr, line, i);
                        }
                        expr.addElifExpr(new ElifExpr(text, line, i-1, i+4));
                        break;
                    case "else":
                        expr = stack.get(stack.size()-1);
                        if (expr.elifExprList.isEmpty()){
                            endIfExpr(expr, line, i);
                        } else {
                            endElIfExpr(expr, line, i);
                        }

                        expr.setElseExpr(Optional.of(new ElseExpr(text, line, i-1)));
                        break;
                    case "end":
                        expr = stack.remove(stack.size()-1).finish(line, i+3);
                        if(expr.elseExpr.isPresent()){
                            endElse(expr, line, i);
                        }else if (expr.elifExprList.isEmpty()){
                            endIfExpr(expr, line, i);
                        } else{
                            endElIfExpr(expr, line, i);
                        }
                        list.add(expr);
                        break;
                    default:
                        break;
                }
            }
        }

        if (!stack.isEmpty()) {
            throw new ExprException("缺失第["+(stack.get(0).getStartLine()+1)+"]行 #if 对应的 #end, 未闭合错误");
        }
        this.tree(list);
        return list;
    }

    private void endIfExpr(Expr expr, Integer line, int i) {
        IfExpr ifExpr = expr.ifExpr;
        if (ifExpr.getBodyStartCol() == null){
            throw new ExprException("第["+(ifExpr.getStartLine()+1)+"]行 #if 缺少 #then 关键字");
        }
        ifExpr.setStopLine(line);
        ifExpr.setBodyStopCol(i-1);
        ifExpr.setStopCol(i-1);
    }

    private void endElIfExpr(Expr expr, Integer line, int i){
        ElifExpr elifExpr = expr.lastElifExpr();
        if (elifExpr.getBodyStartCol() == null){
            throw new ExprException("第["+(elifExpr.getStartLine()+1)+"]行 #elif 缺少 #then 关键字");
        }
        elifExpr.setStopLine(line);
        elifExpr.setBodyStopCol(i-1);
        elifExpr.setStopCol(i-1);
    }

    private void endElse(Expr expr, Integer line, int i) {
        ElseExpr elseExpr = expr.elseExpr.get();
        elseExpr.setStopLine(line);
        elseExpr.setBodyStopCol(i-1);
        elseExpr.setStopCol(i+3);
    }

    /**
     * @param text
     * @param start
     * @return
     */
    public static String getWord(String text, int start){
        int end = getWordEnd(text, start);
        if (start == end){
            return String.valueOf(text.charAt(start));
        }
        return text.substring(start, end);
    }

    private static Integer getWordEnd(String text, int start){
        int index = start;
        char c = text.charAt(index);
        if (isSplitChar(c)){
            return index;
        }
        while (!isSplitChar(c)){
            index++;
            if (index > text.length()-1){
                return index;
            }
            c = text.charAt(index);
        }
        return index;
    }

    /**
     * TODO 不同场景，分词字符不一样，需要优化
     * @param c
     * @return
     */
    public static boolean isSplitChar(Character c){
        return c == ' ' || c == '\n' || c == '\t' || c == ',' || c == ';' || c == '(' || c == ')' || c == '=' || c == '+';
    }

}
