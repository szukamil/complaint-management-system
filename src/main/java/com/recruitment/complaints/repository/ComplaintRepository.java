package com.recruitment.complaints.repository;

import com.recruitment.complaints.model.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    Optional<Complaint> findByProductIdAndReporter(String productId, String reporter);

    boolean existsByProductIdAndReporter(String productId, String reporter);
}