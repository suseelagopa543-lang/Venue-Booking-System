package com.spring.Request;

import com.spring.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RegisterRequest {

    @NotBlank(message = "Username is required")
    private String username;
    @NotBlank(message = "Password is required")
    private String password;
    @NotBlank(message = "Email is required")
    private String email;
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
    @NotNull(message = "Role is required")
    private Role role;

    //@NotBlank(message = "Business name is required for venue owners")
    private String businessName;
    //@NotBlank(message = "Business address is required for venue owners")
    private String businessAddress;
}
