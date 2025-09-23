package com.cartechindia.service;

import com.cartechindia.dto.LoginDetailDto;
import com.cartechindia.dto.UserDetailDto;
import com.cartechindia.entity.User;
import com.cartechindia.entity.UserStatus;

import java.util.List;

public interface UserService {

    String login(LoginDetailDto loginDetailDto);

    String register(UserDetailDto userDetailDto);

    User findByEmail(String email);

    void updateUserStatus(Long userId, UserStatus status);

    List<User> getUnapprovedUsers();
}
