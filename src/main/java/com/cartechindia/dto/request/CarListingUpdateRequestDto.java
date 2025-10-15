package com.cartechindia.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Schema(description = "Fields for updating a car listing. Only provided fields will be updated.")
public class CarListingUpdateRequestDto {

    @Schema(description = "Registration number of the car", example = "AB123CD")
    private String regNumber;

    @Schema(description = "Year of manufacture", example = "2018")
    private Integer manufactureYear;

    @Schema(description = "Kilometers driven", example = "50000")
    private Integer kmDriven;

    @Schema(description = "Car color", example = "Red")
    private String color;

    @Schema(description = "Number of owners", example = "1")
    private Integer owners;

    @Schema(description = "Price of the car", example = "450000.00")
    private BigDecimal price;

    @Schema(description = "Health status of the car", example = "Good")
    private String health;

    @Schema(description = "Insurance details", example = "Full coverage till 2025-12-31")
    private String insurance;

    @Schema(description = "Registration date of the car", example = "2019-06-15")
    private LocalDate registrationDate;

    @Schema(description = "State where the car is registered", example = "Karnataka")
    private String state;

    @Schema(description = "City where the car is registered", example = "Bengaluru")
    private String city;

    @Schema(description = "ID of the CarMasterData record")
    private Long carMasterDataId;

    private List<MultipartFile> images;

}
