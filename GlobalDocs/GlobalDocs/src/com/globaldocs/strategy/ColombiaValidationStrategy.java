package com.globaldocs.strategy;

import com.globaldocs.model.Document;
import com.globaldocs.model.DocumentFormat;
import com.globaldocs.model.DocumentType;

/**
 * PATTERN: Strategy - Colombia
 * Rules: DIAN, VUCE, IVA 19%, retención en la fuente 3.5%, ReteICA 0.414%
 */
public class ColombiaValidationStrategy implements ValidationStrategy {

    @Override
    public boolean validate(Document documento) {
        DocumentType tipo = documento.getTipo();
        DocumentFormat formato = documento.getFormato();

        if (tipo == DocumentType.FACTURA_ELECTRONICA) {
            // DIAN: Facturas electrónicas deben ser PDF o DOCX (CFDI equivalente)
            return formato == DocumentFormat.PDF || formato == DocumentFormat.DOCX;
        }
        if (tipo == DocumentType.CONTRATO_LEGAL) {
            // Contratos solo en PDF o DOCX
            return formato == DocumentFormat.PDF || formato == DocumentFormat.DOCX;
        }
        if (tipo == DocumentType.DECLARACION_TRIBUTARIA) {
            // Declaraciones tributarias ante la DIAN deben ser PDF
            return formato == DocumentFormat.PDF;
        }
        // Otros tipos: todos los formatos aceptados
        return true;
    }

    @Override
    public String getTaxInfo() {
        return "IVA: 19% | Retención Fuente: 3.5% | ReteICA: 0.414% | ICA Municipal";
    }

    @Override
    public String getRegulationName() {
        return "DIAN - Res. 000042-2020 | VUCE Colombia";
    }
}
