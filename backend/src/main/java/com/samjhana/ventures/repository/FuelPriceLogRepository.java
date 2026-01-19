package com.samjhana.ventures.repository;

import com.samjhana.ventures.model.FuelPriceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FuelPriceLogRepository extends JpaRepository<FuelPriceLog, Long> {

    Optional<FuelPriceLog> findByFuelTypeAndIsCurrentTrue(FuelPriceLog.FuelType fuelType);

    List<FuelPriceLog> findByFuelTypeOrderByEffectiveDateDesc(FuelPriceLog.FuelType fuelType);

    List<FuelPriceLog> findByIsCurrentTrue();
}
