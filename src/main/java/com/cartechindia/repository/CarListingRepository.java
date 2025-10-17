package com.cartechindia.repository;

import com.cartechindia.entity.CarListing;
import com.cartechindia.entity.CarMasterData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarListingRepository extends JpaRepository<CarListing, Long> {

    // ===============================
    // Paginated list of all active listings
    // ===============================
    @Query("SELECT cl FROM CarListing cl WHERE cl.deleted = false AND cl.isApproved = 'APPROVED'")
    Page<CarListing> findAllApprovedCars(Pageable pageable);

    // Single car by ID, only if not deleted and approved
    @Query("SELECT cl FROM CarListing cl WHERE cl.deleted = false AND cl.isApproved = 'APPROVED' AND cl.id = :id")
    Optional<CarListing> findApprovedCarById(@Param("id") Long id);

    // ===============================
    // Fetch pending listing by id
    // ===============================
    @Query("SELECT cl FROM CarListing cl WHERE cl.id = :id AND cl.deleted = false AND cl.isApproved = 'PENDING'")
    Optional<CarListing> findPendingCarByIdIgnoreIsApproved(@Param("id") Long id);

    // ===============================
    // Fetch all pending listings
    // ===============================
    @Query("SELECT cl FROM CarListing cl WHERE cl.deleted = false AND cl.isApproved = 'PENDING'")
    List<CarListing> findAllPendingListings();

    // ===============================
    // Master data queries
    // ===============================
    @Query("SELECT DISTINCT c.make FROM CarMasterData c ORDER BY c.make ASC")
    List<String> findAllDistinctMakes();

    @Query("SELECT DISTINCT c.model FROM CarMasterData c WHERE LOWER(c.make) = LOWER(:make) ORDER BY c.model ASC")
    List<String> findModelsByMake(@Param("make") String make);

    @Query("SELECT DISTINCT c.variant FROM CarMasterData c WHERE LOWER(c.model) = LOWER(:model) ORDER BY c.variant ASC")
    List<String> findVariantsByModel(@Param("model") String model);

    @Query("SELECT c FROM CarMasterData c WHERE LOWER(c.variant) = LOWER(:variant)")
    List<CarMasterData> findByVariant(@Param("variant") String variant);

    // ===============================
    // Pending cars by ID
    // ===============================
    @Query("SELECT cl FROM CarListing cl WHERE cl.deleted = false AND cl.isApproved = 'PENDING' AND cl.id = :id")
    Optional<CarListing> findPendingCarById(@Param("id") Long id);

    @Query("SELECT cl FROM CarListing cl WHERE cl.deleted = false AND cl.isApproved = 'PENDING'")
    List<CarListing> findAllPendingCars();
}
