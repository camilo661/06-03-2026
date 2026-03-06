package com.globaldocs.factory;

import com.globaldocs.model.Document;
import com.globaldocs.observer.ProcessingEventManager;
import com.globaldocs.strategy.ArgentinaValidationStrategy;

public class ArgentinaDocumentProcessor extends BaseDocumentProcessor {

    public ArgentinaDocumentProcessor(ProcessingEventManager gestorEventos) {
        super(gestorEventos, new ArgentinaValidationStrategy());
    }

    @Override
    public void applyTaxRules(Document documento) {
        documento.setLogProcesamiento(
            documento.getLogProcesamiento() +
            "\n[AFIP] IVA 21% registrado | Ganancias 35% | Ingresos Brutos 3% | CUIT validado"
        );
    }

    @Override
    public String generateLog(Document documento) {
        return "[ARGENTINA - AFIP]\n" + buildBaseLog(documento) +
               "\nEstado AFIP: " + documento.getEstado().getEtiqueta().toUpperCase() +
               " | Comprobante: AUTORIZADO";
    }

    @Override
    public String getProcessorName() { return "Procesador Argentina (AFIP - CUIT)"; }
}
