package com.cartechindia.dto;

import com.cartechindia.entity.UserStatus;
import lombok.Data;

@Data
public class DealerApprovalDto {
    private UserStatus status; // APPROVED or REJECTED
    private String remarks;    // Optional remarks
}
