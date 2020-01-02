package com.wjs.expr.exprNative;

/**
 * Sql 方言处理器
 * @author wjs
 * @date 2019-12-31 22:37
 **/
public class SqlExprNativeService implements ExprNativeService {

    @Override
    public String exprNative(String tmp) {
        StringBuilder sb = new StringBuilder();
        Character c = null;
        for (int i=0; i<tmp.length()-1; i++){
            c = tmp.charAt(i);
            if (c == '\n'){
                sb.append(' ');
                continue;
            }
            if (c == '='){
                sb.append(c);
                sb.append("=");
                if (tmp.charAt(i+1) == '='){
                    i++;
                }
                continue;
            }
            sb.append(c);
        }
        c = tmp.charAt(tmp.length()-1);
        if (c != '\n'){
            sb.append(c);
        }
        System.out.println(">>> "+sb.toString());
        return sb.toString();
    }

    @Override
    public boolean isSplitChar(Character c) {
        return c == ' ' || c == '\n' || c == '\t' || c == ',' || c == ';' || c == '(' || c == ')' || c == '=' || c == '+';
    }
}
