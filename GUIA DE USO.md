# GlobalDocs Solutions — Taller Patrón Factory Method
## Sistema de Procesamiento de Documentos Empresariales

---

## 🏗 Patrones de Diseño Implementados

| Patrón | Rol en el proyecto |
|---|---|
| **Factory Method** | `DocumentProcessorFactory` crea el procesador correcto según el país |
| **Strategy** | `ValidationStrategy` encapsula las reglas de validación por país |
| **Observer** | `ProcessingEventManager` notifica cambios de estado a la UI en tiempo real |
| **Template Method** | `BaseDocumentProcessor.process()` define el pipeline fijo de procesamiento |

---

## 📁 Estructura del Proyecto

```
GlobalDocs/
├── src/
│   └── com/globaldocs/
│       ├── model/
│       │   ├── Document.java          ← Entidad principal
│       │   ├── DocumentType.java      ← Enum tipos
│       │   ├── DocumentFormat.java    ← Enum formatos (.pdf, .docx…)
│       │   ├── Country.java           ← Enum países
│       │   ├── ProcessingStatus.java  ← Enum estados
│       │   └── ProcessingResult.java  ← DTO resultado
│       ├── factory/
│       │   ├── DocumentProcessor.java         ← Interfaz producto (Factory)
│       │   ├── BaseDocumentProcessor.java     ← Producto base (Template Method)
│       │   ├── DocumentProcessorFactory.java  ← Creator abstracto (Factory Method)
│       │   ├── ConcreteFactories.java         ← 4 fábricas concretas
│       │   ├── ColombiaDocumentProcessor.java
│       │   ├── MexicoDocumentProcessor.java
│       │   ├── ArgentinaDocumentProcessor.java
│       │   └── ChileDocumentProcessor.java
│       ├── strategy/
│       │   ├── ValidationStrategy.java        ← Interfaz Strategy
│       │   ├── ColombiaValidationStrategy.java
│       │   ├── MexicoValidationStrategy.java
│       │   ├── ArgentinaValidationStrategy.java
│       │   └── ChileValidationStrategy.java
│       ├── observer/
│       │   ├── ProcessingEventListener.java   ← Interfaz Observer
│       │   └── ProcessingEventManager.java    ← Gestor de eventos
│       ├── processor/
│       │   └── BatchProcessor.java            ← Procesamiento en lotes
│       └── ui/
│           └── GlobalDocsApp.java             ← UI Swing dinámica
```

---

## 🚀 Cómo Abrir en IntelliJ IDEA

1. Abrir IntelliJ → **File → Open** → seleccionar la carpeta `GlobalDocs/`
2. IntelliJ detectará el `.iml` automáticamente
3. Si pide configurar SDK: **File → Project Structure → SDK** → seleccionar **JDK 17 o 21**
4. Click derecho en `GlobalDocsApp.java` → **Run 'GlobalDocsApp.main()'**

### Configuración manual de Run (si es necesario):
- **Main class:** `com.globaldocs.ui.GlobalDocsApp`
- **Source root:** `src/`
- **JDK:** 17 o superior (se usa switch expressions y records de Java 17+)

---

## 🎯 Flujo de uso de la aplicación

1. **Agregar documento:** Llenar nombre, seleccionar tipo, formato y país → **Agregar**
2. **Procesar uno:** Seleccionar fila en la tabla → **Procesar**
3. **Ver detalle:** Doble clic en cualquier fila de la tabla
4. **Procesar lote:** Agregar varios documentos → **Procesar Todo**
5. **Probar rechazo:** Seleccionar "Declaración Tributaria" con formato "XLSX" para Colombia → será rechazado

---

## 📋 Reglas de Validación por País

| País | Entidad | Factura | Declaración Trib. | IVA |
|---|---|---|---|---|
| 🇨🇴 Colombia | DIAN/VUCE | PDF, DOCX | Solo PDF | 19% |
| 🇲🇽 México | SAT/ANAM | PDF, DOCX | Solo PDF | 16% |
| 🇦🇷 Argentina | AFIP | PDF, DOCX | Solo PDF | 21% |
| 🇨🇱 Chile | SII | PDF, DOCX | PDF | 19% |
