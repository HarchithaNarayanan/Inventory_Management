package com.inventory.service;

import com.inventory.dto.CompanyRequestDto;
import com.inventory.dto.CompanyResponseDto;
import com.inventory.entity.Company;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    @Transactional
    public CompanyResponseDto createCompany(CompanyRequestDto request) {
        if (companyRepository.existsByCompanyCodeAndIsActiveTrue(request.getCompanyCode())) {
            throw new IllegalArgumentException("Company with code " + request.getCompanyCode() + " already exists.");
        }

        Company company = Company.builder()
                .companyCode(request.getCompanyCode())
                .companyName(request.getCompanyName())
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .city(request.getCity())
                .pincode(request.getPincode())
                .phoneNo(request.getPhoneNo())
                .emailId(request.getEmailId())
                .website(request.getWebsite())
                .gstNo(request.getGstNo())
                .financialYear(request.getFinancialYear())
                .logoUrl(request.getLogoUrl())
                .status(request.getStatus() != null ? request.getStatus() : "ACTIVE")
                .isActive(true)
                .build();

        Company savedCompany = companyRepository.save(company);
        return mapToDto(savedCompany);
    }

    public List<CompanyResponseDto> getAllCompanies() {
        return companyRepository.findByIsActiveTrue().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<CompanyResponseDto> getPaginatedEntities(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("companyId").descending());
        String query = (search == null || search.trim().isEmpty()) ? null : search.trim();
        return companyRepository.findBySearchAndPagination(query, pageable).map(this::mapToDto);
    }

    public CompanyResponseDto getCompanyById(Long id) {
        Company company = companyRepository.findById(id)
                .filter(Company::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", id));
        return mapToDto(company);
    }

    @Transactional
    public CompanyResponseDto updateCompany(Long id, CompanyRequestDto request) {
        Company company = companyRepository.findById(id)
                .filter(Company::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", id));

        if (!company.getCompanyCode().equals(request.getCompanyCode()) &&
                companyRepository.existsByCompanyCodeAndIsActiveTrue(request.getCompanyCode())) {
            throw new IllegalArgumentException("Company with code " + request.getCompanyCode() + " already exists.");
        }

        company.setCompanyCode(request.getCompanyCode());
        company.setCompanyName(request.getCompanyName());
        company.setAddressLine1(request.getAddressLine1());
        company.setAddressLine2(request.getAddressLine2());
        company.setCity(request.getCity());
        company.setPincode(request.getPincode());
        company.setPhoneNo(request.getPhoneNo());
        company.setEmailId(request.getEmailId());
        company.setWebsite(request.getWebsite());
        company.setGstNo(request.getGstNo());
        company.setFinancialYear(request.getFinancialYear());
        company.setLogoUrl(request.getLogoUrl());
        if (request.getStatus() != null) {
            company.setStatus(request.getStatus());
        }

        Company updatedCompany = companyRepository.save(company);
        return mapToDto(updatedCompany);
    }

    @Transactional
    public void deleteCompany(Long id) {
        Company company = companyRepository.findById(id)
                .filter(Company::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", id));
        company.setActive(false);
        companyRepository.save(company);
    }

    private CompanyResponseDto mapToDto(Company company) {
        return CompanyResponseDto.builder()
                .companyId(company.getCompanyId())
                .companyCode(company.getCompanyCode())
                .companyName(company.getCompanyName())
                .addressLine1(company.getAddressLine1())
                .addressLine2(company.getAddressLine2())
                .city(company.getCity())
                .pincode(company.getPincode())
                .phoneNo(company.getPhoneNo())
                .emailId(company.getEmailId())
                .website(company.getWebsite())
                .gstNo(company.getGstNo())
                .financialYear(company.getFinancialYear())
                .logoUrl(company.getLogoUrl())
                .status(company.getStatus())
                .build();
    }
}
