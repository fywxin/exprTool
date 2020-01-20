package com.wjs.expr.commons;

/**
 * @author wjs
 * @date 2020-01-19 10:42
 **/
public class Tuple4<T1, T2, T3, T4> {

    private Object[] contents;

    public Tuple4(T1 first, T2 second, T3 third, T4 four) {
        contents = new Object[]{first, second, third, four};
    }

    public Object get(int index) {
        return this.contents[index];
    }

    public T1 getFirst() {
        return (T1)this.get(0);
    }

    public T2 getSecond() {
        return (T2)this.get(1);
    }

    public T3 getThird() {
        return (T3)this.get(2);
    }

    public T4 getFour() {
        return (T4)this.get(3);
    }

    public void setFirst(T1 first){
        contents[0] = first;
    }

    public void setSecond(T2 second){
        contents[1] = second;
    }

    public void setThird(T3 third){
        contents[2] = third;
    }

    public void setFour(T4 four){
        contents[3] = four;
    }

    public void update(T1 first, T2 second, T3 third, T4 four) {
        contents = new Object[]{first, second, third, four};
    }

    @Override
    public String toString() {
        return "{" + contents[0] +
                ", "+ contents[1] +
                ", "+ contents[2] +
                ", "+ contents[3] + "}\n";
    }
}
