package com.wjs.expr.commons;

/**
 * @author wjs
 * @date 2020-01-16 14:34
 **/
public class ExprUtil {

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

    public static void main(String[] args) {
        String str=Integer.MAX_VALUE+"";
        int chr = str.length();
        System.out.println(chr);
        System.out.println(isNumeric("122122"));
    }
}
