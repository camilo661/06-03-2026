package com.globaldocs.model;

public enum DocumentType {
    FACTURA_ELECTRONICA("Factura Electrónica", "📄"),
    CONTRATO_LEGAL("Contrato Legal", "📋"),
    REPORTE_FINANCIERO("Reporte Financiero", "📊"),
    CERTIFICADO_DIGITAL("Certificado Digital", "🔐"),
    DECLARACION_TRIBUTARIA("Declaración Tributaria", "🏛");

    private final String displayName;
    private final String icon;

    DocumentType(String displayName, String icon) {
        this.displayName = displayName;
        this.icon = icon;
    }

    public String getDisplayName() { return displayName; }
    public String getIcon() { return icon; }

    @Override
    public String toString() { return icon + " " + displayName; }
}
