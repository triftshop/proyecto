package com.trif.mitfg_v2;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.io.InputStream;

public class DocumentsDBHelper extends SQLiteOpenHelper {
    private static final String TAG = "DocumentsDBHelper";
    private static final int DATABASE_VERSION = 3; // Incrementamos para forzar recreación completa
    private static final String DATABASE_NAME = "PescAppDocuments.db";

    // Tabla de documentos
    public static final String TABLE_DOCUMENTS = "documents";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_FILE_PATH = "file_path";
    public static final String COLUMN_THUMBNAIL_PATH = "thumbnail_path";
    public static final String COLUMN_DATE_ADDED = "date_added";
    public static final String COLUMN_IS_PREDEFINED = "is_predefined";
    public static final String COLUMN_AUTONOMOUS_REGION = "autonomous_region";

    private static final String SQL_CREATE_DOCUMENTS_TABLE =
            "CREATE TABLE " + TABLE_DOCUMENTS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_TITLE + " TEXT NOT NULL," +
                    COLUMN_FILE_PATH + " TEXT NOT NULL," +
                    COLUMN_THUMBNAIL_PATH + " TEXT," +
                    COLUMN_DATE_ADDED + " INTEGER NOT NULL," +
                    COLUMN_IS_PREDEFINED + " INTEGER DEFAULT 0," +
                    COLUMN_AUTONOMOUS_REGION + " TEXT" +
                    ");";

    private static final String SQL_DELETE_DOCUMENTS_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_DOCUMENTS;

    private Context context;

    public DocumentsDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Log.d(TAG, "Creating database table...");
            db.execSQL(SQL_CREATE_DOCUMENTS_TABLE);
            Log.d(TAG, "Table created successfully");

            insertTestDocuments(db);
        } catch (SQLException e) {
            Log.e(TAG, "Error creating table", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
        db.execSQL(SQL_DELETE_DOCUMENTS_TABLE);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    private void insertTestDocuments(SQLiteDatabase db) {
        Log.d(TAG, "=== STARTING COMPLETE DOCUMENT INSERTION ===");

        // Primero verificar qué tenemos en assets
        checkAssetsStructure();

        // Lista COMPLETA de todas las comunidades autónomas
        String[][] allRegions = {
                {"Andalucía", "normativa_andalucia.pdf"},
                {"Aragón", "normativa_aragon.pdf"},
                {"Asturias", "normativa_asturias.pdf"},
                {"Islas Baleares", "normativa_islas_baleares.pdf"},
                {"Canarias", "normativa_canarias.pdf"},
                {"Cantabria", "normativa_cantabria.pdf"},
                {"Castilla-La Mancha", "normativa_castilla_la_mancha.pdf"},
                {"Castilla y León", "normativa_castilla_y_leon.pdf"},
                {"Cataluña", "normativa_cataluna.pdf"},
                {"Extremadura", "normativa_extremadura.pdf"},
                {"Galicia", "normativa_galicia.pdf"},
                {"La Rioja", "normativa_la_rioja.pdf"},
                {"Madrid", "normativa_madrid.pdf"},
                {"Murcia", "normativa_murcia.pdf"},
                {"Navarra", "normativa_navarra.pdf"},
                {"País Vasco", "normativa_pais_vasco.pdf"},
                {"Valencia", "normativa_valencia.pdf"},
                {"Ceuta", "normativa_ceuta.pdf"},
                {"Melilla", "normativa_melilla.pdf"}
        };

        int foundFiles = 0;
        int totalFiles = allRegions.length;

        // Buscar TODOS los archivos
        for (String[] regionData : allRegions) {
            String region = regionData[0];
            String fileName = regionData[1];
            String fullPath = "predefined_docs/" + fileName;

            Log.d(TAG, "Checking file: " + fullPath);

            try {
                // Intentar abrir el archivo
                InputStream inputStream = context.getAssets().open(fullPath);
                long size = inputStream.available();
                inputStream.close();

                // El archivo existe y tiene contenido
                if (size > 0) {
                    String title = "Normativa de Pesca - " + region;

                    db.execSQL("INSERT INTO " + TABLE_DOCUMENTS + " (" +
                                    COLUMN_TITLE + ", " + COLUMN_FILE_PATH + ", " + COLUMN_DATE_ADDED + ", " +
                                    COLUMN_IS_PREDEFINED + ", " + COLUMN_AUTONOMOUS_REGION + ") VALUES (?, ?, ?, ?, ?)",
                            new Object[]{title, fullPath, System.currentTimeMillis(), 1, region});

                    Log.d(TAG, "✅ SUCCESS: Inserted " + region + " (size: " + size + " bytes)");
                    foundFiles++;
                } else {
                    Log.w(TAG, "❌ File exists but is empty: " + fullPath);
                }

            } catch (Exception e) {
                Log.w(TAG, "❌ File not found: " + fullPath + " - " + e.getMessage());
            }
        }

        Log.d(TAG, "📊 SUMMARY: Found " + foundFiles + " out of " + totalFiles + " expected files");

        // Si no encontramos ningún archivo, insertar documentos de prueba falsos
        if (foundFiles == 0) {
            Log.w(TAG, "⚠️ No PDF files found in assets. Inserting fake documents for testing...");
            insertFakeDocuments(db);
        }

        Log.d(TAG, "=== FINISHED COMPLETE DOCUMENT INSERTION ===");
    }

    private void checkAssetsStructure() {
        Log.d(TAG, "=== CHECKING ASSETS STRUCTURE ===");

        try {
            // Verificar carpeta raíz de assets
            String[] rootFiles = context.getAssets().list("");
            Log.d(TAG, "Files in assets root:");
            if (rootFiles != null && rootFiles.length > 0) {
                for (String file : rootFiles) {
                    Log.d(TAG, "  📁 " + file);
                }
            } else {
                Log.w(TAG, "  ❌ No files in assets root");
            }

            // Verificar carpeta predefined_docs
            String[] predefinedFiles = context.getAssets().list("predefined_docs");
            Log.d(TAG, "Files in predefined_docs:");
            if (predefinedFiles != null && predefinedFiles.length > 0) {
                for (String file : predefinedFiles) {
                    Log.d(TAG, "  📄 " + file);

                    // Verificar tamaño del archivo
                    try {
                        InputStream is = context.getAssets().open("predefined_docs/" + file);
                        long size = is.available();
                        is.close();
                        Log.d(TAG, "     Size: " + size + " bytes");
                    } catch (Exception e) {
                        Log.e(TAG, "     Error reading file size: " + e.getMessage());
                    }
                }
            } else {
                Log.w(TAG, "  ❌ No files in predefined_docs folder (or folder doesn't exist)");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error checking assets structure", e);
        }

        Log.d(TAG, "=== END ASSETS STRUCTURE CHECK ===");
    }

    private void insertFakeDocuments(SQLiteDatabase db) {
        Log.d(TAG, "Inserting fake documents for testing UI...");

        String[][] fakeRegions = {
                {"Andalucía", "📄 Sin archivo PDF"},
                {"Madrid", "📄 Sin archivo PDF"},
                {"Cataluña", "📄 Sin archivo PDF"}
        };

        for (String[] regionData : fakeRegions) {
            String region = regionData[0];
            String title = "Normativa de Pesca - " + region;
            String fakePath = "fake/" + region.toLowerCase().replace(" ", "_") + ".pdf";

            db.execSQL("INSERT INTO " + TABLE_DOCUMENTS + " (" +
                            COLUMN_TITLE + ", " + COLUMN_FILE_PATH + ", " + COLUMN_DATE_ADDED + ", " +
                            COLUMN_IS_PREDEFINED + ", " + COLUMN_AUTONOMOUS_REGION + ") VALUES (?, ?, ?, ?, ?)",
                    new Object[]{title, fakePath, System.currentTimeMillis(), 1, region});

            Log.d(TAG, "🔶 Inserted fake document: " + title);
        }
    }
}