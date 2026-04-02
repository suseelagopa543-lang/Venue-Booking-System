package com.spring.Request;

import com.spring.model.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private String phoneNumber;
    private Role role;

    private String businessName;
    private String businessAddress;
}
