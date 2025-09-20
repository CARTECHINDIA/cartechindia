package com.cartechindia.service;

import com.cartechindia.dto.LoginDetailDto;
import com.cartechindia.dto.UserDetailDto;
import com.cartechindia.entity.User;

public interface UserService {

    String login(LoginDetailDto loginDetailDto);

    String register(UserDetailDto userDetailDto);

    User findByEmail(String email);
}
