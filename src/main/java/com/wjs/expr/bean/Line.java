package com.wjs.expr.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * @author wjs
 * @date 2020-01-19 10:49
 **/
@Getter
@Setter
public class Line {

    public Integer lineNum;

    public Integer start;

    public Integer stop;

    public boolean empty;

    public Line(Integer lineNum, Integer start) {
        this.lineNum = lineNum;
        this.start = start;
    }

    public String info(int stop){
        return "第["+(lineNum+1)+"]行,区间["+start+"-"+stop+"] ";
    }

    public String info(){
        return "第["+(lineNum+1)+"]行,区间["+start+"-"+stop+"] ";
    }
}
