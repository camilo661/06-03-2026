package com.globaldocs.ui;

import com.globaldocs.factory.DocumentProcessorFactory;
import com.globaldocs.model.*;
import com.globaldocs.observer.ProcessingEventManager;
import com.globaldocs.processor.BatchProcessor;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main Swing UI for GlobalDocs Solutions
 * Dynamic Java interface — no HTML, pure Swing components.
 */
public class GlobalDocsApp extends JFrame {

    // ── Colors ───────────────────────────────────────────────────────────────
    private static final Color COLOR_BG          = new Color(15, 23, 42);
    private static final Color COLOR_PANEL        = new Color(30, 41, 59);
    private static final Color COLOR_CARD         = new Color(51, 65, 85);
    private static final Color COLOR_ACCENT       = new Color(99, 102, 241);
    private static final Color COLOR_ACCENT2      = new Color(139, 92, 246);
    private static final Color COLOR_SUCCESS      = new Color(34, 197, 94);
    private static final Color COLOR_ERROR        = new Color(239, 68, 68);
    private static final Color COLOR_WARNING      = new Color(245, 158, 11);
    private static final Color COLOR_TEXT         = new Color(248, 250, 252);
    private static final Color COLOR_TEXT_MUTED   = new Color(148, 163, 184);
    private static final Color COLOR_BORDER       = new Color(71, 85, 105);

    // ── Fonts ────────────────────────────────────────────────────────────────
    private static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_HEADER  = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_BODY    = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font FONT_MONO    = new Font("Consolas", Font.PLAIN, 12);

    // ── State ────────────────────────────────────────────────────────────────
    private final ProcessingEventManager gestorEventos = new ProcessingEventManager();
    private final List<Document> listaDocumentos = new ArrayList<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    // ── UI Components ────────────────────────────────────────────────────────
    private JTextField campoNombre;
    private JComboBox<DocumentType> combTipo;
    private JComboBox<DocumentFormat> combFormato;
    private JComboBox<Country> combPais;
    private JTable tablaDocumentos;
    private DefaultTableModel modeloTabla;
    private JTextArea areaLog;
    private JLabel lblEstadoBar;
    private JProgressBar barraProgreso;
    private JLabel lblContadorTotal;
    private JLabel lblContadorExito;
    private JLabel lblContadorError;

    // ── Constructor ───────────────────────────────────────────────────────────
    public GlobalDocsApp() {
        setTitle("GlobalDocs Solutions — Sistema de Procesamiento de Documentos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 820);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_BG);

        setupObservers();
        buildUI();
        setVisible(true);
    }

    // ── Observer wiring ───────────────────────────────────────────────────────
    private void setupObservers() {
        gestorEventos.subscribe("STATUS_CHANGE", (tipo, doc) ->
            SwingUtilities.invokeLater(() -> {
                refreshTable();
                appendLog("🔄 [" + doc.getId() + "] " + doc.getNombre() + " → " + doc.getEstado());
            })
        );
        gestorEventos.subscribe("COMPLETED", (tipo, doc) ->
            SwingUtilities.invokeLater(() -> {
                refreshTable();
                updateCounters();
                appendLog("✅ [" + doc.getId() + "] Completado en " + doc.getTiempoProcesamiento() + "ms");
            })
        );
        gestorEventos.subscribe("ERROR", (tipo, doc) ->
            SwingUtilities.invokeLater(() -> {
                refreshTable();
                updateCounters();
                appendLog("❌ [" + doc.getId() + "] " + doc.getNombre() + " → " + doc.getEstado());
            })
        );
        gestorEventos.subscribe("BATCH_PROGRESS", (tipo, doc) ->
            SwingUtilities.invokeLater(this::updateCounters)
        );
    }

    // ── Build full UI ─────────────────────────────────────────────────────────
    private void buildUI() {
        setLayout(new BorderLayout(0, 0));
        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);
    }

    // ── Header ────────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_PANEL);
        header.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, COLOR_BORDER),
            new EmptyBorder(14, 24, 14, 24)
        ));

        // Left: logo + title
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);

        JLabel logo = new JLabel("📁");
        logo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        JLabel title = styledLabel("GlobalDocs Solutions", FONT_TITLE, COLOR_TEXT);
        JLabel sub   = styledLabel("Sistema Multinacional · Factory Method + Strategy + Observer",
                                   FONT_SMALL, COLOR_TEXT_MUTED);
        titlePanel.add(title);
        titlePanel.add(sub);

        left.add(logo);
        left.add(titlePanel);

        // Right: counters
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);
        lblContadorTotal = counterBadge("Total: 0", COLOR_ACCENT);
        lblContadorExito = counterBadge("OK: 0", COLOR_SUCCESS);
        lblContadorError = counterBadge("Err: 0", COLOR_ERROR);
        right.add(lblContadorTotal);
        right.add(lblContadorExito);
        right.add(lblContadorError);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    // ── Center split ──────────────────────────────────────────────────────────
    private JSplitPane buildCenter() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                          buildLeftPanel(), buildRightPanel());
        split.setDividerLocation(380);
        split.setDividerSize(4);
        split.setBorder(null);
        split.setBackground(COLOR_BG);
        return split;
    }

    // ── Left panel (form + batch) ─────────────────────────────────────────────
    private JPanel buildLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(16, 16, 16, 8));

        panel.add(buildFormCard(), BorderLayout.CENTER);
        panel.add(buildBatchCard(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildFormCard() {
        JPanel card = card("➕  Nuevo Documento");

        JPanel fields = new JPanel(new GridBagLayout());
        fields.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Name
        campoNombre = styledTextField("Ej: Factura-2025-001");
        addFormRow(fields, gbc, 0, "Nombre del Documento", campoNombre);

        // Type
        combTipo = styledCombo(DocumentType.values());
        addFormRow(fields, gbc, 1, "Tipo de Documento", combTipo);

        // Format
        combFormato = styledCombo(DocumentFormat.values());
        addFormRow(fields, gbc, 2, "Formato", combFormato);

        // Country
        combPais = styledCombo(Country.values());
        addFormRow(fields, gbc, 3, "País de Procesamiento", combPais);

        // Buttons
        JPanel btnRow = new JPanel(new GridLayout(1, 2, 8, 0));
        btnRow.setOpaque(false);
        btnRow.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton btnAgregar   = accentButton("➕ Agregar",    COLOR_ACCENT);
        JButton btnProcesarU = accentButton("⚡ Procesar",   COLOR_SUCCESS);

        btnAgregar.addActionListener(e -> addDocument());
        btnProcesarU.addActionListener(e -> processSingleSelected());

        btnRow.add(btnAgregar);
        btnRow.add(btnProcesarU);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 0, 0, 0);
        fields.add(btnRow, gbc);

        card.add(fields, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildBatchCard() {
        JPanel card = card("🔄  Procesamiento por Lotes");
        card.setPreferredSize(new Dimension(0, 140));

        JPanel content = new JPanel(new BorderLayout(0, 8));
        content.setOpaque(false);

        barraProgreso = new JProgressBar(0, 100);
        barraProgreso.setStringPainted(true);
        barraProgreso.setString("Sin procesar");
        barraProgreso.setForeground(COLOR_ACCENT);
        barraProgreso.setBackground(COLOR_CARD);
        barraProgreso.setFont(FONT_SMALL);
        barraProgreso.setBorderPainted(false);
        barraProgreso.setPreferredSize(new Dimension(0, 22));

        JPanel btnRow = new JPanel(new GridLayout(1, 2, 8, 0));
        btnRow.setOpaque(false);

        JButton btnLote  = accentButton("🚀 Procesar Todo", COLOR_ACCENT2);
        JButton btnLimpi = accentButton("🗑 Limpiar Lista", new Color(71, 85, 105));

        btnLote.addActionListener(e -> processBatch());
        btnLimpi.addActionListener(e -> clearList());

        btnRow.add(btnLote);
        btnRow.add(btnLimpi);

        content.add(barraProgreso, BorderLayout.NORTH);
        content.add(btnRow, BorderLayout.SOUTH);

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    // ── Right panel (table + log) ─────────────────────────────────────────────
    private JSplitPane buildRightPanel() {
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                                          buildTableCard(), buildLogCard());
        split.setDividerLocation(380);
        split.setDividerSize(4);
        split.setBorder(null);
        split.setBackground(COLOR_BG);
        return split;
    }

    private JPanel buildTableCard() {
        JPanel card = card("📋  Cola de Documentos");

        String[] columnas = {"ID", "Nombre", "Tipo", "Formato", "País", "Estado", "Tiempo (ms)"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaDocumentos = new JTable(modeloTabla);
        tablaDocumentos.setBackground(COLOR_CARD);
        tablaDocumentos.setForeground(COLOR_TEXT);
        tablaDocumentos.setFont(FONT_BODY);
        tablaDocumentos.setRowHeight(32);
        tablaDocumentos.setShowGrid(false);
        tablaDocumentos.setIntercellSpacing(new Dimension(0, 1));
        tablaDocumentos.setSelectionBackground(new Color(99, 102, 241, 80));
        tablaDocumentos.setSelectionForeground(COLOR_TEXT);
        tablaDocumentos.getTableHeader().setBackground(COLOR_PANEL);
        tablaDocumentos.getTableHeader().setForeground(COLOR_TEXT_MUTED);
        tablaDocumentos.getTableHeader().setFont(FONT_SMALL);
        tablaDocumentos.getTableHeader().setBorder(BorderFactory.createEmptyBorder());

        // Column widths
        int[] widths = {70, 180, 155, 65, 110, 100, 90};
        for (int i = 0; i < widths.length; i++)
            tablaDocumentos.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Status renderer
        tablaDocumentos.getColumnModel().getColumn(5).setCellRenderer(new StatusCellRenderer());

        // Double-click → show detail
        tablaDocumentos.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) showDocumentDetail();
            }
        });

        JScrollPane scroll = new JScrollPane(tablaDocumentos);
        scroll.setBackground(COLOR_CARD);
        scroll.getViewport().setBackground(COLOR_CARD);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildLogCard() {
        JPanel card = card("📜  Log de Actividad");

        areaLog = new JTextArea();
        areaLog.setBackground(new Color(10, 15, 28));
        areaLog.setForeground(new Color(134, 239, 172));
        areaLog.setFont(FONT_MONO);
        areaLog.setEditable(false);
        areaLog.setLineWrap(true);
        areaLog.setWrapStyleWord(true);
        areaLog.setBorder(new EmptyBorder(8, 10, 8, 10));
        areaLog.setText("GlobalDocs Solutions v2.0 — Sistema iniciado\n" +
                         "Patrones: Factory Method · Strategy · Observer\n" +
                         "────────────────────────────────────────\n");

        JScrollPane scroll = new JScrollPane(areaLog);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(new Color(10, 15, 28));

        JButton btnClear = smallButton("Limpiar log");
        btnClear.addActionListener(e -> areaLog.setText(""));

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 4));
        bottom.setOpaque(false);
        bottom.add(btnClear);

        card.add(scroll, BorderLayout.CENTER);
        card.add(bottom, BorderLayout.SOUTH);
        return card;
    }

    // ── Status bar ────────────────────────────────────────────────────────────
    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(COLOR_PANEL);
        bar.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, COLOR_BORDER),
            new EmptyBorder(6, 16, 6, 16)
        ));
        lblEstadoBar = styledLabel("Listo — Agregue documentos y presione Procesar", FONT_SMALL, COLOR_TEXT_MUTED);
        JLabel patrones = styledLabel(
            "Factory Method · Strategy · Observer · Template Method",
            FONT_SMALL, new Color(99, 102, 241));
        bar.add(lblEstadoBar, BorderLayout.WEST);
        bar.add(patrones, BorderLayout.EAST);
        return bar;
    }

    // ── Actions ───────────────────────────────────────────────────────────────
    private void addDocument() {
        String nombre = campoNombre.getText().trim();
        if (nombre.isEmpty()) {
            showError("Ingrese un nombre para el documento.");
            return;
        }
        Document doc = new Document(
            nombre,
            (DocumentType)   combTipo.getSelectedItem(),
            (DocumentFormat) combFormato.getSelectedItem(),
            (Country)        combPais.getSelectedItem()
        );
        listaDocumentos.add(doc);
        addTableRow(doc);
        campoNombre.setText("");
        setStatus("Documento agregado: " + doc.getId() + " — " + doc.getNombre());
        appendLog("📄 Agregado [" + doc.getId() + "] " + doc.getNombre() +
                  " | " + doc.getTipo().getDisplayName() + " | " + doc.getPais());
    }

    private void processSingleSelected() {
        int row = tablaDocumentos.getSelectedRow();
        if (row < 0) {
            showError("Seleccione un documento de la tabla para procesar.");
            return;
        }
        Document doc = listaDocumentos.get(row);
        if (doc.getEstado() == ProcessingStatus.COMPLETED) {
            showError("Este documento ya fue procesado.");
            return;
        }
        setStatus("Procesando: " + doc.getNombre() + "...");
        executor.submit(() -> {
            DocumentProcessorFactory fabrica =
                DocumentProcessorFactory.getFactory(doc.getPais(), gestorEventos);
            var resultado = fabrica.processDocument(doc);
            SwingUtilities.invokeLater(() -> {
                showResultDetail(resultado);
                setStatus("Procesado: " + doc.getId());
            });
        });
    }

    private void processBatch() {
        List<Document> pendientes = listaDocumentos.stream()
            .filter(d -> d.getEstado() == ProcessingStatus.PENDING)
            .toList();

        if (pendientes.isEmpty()) {
            showError("No hay documentos pendientes de procesar.");
            return;
        }

        barraProgreso.setValue(0);
        barraProgreso.setMaximum(pendientes.size());
        barraProgreso.setString("0 / " + pendientes.size());
        setStatus("Procesando lote de " + pendientes.size() + " documentos...");

        executor.submit(() -> {
            BatchProcessor lote = new BatchProcessor(gestorEventos);
            int[] cont = {0};

            for (Document doc : pendientes) {
                DocumentProcessorFactory fabrica =
                    DocumentProcessorFactory.getFactory(doc.getPais(), gestorEventos);
                fabrica.processDocument(doc);
                cont[0]++;
                int progreso = cont[0];
                SwingUtilities.invokeLater(() -> {
                    barraProgreso.setValue(progreso);
                    barraProgreso.setString(progreso + " / " + pendientes.size());
                    updateCounters();
                });
            }
            SwingUtilities.invokeLater(() -> {
                barraProgreso.setString("✅ Completado");
                setStatus("Lote procesado: " + pendientes.size() + " documentos");
            });
        });
    }

    private void clearList() {
        listaDocumentos.clear();
        modeloTabla.setRowCount(0);
        updateCounters();
        barraProgreso.setValue(0);
        barraProgreso.setString("Sin procesar");
        appendLog("🗑 Lista limpiada");
        setStatus("Lista de documentos limpiada");
    }

    private void showDocumentDetail() {
        int row = tablaDocumentos.getSelectedRow();
        if (row < 0) return;
        Document doc = listaDocumentos.get(row);

        JTextArea ta = new JTextArea(doc.getLogProcesamiento().isEmpty()
            ? "Este documento aún no ha sido procesado."
            : doc.getLogProcesamiento());
        ta.setFont(FONT_MONO);
        ta.setEditable(false);
        ta.setBackground(new Color(15, 23, 42));
        ta.setForeground(new Color(134, 239, 172));
        ta.setMargin(new Insets(10, 12, 10, 12));

        JScrollPane sp = new JScrollPane(ta);
        sp.setPreferredSize(new Dimension(540, 320));

        JOptionPane.showMessageDialog(this, sp,
            "📄 Detalle: " + doc.getNombre() + "  [" + doc.getId() + "]",
            JOptionPane.PLAIN_MESSAGE);
    }

    private void showResultDetail(com.globaldocs.model.ProcessingResult resultado) {
        JTextArea ta = new JTextArea(resultado.getResumen());
        ta.setFont(FONT_MONO);
        ta.setEditable(false);
        ta.setBackground(new Color(15, 23, 42));
        ta.setForeground(resultado.isExitoso() ? new Color(134, 239, 172) : new Color(252, 165, 165));
        ta.setMargin(new Insets(10, 12, 10, 12));

        JScrollPane sp = new JScrollPane(ta);
        sp.setPreferredSize(new Dimension(560, 400));

        JOptionPane.showMessageDialog(this, sp,
            resultado.isExitoso() ? "✅ Procesamiento Exitoso" : "❌ Procesamiento con Errores",
            resultado.isExitoso() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
    }

    // ── Table helpers ─────────────────────────────────────────────────────────
    private void addTableRow(Document doc) {
        modeloTabla.addRow(new Object[]{
            doc.getId(), doc.getNombre(), doc.getTipo().getDisplayName(),
            doc.getFormato().toString(), doc.getPais().toString(),
            doc.getEstado(), doc.getTiempoProcesamiento() > 0 ? doc.getTiempoProcesamiento() : "-"
        });
    }

    private void refreshTable() {
        modeloTabla.setRowCount(0);
        listaDocumentos.forEach(this::addTableRow);
    }

    private void updateCounters() {
        long total = listaDocumentos.size();
        long ok    = listaDocumentos.stream().filter(d -> d.getEstado() == ProcessingStatus.COMPLETED).count();
        long err   = listaDocumentos.stream().filter(d ->
            d.getEstado() == ProcessingStatus.ERROR || d.getEstado() == ProcessingStatus.REJECTED).count();

        lblContadorTotal.setText("Total: " + total);
        lblContadorExito.setText("OK: " + ok);
        lblContadorError.setText("Err: " + err);
    }

    private void appendLog(String msg) {
        areaLog.append(msg + "\n");
        areaLog.setCaretPosition(areaLog.getDocument().getLength());
    }

    private void setStatus(String msg) {
        lblEstadoBar.setText(msg);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    // ── UI Factories (builder helpers) ────────────────────────────────────────
    private JPanel card(String title) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(new CompoundBorder(
            new LineBorder(COLOR_BORDER, 1, true),
            new EmptyBorder(14, 16, 14, 16)
        ));

        JLabel lbl = styledLabel(title, FONT_HEADER, COLOR_TEXT);
        lbl.setBorder(new EmptyBorder(0, 0, 8, 0));
        panel.add(lbl, BorderLayout.NORTH);
        return panel;
    }

    private JLabel styledLabel(String text, Font font, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(font);
        lbl.setForeground(color);
        return lbl;
    }

    private JLabel counterBadge(String text, Color color) {
        JLabel lbl = new JLabel(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color.darker());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(Color.WHITE);
        lbl.setBorder(new EmptyBorder(4, 10, 4, 10));
        lbl.setOpaque(false);
        return lbl;
    }

    private <T> JComboBox<T> styledCombo(T[] items) {
        JComboBox<T> combo = new JComboBox<>(items);
        combo.setBackground(COLOR_CARD);
        combo.setForeground(COLOR_TEXT);
        combo.setFont(FONT_BODY);
        combo.setBorder(new LineBorder(COLOR_BORDER, 1));
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> list,
                    Object value, int idx, boolean sel, boolean focus) {
                super.getListCellRendererComponent(list, value, idx, sel, focus);
                setBackground(sel ? COLOR_ACCENT : COLOR_CARD);
                setForeground(COLOR_TEXT);
                setFont(FONT_BODY);
                setBorder(new EmptyBorder(4, 8, 4, 8));
                return this;
            }
        });
        return combo;
    }

    private JTextField styledTextField(String placeholder) {
        JTextField tf = new JTextField();
        tf.setBackground(COLOR_CARD);
        tf.setForeground(COLOR_TEXT);
        tf.setFont(FONT_BODY);
        tf.setCaretColor(COLOR_TEXT);
        tf.setBorder(new CompoundBorder(
            new LineBorder(COLOR_BORDER, 1),
            new EmptyBorder(6, 10, 6, 10)
        ));
        tf.setToolTipText(placeholder);
        tf.putClientProperty("placeholder", placeholder);
        tf.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                tf.setBorder(new CompoundBorder(
                    new LineBorder(COLOR_ACCENT, 1),
                    new EmptyBorder(6, 10, 6, 10)));
            }
            @Override public void focusLost(FocusEvent e) {
                tf.setBorder(new CompoundBorder(
                    new LineBorder(COLOR_BORDER, 1),
                    new EmptyBorder(6, 10, 6, 10)));
            }
        });
        return tf;
    }

    private JButton accentButton(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? color.darker() :
                             getModel().isRollover() ? color.brighter() : color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 36));
        return btn;
    }

    private JButton smallButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_SMALL);
        btn.setBackground(COLOR_CARD);
        btn.setForeground(COLOR_TEXT_MUTED);
        btn.setBorder(new LineBorder(COLOR_BORDER, 1));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc,
                             int row, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row * 2;
        gbc.gridwidth = 2; gbc.insets = new Insets(6, 0, 2, 0);
        panel.add(styledLabel(label, FONT_SMALL, COLOR_TEXT_MUTED), gbc);

        gbc.gridx = 0; gbc.gridy = row * 2 + 1;
        gbc.insets = new Insets(0, 0, 4, 0);
        panel.add(field, gbc);
    }

    // ── Status cell renderer ──────────────────────────────────────────────────
    private static class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable table,
                Object value, boolean selected, boolean focused, int row, int col) {
            super.getTableCellRendererComponent(table, value, selected, focused, row, col);
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            setHorizontalAlignment(CENTER);
            setOpaque(true);

            if (value instanceof ProcessingStatus ps) {
                setForeground(ps.getColor());
                setBackground(selected ? new Color(99, 102, 241, 80)
                                       : new Color(ps.getColor().getRed(),
                                                   ps.getColor().getGreen(),
                                                   ps.getColor().getBlue(), 25));
                setText(ps.getEtiqueta());
            } else {
                setBackground(selected ? new Color(99, 102, 241, 80)
                                       : new Color(30, 41, 59));
                setForeground(new Color(148, 163, 184));
            }
            return this;
        }
    }

    // ── Entry point ───────────────────────────────────────────────────────────
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(GlobalDocsApp::new);
    }
}
