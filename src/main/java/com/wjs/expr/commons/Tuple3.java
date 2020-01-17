package com.wjs.expr.commons;

public class Tuple3<T1, T2, T3> {

    private Object[] contents;

    public Tuple3(T1 first, T2 second, T3 third) {
        contents = new Object[]{first, second, third};
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

    public void setFirst(T1 first){
        contents[0] = first;
    }

    public void setSecond(T2 second){
        contents[1] = second;
    }

    public void setThird(T3 third){
        contents[2] = third;
    }

    public void update(T1 first, T2 second, T3 third) {
        contents = new Object[]{first, second, third};
    }

    @Override
    public String toString() {
        return "{1. " + contents[0] +
                ", 2. "+ contents[1] +
                ", 3. "+ contents[2] + "}\n";
    }
}
