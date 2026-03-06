package com.globaldocs.model;

import java.util.ArrayList;
import java.util.List;

public class ProcessingResult {
    private final Document documento;
    private final boolean exitoso;
    private final List<String> mensajes;
    private final List<String> advertencias;

    public ProcessingResult(Document documento, boolean exitoso) {
        this.documento = documento;
        this.exitoso = exitoso;
        this.mensajes = new ArrayList<>();
        this.advertencias = new ArrayList<>();
    }

    public void addMensaje(String mensaje) { mensajes.add(mensaje); }
    public void addAdvertencia(String advertencia) { advertencias.add(advertencia); }

    public Document getDocumento() { return documento; }
    public boolean isExitoso() { return exitoso; }
    public List<String> getMensajes() { return mensajes; }
    public List<String> getAdvertencias() { return advertencias; }

    public String getResumen() {
        StringBuilder sb = new StringBuilder();
        sb.append("════════════════════════════════════════\n");
        sb.append("   RESULTADO DE PROCESAMIENTO - GlobalDocs\n");
        sb.append("════════════════════════════════════════\n\n");
        sb.append("  ID:        ").append(documento.getId()).append("\n");
        sb.append("  Documento: ").append(documento.getNombre()).append("\n");
        sb.append("  Tipo:      ").append(documento.getTipo().getDisplayName()).append("\n");
        sb.append("  Formato:   ").append(documento.getFormato()).append("\n");
        sb.append("  País:      ").append(documento.getPais()).append("\n");
        sb.append("  Fecha:     ").append(documento.getFechaFormateada()).append("\n");
        sb.append("  Tiempo:    ").append(documento.getTiempoProcesamiento()).append(" ms\n\n");

        sb.append("─────────────── Pasos ───────────────\n");
        mensajes.forEach(m -> sb.append("  ✔ ").append(m).append("\n"));

        if (!advertencias.isEmpty()) {
            sb.append("\n─────────────── Advertencias ───────────────\n");
            advertencias.forEach(w -> sb.append("  ⚠ ").append(w).append("\n"));
        }

        sb.append("\n  Log del procesador:\n");
        sb.append("  ").append(documento.getLogProcesamiento().replace("\n", "\n  ")).append("\n\n");
        sb.append(exitoso
                ? "  ✅  PROCESAMIENTO EXITOSO\n"
                : "  ❌  PROCESAMIENTO FALLIDO\n");
        sb.append("════════════════════════════════════════\n");
        return sb.toString();
    }
}
