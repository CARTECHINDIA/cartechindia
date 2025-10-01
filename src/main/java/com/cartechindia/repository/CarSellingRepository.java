package com.cartechindia.repository;

import com.cartechindia.entity.CarSelling;
import com.cartechindia.util.CarSellingProjection;
import com.cartechindia.util.CarsProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarSellingRepository extends JpaRepository<CarSelling, Long> {

    Page<CarSelling> findAllByDeletedFalse(Pageable pageable);

    Optional<CarSelling> findByIdAndDeletedFalse(Long id);

    // ========================
    // Distinct brands from CARS
    // Case-insensitive, sorted, first letter capitalized
    // ========================
    @Query(value = """
        SELECT DISTINCT CONCAT(UCASE(LEFT(LOWER(c.brand),1)), SUBSTRING(LOWER(c.brand),2)) AS brand
        FROM CARS c
        ORDER BY brand ASC
        """, nativeQuery = true)
    List<String> findAllDistinctBrands();

    // ========================
    // Distinct models by brand from CARS
    // Case-insensitive, sorted, first letter capitalized
    // ========================
    @Query(value = """
        SELECT DISTINCT CONCAT(UCASE(LEFT(LOWER(c.model),1)), SUBSTRING(LOWER(c.model),2)) AS model
        FROM CARS c
        WHERE LOWER(c.brand) = LOWER(:brand)
        ORDER BY model ASC
        """, nativeQuery = true)
    List<String> findModelsByBrand(@Param("brand") String brand);

    // ========================
    // Distinct variants by model from CARS
    // Case-insensitive, sorted, first letter capitalized
    // ========================
    @Query(value = """
        SELECT DISTINCT CONCAT(UCASE(LEFT(LOWER(c.variant),1)), SUBSTRING(LOWER(c.variant),2)) AS variant
        FROM CARS c
        WHERE LOWER(c.model) = LOWER(:model)
        ORDER BY variant ASC
        """, nativeQuery = true)
    List<String> findVariantsByModel(@Param("model") String model);

    // ========================
    // Paginated list of cars from CARS table only
    // ========================
    @Query(value = """
        SELECT c.car_id as carId,
               c.brand as brand,
               c.model as model,
               c.variant as variant,
               c.fuel_type as fuelType,
               c.transmission as transmission,
               c.body_type as bodyType
        FROM CARS c
        ORDER BY c.brand ASC, c.model ASC, c.variant ASC
        """,
            countQuery = "SELECT COUNT(c.car_id) FROM CARS c",
            nativeQuery = true)
    Page<CarSellingProjection> findAllCarsFromCARS(Pageable pageable);

    // ========================
    // Paginated list with full car details (JOIN car_selling + CARS)
    // ========================
    @Query(value = """
    SELECT cs.id as id,
           cs.reg_number as regNumber,
           cs.car_id as carId,
           cs.manufacture_year as manufactureYear,
           cs.km_driven as kmDriven,
           cs.color as color,
           cs.owners as owners,
           cs.price as price,
           cs.health as health,
           cs.insurance as insurance,
           cs.registration_date as registrationDate,
           cs.state as state,
           cs.city as city,
           cs.status as status,
           c.brand as brand,
           c.model as model,
           c.variant as variant,
           c.fuel_type as fuelType,
           c.transmission as transmission,
           c.body_type as bodyType,
           cs.created_at as createdAt
    FROM car_selling cs
    JOIN CARS c ON cs.car_id = c.car_id
    WHERE cs.deleted = false
      AND cs.is_approved = 'APPROVED'
    """, nativeQuery = true)
    Page<CarSellingProjection> findAllCarsSelling(Pageable pageable);




    // ========================
    // Find by variant (case-insensitive, newest first)
    // ========================

    @Query(value = """
        SELECT c.car_id as carId,
               c.brand as brand,
               c.model as model,
               c.variant as variant,
               c.fuel_type as fuelType,
               c.transmission as transmission,
               c.drivetrain as drivetrain,
               c.body_type as bodyType,
               c.segment as segment,
               c.seating_capacity as seatingCapacity,
               c.safety_rating_stars as safetyRatingStars,
               c.launch_year as launchYear,
               c.engine_cc as engineCc,
               c.power_bhp as powerBhp,
               c.torque_nm as torqueNm,
               c.length_mm as lengthMm,
               c.width_mm as widthMm,
               c.height_mm as heightMm,
               c.wheelbase_mm as wheelbaseMm,
               c.ground_clearance_mm as groundClearanceMm,
               c.battery_kwh as batteryKwh,
               c.ex_showroom_price_lakh as exShowroomPriceLakh,
               c.efficiency as efficiency,
               c.efficiency_unit as efficiencyUnit,
               c.color_options as colorOptions,
               c.market_status as marketStatus,
               c.bs6_phase as bs6Phase,
               c.updated_date as updatedDate,
               c.updated_by as updatedBy,
               c.created_by as createdBy
        FROM CARS c
        WHERE LOWER(c.variant) = LOWER(:variant)
        """, nativeQuery = true)
    List<CarsProjection> findByVariant(@Param("variant") String variant);


    // ========================
    // Single car details by ID (JOIN car_selling + CARS)
    // ========================
    @Query(value = """
    SELECT cs.id as id,
           cs.reg_number as regNumber,
           cs.car_id as carId,
           cs.manufacture_year as manufactureYear,
           cs.km_driven as kmDriven,
           cs.color as color,
           cs.owners as owners,
           cs.price as price,
           cs.health as health,
           cs.insurance as insurance,
           cs.registration_date as registrationDate,
           cs.state as state,
           cs.city as city,
           cs.status as status,
           c.brand as brand,
           c.model as model,
           c.variant as variant,
           c.fuel_type as fuelType,
           c.transmission as transmission,
           c.body_type as bodyType,
           cs.created_at as createdAt
    FROM car_selling cs
    JOIN CARS c ON cs.car_id = c.car_id
    WHERE cs.id = :id
      AND cs.deleted = false
      AND cs.is_approved = 'APPROVED'
    """, nativeQuery = true)
    Optional<CarSellingProjection> findCarById(@Param("id") Long id);


    // Fetch all unapproved (PENDING) cars
    @Query(value = """
    SELECT cs.id as id,
           cs.reg_number as regNumber,
           cs.car_id as carId,
           cs.manufacture_year as manufactureYear,
           cs.km_driven as kmDriven,
           cs.color as color,
           cs.owners as owners,
           cs.price as price,
           cs.health as health,
           cs.insurance as insurance,
           cs.registration_date as registrationDate,
           cs.state as state,
           cs.city as city,
           cs.status as status,
           c.brand as brand,
           c.model as model,
           c.variant as variant,
           c.fuel_type as fuelType,
           c.transmission as transmission,
           c.body_type as bodyType,
           cs.created_at as createdAt
    FROM car_selling cs
    JOIN CARS c ON cs.car_id = c.car_id
    WHERE cs.deleted = false
      AND cs.is_approved = 'PENDING'
    """, nativeQuery = true)
    List<CarSellingProjection> findAllPendingCars();

    // Fetch single pending car by ID
    @Query(value = """
    SELECT cs.id as id,
           cs.reg_number as regNumber,
           cs.car_id as carId,
           cs.manufacture_year as manufactureYear,
           cs.km_driven as kmDriven,
           cs.color as color,
           cs.owners as owners,
           cs.price as price,
           cs.health as health,
           cs.insurance as insurance,
           cs.registration_date as registrationDate,
           cs.state as state,
           cs.city as city,
           cs.status as status,
           c.brand as brand,
           c.model as model,
           c.variant as variant,
           c.fuel_type as fuelType,
           c.transmission as transmission,
           c.body_type as bodyType,
           cs.created_at as createdAt
    FROM car_selling cs
    JOIN CARS c ON cs.car_id = c.car_id
    WHERE cs.deleted = false
      AND cs.is_approved = 'PENDING'
      AND cs.id = :id
    """, nativeQuery = true)
    Optional<CarSellingProjection> findPendingCarById(@Param("id") Long id);


    @Query(value = """
    SELECT cs.id as id,
           cs.reg_number as regNumber,
           cs.car_id as carId,
           cs.manufacture_year as manufactureYear,
           cs.km_driven as kmDriven,
           cs.color as color,
           cs.owners as owners,
           cs.price as price,
           cs.health as health,
           cs.insurance as insurance,
           cs.registration_date as registrationDate,
           cs.state as state,
           cs.city as city,
           cs.status as status,
           c.brand as brand,
           c.model as model,
           c.variant as variant,
           c.fuel_type as fuelType,
           c.transmission as transmission,
           c.body_type as bodyType,
           cs.created_at as createdAt
    FROM car_selling cs
    JOIN CARS c ON cs.car_id = c.car_id
    WHERE cs.id = :id
      AND cs.deleted = false
    """, nativeQuery = true)
    Optional<CarSellingProjection> findCarByIdIgnoreIsApproved(@Param("id") Long id);





}
