package com.ems.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class EmployeeCodeGenerator {

    private static final AtomicInteger sequence = new AtomicInteger(1000);

    /**
     * Generates employee code in format: EMP-YYYY-XXXX
     * Example: EMP-2024-1001
     */
    public String generate() {
        int year = LocalDate.now().getYear();
        int seq = sequence.getAndIncrement();
        return String.format("EMP-%d-%04d", year, seq);
    }
}
