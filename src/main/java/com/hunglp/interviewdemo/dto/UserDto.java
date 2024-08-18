package com.hunglp.interviewdemo.dto;

import com.hunglp.interviewdemo.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {
    private int id;
    private String name;
    private String email;
    private String gender;
    private int indexed = 0;

    public UserDto(User user){
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.gender = user.getGender();
    }
}
