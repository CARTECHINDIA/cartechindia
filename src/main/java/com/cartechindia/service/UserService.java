package com.cartechindia.service;

import com.cartechindia.constraints.UserStatus;
import com.cartechindia.dto.request.LoginRequestDto;
import com.cartechindia.dto.request.UserRequestDto;
import com.cartechindia.dto.request.UserUpdateRequestDto;
import com.cartechindia.dto.response.UserResponseDto;
import com.cartechindia.entity.User;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {


    String login(LoginRequestDto loginDetailDto);

    String register(UserRequestDto userRequestDto);

    User findByEmail(String email);

    void updateUserStatus(Long userId, UserStatus status);

    List<User> getUnapprovedUsers();

    void updateDealerStatus(Long userId, UserStatus status, String remarks);

    public String getDocumentPathByEmail(String email);

    Resource getUserDocumentForApproval(Long userId, String action);

    Page<UserResponseDto> getUsersByStatus(UserStatus status, Pageable pageable);
    Page<UserResponseDto> getAllActiveUsers(int page, int size);

}
