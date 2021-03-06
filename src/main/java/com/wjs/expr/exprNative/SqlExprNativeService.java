package com.wjs.expr.exprNative;

import com.wjs.expr.ExprException;
import com.wjs.expr.ExprFunction;
import com.wjs.expr.bean.BaseExpr;

/**
 * Sql 方言处理器
 * @author wjs
 * @date 2019-12-31 22:37
 **/
public class SqlExprNativeService implements ExprNativeService {

    @Override
    public String exprNative(String tmp) {
        StringBuilder sb = new StringBuilder();
        int symbolIndex = -1;
        Character symbol = null;
        Character ppp = null;
        Character pp = null;
        Character p = null;
        Character c = null;
        Character n = null;
        for (int i=0; i<tmp.length(); i++){
            c = tmp.charAt(i);
            //字符串，不处理
            if (symbolIndex >= 0){
                if (c == symbol){
                    symbolIndex = -1;
                    symbol = null;
                }
                if (c == '\n'){
                    throw new ExprException("表达式值不支持包含换行符:["+tmp+"]");
                }
                sb.append(c);
                continue;
            }
            if (c == '\'' || c == '"'){
                symbolIndex = i;
                symbol=c;
                sb.append(c);
                continue;
            }
            if (i>0){
                p = tmp.charAt(i-1);
            }
            if (i < tmp.length()-1){
                n = tmp.charAt(i+1);
            }else{
                n = null;
            }

            if (c == '\n'){
                if (p!=null && n!=null && p != ' ' && p != '\t' && n != ' ' && n != '\t'){
                    sb.append(' ');
                }
                continue;
            }
            if (c == '='){
                sb.append(c);
                if (!(n == null || n == '=' || p==null || p == '>' || p=='<' || p == '=' || p == '!')){
                    sb.append("=");
                }
                continue;
            }
            if (i > 3 && p != null && n != null){
                if (n == ' ' || n=='(' || n == '\t' || n=='\n'){
                    pp = tmp.charAt(i-2);
                    if ((c == 'r' || c == 'R') && (p == 'o' || p=='O')){
                        if (pp == ' ' || pp == ')' || pp == '\t' || pp == '\n') {
                            sb.deleteCharAt(sb.length()-1);
                            sb.append("||");
                            continue;
                        }
                    }
                    if ((c == 'd' || c == 'D') && (p == 'n' || p=='N') && (pp == 'a' || pp=='A')){
                        ppp = tmp.charAt(i-3);
                        if (ppp == ' ' || ppp == ')' || ppp == '\t' || ppp == '\n') {
                            sb.deleteCharAt(sb.length()-1);
                            sb.deleteCharAt(sb.length()-1);
                            sb.append("&&");
                            continue;
                        }
                    }
                }
            }
            sb.append(c);
        }
        return escapeFunc(sb);
    }

    /**
     *  '$func(args)' ->  str($func(args))
     *  "$func(args)" ->  str($func(args))
     *
     * @param sb
     * @return
     */
    public static String escapeFunc(StringBuilder sb){
        int len = sb.length();
        if (len < 3){
            return sb.toString();
        }
        StringBuilder str = new StringBuilder(sb.length());
        str.append(sb.charAt(0));
        Character pre = null;
        Character cur = null;
        int index = -1;
        boolean found = false;
        for (int i=1; i<len; i++){
            pre = sb.charAt(i-1);
            cur = sb.charAt(i);
            found = false;
            if (cur == '$' && (pre == '"' || pre == '\'')){
                index = i;
                int left = sb.indexOf("(", index+1);
                if (left != -1){
                    int right = sb.indexOf(")", index+1);
                    if (right > left && right < len-1 && sb.charAt(right+1) == pre){
                        String funName = sb.substring(index, left);
                        if (ExprFunction.support(funName)){
                            str.deleteCharAt(str.length()-1);
                            str.append("str(").append(sb, index, right+1).append(')');
                            i = right+1;
                            found = true;
                        }
                    }
                }
            }
            if (!found){
                str.append(cur);
            }
        }
        return str.toString();
    }

    @Override
    public boolean test(Character c) {
        return c == ' ' || c == '\n' || c == '\t' || c == BaseExpr.GRAMMAR || c == ',' || c == ';' || c == '(' || c == ')' || c == '=' || c == '}' || c == '{';
    }

    public static void main(String[] args) {
//        StringBuilder sb = new StringBuilder("sss11");
//        System.out.println(escapeFunc(sb));
//        sb = new StringBuilder("$dfd()=''");
//        System.out.println(escapeFunc(sb));
        StringBuilder sb = new StringBuilder("'$dfd()' == '' and   32='$dfd(ddd)'");
        System.out.println(escapeFunc(sb));
    }
}
