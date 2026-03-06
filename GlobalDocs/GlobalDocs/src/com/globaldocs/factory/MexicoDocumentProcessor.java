package com.globaldocs.factory;

import com.globaldocs.model.Document;
import com.globaldocs.observer.ProcessingEventManager;
import com.globaldocs.strategy.MexicoValidationStrategy;

public class MexicoDocumentProcessor extends BaseDocumentProcessor {

    public MexicoDocumentProcessor(ProcessingEventManager gestorEventos) {
        super(gestorEventos, new MexicoValidationStrategy());
    }

    @Override
    public void applyTaxRules(Document documento) {
        documento.setLogProcesamiento(
            documento.getLogProcesamiento() +
            "\n[SAT] IVA 16% timbrado | ISR 30% retenido | CFDI 4.0 sellado"
        );
    }

    @Override
    public String generateLog(Document documento) {
        return "[MEXICO - SAT/ANAM]\n" + buildBaseLog(documento) +
               "\nEstado SAT: " + documento.getEstado().getEtiqueta().toUpperCase() +
               " | CFDI: TIMBRADO";
    }

    @Override
    public String getProcessorName() { return "Procesador México (SAT/ANAM - CFDI 4.0)"; }
}
