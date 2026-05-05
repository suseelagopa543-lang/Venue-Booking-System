package com.spring.Request;

import com.spring.model.ApprovalStatus;
import com.spring.model.Role;
import com.spring.model.Status;
import lombok.*;

import java.sql.Statement;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class VendorResponse {

    private String userName;
    private String userEmail;
    private String phoneNumber;
    private String businessName;
    private String businessAddress;
    private ApprovalStatus approvalStatus;
    private Status vendorStatus;

}
