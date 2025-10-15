package com.cartechindia.repository;

import com.cartechindia.constraints.UserStatus;
import com.cartechindia.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    List<User> findByActiveTrue();

    List<User> findByStatus(UserStatus status);


    Optional<User> findByPhone(String email);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    Optional<User> findByUsername(String username);

    List<User> findByStatusNot(UserStatus status);
}
