package com.cartechindia.dto.response;

import com.cartechindia.constraints.CarStatus;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Data
@Getter
@Setter
public class CarListingResponseDto {

    private Long id;

    private CarMasterDataResponseDto carMasterData; // nested DTO

    private Long sellerId;
    private String sellerName; // optional

    private Long locationId;
    private String locationName; // optional

    private Long rtoRegistrationId;
    private String rtoRegistrationNumber; // optional

    private int mileage;
    private int ownershipCount;

    private BigDecimal expectedPrice;
    private boolean negotiable;

    private CarStatus status;

}
