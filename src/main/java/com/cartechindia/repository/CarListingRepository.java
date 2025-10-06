// CarListingRepository.java
package com.cartechindia.repository;
import com.cartechindia.entity.CarListing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarListingRepository extends JpaRepository<CarListing, Long> {}
