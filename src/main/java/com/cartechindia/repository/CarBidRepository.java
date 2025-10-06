/*
package com.cartechindia.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CarBidRepository extends JpaRepository<CarBid, Long> {

    @Query("SELECT MAX(b.bidAmount) FROM CarBid b WHERE b.carListing.id = :listingId")
    Optional<Double> findHighestBidAmountByCarListing(@Param("listingId") Long listingId);

    boolean existsByCarListingIdAndBuyerIdAndStatus(Long carListingId, Long buyerId, BidStatus status);
}
*/
