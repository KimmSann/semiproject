package com.example.weblogin.web.dto.auth;

import com.example.weblogin.domain.user.User;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class SignupDto {
    private String username;
    private String password;
    private String email;
    private String name;
    private String address;
    private String phone;
    private String role;

    public User toEntity() {
        return User.builder()
                .username(username)
                .password(password)
                .email(email)
                .name(name)
                .address(address)
                .phone(phone)
                .role(role)
                .build();
    }
}
