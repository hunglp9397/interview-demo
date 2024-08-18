package com.hunglp.interviewdemo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;



@Data
@Entity
@Table(name = "USER_TBL")

public class User {

    @Id
    @GeneratedValue
    private int id;
    private String name;
    private String email;
    private String gender;

    public User(int id, String name, String email, String gender) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.gender = gender;
    }

    public User(String name, String email, String gender) {
        this.name = name;
        this.email = email;
        this.gender = gender;
    }


    public User() {

    }
}
