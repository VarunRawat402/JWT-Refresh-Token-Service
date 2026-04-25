package com.example.RefreshTokenPrac.model;

import com.example.RefreshTokenPrac.entities.UserInfo;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoDto
{
    private String username;
    private String password;

    private String name;
    private String age;
    private String Designation;

    public UserInfo toUserInfo() {
        return UserInfo.builder()
                .username(this.username)
                .password(this.password)
                .name(this.name)
                .age(this.age)
                .Designation(this.Designation)
                .build();
    }
}
