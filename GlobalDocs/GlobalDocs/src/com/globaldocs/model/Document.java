package com.globaldocs.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Document {
    private final String id;
    private final String nombre;
    private final DocumentType tipo;
    private final DocumentFormat formato;
    private final Country pais;
    private final LocalDateTime fechaCreacion;
    private ProcessingStatus estado;
    private String logProcesamiento;
    private long tiempoProcesamiento;

    public Document(String nombre, DocumentType tipo, DocumentFormat formato, Country pais) {
        this.id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.nombre = nombre;
        this.tipo = tipo;
        this.formato = formato;
        this.pais = pais;
        this.fechaCreacion = LocalDateTime.now();
        this.estado = ProcessingStatus.PENDING;
        this.logProcesamiento = "";
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public DocumentType getTipo() { return tipo; }
    public DocumentFormat getFormato() { return formato; }
    public Country getPais() { return pais; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public ProcessingStatus getEstado() { return estado; }
    public String getLogProcesamiento() { return logProcesamiento; }
    public long getTiempoProcesamiento() { return tiempoProcesamiento; }

    public void setEstado(ProcessingStatus estado) { this.estado = estado; }
    public void setLogProcesamiento(String log) { this.logProcesamiento = log; }
    public void setTiempoProcesamiento(long ms) { this.tiempoProcesamiento = ms; }

    public String getFechaFormateada() {
        return fechaCreacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | %s | %s | %s",
                id, nombre, tipo.getDisplayName(), formato, pais.getNombre());
    }
}
