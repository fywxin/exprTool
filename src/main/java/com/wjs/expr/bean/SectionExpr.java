package com.wjs.expr.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

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

    public Map<String, Object> params;

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
            if (c != '\n'){
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
