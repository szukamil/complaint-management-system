package com.recruitment.complaints.exception;

import org.springframework.http.HttpStatus;

public class ComplaintNotFoundException extends ApiException {

    public ComplaintNotFoundException(Long id) {
        super("Complaint not found with id: " + id, HttpStatus.NOT_FOUND);
    }
}
