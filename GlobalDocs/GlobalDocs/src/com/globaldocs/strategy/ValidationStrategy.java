package com.globaldocs.strategy;

import com.globaldocs.model.Document;

/**
 * PATTERN: Strategy
 * Defines the contract for country-specific validation strategies.
 * Each country implements its own validation rules independently.
 */
public interface ValidationStrategy {
    boolean validate(Document documento);
    String getTaxInfo();
    String getRegulationName();
}
