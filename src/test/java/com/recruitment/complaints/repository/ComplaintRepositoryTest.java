package com.recruitment.complaints.repository;

import com.recruitment.complaints.model.Complaint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ComplaintRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private ComplaintRepository complaintRepository;

    @Test
    void shouldSaveComplaint() {
        // given
        Complaint complaint = Complaint.builder()
                .productId("test-product")
                .content("Test complaint content")
                .reporter("test-user")
                .country("PL")
                .counter(1)
                .createdAt(LocalDateTime.now())
                .build();

        // when
        Complaint savedComplaint = complaintRepository.save(complaint);

        // then
        assertThat(savedComplaint).isNotNull();
        assertThat(savedComplaint.getId()).isNotNull();
        assertThat(savedComplaint.getProductId()).isEqualTo("test-product");
    }

    @Test
    void shouldFindByProductIdAndReporter() {
        // given
        Complaint complaint = Complaint.builder()
                .productId("test-product")
                .content("Test complaint content")
                .reporter("test-user")
                .country("PL")
                .counter(1)
                .createdAt(LocalDateTime.now())
                .build();
        complaintRepository.save(complaint);

        // when
        Optional<Complaint> foundComplaint = complaintRepository.findByProductIdAndReporter("test-product", "test-user");

        // then
        assertThat(foundComplaint).isPresent();
        assertThat(foundComplaint.get().getProductId()).isEqualTo("test-product");
        assertThat(foundComplaint.get().getReporter()).isEqualTo("test-user");
    }

    @Test
    void shouldCheckIfComplaintExists() {
        // given
        Complaint complaint = Complaint.builder()
                .productId("test-product")
                .content("Test complaint content")
                .reporter("test-user")
                .country("PL")
                .counter(1)
                .createdAt(LocalDateTime.now())
                .build();
        complaintRepository.save(complaint);

        // when
        boolean exists = complaintRepository.existsByProductIdAndReporter("test-product", "test-user");
        boolean notExists = complaintRepository.existsByProductIdAndReporter("non-existent", "test-user");

        // then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }
}
