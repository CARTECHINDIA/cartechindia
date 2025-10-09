package com.cartechindia.service;

import com.cartechindia.constraints.UserStatus;
import com.cartechindia.dto.request.LoginRequestDto;
import com.cartechindia.dto.request.UserRequestDto;
import com.cartechindia.entity.User;
import org.springframework.core.io.Resource;

import java.util.List;

public interface UserService {

    String login(LoginRequestDto loginDetailDto);

    String register(UserRequestDto userRequestDto);

    User findByEmail(String email);

    void updateUserStatus(Long userId, UserStatus status);

    List<User> getUnapprovedUsers();

    void updateDealerStatus(Long userId, UserStatus status, String remarks);

    public String getDocumentPathByEmail(String email);

    User findById(Long id);

    Resource getUserDocumentForApproval(Long userId, String action);
}
