package com.recruitment.complaints.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recruitment.complaints.config.SecurityTestConfig;
import com.recruitment.complaints.dto.ComplaintResponseDTO;
import com.recruitment.complaints.dto.CreateComplaintDTO;
import com.recruitment.complaints.dto.UpdateComplaintDTO;
import com.recruitment.complaints.exception.ComplaintNotFoundException;
import com.recruitment.complaints.service.ComplaintService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ComplaintController.class)
@Import(SecurityTestConfig.class)
@WithMockUser(roles = {"USER"})
class ComplaintControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ComplaintService complaintService;

    @Test
    void shouldCreateComplaint() throws Exception {
        CreateComplaintDTO createDTO = new CreateComplaintDTO();
        createDTO.setProductId("test-product");
        createDTO.setContent("Test complaint");
        createDTO.setReporter("test-user");

        ComplaintResponseDTO responseDTO = ComplaintResponseDTO.builder()
                .id(1L)
                .productId("test-product")
                .content("Test complaint")
                .reporter("test-user")
                .country("PL")
                .counter(1)
                .createdAt(LocalDateTime.now())
                .build();

        when(complaintService.createComplaint(any(CreateComplaintDTO.class), anyString())).thenReturn(responseDTO);

        // when & then
        mockMvc.perform(post("/api/complaints")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.productId", is("test-product")))
                .andExpect(jsonPath("$.content", is("Test complaint")))
                .andExpect(jsonPath("$.reporter", is("test-user")))
                .andExpect(jsonPath("$.country", is("PL")))
                .andExpect(jsonPath("$.counter", is(1)));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldUpdateComplaint() throws Exception {
        // given
        UpdateComplaintDTO updateDTO = new UpdateComplaintDTO();
        updateDTO.setContent("Updated content");

        ComplaintResponseDTO responseDTO = ComplaintResponseDTO.builder()
                .id(1L)
                .productId("test-product")
                .content("Updated content")
                .reporter("test-user")
                .country("PL")
                .counter(1)
                .createdAt(LocalDateTime.now())
                .build();

        when(complaintService.updateComplaint(anyLong(), any(UpdateComplaintDTO.class))).thenReturn(responseDTO);

        // when & then
        mockMvc.perform(put("/api/complaints/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.content", is("Updated content")));
    }

    @Test
    void shouldGetComplaintById() throws Exception {
        // given
        ComplaintResponseDTO responseDTO = ComplaintResponseDTO.builder()
                .id(1L)
                .productId("test-product")
                .content("Test complaint")
                .reporter("test-user")
                .country("PL")
                .counter(1)
                .createdAt(LocalDateTime.now())
                .build();

        when(complaintService.getComplaintById(1L)).thenReturn(responseDTO);

        // when & then
        mockMvc.perform(get("/api/complaints/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.productId", is("test-product")));
    }

    @Test
    void shouldGetAllComplaints() throws Exception {
        // given
        ComplaintResponseDTO complaint1 = ComplaintResponseDTO.builder()
                .id(1L)
                .productId("product-1")
                .content("Complaint 1")
                .reporter("user-1")
                .country("PL")
                .counter(1)
                .createdAt(LocalDateTime.now())
                .build();

        ComplaintResponseDTO complaint2 = ComplaintResponseDTO.builder()
                .id(2L)
                .productId("product-2")
                .content("Complaint 2")
                .reporter("user-2")
                .country("US")
                .counter(3)
                .createdAt(LocalDateTime.now())
                .build();

        when(complaintService.getAllComplaints()).thenReturn(List.of(complaint1, complaint2));

        // when & then
        mockMvc.perform(get("/api/complaints"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));
    }

    @Test
    void shouldReturn404WhenComplaintNotFound() throws Exception {
        // given
        when(complaintService.getComplaintById(999L)).thenThrow(new ComplaintNotFoundException(999L));

        // when & then
        mockMvc.perform(get("/api/complaints/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldValidateCreateComplaintDTO() throws Exception {
        // given
        CreateComplaintDTO invalidDTO = new CreateComplaintDTO();

        // when & then
        mockMvc.perform(post("/api/complaints")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void shouldReturn403WhenUserWithoutAdminRoleTryingToUpdateComplaint() throws Exception {
        // given
        UpdateComplaintDTO updateDTO = new UpdateComplaintDTO();
        updateDTO.setContent("Updated content");

        // when & then
        mockMvc.perform(put("/api/complaints/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {})
    void shouldReturn401WhenUnauthenticatedUserAccessesProtectedEndpoint() throws Exception {
        // when & then
        mockMvc.perform(get("/api/complaints"))
                .andExpect(status().isForbidden());
    }
}