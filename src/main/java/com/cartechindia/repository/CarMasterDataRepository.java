package com.cartechindia.repository;

import com.cartechindia.entity.CarMasterData;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CarMasterDataRepository extends JpaRepository<CarMasterData, Long> {
    Optional<CarMasterData> findByMakeAndModelAndVariantAndYearOfManufacture(
            String make, String model, String variant, int yearOfManufacture);
}
