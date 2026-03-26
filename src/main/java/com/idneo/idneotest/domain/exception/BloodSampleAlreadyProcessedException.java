package com.idneo.idneotest.domain.exception;

public class BloodSampleAlreadyProcessedException extends RuntimeException {
    public BloodSampleAlreadyProcessedException(String message) {
        super(message);
    }
}
