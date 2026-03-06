package com.globaldocs.strategy;

import com.globaldocs.model.Document;
import com.globaldocs.model.DocumentFormat;
import com.globaldocs.model.DocumentType;

/**
 * PATTERN: Strategy - Argentina
 * Rules: AFIP, CUIT obligatorio, IVA 21%, Ingresos Brutos, Ganancias 35%
 */
public class ArgentinaValidationStrategy implements ValidationStrategy {

    @Override
    public boolean validate(Document documento) {
        DocumentType tipo = documento.getTipo();
        DocumentFormat formato = documento.getFormato();

        if (tipo == DocumentType.FACTURA_ELECTRONICA) {
            // AFIP: Facturas A, B, C deben ser PDF
            return formato == DocumentFormat.PDF || formato == DocumentFormat.DOCX;
        }
        if (tipo == DocumentType.REPORTE_FINANCIERO) {
            // AFIP acepta PDF y XLSX para reportes
            return formato == DocumentFormat.PDF || formato == DocumentFormat.XLSX ||
                   formato == DocumentFormat.CSV;
        }
        if (tipo == DocumentType.DECLARACION_TRIBUTARIA) {
            // Solo PDF ante AFIP
            return formato == DocumentFormat.PDF;
        }
        return true;
    }

    @Override
    public String getTaxInfo() {
        return "IVA: 21% | Ganancias: 35% | Ingresos Brutos: 3% | Sellos: 1%";
    }

    @Override
    public String getRegulationName() {
        return "AFIP - RG 4291/2018 | CUIT Obligatorio";
    }
}
