package com.recruitment.complaints.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Slf4j
@Service
public class LocationServiceImpl implements LocationService {

    private final Random random = new Random();
    private final Map<String, String> ipCountryMap = new HashMap<>();
    private final String[] countryCodes = {"PL", "US", "DE", "FR", "GB", "ES", "IT", "CA", "JP", "AU"};

    public LocationServiceImpl() {
        ipCountryMap.put("127.0.0.1", "LOCAL");
        ipCountryMap.put("0.0.0.0", "DEMO");
        ipCountryMap.put("192.168.1.1", "PL");
        ipCountryMap.put("8.8.8.8", "US");
        ipCountryMap.put("8.8.4.4", "US");
        log.info("Initialized simplified LocationService with demo data");
    }

    @Cacheable(value = "countries", key = "#ipAddress")
    @Override
    public String getCountryFromIp(String ipAddress) {
        log.debug("Getting country for IP: {}", ipAddress);
        if (ipCountryMap.containsKey(ipAddress)) {
            String country = ipCountryMap.get(ipAddress);
            log.debug("Found predefined country for IP {}: {}", ipAddress, country);
            return country;
        }
        int index = Math.abs(ipAddress.hashCode() % countryCodes.length);
        String country = countryCodes[index];

        log.debug("Generated country for IP {}: {}", ipAddress, country);
        return country;
    }
}