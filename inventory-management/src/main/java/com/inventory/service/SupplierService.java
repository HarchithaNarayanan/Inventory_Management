package com.inventory.service;

import com.inventory.dto.SupplierRequestDto;
import com.inventory.dto.SupplierResponseDto;
import com.inventory.entity.Supplier;
import com.inventory.exception.DuplicateResourceException;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    public SupplierResponseDto createSupplier(SupplierRequestDto requestDto) {
        if (supplierRepository.existsBySupplierCode(requestDto.getSupplierCode())) {
            throw new DuplicateResourceException("Supplier", "supplierCode", requestDto.getSupplierCode());
        }

        Supplier supplier = mapToEntity(requestDto);
        supplier.setActive(true); // Ensure new suppliers are active by default
        Supplier savedSupplier = supplierRepository.save(supplier);
        return mapToResponseDto(savedSupplier);
    }

    public SupplierResponseDto updateSupplier(Long supplierId, SupplierRequestDto requestDto) {
        Supplier existingSupplier = supplierRepository.findBySupplierIdAndIsActiveTrue(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", supplierId));

        if (supplierRepository.existsBySupplierCodeAndSupplierIdNot(requestDto.getSupplierCode(), supplierId)) {
            throw new DuplicateResourceException("Supplier", "supplierCode", requestDto.getSupplierCode());
        }

        updateEntityFromDto(existingSupplier, requestDto);
        Supplier updatedSupplier = supplierRepository.save(existingSupplier);
        return mapToResponseDto(updatedSupplier);
    }

    public void deleteSupplier(Long supplierId) {
        Supplier supplier = supplierRepository.findBySupplierIdAndIsActiveTrue(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", supplierId));
        
        // Soft delete: set isActive to false
        supplier.setActive(false);
        supplierRepository.save(supplier);
    }

    @Transactional(readOnly = true)
    public SupplierResponseDto getSupplierById(Long supplierId) {
        Supplier supplier = supplierRepository.findBySupplierIdAndIsActiveTrue(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", supplierId));
        return mapToResponseDto(supplier);
    }

    @Transactional(readOnly = true)
    public List<SupplierResponseDto> getAllSuppliers() {
        return supplierRepository.findByIsActiveTrue()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<SupplierResponseDto> getPaginatedEntities(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("supplierId").descending());
        String query = (search == null || search.trim().isEmpty()) ? null : search.trim();
        return supplierRepository.findBySearchAndPagination(query, pageable).map(this::mapToResponseDto);
    }

    private Supplier mapToEntity(SupplierRequestDto dto) {
        return Supplier.builder()
                .supplierCode(dto.getSupplierCode())
                .supplierName(dto.getSupplierName())
                .addressLine1(dto.getAddressLine1())
                .addressLine2(dto.getAddressLine2())
                .city(dto.getCity())
                .pincode(dto.getPincode())
                .type(dto.getType())
                .contactPerson(dto.getContactPerson())
                .phoneNo(dto.getPhoneNo())
                .emailId(dto.getEmailId())
                .gstNo(dto.getGstNo())
                .status(dto.getStatus() != null ? dto.getStatus() : "ACTIVE")
                .isActive(true)
                .build();
    }

    private void updateEntityFromDto(Supplier entity, SupplierRequestDto dto) {
        entity.setSupplierCode(dto.getSupplierCode());
        entity.setSupplierName(dto.getSupplierName());
        entity.setAddressLine1(dto.getAddressLine1());
        entity.setAddressLine2(dto.getAddressLine2());
        entity.setCity(dto.getCity());
        entity.setPincode(dto.getPincode());
        entity.setType(dto.getType());
        entity.setContactPerson(dto.getContactPerson());
        entity.setPhoneNo(dto.getPhoneNo());
        entity.setEmailId(dto.getEmailId());
        entity.setGstNo(dto.getGstNo());
        entity.setStatus(dto.getStatus());
    }

    private SupplierResponseDto mapToResponseDto(Supplier supplier) {
        return SupplierResponseDto.builder()
                .supplierId(supplier.getSupplierId())
                .supplierCode(supplier.getSupplierCode())
                .supplierName(supplier.getSupplierName())
                .addressLine1(supplier.getAddressLine1())
                .addressLine2(supplier.getAddressLine2())
                .city(supplier.getCity())
                .pincode(supplier.getPincode())
                .type(supplier.getType())
                .contactPerson(supplier.getContactPerson())
                .phoneNo(supplier.getPhoneNo())
                .emailId(supplier.getEmailId())
                .gstNo(supplier.getGstNo())
                .status(supplier.getStatus())
                .createdAt(supplier.getCreatedAt())
                .updatedAt(supplier.getUpdatedAt())
                .build();
    }
}