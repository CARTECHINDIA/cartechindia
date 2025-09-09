package com.cartechindia.repository;

import com.cartechindia.entity.Bidding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BiddingRepository extends JpaRepository<Bidding, Long> {


}
