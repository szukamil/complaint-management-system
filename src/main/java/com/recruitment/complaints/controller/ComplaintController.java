package com.recruitment.complaints.controller;

import com.recruitment.complaints.dto.ComplaintResponseDTO;
import com.recruitment.complaints.dto.CreateComplaintDTO;
import com.recruitment.complaints.dto.UpdateComplaintDTO;
import com.recruitment.complaints.service.ComplaintService;
import com.recruitment.complaints.util.IPUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;

    @PostMapping
    public ResponseEntity<ComplaintResponseDTO> createComplaint(
            @Valid @RequestBody CreateComplaintDTO createComplaintDTO,
            HttpServletRequest request) {
        log.info("Received request to create complaint for product ID: {}", createComplaintDTO.getProductId());

        String ipAddress = IPUtils.getClientIp(request);
        log.debug("Client IP address: {}", ipAddress);

        ComplaintResponseDTO createdComplaint = complaintService.createComplaint(createComplaintDTO, ipAddress);
        return new ResponseEntity<>(createdComplaint, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ComplaintResponseDTO> updateComplaint(
            @PathVariable Long id,
            @Valid @RequestBody UpdateComplaintDTO updateComplaintDTO) {
        log.info("Received request to update complaint with ID: {}", id);

        ComplaintResponseDTO updatedComplaint = complaintService.updateComplaint(id, updateComplaintDTO);
        return ResponseEntity.ok(updatedComplaint);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComplaintResponseDTO> getComplaintById(@PathVariable Long id) {
        log.info("Received request to get complaint with ID: {}", id);

        ComplaintResponseDTO complaint = complaintService.getComplaintById(id);
        return ResponseEntity.ok(complaint);
    }

    @GetMapping
    public ResponseEntity<List<ComplaintResponseDTO>> getAllComplaints() {
        log.info("Received request to get all complaints");

        List<ComplaintResponseDTO> complaints = complaintService.getAllComplaints();
        return ResponseEntity.ok(complaints);
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<ComplaintResponseDTO>> getPaginatedComplaints(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "ASC") String direction) {

        log.info("Received request to get paginated complaints: page={}, size={}, sort={}, direction={}",
                page, size, sort, direction);

        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        Page<ComplaintResponseDTO> complaints = complaintService.getPaginatedComplaints(pageable);
        return ResponseEntity.ok(complaints);
    }
}
