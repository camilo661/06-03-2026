package com.globaldocs.factory;

import com.globaldocs.model.Country;
import com.globaldocs.observer.ProcessingEventManager;
import com.globaldocs.strategy.*;

// ══════════════════════════════════════════════════════════════
//  Concrete Factory – Colombia
// ══════════════════════════════════════════════════════════════
class ColombiaProcessorFactory extends DocumentProcessorFactory {
    public ColombiaProcessorFactory(ProcessingEventManager gestorEventos) { super(gestorEventos); }
    @Override public DocumentProcessor createProcessor() { return new ColombiaDocumentProcessor(gestorEventos); }
    @Override public ValidationStrategy createValidationStrategy() { return new ColombiaValidationStrategy(); }
    @Override public Country getCountry() { return Country.COLOMBIA; }
}

// ══════════════════════════════════════════════════════════════
//  Concrete Factory – Mexico
// ══════════════════════════════════════════════════════════════
class MexicoProcessorFactory extends DocumentProcessorFactory {
    public MexicoProcessorFactory(ProcessingEventManager gestorEventos) { super(gestorEventos); }
    @Override public DocumentProcessor createProcessor() { return new MexicoDocumentProcessor(gestorEventos); }
    @Override public ValidationStrategy createValidationStrategy() { return new MexicoValidationStrategy(); }
    @Override public Country getCountry() { return Country.MEXICO; }
}

// ══════════════════════════════════════════════════════════════
//  Concrete Factory – Argentina
// ══════════════════════════════════════════════════════════════
class ArgentinaProcessorFactory extends DocumentProcessorFactory {
    public ArgentinaProcessorFactory(ProcessingEventManager gestorEventos) { super(gestorEventos); }
    @Override public DocumentProcessor createProcessor() { return new ArgentinaDocumentProcessor(gestorEventos); }
    @Override public ValidationStrategy createValidationStrategy() { return new ArgentinaValidationStrategy(); }
    @Override public Country getCountry() { return Country.ARGENTINA; }
}

// ══════════════════════════════════════════════════════════════
//  Concrete Factory – Chile
// ══════════════════════════════════════════════════════════════
class ChileProcessorFactory extends DocumentProcessorFactory {
    public ChileProcessorFactory(ProcessingEventManager gestorEventos) { super(gestorEventos); }
    @Override public DocumentProcessor createProcessor() { return new ChileDocumentProcessor(gestorEventos); }
    @Override public ValidationStrategy createValidationStrategy() { return new ChileValidationStrategy(); }
    @Override public Country getCountry() { return Country.CHILE; }
}
