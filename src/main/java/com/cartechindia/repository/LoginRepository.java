package com.cartechindia.repository;

import com.cartechindia.entity.Login;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoginRepository extends JpaRepository<Login, Long> {

    List<Login> findByUserId(Long userId);

    Login findTop1ByUserIdOrderByLoginTimeDesc(Long userId); // latest login
}
