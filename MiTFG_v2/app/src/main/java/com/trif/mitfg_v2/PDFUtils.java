package com.trif.mitfg_v2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PDFUtils {
    private static final String TAG = "PDFUtils";

    /**
     * Copia un archivo PDF desde URI a almacenamiento interno
     */
    public static String copyPDFToInternalStorage(Context context, Uri sourceUri, String fileName) {
        try {
            File documentsDir = new File(context.getFilesDir(), "documents");
            if (!documentsDir.exists()) {
                documentsDir.mkdirs();
            }

            File destinationFile = new File(documentsDir, fileName);

            InputStream inputStream = context.getContentResolver().openInputStream(sourceUri);
            FileOutputStream outputStream = new FileOutputStream(destinationFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outputStream.close();

            return destinationFile.getAbsolutePath();
        } catch (IOException e) {
            Log.e(TAG, "Error copying PDF file", e);
            return null;
        }
    }

    /**
     * Genera una miniatura de la primera página del PDF
     */
    public static String generatePDFThumbnail(Context context, String pdfPath) {
        try {
            File pdfFile = new File(pdfPath);
            if (!pdfFile.exists()) {
                return null;
            }

            ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(
                    pdfFile, ParcelFileDescriptor.MODE_READ_ONLY);

            PdfRenderer pdfRenderer = new PdfRenderer(fileDescriptor);

            if (pdfRenderer.getPageCount() > 0) {
                PdfRenderer.Page page = pdfRenderer.openPage(0);

                // Crear bitmap para la miniatura
                int width = 200;
                int height = (int) ((float) width * page.getHeight() / page.getWidth());
                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

                // Guardar miniatura
                File thumbnailsDir = new File(context.getFilesDir(), "thumbnails");
                if (!thumbnailsDir.exists()) {
                    thumbnailsDir.mkdirs();
                }

                String thumbnailFileName = "thumb_" + System.currentTimeMillis() + ".png";
                File thumbnailFile = new File(thumbnailsDir, thumbnailFileName);

                FileOutputStream out = new FileOutputStream(thumbnailFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.close();

                page.close();
                pdfRenderer.close();
                fileDescriptor.close();

                return thumbnailFile.getAbsolutePath();
            }

            pdfRenderer.close();
            fileDescriptor.close();

        } catch (Exception e) {
            Log.e(TAG, "Error generating PDF thumbnail", e);
        }

        return null;
    }

    /**
     * Verifica si un archivo es un PDF válido
     */
    public static boolean isPDFFile(Context context, Uri uri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            byte[] header = new byte[4];
            inputStream.read(header);
            inputStream.close();

            // Verificar magic number del PDF (%PDF)
            return header[0] == 0x25 && header[1] == 0x50 &&
                    header[2] == 0x44 && header[3] == 0x46;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Obtiene el nombre del archivo desde URI
     */
    public static String getFileName(Context context, Uri uri) {
        String fileName = "document_" + System.currentTimeMillis() + ".pdf";

        try {
            android.database.Cursor cursor = context.getContentResolver().query(
                    uri, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(
                        android.provider.OpenableColumns.DISPLAY_NAME);
                if (nameIndex != -1) {
                    String name = cursor.getString(nameIndex);
                    if (name != null && !name.isEmpty()) {
                        fileName = name;
                    }
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting file name", e);
        }

        return fileName;
    }

    /**
     * Elimina archivo y su miniatura
     */
    public static boolean deleteDocumentFiles(String pdfPath, String thumbnailPath) {
        boolean success = true;

        if (pdfPath != null) {
            File pdfFile = new File(pdfPath);
            if (pdfFile.exists()) {
                success &= pdfFile.delete();
            }
        }

        if (thumbnailPath != null) {
            File thumbnailFile = new File(thumbnailPath);
            if (thumbnailFile.exists()) {
                success &= thumbnailFile.delete();
            }
        }

        return success;
    }
}