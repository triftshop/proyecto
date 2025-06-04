package com.trif.mitfg_v2;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class DocumentationActivity extends BaseActivity implements DocumentsAdapter.OnDocumentClickListener {

    private static final String TAG = "DocumentationActivity";
    private RecyclerView recyclerDocuments;
    private DocumentsAdapter adapter;
    private DocumentsDatabase database;
    private FloatingActionButton fabAddDocument;

    // Elementos de filtro
    private EditText editSearchText;
    private Spinner spinnerRegionFilter;
    private Spinner spinnerTypeFilter;
    private MaterialButton btnToggleFilters;
    private MaterialButton btnClearFilters;
    private LinearLayout layoutFilters;

    // Estado de filtros
    private boolean filtersVisible = false;

    // Datos para filtros
    private List<DocumentItem> allDocuments;
    private List<String> availableRegions;

    private ActivityResultLauncher<Intent> documentPickerLauncher;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_documentation;
    }

    @Override
    protected void setupToolbar() {
        super.setupToolbar();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.documentation_title);
        }
    }

    @Override
    protected void setupUI() {
        super.setupUI();

        Log.d(TAG, "=== STARTING DOCUMENTATION ACTIVITY ===");

        initializeDatabase();
        initializeViews();
        setupRecyclerView();
        setupFilters();
        setupFAB();
        setupDocumentPicker();
        loadDocuments();

        Log.d(TAG, "=== DOCUMENTATION ACTIVITY SETUP COMPLETE ===");
    }

    private void initializeDatabase() {
        Log.d(TAG, "Initializing database...");
        database = new DocumentsDatabase(this);
        database.open();
        Log.d(TAG, "Database opened successfully");
    }

    private void initializeViews() {
        Log.d(TAG, "Initializing views...");
        recyclerDocuments = findViewById(R.id.recyclerDocuments);
        fabAddDocument = findViewById(R.id.fabAddDocument);

        // Elementos de filtro
        editSearchText = findViewById(R.id.editSearchText);
        spinnerRegionFilter = findViewById(R.id.spinnerRegionFilter);
        spinnerTypeFilter = findViewById(R.id.spinnerTypeFilter);
        btnToggleFilters = findViewById(R.id.btnToggleFilters);
        btnClearFilters = findViewById(R.id.btnClearFilters);
        layoutFilters = findViewById(R.id.layoutFilters);

        Log.d(TAG, "Views initialized");
    }

    private void setupRecyclerView() {
        Log.d(TAG, "Setting up RecyclerView...");
        recyclerDocuments.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar con lista vacía temporalmente
        allDocuments = new ArrayList<>();
        adapter = new DocumentsAdapter(new ArrayList<>(), this);
        recyclerDocuments.setAdapter(adapter);
        Log.d(TAG, "RecyclerView setup complete");
    }

    private void setupFilters() {
        Log.d(TAG, "Setting up filters...");

        // Configurar botón de toggle filtros
        setupToggleFiltersButton();

        // Configurar filtro de tipo de documento
        setupTypeFilter();

        // Configurar búsqueda por texto
        setupTextSearch();

        // Configurar botón limpiar filtros
        setupClearFiltersButton();

        Log.d(TAG, "Filters setup complete");
    }

    private void setupToggleFiltersButton() {
        btnToggleFilters.setOnClickListener(v -> {
            toggleFiltersVisibility();
        });
    }

    private void toggleFiltersVisibility() {
        filtersVisible = !filtersVisible;

        if (filtersVisible) {
            // Mostrar filtros con animación
            layoutFilters.setVisibility(View.VISIBLE);
            layoutFilters.setAlpha(0f);
            layoutFilters.animate()
                    .alpha(1f)
                    .setDuration(300)
                    .start();

            // Actualizar botón (solo icono, más oscuro cuando activo)
            btnToggleFilters.setIcon(getDrawable(R.drawable.ic_expand_less));
            btnToggleFilters.setBackgroundTintList(getColorStateList(R.color.river_blue_dark));

        } else {
            // Ocultar filtros con animación
            layoutFilters.animate()
                    .alpha(0f)
                    .setDuration(300)
                    .withEndAction(() -> layoutFilters.setVisibility(View.GONE))
                    .start();

            // Actualizar botón (color normal)
            btnToggleFilters.setIcon(getDrawable(R.drawable.ic_filter));
            btnToggleFilters.setBackgroundTintList(getColorStateList(R.color.river_blue));
        }
    }

    private void setupClearFiltersButton() {
        btnClearFilters.setOnClickListener(v -> {
            clearAllFilters();
        });
    }

    private void clearAllFilters() {
        // Limpiar búsqueda
        editSearchText.setText("");

        // Resetear spinners
        spinnerTypeFilter.setSelection(0);
        spinnerRegionFilter.setSelection(0);

        // Aplicar filtros (que mostrará todos los documentos)
        applyFilters();

        // Mostrar mensaje
        Toast.makeText(this, R.string.filters_cleared, Toast.LENGTH_SHORT).show();
    }

    private void setupTypeFilter() {
        // Opciones para el filtro de tipo
        String[] typeOptions = {
                getString(R.string.filter_all_documents),
                getString(R.string.filter_official_documents),
                getString(R.string.filter_personal_documents)
        };

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, typeOptions);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTypeFilter.setAdapter(typeAdapter);

        spinnerTypeFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupRegionFilter() {
        // Se ejecuta después de cargar documentos para tener las regiones disponibles
        List<String> regionOptions = new ArrayList<>();
        regionOptions.add(getString(R.string.filter_all_regions));

        if (availableRegions != null) {
            regionOptions.addAll(availableRegions);
        }

        ArrayAdapter<String> regionAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, regionOptions);
        regionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRegionFilter.setAdapter(regionAdapter);

        spinnerRegionFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupTextSearch() {
        editSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                applyFilters();
            }
        });
    }

    private void applyFilters() {
        if (allDocuments == null || allDocuments.isEmpty()) {
            return;
        }

        List<DocumentItem> filteredDocuments = new ArrayList<>();

        String searchText = editSearchText.getText().toString().toLowerCase().trim();
        int typeFilterPosition = spinnerTypeFilter.getSelectedItemPosition();
        int regionFilterPosition = spinnerRegionFilter.getSelectedItemPosition();
        String selectedRegion = regionFilterPosition > 0 && availableRegions != null &&
                regionFilterPosition <= availableRegions.size() ?
                availableRegions.get(regionFilterPosition - 1) : null;

        for (DocumentItem document : allDocuments) {
            boolean matchesSearch = true;
            boolean matchesType = true;
            boolean matchesRegion = true;

            // Filtro por texto de búsqueda
            if (!searchText.isEmpty()) {
                matchesSearch = document.getTitle().toLowerCase().contains(searchText) ||
                        (document.getAutonomousRegion() != null &&
                                document.getAutonomousRegion().toLowerCase().contains(searchText));
            }

            // Filtro por tipo de documento
            switch (typeFilterPosition) {
                case 1: // Solo documentos oficiales
                    matchesType = document.isPredefined();
                    break;
                case 2: // Solo documentos personales
                    matchesType = !document.isPredefined();
                    break;
                default: // Todos los documentos
                    matchesType = true;
                    break;
            }

            // Filtro por región
            if (selectedRegion != null) {
                matchesRegion = selectedRegion.equals(document.getAutonomousRegion());
            }

            if (matchesSearch && matchesType && matchesRegion) {
                filteredDocuments.add(document);
            }
        }

        adapter.updateDocuments(filteredDocuments);

        Log.d(TAG, "Filters applied. Showing " + filteredDocuments.size() + " of " + allDocuments.size() + " documents");
    }

    private void setupFAB() {
        Log.d(TAG, "Setting up FAB...");
        fabAddDocument.setOnClickListener(v -> {
            Log.d(TAG, "FAB clicked - opening document picker");
            openDocumentPicker();
        });
        Log.d(TAG, "FAB setup complete");
    }

    private void setupDocumentPicker() {
        Log.d(TAG, "Setting up document picker...");
        documentPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Log.d(TAG, "Document picker result: " + result.getResultCode());
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedFileUri = result.getData().getData();
                        if (selectedFileUri != null) {
                            Log.d(TAG, "File selected: " + selectedFileUri.toString());
                            handleSelectedDocument(selectedFileUri);
                        }
                    }
                }
        );
        Log.d(TAG, "Document picker setup complete");
    }

    private void openDocumentPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            documentPickerLauncher.launch(Intent.createChooser(intent, getString(R.string.select_pdf_file)));
        } catch (Exception e) {
            Log.e(TAG, "Error opening file picker", e);
            Toast.makeText(this, R.string.error_opening_file_picker, Toast.LENGTH_SHORT).show();
        }
    }

    private void handleSelectedDocument(Uri uri) {
        Log.d(TAG, "Handling selected document: " + uri.toString());

        // Verificar que es un PDF
        if (!PDFUtils.isPDFFile(this, uri)) {
            Log.w(TAG, "Selected file is not a PDF");
            Toast.makeText(this, R.string.error_not_pdf_file, Toast.LENGTH_SHORT).show();
            return;
        }

        // Mostrar diálogo para el nombre del documento
        showNameDocumentDialog(uri);
    }

    private void showNameDocumentDialog(Uri uri) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.enter_document_name);

        final EditText input = new EditText(this);
        input.setHint(R.string.document_name_hint);

        // Sugerir nombre basado en el archivo
        String suggestedName = PDFUtils.getFileName(this, uri);
        if (suggestedName.endsWith(".pdf")) {
            suggestedName = suggestedName.substring(0, suggestedName.length() - 4);
        }
        input.setText(suggestedName);

        builder.setView(input);

        builder.setPositiveButton(R.string.save, (dialog, which) -> {
            String documentName = input.getText().toString().trim();
            if (!documentName.isEmpty()) {
                saveDocumentToDatabase(uri, documentName);
            } else {
                Toast.makeText(this, R.string.error_empty_name, Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void saveDocumentToDatabase(Uri uri, String documentName) {
        Log.d(TAG, "Saving document to database: " + documentName);

        new Thread(() -> {
            try {
                // Generar nombre único para el archivo
                String fileName = "doc_" + System.currentTimeMillis() + ".pdf";

                // Copiar archivo a almacenamiento interno
                String filePath = PDFUtils.copyPDFToInternalStorage(this, uri, fileName);

                if (filePath != null) {
                    Log.d(TAG, "File copied to: " + filePath);

                    // Generar miniatura
                    String thumbnailPath = PDFUtils.generatePDFThumbnail(this, filePath);
                    Log.d(TAG, "Thumbnail generated: " + thumbnailPath);

                    // Crear objeto documento
                    DocumentItem document = new DocumentItem(documentName, filePath, thumbnailPath);

                    // Guardar en base de datos
                    long result = database.insertDocument(document);

                    runOnUiThread(() -> {
                        if (result > 0) {
                            Log.d(TAG, "Document saved successfully with ID: " + result);
                            Toast.makeText(this, R.string.document_saved_successfully, Toast.LENGTH_SHORT).show();
                            loadDocuments();
                        } else {
                            Log.e(TAG, "Failed to save document to database");
                            Toast.makeText(this, R.string.error_saving_document, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    runOnUiThread(() -> {
                        Log.e(TAG, "Failed to copy file");
                        Toast.makeText(this, R.string.error_copying_file, Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Error processing document", e);
                runOnUiThread(() -> {
                    Toast.makeText(this, R.string.error_processing_document, Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void loadDocuments() {
        Log.d(TAG, "Loading documents from database...");

        try {
            allDocuments = database.getAllDocuments();
            Log.d(TAG, "Found " + allDocuments.size() + " documents in database");

            // Extraer regiones disponibles
            extractAvailableRegions();

            // Configurar filtro de regiones
            setupRegionFilter();

            // Mostrar todos los documentos inicialmente
            adapter.updateDocuments(allDocuments);
            Log.d(TAG, "Adapter updated with documents");

        } catch (Exception e) {
            Log.e(TAG, "Error loading documents", e);
            Toast.makeText(this, "Error loading documents: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void extractAvailableRegions() {
        availableRegions = new ArrayList<>();
        for (DocumentItem document : allDocuments) {
            if (document.isPredefined() && document.getAutonomousRegion() != null &&
                    !availableRegions.contains(document.getAutonomousRegion())) {
                availableRegions.add(document.getAutonomousRegion());
            }
        }
        // Ordenar alfabéticamente
        java.util.Collections.sort(availableRegions);
        Log.d(TAG, "Found " + availableRegions.size() + " regions: " + availableRegions);
    }

    @Override
    public void onDocumentClick(DocumentItem document) {
        Log.d(TAG, "Document clicked: " + document.getTitle());
        Log.d(TAG, "Document path: " + document.getFilePath());
        Log.d(TAG, "Is predefined: " + document.isPredefined());

        // Verificar si el archivo existe antes de abrir el viewer
        if (document.isPredefined()) {
            try {
                getAssets().open(document.getFilePath()).close();
                Log.d(TAG, "Predefined document file exists");
            } catch (Exception e) {
                Log.e(TAG, "Predefined document file not found: " + document.getFilePath(), e);
                Toast.makeText(this, "Archivo no encontrado: " + document.getFilePath(), Toast.LENGTH_LONG).show();
                return;
            }
        } else {
            java.io.File file = new java.io.File(document.getFilePath());
            if (!file.exists()) {
                Log.e(TAG, "User document file not found: " + document.getFilePath());
                Toast.makeText(this, "Archivo no encontrado: " + document.getFilePath(), Toast.LENGTH_LONG).show();
                return;
            }
            Log.d(TAG, "User document file exists");
        }

        // Abrir el PDF
        Intent intent = new Intent(this, PDFViewerActivity.class);
        intent.putExtra("document_id", document.getId());
        intent.putExtra("document_title", document.getTitle());
        intent.putExtra("document_path", document.getFilePath());
        intent.putExtra("is_predefined", document.isPredefined());
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onDocumentLongClick(DocumentItem document) {
        Log.d(TAG, "Document long clicked: " + document.getTitle());

        // Solo para documentos de usuario (no predefinidos)
        if (!document.isPredefined()) {
            showDocumentOptionsDialog(document);
        }
    }

    private void showDocumentOptionsDialog(DocumentItem document) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(document.getTitle());

        String[] options = {
                getString(R.string.rename_document),
                getString(R.string.delete_document)
        };

        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    showRenameDocumentDialog(document);
                    break;
                case 1:
                    showDeleteDocumentDialog(document);
                    break;
            }
        });

        builder.show();
    }

    private void showRenameDocumentDialog(DocumentItem document) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.rename_document);

        final EditText input = new EditText(this);
        input.setText(document.getTitle());
        input.selectAll();
        builder.setView(input);

        builder.setPositiveButton(R.string.save, (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty() && !newName.equals(document.getTitle())) {
                document.setTitle(newName);
                if (database.updateDocument(document)) {
                    Toast.makeText(this, R.string.document_renamed_successfully, Toast.LENGTH_SHORT).show();
                    loadDocuments();
                } else {
                    Toast.makeText(this, R.string.error_renaming_document, Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showDeleteDocumentDialog(DocumentItem document) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.delete_document);
        builder.setMessage(getString(R.string.delete_document_confirmation, document.getTitle()));

        builder.setPositiveButton(R.string.delete, (dialog, which) -> {
            if (database.deleteDocument(document.getId())) {
                // Eliminar archivos físicos
                PDFUtils.deleteDocumentFiles(document.getFilePath(), document.getThumbnailPath());
                Toast.makeText(this, R.string.document_deleted_successfully, Toast.LENGTH_SHORT).show();
                loadDocuments();
            } else {
                Toast.makeText(this, R.string.error_deleting_document, Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());
        builder.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finishWithTransition();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (database != null) {
            database.close();
        }
        Log.d(TAG, "DocumentationActivity destroyed");
    }
}