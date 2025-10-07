package com.cartechindia.dto.request;

import com.cartechindia.constraints.CarStatus;
import com.cartechindia.entity.CarMasterData;
import com.cartechindia.entity.Location;
import com.cartechindia.entity.RtoRegistration;
import com.cartechindia.entity.User;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class CarListingRequestDto {

    @NotNull(message = "Car master ID is required")
    private Long carMasterId;

    @NotNull(message = "Seller ID is required")
    private Long sellerId;

    @NotNull(message = "Location ID is required")
    private Long locationId;

    @NotNull(message = "RTO registration ID is required")
    private Long rtoRegistrationId;

    @Min(value = 0, message = "Mileage cannot be negative")
    private int mileage;

    @Min(value = 0, message = "Ownership count cannot be negative")
    private int ownershipCount;

    @NotNull(message = "Expected price is required")
    private double expectedPrice;

    private boolean negotiable;

    // Optional: If you want clients to set status during creation
    // private CarStatus status;
}
