package com.samjhana.ventures.repository;

import com.samjhana.ventures.model.BusinessUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusinessUnitRepository extends JpaRepository<BusinessUnit, Long> {

    List<BusinessUnit> findByActiveTrue();

    Optional<BusinessUnit> findByName(String name);

    List<BusinessUnit> findByType(BusinessUnit.BusinessType type);
}
