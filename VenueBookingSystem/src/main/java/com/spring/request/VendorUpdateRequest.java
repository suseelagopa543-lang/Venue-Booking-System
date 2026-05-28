package com.spring.request;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class VendorUpdateRequest {

    private String userName;
    private String userEmail;
    private String phoneNumber;
    private String businessName;
    private String businessAddress;
    private String userPassword;

}
