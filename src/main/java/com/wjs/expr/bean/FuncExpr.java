package com.wjs.expr.bean;

import com.wjs.expr.commons.ExprUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户自定义方法混入
 * @author wjs
 * @date 2020-01-06 19:10
 **/
@Getter
@Setter
public class FuncExpr extends SectionExpr {

    //是否稳幂等, 不随执行次数与上线文而改变
    public boolean idempotent = false;
    //参数文本列表
    public List<String> params;

    public FuncExpr(String text) {
        super(text);
    }

    public FuncExpr(String text, Line startLine, Line stopLine, int startCol, int stopCol) {
        super(text, startLine, stopLine, startCol, stopCol);
    }

    @Override
    public String getSectionText(){
        return text.substring(startCol, stopCol+1);
    }

    /**
     * 返回方法名称
     * @return
     */
    public String getFuncName(){
        return text.substring(startCol, text.indexOf('(', startCol)).trim();
    }

    /**
     * 返回方法参数内容
     * @return
     */
    public String getFuncParamStr(){
        return text.substring(text.indexOf('(', startCol)+1, stopCol).trim();
    }

    /**
     * 获取指定位置参数
     * @param index
     * @return
     */
    public String getFuncParam(Integer index){
        if (params == null){
            params = new ArrayList<>();
            String param = getFuncParamStr();
            Character c = null;
            Character wordChar = null;
            StringBuilder sb = new StringBuilder();
            for (int i=0; i<param.length(); i++){
                c = param.charAt(i);
                if (wordChar != null){
                    if (c == wordChar){
                        wordChar = null;
                    }
                    sb.append(c);
                }else{
                    if (c == ','){
                        ExprUtil.rtrim(sb);
                        params.add(sb.toString());
                        sb.setLength(0);
                    }else{
                        if (sb.length() == 0){
                            if (ExprUtil.isWS(c)){
                                continue;
                            }
                        }
                        sb.append(c);
                    }
                }
            }
            if (sb.length()>0){
                ExprUtil.rtrim(sb);
                params.add(sb.toString());
            }
        }
        if (params.size() <= index){
            return null;
        }
        return params.get(index);
    }
}
