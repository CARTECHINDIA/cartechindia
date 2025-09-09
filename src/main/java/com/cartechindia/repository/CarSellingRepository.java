package com.cartechindia.repository;

import com.cartechindia.entity.Bidding;
import com.cartechindia.entity.CarSelling;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarSellingRepository extends JpaRepository<CarSelling, Long> {

    Optional<CarSelling> findBySellingId(Long id);

    @Query("SELECT DISTINCT c.brand FROM CarSelling c")
    List<String> findAllDistinctBrands();

    @Query("SELECT DISTINCT c.model FROM CarSelling c WHERE c.brand = :brand")
    List<String> findModelsByBrand(String brand);

    @Query("SELECT DISTINCT c.variant FROM CarSelling c WHERE c.model = :model")
    List<String> findVariantsByModel(String model);

    List<CarSelling> findByVariant(String variant);
}
