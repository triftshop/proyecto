package com.trif.mitfg_v2;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PDFViewerActivity extends BaseActivity {

    private RecyclerView recyclerPDFPages;
    private PDFPagesAdapter adapter;
    private FloatingActionButton fabZoomIn, fabZoomOut;

    private PdfRenderer pdfRenderer;
    private ParcelFileDescriptor fileDescriptor;
    private List<Bitmap> pages;
    private String documentTitle;
    private String documentPath;
    private boolean isPredefined;
    private float currentZoom = 1.0f;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_pdf_viewer;
    }

    @Override
    protected void setupToolbar() {
        super.setupToolbar();

        documentTitle = getIntent().getStringExtra("document_title");
        documentPath = getIntent().getStringExtra("document_path");
        isPredefined = getIntent().getBooleanExtra("is_predefined", false);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(documentTitle != null ? documentTitle : getString(R.string.pdf_viewer_title));
        }
    }

    @Override
    protected void setupUI() {
        super.setupUI();

        initializeViews();
        setupRecyclerView();
        setupFABs();
        loadPDF();
    }

    private void initializeViews() {
        recyclerPDFPages = findViewById(R.id.recyclerPDFPages);
        fabZoomIn = findViewById(R.id.fabZoomIn);
        fabZoomOut = findViewById(R.id.fabZoomOut);
    }

    private void setupRecyclerView() {
        pages = new ArrayList<>();
        adapter = new PDFPagesAdapter(pages);
        recyclerPDFPages.setLayoutManager(new LinearLayoutManager(this));
        recyclerPDFPages.setAdapter(adapter);
    }

    private void setupFABs() {
        fabZoomIn.setOnClickListener(v -> {
            if (currentZoom < 3.0f) {
                currentZoom += 0.25f;
                renderPagesWithZoom();
            }
        });

        fabZoomOut.setOnClickListener(v -> {
            if (currentZoom > 0.5f) {
                currentZoom -= 0.25f;
                renderPagesWithZoom();
            }
        });
    }

    private void loadPDF() {
        new Thread(() -> {
            try {
                File pdfFile = null;

                if (isPredefined) {
                    // Para documentos predefinidos en assets
                    android.util.Log.d("PDFViewer", "Loading predefined PDF: " + documentPath);
                    pdfFile = copyAssetToTempFile(documentPath);
                } else {
                    // Para documentos de usuario
                    android.util.Log.d("PDFViewer", "Loading user PDF: " + documentPath);
                    pdfFile = new File(documentPath);
                }

                if (pdfFile != null && pdfFile.exists()) {
                    android.util.Log.d("PDFViewer", "PDF file found, size: " + pdfFile.length());
                    fileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY);
                    pdfRenderer = new PdfRenderer(fileDescriptor);

                    renderAllPages();
                } else {
                    android.util.Log.e("PDFViewer", "PDF file not found: " + (pdfFile != null ? pdfFile.getAbsolutePath() : "null"));
                    runOnUiThread(() -> {
                        Toast.makeText(this, R.string.error_file_not_found, Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            } catch (Exception e) {
                android.util.Log.e("PDFViewer", "Error loading PDF", e);
                runOnUiThread(() -> {
                    Toast.makeText(this, R.string.error_loading_pdf + ": " + e.getMessage(), Toast.LENGTH_LONG).show();
                    finish();
                });
            }
        }).start();
    }

    private File copyAssetToTempFile(String assetPath) {
        try {
            android.util.Log.d("PDFViewer", "Copying asset: " + assetPath);

            // Listar archivos en predefined_docs para debug
            try {
                String[] files = getAssets().list("predefined_docs");
                android.util.Log.d("PDFViewer", "Available files in predefined_docs:");
                if (files != null) {
                    for (String file : files) {
                        android.util.Log.d("PDFViewer", "  - " + file);
                    }
                } else {
                    android.util.Log.w("PDFViewer", "No files found in predefined_docs");
                }
            } catch (Exception e) {
                android.util.Log.e("PDFViewer", "Error listing assets", e);
            }

            InputStream inputStream = getAssets().open(assetPath);
            File tempFile = new File(getCacheDir(), "temp_" + System.currentTimeMillis() + ".pdf");

            java.io.FileOutputStream outputStream = new java.io.FileOutputStream(tempFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            long totalBytes = 0;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                totalBytes += bytesRead;
            }

            inputStream.close();
            outputStream.close();

            android.util.Log.d("PDFViewer", "Asset copied successfully. Size: " + totalBytes + " bytes");
            return tempFile;
        } catch (IOException e) {
            android.util.Log.e("PDFViewer", "Error copying asset: " + assetPath, e);
            return null;
        }
    }

    private void renderAllPages() {
        if (pdfRenderer == null) return;

        try {
            pages.clear();
            int pageCount = pdfRenderer.getPageCount();

            for (int i = 0; i < pageCount; i++) {
                PdfRenderer.Page page = pdfRenderer.openPage(i);

                // Calcular dimensiones con zoom
                int width = (int) (page.getWidth() * currentZoom);
                int height = (int) (page.getHeight() * currentZoom);

                // Limitar dimensiones máximas para evitar OutOfMemoryError
                if (width > 2048) {
                    float ratio = 2048f / width;
                    width = 2048;
                    height = (int) (height * ratio);
                }

                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

                pages.add(bitmap);
                page.close();
            }

            runOnUiThread(() -> adapter.notifyDataSetChanged());

        } catch (Exception e) {
            runOnUiThread(() -> {
                Toast.makeText(this, R.string.error_rendering_pdf, Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void renderPagesWithZoom() {
        new Thread(this::renderAllPages).start();
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
        closePDFRenderer();
    }

    private void closePDFRenderer() {
        try {
            if (pdfRenderer != null) {
                pdfRenderer.close();
            }
            if (fileDescriptor != null) {
                fileDescriptor.close();
            }

            // Limpiar bitmaps para liberar memoria
            if (pages != null) {
                for (Bitmap bitmap : pages) {
                    if (bitmap != null && !bitmap.isRecycled()) {
                        bitmap.recycle();
                    }
                }
                pages.clear();
            }
        } catch (IOException e) {
            // Ignorar errores al cerrar
        }
    }
}