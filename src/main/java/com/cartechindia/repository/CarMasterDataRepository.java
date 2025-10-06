// CarMasterDataRepository.java
package com.cartechindia.repository;
import com.cartechindia.entity.CarMasterData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarMasterDataRepository extends JpaRepository<CarMasterData, Long> {}
