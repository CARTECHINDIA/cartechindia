// RTORepository.java
package com.cartechindia.repository;
import com.cartechindia.entity.RtoMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RTOMasterRepository extends JpaRepository<RtoMaster, Long> {}
