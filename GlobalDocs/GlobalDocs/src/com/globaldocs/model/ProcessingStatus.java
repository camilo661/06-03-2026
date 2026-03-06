package com.globaldocs.model;

import java.awt.*;

public enum ProcessingStatus {
    PENDING("Pendiente", new Color(255, 165, 0)),
    PROCESSING("Procesando...", new Color(33, 150, 243)),
    VALIDATED("Validado", new Color(156, 39, 176)),
    COMPLETED("Completado", new Color(76, 175, 80)),
    ERROR("Error", new Color(244, 67, 54)),
    REJECTED("Rechazado", new Color(255, 87, 34));

    private final String etiqueta;
    private final Color color;

    ProcessingStatus(String etiqueta, Color color) {
        this.etiqueta = etiqueta;
        this.color = color;
    }

    public String getEtiqueta() { return etiqueta; }
    public Color getColor() { return color; }

    @Override
    public String toString() { return etiqueta; }
}
