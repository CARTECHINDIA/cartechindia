package com.cartechindia.dto;

import lombok.Data;

@Data
public class LoginDetailDto {
    private String email;
    private String password;
    private Double longitude;
    private Double latitude;
}
