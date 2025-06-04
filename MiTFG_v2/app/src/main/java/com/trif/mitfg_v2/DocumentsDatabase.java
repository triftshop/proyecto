package com.trif.mitfg_v2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

public class DocumentsDatabase {
    private DocumentsDBHelper helper;
    private SQLiteDatabase db;

    public DocumentsDatabase(Context context) {
        helper = new DocumentsDBHelper(context);
    }

    public void open() {
        db = helper.getWritableDatabase();
    }

    public void close() {
        if (helper != null) {
            helper.close();
        }
    }

    public long insertDocument(DocumentItem document) {
        ContentValues values = new ContentValues();
        values.put(DocumentsDBHelper.COLUMN_TITLE, document.getTitle());
        values.put(DocumentsDBHelper.COLUMN_FILE_PATH, document.getFilePath());
        values.put(DocumentsDBHelper.COLUMN_THUMBNAIL_PATH, document.getThumbnailPath());
        values.put(DocumentsDBHelper.COLUMN_DATE_ADDED, document.getDateAdded());
        values.put(DocumentsDBHelper.COLUMN_IS_PREDEFINED, document.isPredefined() ? 1 : 0);
        values.put(DocumentsDBHelper.COLUMN_AUTONOMOUS_REGION, document.getAutonomousRegion());

        return db.insert(DocumentsDBHelper.TABLE_DOCUMENTS, null, values);
    }

    public boolean deleteDocument(int id) {
        return db.delete(DocumentsDBHelper.TABLE_DOCUMENTS,
                DocumentsDBHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}) > 0;
    }

    public boolean updateDocument(DocumentItem document) {
        ContentValues values = new ContentValues();
        values.put(DocumentsDBHelper.COLUMN_TITLE, document.getTitle());
        values.put(DocumentsDBHelper.COLUMN_FILE_PATH, document.getFilePath());
        values.put(DocumentsDBHelper.COLUMN_THUMBNAIL_PATH, document.getThumbnailPath());
        values.put(DocumentsDBHelper.COLUMN_AUTONOMOUS_REGION, document.getAutonomousRegion());

        return db.update(DocumentsDBHelper.TABLE_DOCUMENTS, values,
                DocumentsDBHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(document.getId())}) > 0;
    }

    public DocumentItem getDocument(int id) {
        Cursor cursor = db.query(DocumentsDBHelper.TABLE_DOCUMENTS, null,
                DocumentsDBHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);

        if (cursor.moveToFirst()) {
            DocumentItem document = createDocumentFromCursor(cursor);
            cursor.close();
            return document;
        }
        cursor.close();
        return null;
    }

    public List<DocumentItem> getAllDocuments() {
        List<DocumentItem> documents = new ArrayList<>();
        Cursor cursor = db.query(DocumentsDBHelper.TABLE_DOCUMENTS, null, null, null, null, null,
                DocumentsDBHelper.COLUMN_DATE_ADDED + " DESC");

        while (cursor.moveToNext()) {
            documents.add(createDocumentFromCursor(cursor));
        }
        cursor.close();
        return documents;
    }

    public List<DocumentItem> getPredefinedDocuments() {
        List<DocumentItem> documents = new ArrayList<>();
        Cursor cursor = db.query(DocumentsDBHelper.TABLE_DOCUMENTS, null,
                DocumentsDBHelper.COLUMN_IS_PREDEFINED + " = ?",
                new String[]{"1"}, null, null,
                DocumentsDBHelper.COLUMN_AUTONOMOUS_REGION + " ASC");

        while (cursor.moveToNext()) {
            documents.add(createDocumentFromCursor(cursor));
        }
        cursor.close();
        return documents;
    }

    public List<DocumentItem> getUserDocuments() {
        List<DocumentItem> documents = new ArrayList<>();
        Cursor cursor = db.query(DocumentsDBHelper.TABLE_DOCUMENTS, null,
                DocumentsDBHelper.COLUMN_IS_PREDEFINED + " = ?",
                new String[]{"0"}, null, null,
                DocumentsDBHelper.COLUMN_DATE_ADDED + " DESC");

        while (cursor.moveToNext()) {
            documents.add(createDocumentFromCursor(cursor));
        }
        cursor.close();
        return documents;
    }

    private DocumentItem createDocumentFromCursor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(DocumentsDBHelper.COLUMN_ID));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(DocumentsDBHelper.COLUMN_TITLE));
        String filePath = cursor.getString(cursor.getColumnIndexOrThrow(DocumentsDBHelper.COLUMN_FILE_PATH));
        String thumbnailPath = cursor.getString(cursor.getColumnIndexOrThrow(DocumentsDBHelper.COLUMN_THUMBNAIL_PATH));
        long dateAdded = cursor.getLong(cursor.getColumnIndexOrThrow(DocumentsDBHelper.COLUMN_DATE_ADDED));
        boolean isPredefined = cursor.getInt(cursor.getColumnIndexOrThrow(DocumentsDBHelper.COLUMN_IS_PREDEFINED)) == 1;
        String autonomousRegion = cursor.getString(cursor.getColumnIndexOrThrow(DocumentsDBHelper.COLUMN_AUTONOMOUS_REGION));

        return new DocumentItem(id, title, filePath, thumbnailPath, dateAdded, isPredefined, autonomousRegion);
    }
}