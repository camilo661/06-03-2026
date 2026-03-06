package com.globaldocs.factory;

import com.globaldocs.model.Document;
import com.globaldocs.observer.ProcessingEventManager;
import com.globaldocs.strategy.ColombiaValidationStrategy;

public class ColombiaDocumentProcessor extends BaseDocumentProcessor {

    public ColombiaDocumentProcessor(ProcessingEventManager gestorEventos) {
        super(gestorEventos, new ColombiaValidationStrategy());
    }

    @Override
    public void applyTaxRules(Document documento) {
        documento.setLogProcesamiento(
            documento.getLogProcesamiento() +
            "\n[DIAN] IVA 19% aplicado | Retención fuente 3.5% calculada | ReteICA 0.414%"
        );
    }

    @Override
    public String generateLog(Document documento) {
        return "[COLOMBIA - DIAN]\n" + buildBaseLog(documento) +
               "\nEstado DIAN: " + documento.getEstado().getEtiqueta().toUpperCase();
    }

    @Override
    public String getProcessorName() { return "Procesador Colombia (DIAN/VUCE)"; }
}
