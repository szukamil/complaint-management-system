package com.recruitment.complaints.service;

public interface LocationService {

    /**
     * Determine the country based on IP address.
     *
     * @param ipAddress The client's IP address
     * @return The country code (e.g., "PL", "US") or "UNKNOWN" if detection fails
     */
    String getCountryFromIp(String ipAddress);
}