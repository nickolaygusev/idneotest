package com.idneo.idneotest.domain.exception;

public class SampleAlreadyProcessedException extends RuntimeException {
    public SampleAlreadyProcessedException(String message) {
        super(message);
    }
}
