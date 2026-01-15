package com.example.furniturestore.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.example.furniturestore.model.ShippingAddress;
import com.example.furniturestore.model.User;
import com.example.furniturestore.repository.ShippingAddressRepository;
import com.example.furniturestore.security.UserPrincipal;

@RestController
@RequestMapping("/api/addresses")
public class ShippingAddressController {

    private final ShippingAddressRepository addressRepository;

    public ShippingAddressController(ShippingAddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public static class AddressRequest {
        public String fullName;
        public String addressLine1;
        public String addressLine2;
        public String city;
        public String state;
        public String zipCode;
        public String country;
        public String phoneNumber;
        public Boolean isDefault;
    }

    @GetMapping
    public ResponseEntity<?> getUserAddresses(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Authentication required"));
        }

        List<ShippingAddress> addresses = addressRepository.findByUserId(userPrincipal.getId());
        return ResponseEntity.ok(addresses);
    }

    @PostMapping
    public ResponseEntity<?> createAddress(@RequestBody AddressRequest request,
                                          @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Authentication required"));
        }

        User user = new User();
        user.setId(userPrincipal.getId());

        ShippingAddress address = new ShippingAddress();
        address.setUser(user);
        address.setFullName(request.fullName);
        address.setAddressLine1(request.addressLine1);
        address.setAddressLine2(request.addressLine2);
        address.setCity(request.city);
        address.setState(request.state);
        address.setZipCode(request.zipCode);
        address.setCountry(request.country);
        address.setPhoneNumber(request.phoneNumber);

        // If this is set as default, unset other defaults
        if (request.isDefault != null && request.isDefault) {
            List<ShippingAddress> userAddresses = addressRepository.findByUserId(userPrincipal.getId());
            userAddresses.forEach(addr -> {
                addr.setIsDefault(false);
                addressRepository.save(addr);
            });
        }

        address.setIsDefault(request.isDefault != null && request.isDefault);

        ShippingAddress saved = addressRepository.save(address);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAddress(@PathVariable Long id,
                                          @RequestBody AddressRequest request,
                                          @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Authentication required"));
        }

        return addressRepository.findById(id).map(address -> {
            if (!address.getUser().getId().equals(userPrincipal.getId())) {
                return ResponseEntity.status(403).body(Map.of("error", "Not authorized"));
            }

            address.setFullName(request.fullName);
            address.setAddressLine1(request.addressLine1);
            address.setAddressLine2(request.addressLine2);
            address.setCity(request.city);
            address.setState(request.state);
            address.setZipCode(request.zipCode);
            address.setCountry(request.country);
            address.setPhoneNumber(request.phoneNumber);

            // If this is set as default, unset other defaults
            if (request.isDefault != null && request.isDefault) {
                List<ShippingAddress> userAddresses = addressRepository.findByUserId(userPrincipal.getId());
                userAddresses.forEach(addr -> {
                    if (!addr.getId().equals(id)) {
                        addr.setIsDefault(false);
                        addressRepository.save(addr);
                    }
                });
            }

            address.setIsDefault(request.isDefault != null && request.isDefault);

            ShippingAddress updated = addressRepository.save(address);
            return ResponseEntity.ok(updated);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAddress(@PathVariable Long id,
                                          @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Authentication required"));
        }

        return addressRepository.findById(id).map(address -> {
            if (!address.getUser().getId().equals(userPrincipal.getId())) {
                return ResponseEntity.status(403).body(Map.of("error", "Not authorized"));
            }

            addressRepository.delete(address);
            return ResponseEntity.noContent().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/default")
    public ResponseEntity<?> getDefaultAddress(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Authentication required"));
        }

        return addressRepository.findByUserIdAndIsDefaultTrue(userPrincipal.getId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
