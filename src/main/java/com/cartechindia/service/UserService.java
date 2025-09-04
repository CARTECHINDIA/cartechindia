package com.cartechindia.service;

import com.cartechindia.dto.LoginDetailDto;
import com.cartechindia.dto.UserDetailDto;

public interface UserService {

    String login(LoginDetailDto loginDetailDto);

    String register(UserDetailDto userDetailDto);
}
