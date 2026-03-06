package com.globaldocs.model;

public enum DocumentFormat {
    PDF(".pdf"),
    DOC(".doc"),
    DOCX(".docx"),
    MD(".md"),
    CSV(".csv"),
    TXT(".txt"),
    XLSX(".xlsx");

    private final String extension;

    DocumentFormat(String extension) { this.extension = extension; }

    public String getExtension() { return extension; }

    @Override
    public String toString() { return extension.toUpperCase().replace(".", ""); }
}
