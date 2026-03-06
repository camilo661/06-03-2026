package com.globaldocs.processor;

import com.globaldocs.factory.DocumentProcessorFactory;
import com.globaldocs.model.Document;
import com.globaldocs.model.ProcessingResult;
import com.globaldocs.observer.ProcessingEventManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles batch processing of multiple documents.
 * Uses DocumentProcessorFactory (Factory Method) to create the right processor per country.
 */
public class BatchProcessor {

    private final ProcessingEventManager gestorEventos;

    public BatchProcessor(ProcessingEventManager gestorEventos) {
        this.gestorEventos = gestorEventos;
    }

    public List<ProcessingResult> processBatch(List<Document> documentos) {
        List<ProcessingResult> resultados = new ArrayList<>();
        int total = documentos.size();

        for (int i = 0; i < total; i++) {
            Document doc = documentos.get(i);
            try {
                DocumentProcessorFactory fabrica =
                    DocumentProcessorFactory.getFactory(doc.getPais(), gestorEventos);
                ProcessingResult resultado = fabrica.processDocument(doc);
                resultados.add(resultado);

                // Notify batch progress
                gestorEventos.notify("BATCH_PROGRESS", doc);

            } catch (Exception e) {
                ProcessingResult error = new ProcessingResult(doc, false);
                error.addMensaje("Error en lote: " + e.getMessage());
                resultados.add(error);
            }
        }
        return resultados;
    }
}
