package com.cartechindia.service;

import com.cartechindia.dto.LoginDetailDto;
import com.cartechindia.dto.UserDetailDto;
import com.cartechindia.entity.User;
import com.cartechindia.entity.UserStatus;
import org.springframework.core.io.Resource;

import java.util.List;

public interface UserService {

    String login(LoginDetailDto loginDetailDto);

    String register(UserDetailDto userDetailDto);

    User findByEmail(String email);

    void updateUserStatus(Long userId, UserStatus status);

    List<User> getUnapprovedUsers();

    void updateDealerStatus(Long userId, UserStatus status, String remarks);

    public String getDocumentPathByEmail(String email);

    User findById(Long id);

    Resource getUserDocumentForApproval(Long userId, String action);
}
