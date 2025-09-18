package com.cartechindia.repository;

import com.cartechindia.entity.CarSelling;
import com.cartechindia.util.CarSellingProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarSellingRepository extends JpaRepository<CarSelling, Long> {

    // ✅ Distinct brands
    @Query(value = "SELECT DISTINCT brand FROM CARS", nativeQuery = true)
    List<String> findAllDistinctBrands();

    // ✅ Distinct models by brand
    @Query(value = "SELECT DISTINCT model FROM CARS WHERE brand = :brand", nativeQuery = true)
    List<String> findModelsByBrand(@Param("brand") String brand);

    // ✅ Distinct variants by model
    @Query(value = "SELECT DISTINCT variant FROM CARS WHERE model = :model", nativeQuery = true)
    List<String> findVariantsByModel(@Param("model") String model);

    // ✅ Paginated list with projection
    @Query(value = """
        SELECT cs.id as id, cs.reg_number as regNumber, cs.car_id as carId, cs.manufacture_year as manufactureYear,
               cs.km_driven as kmDriven, cs.color as color, cs.owners as owners, cs.price as price,
               cs.health as health, cs.insurance as insurance, cs.registration_date as registrationDate,
               cs.state as state, cs.city as city, cs.status as status,
               c.brand as brand, c.model as model, c.variant as variant, c.fuel_type as fuelType,
               c.transmission as transmission, c.body_type as bodyType,
               cs.created_at as createdAt
        FROM car_selling cs
        JOIN CARS c ON cs.car_id = c.car_id
        """,
            countQuery = "SELECT COUNT(cs.id) FROM car_selling cs",
            nativeQuery = true)
    Page<CarSellingProjection> findAllWithDetails(Pageable pageable);

    // ✅ Find by variant
    @Query(value = """
        SELECT cs.id as id, cs.reg_number as regNumber, cs.car_id as carId, cs.manufacture_year as manufactureYear,
               cs.km_driven as kmDriven, cs.color as color, cs.owners as owners, cs.price as price,
               cs.health as health, cs.insurance as insurance, cs.registration_date as registrationDate,
               cs.state as state, cs.city as city, cs.status as status,
               c.brand as brand, c.model as model, c.variant as variant, c.fuel_type as fuelType,
               c.transmission as transmission, c.body_type as bodyType,
               cs.created_at as createdAt
        FROM car_selling cs
        JOIN CARS c ON cs.car_id = c.car_id
        WHERE c.variant = :variant
        """, nativeQuery = true)
    List<CarSellingProjection> findByVariant(@Param("variant") String variant);

    // ✅ Single car details by ID
    @Query(value = """
        SELECT cs.id as id, cs.reg_number as regNumber, cs.car_id as carId, cs.manufacture_year as manufactureYear,
               cs.km_driven as kmDriven, cs.color as color, cs.owners as owners, cs.price as price,
               cs.health as health, cs.insurance as insurance, cs.registration_date as registrationDate,
               cs.state as state, cs.city as city, cs.status as status,
               c.brand as brand, c.model as model, c.variant as variant, c.fuel_type as fuelType,
               c.transmission as transmission, c.body_type as bodyType,
               cs.created_at as createdAt
        FROM car_selling cs
        JOIN CARS c ON cs.car_id = c.car_id
        WHERE cs.id = :id
        """, nativeQuery = true)
    Optional<CarSellingProjection> findCarSellingWithDetails(@Param("id") Long id);
}
