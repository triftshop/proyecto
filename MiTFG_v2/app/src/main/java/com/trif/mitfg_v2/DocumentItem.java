// ARCHIVO: app/src/main/java/com/trif/mitfg_v2/DocumentItem.java
// ACCIÓN: REEMPLAZAR completamente tu archivo existente

package com.trif.mitfg_v2;

public class DocumentItem {
    private int id;
    private String title;
    private String filePath;
    private String thumbnailPath;
    private long dateAdded;
    private boolean isPredefined;
    private String autonomousRegion;

    // Constructor completo (para cargar desde BD)
    public DocumentItem(int id, String title, String filePath, String thumbnailPath,
                        long dateAdded, boolean isPredefined, String autonomousRegion) {
        this.id = id;
        this.title = title;
        this.filePath = filePath;
        this.thumbnailPath = thumbnailPath;
        this.dateAdded = dateAdded;
        this.isPredefined = isPredefined;
        this.autonomousRegion = autonomousRegion;
    }

    // Constructor para documentos de usuario (nuevos)
    public DocumentItem(String title, String filePath, String thumbnailPath) {
        this.title = title;
        this.filePath = filePath;
        this.thumbnailPath = thumbnailPath;
        this.dateAdded = System.currentTimeMillis();
        this.isPredefined = false;
        this.autonomousRegion = null;
    }

    // Constructor para documentos predefinidos
    public DocumentItem(String title, String filePath, String thumbnailPath, String autonomousRegion) {
        this.title = title;
        this.filePath = filePath;
        this.thumbnailPath = thumbnailPath;
        this.dateAdded = System.currentTimeMillis();
        this.isPredefined = true;
        this.autonomousRegion = autonomousRegion;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }

    public boolean isPredefined() {
        return isPredefined;
    }

    public void setPredefined(boolean predefined) {
        isPredefined = predefined;
    }

    public String getAutonomousRegion() {
        return autonomousRegion;
    }

    public void setAutonomousRegion(String autonomousRegion) {
        this.autonomousRegion = autonomousRegion;
    }

    // Métodos adicionales para compatibilidad (si los necesitas)
    public String getName() {
        return getTitle();
    }

    public void setName(String name) {
        setTitle(name);
    }
}