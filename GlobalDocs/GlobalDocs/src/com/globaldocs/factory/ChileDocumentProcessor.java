package com.globaldocs.factory;

import com.globaldocs.model.Document;
import com.globaldocs.observer.ProcessingEventManager;
import com.globaldocs.strategy.ChileValidationStrategy;

public class ChileDocumentProcessor extends BaseDocumentProcessor {

    public ChileDocumentProcessor(ProcessingEventManager gestorEventos) {
        super(gestorEventos, new ChileValidationStrategy());
    }

    @Override
    public void applyTaxRules(Document documento) {
        documento.setLogProcesamiento(
            documento.getLogProcesamiento() +
            "\n[SII] IVA 19% aplicado | PPM 0.15% | Impuesto 1a Categoría 27% | RUT validado"
        );
    }

    @Override
    public String generateLog(Document documento) {
        return "[CHILE - SII]\n" + buildBaseLog(documento) +
               "\nEstado SII: " + documento.getEstado().getEtiqueta().toUpperCase() +
               " | DTE: ACEPTADO";
    }

    @Override
    public String getProcessorName() { return "Procesador Chile (SII - DTE)"; }
}
