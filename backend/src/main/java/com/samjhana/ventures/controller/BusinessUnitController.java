package com.samjhana.ventures.controller;

import com.samjhana.ventures.model.BusinessUnit;
import com.samjhana.ventures.repository.BusinessUnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/businesses")
@RequiredArgsConstructor
public class BusinessUnitController {

    private final BusinessUnitRepository businessUnitRepository;

    @GetMapping("/all")
    public ResponseEntity<List<BusinessUnit>> getAllBusinesses() {
        return ResponseEntity.ok(businessUnitRepository.findAll());
    }

    @GetMapping("/active")
    public ResponseEntity<List<BusinessUnit>> getActiveBusinesses() {
        return ResponseEntity.ok(businessUnitRepository.findByActiveTrue());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BusinessUnit> getBusinessById(@PathVariable Long id) {
        return businessUnitRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<BusinessUnit> createBusiness(@RequestBody BusinessUnit business) {
        BusinessUnit saved = businessUnitRepository.save(business);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BusinessUnit> updateBusiness(
        @PathVariable Long id,
        @RequestBody BusinessUnit business
    ) {
        return businessUnitRepository.findById(id)
            .map(existing -> {
                existing.setName(business.getName());
                existing.setType(business.getType());
                existing.setDescription(business.getDescription());
                existing.setActive(business.getActive());
                return ResponseEntity.ok(businessUnitRepository.save(existing));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBusiness(@PathVariable Long id) {
        if (businessUnitRepository.existsById(id)) {
            businessUnitRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
