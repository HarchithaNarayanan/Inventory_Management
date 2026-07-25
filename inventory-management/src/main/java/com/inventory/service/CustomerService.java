package com.inventory.service;

import com.inventory.dto.CustomerRequestDto;
import com.inventory.dto.CustomerResponseDto;
import com.inventory.entity.Customer;
import com.inventory.exception.DuplicateResourceException;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public CustomerResponseDto createCustomer(CustomerRequestDto requestDto) {
        if (customerRepository.existsByCustomerCode(requestDto.getCustomerCode())) {
            throw new DuplicateResourceException("Customer", "customerCode", requestDto.getCustomerCode());
        }
        Customer customer = mapToEntity(requestDto);
        customer.setActive(true);
        Customer saved = customerRepository.save(customer);
        return mapToResponseDto(saved);
    }

    public CustomerResponseDto updateCustomer(Long customerId, CustomerRequestDto requestDto) {
        Customer existing = customerRepository.findByCustomerIdAndIsActiveTrue(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

        if (customerRepository.existsByCustomerCodeAndCustomerIdNot(requestDto.getCustomerCode(), customerId)) {
            throw new DuplicateResourceException("Customer", "customerCode", requestDto.getCustomerCode());
        }

        updateEntityFromDto(existing, requestDto);
        return mapToResponseDto(customerRepository.save(existing));
    }

    public void deleteCustomer(Long customerId) {
        Customer existing = customerRepository.findByCustomerIdAndIsActiveTrue(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));
        existing.setActive(false);
        customerRepository.save(existing);
    }

    @Transactional(readOnly = true)
    public CustomerResponseDto getCustomerById(Long customerId) {
        return mapToResponseDto(customerRepository.findByCustomerIdAndIsActiveTrue(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId)));
    }

    @Transactional(readOnly = true)
    public List<CustomerResponseDto> getAllCustomers() {
        return customerRepository.findByIsActiveTrue().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<CustomerResponseDto> getPaginatedEntities(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("customerId").descending());
        String query = (search == null || search.trim().isEmpty()) ? null : search.trim();
        return customerRepository.findBySearchAndPagination(query, pageable).map(this::mapToResponseDto);
    }

    private Customer mapToEntity(CustomerRequestDto dto) {
        return Customer.builder()
                .customerCode(dto.getCustomerCode())
                .customerName(dto.getCustomerName())
                .addressLine1(dto.getAddressLine1())
                .addressLine2(dto.getAddressLine2())
                .city(dto.getCity())
                .pincode(dto.getPincode())
                .contactPerson(dto.getContactPerson())
                .phoneNo(dto.getPhoneNo())
                .emailId(dto.getEmailId())
                .gstNo(dto.getGstNo())
                .type(dto.getType())
                .creditDays(dto.getCreditDays())
                .creditAmount(dto.getCreditAmount() != null ? dto.getCreditAmount() : BigDecimal.ZERO)
                .openingBalance(dto.getOpeningBalance() != null ? dto.getOpeningBalance() : BigDecimal.ZERO)
                .status(dto.getStatus() != null ? dto.getStatus() : "ACTIVE")
                .isActive(true)
                .build();
    }

    private void updateEntityFromDto(Customer entity, CustomerRequestDto dto) {
        entity.setCustomerCode(dto.getCustomerCode());
        entity.setCustomerName(dto.getCustomerName());
        entity.setAddressLine1(dto.getAddressLine1());
        entity.setAddressLine2(dto.getAddressLine2());
        entity.setCity(dto.getCity());
        entity.setPincode(dto.getPincode());
        entity.setContactPerson(dto.getContactPerson());
        entity.setPhoneNo(dto.getPhoneNo());
        entity.setEmailId(dto.getEmailId());
        entity.setGstNo(dto.getGstNo());
        entity.setType(dto.getType());
        entity.setCreditDays(dto.getCreditDays());
        if (dto.getCreditAmount() != null) entity.setCreditAmount(dto.getCreditAmount());
        if (dto.getOpeningBalance() != null) entity.setOpeningBalance(dto.getOpeningBalance());
        entity.setStatus(dto.getStatus());
    }

    private CustomerResponseDto mapToResponseDto(Customer c) {
        return CustomerResponseDto.builder()
                .customerId(c.getCustomerId())
                .customerCode(c.getCustomerCode())
                .customerName(c.getCustomerName())
                .addressLine1(c.getAddressLine1())
                .addressLine2(c.getAddressLine2())
                .city(c.getCity())
                .pincode(c.getPincode())
                .contactPerson(c.getContactPerson())
                .phoneNo(c.getPhoneNo())
                .emailId(c.getEmailId())
                .gstNo(c.getGstNo())
                .type(c.getType())
                .creditDays(c.getCreditDays())
                .creditAmount(c.getCreditAmount())
                .openingBalance(c.getOpeningBalance())
                .status(c.getStatus())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}
