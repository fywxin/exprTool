package com.wjs.expr.bean;

import com.wjs.expr.commons.ExprUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * 段落表达式，跨行则用封号分割 <$ 表达式 $>
 * @author wjs
 * @date 2020-01-09 09:14
 **/
@Getter
@Setter
public class SectionExpr extends BaseExpr {

    public static final Character SECTION_OPEN = '<';

    public static final Character SECTION_CLOSE = '>';

    //默认值分割符号 !!
    public static final Character SECTION_NULL_VALUE = '!';
    //默认值文本
    private String defaultValueSection = null;

    public SectionExpr(String text) {
        super(text);
    }

    public SectionExpr(String text, int startLine, int stopLine, int startCol, int stopCol) {
        super(text);
        this.startLine = startLine;
        this.stopLine = stopLine;
        this.startCol = startCol;
        this.stopCol = stopCol;
    }

    public String getSectionText(){
        StringBuilder sb = new StringBuilder(stopCol-startCol-4);
        Character c = null;
        for (int i=startCol+2; i<stopCol-2; i++){
            c = text.charAt(i);
            if (c == SECTION_NULL_VALUE && text.charAt(i+1) == SECTION_NULL_VALUE){
                if (i+2 < stopCol-2) {
                    defaultValueSection = text.substring(i+2, stopCol-2);
                    break;
                }
            }
            if (c != '\n'){
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public boolean hasDefaultValue(){
        return defaultValueSection != null;
    }

    public String defaultValueText(){
        if (defaultValueSection == null){
            return "";
        }
        defaultValueSection = defaultValueSection.trim();
        if (trimStr()) return defaultValueSection.substring(1, defaultValueSection.length() - 1);
        return defaultValueSection;
    }

    private boolean trimStr() {
        return (defaultValueSection.charAt(0) == '"' && defaultValueSection.charAt(defaultValueSection.length() - 1) == '"')
                || (defaultValueSection.charAt(0) == '\'' && defaultValueSection.charAt(defaultValueSection.length() - 1) == '\'');
    }

    /**
     * 表达式默认值 用法<$ xx!!dd $>
     * @return
     */
    public Object getDefaultValue(){
        if (defaultValueSection == null){
            return null;
        }
        defaultValueSection = defaultValueSection.trim();
        if ("true".equalsIgnoreCase(defaultValueSection)){
            return true;
        }
        if ("false".equalsIgnoreCase(defaultValueSection)){
            return false;
        }
        if (trimStr()) return defaultValueSection.substring(1, defaultValueSection.length() - 1);
        if (ExprUtil.isDig(defaultValueSection)){
            if (defaultValueSection.length() < 10){
                return Integer.parseInt(defaultValueSection);
            }else{
                return Long.parseLong(defaultValueSection);
            }
        }
        if (ExprUtil.isNumeric(defaultValueSection)){
            return Double.parseDouble(defaultValueSection);
        }
        return defaultValueSection;
    }
}
