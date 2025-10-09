package com.cartechindia.dto.request;

import lombok.Data;

@Data
public class LoginRequestDto {
    private String email;
    private String password;
    private Double longitude;
    private Double latitude;
}
