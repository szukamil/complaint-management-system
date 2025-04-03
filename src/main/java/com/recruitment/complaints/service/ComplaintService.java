package com.recruitment.complaints.service;

import com.recruitment.complaints.dto.ComplaintResponseDTO;
import com.recruitment.complaints.dto.CreateComplaintDTO;
import com.recruitment.complaints.dto.UpdateComplaintDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ComplaintService {

    /**
     * Create a new complaint or increment counter if it already exists
     *
     * @param createComplaintDTO Data for creating a complaint
     * @param ipAddress The IP address of the client
     * @return The created or updated complaint
     */
    ComplaintResponseDTO createComplaint(CreateComplaintDTO createComplaintDTO, String ipAddress);

    /**
     * Update the content of an existing complaint
     *
     * @param id The ID of the complaint to update
     * @param updateComplaintDTO The data to update the complaint with
     * @return The updated complaint
     */
    ComplaintResponseDTO updateComplaint(Long id, UpdateComplaintDTO updateComplaintDTO);

    /**
     * Get a complaint by ID
     *
     * @param id The ID of the complaint
     * @return The complaint
     */
    ComplaintResponseDTO getComplaintById(Long id);

    /**
     * Get all complaints
     *
     * @return A list of all complaints
     */
    List<ComplaintResponseDTO> getAllComplaints();

    /**
     * Get paginated complaints
     *
     * @param pageable Pagination information
     * @return Page of complaints
     */
    Page<ComplaintResponseDTO> getPaginatedComplaints(Pageable pageable);
}
