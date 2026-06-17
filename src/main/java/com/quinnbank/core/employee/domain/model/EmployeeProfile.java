package com.quinnbank.core.employee.domain.model;

import com.quinnbank.core.employee.domain.exception.EmployeeCreationRejectedException;

public record EmployeeProfile(
        String fullName,
        String workEmail,
        String jobTitle
) {

    public EmployeeProfile {
        if (fullName == null || fullName.isBlank()) {
            throw new EmployeeCreationRejectedException("employee full name is required");
        }
        if (workEmail == null || workEmail.isBlank()) {
            throw new EmployeeCreationRejectedException("employee work email is required");
        }

        fullName = fullName.trim();
        workEmail = normalizeEmail(workEmail);
        jobTitle = normalizeOptional(jobTitle);
    }

    public static String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }

        return email.trim().toLowerCase();
    }

    private static String normalizeOptional(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}
