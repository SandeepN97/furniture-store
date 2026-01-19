package com.samjhana.ventures.controller;

import com.samjhana.ventures.model.EVMeterReading;
import com.samjhana.ventures.repository.EVMeterReadingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/ev-meter")
@RequiredArgsConstructor
public class EVMeterReadingController {

    private final EVMeterReadingRepository meterReadingRepository;

    @GetMapping("/all")
    public ResponseEntity<List<EVMeterReading>> getAllReadings() {
        return ResponseEntity.ok(meterReadingRepository.findAll());
    }

    @GetMapping("/latest")
    public ResponseEntity<EVMeterReading> getLatestReading() {
        return meterReadingRepository.findLatestReading()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<EVMeterReading> getReadingByDate(
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return meterReadingRepository.findByReadingDate(date)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/range")
    public ResponseEntity<List<EVMeterReading>> getReadingsByDateRange(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<EVMeterReading> readings = meterReadingRepository
            .findByReadingDateBetweenOrderByReadingDateDesc(startDate, endDate);
        return ResponseEntity.ok(readings);
    }

    @PostMapping("/add")
    public ResponseEntity<EVMeterReading> addReading(@RequestBody EVMeterReading reading) {
        // Check if reading for this date already exists
        if (meterReadingRepository.findByReadingDate(reading.getReadingDate()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        EVMeterReading saved = meterReadingRepository.save(reading);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EVMeterReading> updateReading(
        @PathVariable Long id,
        @RequestBody EVMeterReading reading
    ) {
        return meterReadingRepository.findById(id)
            .map(existing -> {
                existing.setOpeningReading(reading.getOpeningReading());
                existing.setClosingReading(reading.getClosingReading());
                existing.setRatePerUnit(reading.getRatePerUnit());
                existing.setNotes(reading.getNotes());
                return ResponseEntity.ok(meterReadingRepository.save(existing));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReading(@PathVariable Long id) {
        if (meterReadingRepository.existsById(id)) {
            meterReadingRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
