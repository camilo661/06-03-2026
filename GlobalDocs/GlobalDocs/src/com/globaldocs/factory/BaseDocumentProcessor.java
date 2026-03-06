package com.globaldocs.factory;

import com.globaldocs.model.Document;
import com.globaldocs.model.ProcessingResult;
import com.globaldocs.model.ProcessingStatus;
import com.globaldocs.observer.ProcessingEventManager;
import com.globaldocs.strategy.ValidationStrategy;

/**
 * PATTERN: Factory Method + Template Method
 * Base processor with shared pipeline. Country subclasses override specifics.
 */
public abstract class BaseDocumentProcessor implements DocumentProcessor {

    protected final ProcessingEventManager gestorEventos;
    protected final ValidationStrategy estrategiaValidacion;

    public BaseDocumentProcessor(ProcessingEventManager gestorEventos,
                                  ValidationStrategy estrategiaValidacion) {
        this.gestorEventos = gestorEventos;
        this.estrategiaValidacion = estrategiaValidacion;
    }

    @Override
    public ProcessingResult process(Document documento) {
        long tiempoInicio = System.currentTimeMillis();
        ProcessingResult resultado = new ProcessingResult(documento, false);

        try {
            // Step 1 – Start
            documento.setEstado(ProcessingStatus.PROCESSING);
            gestorEventos.notify("STATUS_CHANGE", documento);
            resultado.addMensaje("Iniciando: " + getProcessorName());
            pause(150);

            // Step 2 – Validate
            if (!validate(documento)) {
                documento.setEstado(ProcessingStatus.REJECTED);
                documento.setTiempoProcesamiento(System.currentTimeMillis() - tiempoInicio);
                gestorEventos.notify("ERROR", documento);
                ProcessingResult rechazado = new ProcessingResult(documento, false);
                rechazado.addMensaje("Iniciando: " + getProcessorName());
                rechazado.addAdvertencia("Formato '" + documento.getFormato() +
                        "' NO permitido para '" + documento.getTipo().getDisplayName() +
                        "' en " + documento.getPais().getNombre());
                rechazado.addMensaje("Documento RECHAZADO por reglas de: " +
                        estrategiaValidacion.getRegulationName());
                documento.setLogProcesamiento(generateLog(documento));
                return rechazado;
            }

            documento.setEstado(ProcessingStatus.VALIDATED);
            gestorEventos.notify("STATUS_CHANGE", documento);
            resultado.addMensaje("Validación OK — " + estrategiaValidacion.getRegulationName());
            pause(200);

            // Step 3 – Tax rules
            applyTaxRules(documento);
            resultado.addMensaje("Reglas tributarias: " + estrategiaValidacion.getTaxInfo());
            pause(150);

            // Step 4 – Generate log
            documento.setLogProcesamiento(generateLog(documento));
            resultado.addMensaje("Log generado por procesador del país");
            pause(100);

            // Step 5 – Done
            documento.setEstado(ProcessingStatus.COMPLETED);
            documento.setTiempoProcesamiento(System.currentTimeMillis() - tiempoInicio);
            gestorEventos.notify("COMPLETED", documento);
            resultado.addMensaje("Completado en " + documento.getTiempoProcesamiento() + " ms");

            ProcessingResult exitoso = new ProcessingResult(documento, true);
            resultado.getMensajes().forEach(exitoso::addMensaje);
            resultado.getAdvertencias().forEach(exitoso::addAdvertencia);
            return exitoso;

        } catch (Exception e) {
            documento.setEstado(ProcessingStatus.ERROR);
            documento.setTiempoProcesamiento(System.currentTimeMillis() - tiempoInicio);
            gestorEventos.notify("ERROR", documento);
            ProcessingResult error = new ProcessingResult(documento, false);
            error.addMensaje("Excepción inesperada: " + e.getMessage());
            return error;
        }
    }

    @Override
    public boolean validate(Document documento) {
        return estrategiaValidacion.validate(documento);
    }

    protected String buildBaseLog(Document doc) {
        return String.format(
            "ID: %s | Nombre: %s\nTipo: %s | Formato: %s\nPaís: %s | Procesador: %s\nRegulación: %s\nImpuestos: %s",
            doc.getId(), doc.getNombre(),
            doc.getTipo().getDisplayName(), doc.getFormato(),
            doc.getPais().getNombre(), getProcessorName(),
            estrategiaValidacion.getRegulationName(),
            estrategiaValidacion.getTaxInfo()
        );
    }

    private void pause(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
