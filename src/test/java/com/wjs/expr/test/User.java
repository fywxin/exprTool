package com.wjs.expr.test;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author wjs
 * @date 2020-01-15 15:50
 **/
@Getter
@Setter
@NoArgsConstructor
public class User {

    public String name;

    private Integer age;

    private List<String> tags;

    private List<User> child;

    public User(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", tags=" + tags +
                '}';
    }
}
