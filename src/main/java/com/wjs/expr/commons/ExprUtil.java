package com.wjs.expr.commons;

import com.wjs.expr.ExprManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author wjs
 * @date 2020-01-16 14:34
 **/
public class ExprUtil {

    public static char[] WS = new char[]{' ', '\r', '\n', '\t'};

    public static void fillLine(StringBuilder sb, Integer count){
        while (count > 0){
            sb.append("\n");
            count--;
        }
    }

    /**
     * 是否展示的为空白内容
     * @param text
     * @param start
     * @param end
     * @return
     */
    public static boolean isWhite(String text, int start, int end){
        if (start >= end){
            return true;
        }
        if (start+1 == end && text.charAt(start) == '\n'){
            return true;
        }
        Character c = null;
        for (int i=start; i<end; i++){
            c = text.charAt(i);
            if (c != ' ' && c != '\t'){
                return false;
            }
        }
        return true;
    }

    public static boolean isNumeric (String str) {
        if (isBlank(str)){
            return false;
        }
        Boolean point = null;
        for (int i = 0; i<str.length(); i++) {
            int chr = str.charAt(i);
            if (chr < 48 || chr > 57) {
                if (i == 0 && chr == 45){
                    continue;
                }
                if (chr == 46 && point == null && i>0 && i != str.length()-1){
                    point = true;
                    continue;
                }
                return false;
            }
        }
        return true;
    }

    public static boolean isDig (String str) {
        if (isBlank(str)){
            return false;
        }
        for (int i = 0; i<str.length(); i++) {
            int chr = str.charAt(i);
            if (chr < 48 || chr > 57) {
                if (i == 0 && chr == 45){
                    continue;
                }
                return false;
            }
        }
        return true;
    }

    public static boolean isBlank(String str){
        if (str == null || str.length() == 0){
            return true;
        }
        return str.trim().length() == 0;
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * 获取前N个单词
     * @param sql
     * @param n
     * @return
     */
    public static List<Tuple2<String, Integer>> headWordN(String sql, int n){
        return headWordN(sql, n, ExprManager.ExprServiceHolder.getInstance().sqlExprNativeService);
    }

    /**
     *  获取前N个单词
     * @param sql
     * @param n
     * @param predicate 单词识别
     * @return
     */
    public static List<Tuple2<String, Integer>> headWordN(String sql, int n, Predicate<Character> predicate){
        Character c = null;
        List<Tuple2<String, Integer>> words = new ArrayList<>(n);
        int wc = -1;
        for (int i=0; i<sql.length(); i++){
            c = sql.charAt(i);
            if (wc == -1){
                if (predicate.test(c)){
                    continue;
                }else{
                    wc = i;
                }
            }else{
                if (predicate.test(c)){
                    words.add(new Tuple2<>(sql.substring(wc, i), wc));
                    if (words.size() >= n){
                        return words;
                    }
                    wc = -1;
                }else{
                    continue;
                }
            }
        }
        if (wc != -1){
            words.add(new Tuple2<>(sql.substring(wc), wc));
        }
        return words;
    }

    public static void rtrim(StringBuilder sb){
        while (sb.length() > 0 && isWS(sb.charAt(sb.length() - 1))){
            sb.deleteCharAt(sb.length()-1);
        }
    }

    public static boolean isWS(char c) {
        for (char c1 : WS){
            if (c == c1){
                return true;
            }
        }
        return false;
    }

    /**
     * 获取尾巴N个单词 - 反序
     * @param sql
     * @param n
     * @return
     */
    public static List<Tuple2<String, Integer>> tailWordN(String sql, int n){
        return tailWordN(sql, n, ExprManager.ExprServiceHolder.getInstance().sqlExprNativeService);
    }

    /**
     *
     * @param sql
     * @param n
     * @param predicate
     * @return
     */
    public static List<Tuple2<String, Integer>> tailWordN(String sql, int n, Predicate<Character> predicate){
        Character c = null;
        List<Tuple2<String, Integer>> words = new ArrayList<>(n);
        int wc = -1;
        int len = sql.length();
        for (int i=len-1; i>=0; i--){
            c = sql.charAt(i);
            if (wc == -1){
                if (predicate.test(c)){
                    continue;
                }else{
                    wc = i;
                }
            }else{
                if (predicate.test(c)){
                    words.add(new Tuple2<>(sql.substring(i+1, wc+1), i+1));
                    if (words.size() >= n){
                        return words;
                    }
                    wc = -1;
                }else{
                    continue;
                }
            }
        }
        if (wc != -1){
            words.add(new Tuple2<>(sql.substring(0, wc+1), 0));
        }
        return words;
    }

    public static void main(String[] args) {
        String str=Integer.MAX_VALUE+"";
        int chr = str.length();
        System.out.println(chr);
        System.out.println(isNumeric("122122"));
        String a = "select uid,xql_flat(dict,backpack,'itemid|iteminfo') from e_1\n" +
                "WHERE id=1 \tand tt=1 as t_1";

        System.out.println(headWordN(a, 200, c -> c == ' '));
        System.out.println(tailWordN(a, 200));
    }
}
