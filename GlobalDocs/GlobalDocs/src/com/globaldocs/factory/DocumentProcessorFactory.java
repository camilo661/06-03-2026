package com.globaldocs.factory;

import com.globaldocs.model.Country;
import com.globaldocs.model.Document;
import com.globaldocs.model.ProcessingResult;
import com.globaldocs.observer.ProcessingEventManager;
import com.globaldocs.strategy.ValidationStrategy;

/**
 * PATTERN: Factory Method - Abstract Creator
 * Defines the factory method createProcessor() that subclasses must implement.
 * Also contains the template method processDocument() with the common workflow.
 */
public abstract class DocumentProcessorFactory {

    protected final ProcessingEventManager gestorEventos;

    public DocumentProcessorFactory(ProcessingEventManager gestorEventos) {
        this.gestorEventos = gestorEventos;
    }

    // ─── Factory Method (must be implemented by each country factory) ───────
    public abstract DocumentProcessor createProcessor();
    public abstract ValidationStrategy createValidationStrategy();
    public abstract Country getCountry();

    // ─── Template method ─────────────────────────────────────────────────────
    public ProcessingResult processDocument(Document documento) {
        DocumentProcessor procesador = createProcessor();
        return procesador.process(documento);
    }

    // ─── Static factory selector ─────────────────────────────────────────────
    public static DocumentProcessorFactory getFactory(Country pais, ProcessingEventManager gestorEventos) {
        return switch (pais) {
            case COLOMBIA  -> new ColombiaProcessorFactory(gestorEventos);
            case MEXICO    -> new MexicoProcessorFactory(gestorEventos);
            case ARGENTINA -> new ArgentinaProcessorFactory(gestorEventos);
            case CHILE     -> new ChileProcessorFactory(gestorEventos);
        };
    }
}
