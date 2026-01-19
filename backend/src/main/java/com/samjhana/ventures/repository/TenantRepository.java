package com.samjhana.ventures.repository;

import com.samjhana.ventures.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {

    List<Tenant> findByStatus(Tenant.TenantStatus status);

    List<Tenant> findByNameContainingIgnoreCase(String name);
}
