// RTORepository.java
package com.cartechindia.repository;
import com.cartechindia.entity.RtoMaster;
import com.cartechindia.entity.RtoRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RTORegistrationRepository extends JpaRepository<RtoRegistration, Long> {}
