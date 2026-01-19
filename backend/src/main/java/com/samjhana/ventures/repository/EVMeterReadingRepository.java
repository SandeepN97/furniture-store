package com.samjhana.ventures.repository;

import com.samjhana.ventures.model.EVMeterReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EVMeterReadingRepository extends JpaRepository<EVMeterReading, Long> {

    Optional<EVMeterReading> findByReadingDate(LocalDate date);

    List<EVMeterReading> findByReadingDateBetweenOrderByReadingDateDesc(
        LocalDate startDate, LocalDate endDate
    );

    @Query("SELECT e FROM EVMeterReading e ORDER BY e.readingDate DESC LIMIT 1")
    Optional<EVMeterReading> findLatestReading();
}
