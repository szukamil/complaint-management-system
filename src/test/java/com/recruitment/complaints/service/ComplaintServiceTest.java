package com.recruitment.complaints.service;

import com.recruitment.complaints.dto.ComplaintResponseDTO;
import com.recruitment.complaints.dto.CreateComplaintDTO;
import com.recruitment.complaints.dto.UpdateComplaintDTO;
import com.recruitment.complaints.exception.ComplaintNotFoundException;
import com.recruitment.complaints.model.Complaint;
import com.recruitment.complaints.repository.ComplaintRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComplaintServiceTest {

    @Mock
    private ComplaintRepository complaintRepository;

    @Mock
    private LocationService locationService;

    @InjectMocks
    private ComplaintServiceImpl complaintService;

    private Complaint testComplaint;
    private CreateComplaintDTO createDTO;
    private UpdateComplaintDTO updateDTO;

    @BeforeEach
    void setUp() {
        testComplaint = Complaint.builder()
                .id(1L)
                .productId("test-product")
                .content("Test complaint")
                .reporter("test-user")
                .country("PL")
                .counter(1)
                .createdAt(LocalDateTime.now())
                .build();

        createDTO = new CreateComplaintDTO();
        createDTO.setProductId("test-product");
        createDTO.setContent("Test complaint");
        createDTO.setReporter("test-user");

        updateDTO = new UpdateComplaintDTO();
        updateDTO.setContent("Updated content");
    }

    @Test
    void shouldCreateNewComplaint() {
        // given
        String ipAddress = "127.0.0.1";
        when(locationService.getCountryFromIp(ipAddress)).thenReturn("PL");
        when(complaintRepository.findByProductIdAndReporter(anyString(), anyString())).thenReturn(Optional.empty());
        when(complaintRepository.save(any(Complaint.class))).thenReturn(testComplaint);

        // when
        ComplaintResponseDTO result = complaintService.createComplaint(createDTO, ipAddress);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getProductId()).isEqualTo("test-product");
        assertThat(result.getReporter()).isEqualTo("test-user");
        assertThat(result.getCountry()).isEqualTo("PL");
        assertThat(result.getCounter()).isEqualTo(1);

        verify(locationService).getCountryFromIp(ipAddress);
        verify(complaintRepository).findByProductIdAndReporter("test-product", "test-user");
        verify(complaintRepository).save(any(Complaint.class));
    }

    @Test
    void shouldIncrementCounterForExistingComplaint() {
        // given
        String ipAddress = "127.0.0.1";
        Complaint existingComplaint = Complaint.builder()
                .id(1L)
                .productId("test-product")
                .content("Existing content")
                .reporter("test-user")
                .country("PL")
                .counter(1)
                .createdAt(LocalDateTime.now())
                .build();

        Complaint updatedComplaint = Complaint.builder()
                .id(1L)
                .productId("test-product")
                .content("Existing content")
                .reporter("test-user")
                .country("PL")
                .counter(2) // Incremented
                .createdAt(existingComplaint.getCreatedAt())
                .build();

        when(complaintRepository.findByProductIdAndReporter("test-product", "test-user"))
                .thenReturn(Optional.of(existingComplaint));
        when(complaintRepository.save(any(Complaint.class))).thenReturn(updatedComplaint);

        // when
        ComplaintResponseDTO result = complaintService.createComplaint(createDTO, ipAddress);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getCounter()).isEqualTo(2);
        assertThat(result.getContent()).isEqualTo("Existing content");

        verify(complaintRepository).findByProductIdAndReporter("test-product", "test-user");
        verify(complaintRepository).save(any(Complaint.class));
        verify(locationService, never()).getCountryFromIp(anyString());
    }

    @Test
    void shouldUpdateComplaintContent() {
        // given
        when(complaintRepository.findById(1L)).thenReturn(Optional.of(testComplaint));

        Complaint updatedComplaint = Complaint.builder()
                .id(1L)
                .productId("test-product")
                .content("Updated content") // Updated content
                .reporter("test-user")
                .country("PL")
                .counter(1)
                .createdAt(testComplaint.getCreatedAt())
                .build();

        when(complaintRepository.save(any(Complaint.class))).thenReturn(updatedComplaint);

        // when
        ComplaintResponseDTO result = complaintService.updateComplaint(1L, updateDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("Updated content");

        verify(complaintRepository).findById(1L);
        verify(complaintRepository).save(any(Complaint.class));
    }

    @Test
    void shouldThrowExceptionWhenComplaintNotFound() {
        // given
        when(complaintRepository.findById(999L)).thenReturn(Optional.empty());

        // when, then
        assertThrows(ComplaintNotFoundException.class, () ->
                complaintService.updateComplaint(999L, updateDTO));

        verify(complaintRepository).findById(999L);
        verify(complaintRepository, never()).save(any(Complaint.class));
    }

    @Test
    void shouldGetComplaintById() {
        // given
        when(complaintRepository.findById(1L)).thenReturn(Optional.of(testComplaint));

        // when
        ComplaintResponseDTO result = complaintService.getComplaintById(1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);

        verify(complaintRepository).findById(1L);
    }

    @Test
    void shouldGetAllComplaints() {
        // given
        Complaint secondComplaint = Complaint.builder()
                .id(2L)
                .productId("product-2")
                .content("Second complaint")
                .reporter("user-2")
                .country("US")
                .counter(1)
                .createdAt(LocalDateTime.now())
                .build();

        when(complaintRepository.findAll()).thenReturn(List.of(testComplaint, secondComplaint));

        // when
        List<ComplaintResponseDTO> result = complaintService.getAllComplaints();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(1).getId()).isEqualTo(2L);

        verify(complaintRepository).findAll();
    }
}