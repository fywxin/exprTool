package com.wjs.expr.commons;

public class Tuple2<T1, T2> {

    private Object[] contents;

    public Tuple2(T1 first, T2 second) {
        contents = new Object[]{first, second};
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

    public void update(T1 first, T2 second){
        contents = new Object[]{first, second};
    }

    @Override
    public String toString() {
        return "1. "+contents[0]+", 2. "+contents[1];
    }
}
