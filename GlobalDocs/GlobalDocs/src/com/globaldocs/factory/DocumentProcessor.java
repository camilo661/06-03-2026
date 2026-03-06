package com.globaldocs.factory;

import com.globaldocs.model.Document;
import com.globaldocs.model.ProcessingResult;

/**
 * PATTERN: Factory Method - Product Interface
 * All concrete processors must implement this contract.
 */
public interface DocumentProcessor {
    ProcessingResult process(Document documento);
    boolean validate(Document documento);
    void applyTaxRules(Document documento);
    String generateLog(Document documento);
    String getProcessorName();
}
