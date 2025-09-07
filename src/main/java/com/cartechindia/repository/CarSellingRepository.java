package com.cartechindia.repository;

import com.cartechindia.entity.CarSelling;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarSellingRepository extends JpaRepository<CarSelling, Long> {
}
