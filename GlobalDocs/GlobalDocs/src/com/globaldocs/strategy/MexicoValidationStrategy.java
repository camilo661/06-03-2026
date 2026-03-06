package com.globaldocs.strategy;

import com.globaldocs.model.Document;
import com.globaldocs.model.DocumentFormat;
import com.globaldocs.model.DocumentType;

/**
 * PATTERN: Strategy - México
 * Rules: SAT/ANAM, CFDI 4.0, IVA 16%, ISR 30%, RFC obligatorio
 */
public class MexicoValidationStrategy implements ValidationStrategy {

    @Override
    public boolean validate(Document documento) {
        DocumentType tipo = documento.getTipo();
        DocumentFormat formato = documento.getFormato();

        if (tipo == DocumentType.FACTURA_ELECTRONICA) {
            // SAT: CFDI 4.0 requiere PDF, DOCX o XML equivalente
            return formato == DocumentFormat.PDF || formato == DocumentFormat.DOCX;
        }
        if (tipo == DocumentType.DECLARACION_TRIBUTARIA) {
            // SAT solo acepta PDF para declaraciones
            return formato == DocumentFormat.PDF;
        }
        if (tipo == DocumentType.CERTIFICADO_DIGITAL) {
            // Certificados e.firma solo en PDF
            return formato == DocumentFormat.PDF;
        }
        return true;
    }

    @Override
    public String getTaxInfo() {
        return "IVA: 16% | ISR: 30% | IEPS: Variable | Pedimento: DVC";
    }

    @Override
    public String getRegulationName() {
        return "SAT - CFDI 4.0 | Res. Miscelánea Fiscal 2024";
    }
}
