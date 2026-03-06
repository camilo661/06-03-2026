package com.globaldocs.strategy;

import com.globaldocs.model.Document;
import com.globaldocs.model.DocumentFormat;
import com.globaldocs.model.DocumentType;

/**
 * PATTERN: Strategy - Chile
 * Rules: SII (Servicio de Impuestos Internos), IVA 19%, RUT obligatorio
 */
public class ChileValidationStrategy implements ValidationStrategy {

    @Override
    public boolean validate(Document documento) {
        DocumentType tipo = documento.getTipo();
        DocumentFormat formato = documento.getFormato();

        if (tipo == DocumentType.FACTURA_ELECTRONICA) {
            // SII: Facturas electrónicas DTE - PDF o DOCX
            return formato == DocumentFormat.PDF || formato == DocumentFormat.DOCX;
        }
        if (tipo == DocumentType.REPORTE_FINANCIERO) {
            // SII acepta PDF, XLSX y CSV
            return formato == DocumentFormat.PDF || formato == DocumentFormat.XLSX ||
                   formato == DocumentFormat.CSV;
        }
        if (tipo == DocumentType.CERTIFICADO_DIGITAL) {
            // Certificados sólo PDF
            return formato == DocumentFormat.PDF;
        }
        return true;
    }

    @Override
    public String getTaxInfo() {
        return "IVA: 19% | PPM: 0.15% | Impuesto 1a Categoría: 27% | Timbres: 0.8%";
    }

    @Override
    public String getRegulationName() {
        return "SII Chile - DTE Res. Ex. 80/2014 | Aduana SNA";
    }
}
