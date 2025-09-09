package com.cartechindia.repository;

import com.cartechindia.entity.Bidding;
import com.cartechindia.entity.CarSelling;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarSellingRepository extends JpaRepository<CarSelling, Long> {

    Optional<CarSelling> findBySellingId(Long id);
}
