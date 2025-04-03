package com.recruitment.complaints.service;

import com.recruitment.complaints.dto.ComplaintResponseDTO;
import com.recruitment.complaints.dto.CreateComplaintDTO;
import com.recruitment.complaints.dto.UpdateComplaintDTO;
import com.recruitment.complaints.exception.ComplaintNotFoundException;
import com.recruitment.complaints.model.Complaint;
import com.recruitment.complaints.repository.ComplaintRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import io.micrometer.core.annotation.Timed;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final LocationService locationService;

    @CacheEvict(value = "complaints", allEntries = true)
    @Timed(value = "complaint.create", description = "Time taken to create a complaint")
    @Override
    @Transactional
    public ComplaintResponseDTO createComplaint(CreateComplaintDTO createComplaintDTO, String ipAddress) {
        log.info("Creating complaint for product ID: {} by reporter: {}",
                createComplaintDTO.getProductId(), createComplaintDTO.getReporter());

        return complaintRepository.findByProductIdAndReporter(
                createComplaintDTO.getProductId(),
                createComplaintDTO.getReporter()
        ).map(existingComplaint -> {
            log.info("Complaint already exists, incrementing counter. Current count: {}",
                    existingComplaint.getCounter());
            existingComplaint.incrementCounter();
            return mapToDTO(complaintRepository.save(existingComplaint));
        }).orElseGet(() -> {
            String country = locationService.getCountryFromIp(ipAddress);
            log.debug("Detected country: {} for IP: {}", country, ipAddress);

            Complaint newComplaint = Complaint.builder()
                    .productId(createComplaintDTO.getProductId())
                    .content(createComplaintDTO.getContent())
                    .reporter(createComplaintDTO.getReporter())
                    .country(country)
                    .counter(1)
                    .build();

            return mapToDTO(complaintRepository.save(newComplaint));
        });
    }

    @CachePut(value = "complaints", key = "#id")
    @Override
    @Transactional
    public ComplaintResponseDTO updateComplaint(Long id, UpdateComplaintDTO updateComplaintDTO) {
        log.info("Updating complaint with ID: {}", id);

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ComplaintNotFoundException(id));

        complaint.setContent(updateComplaintDTO.getContent());

        return mapToDTO(complaintRepository.save(complaint));
    }

    @Cacheable(value = "complaints", key = "#id")
    @Override
    public ComplaintResponseDTO getComplaintById(Long id) {
        log.info("Fetching complaint with ID: {}", id);

        return complaintRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ComplaintNotFoundException(id));
    }

    @Override
    public List<ComplaintResponseDTO> getAllComplaints() {
        log.info("Fetching all complaints");

        return complaintRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private ComplaintResponseDTO mapToDTO(Complaint complaint) {
        return ComplaintResponseDTO.builder()
                .id(complaint.getId())
                .productId(complaint.getProductId())
                .content(complaint.getContent())
                .createdAt(complaint.getCreatedAt())
                .reporter(complaint.getReporter())
                .country(complaint.getCountry())
                .counter(complaint.getCounter())
                .build();
    }

    @Override
    public Page<ComplaintResponseDTO> getPaginatedComplaints(Pageable pageable) {
        log.info("Fetching paginated complaints with page: {}, size: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        return complaintRepository.findAll(pageable)
                .map(this::mapToDTO);
    }
}
