package io.github.jihch.bean;

import java.sql.Date;

/**
 * ORM 编程思想（object relational mapping）
 * 一个数据表对应一个 Java 类
 * 表中的一条记录对应 Java 类的一个对象
 * 表中的一个字段对应 Java 类的一个属性
 */
public class Customer {

    private int id;

    private String name;

    private String email;

    private Date birth;

    public Customer() {
    }

    public Customer(int id, String name, String email, Date birth) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.birth = birth;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", birth=" + birth +
                '}';
    }

}
