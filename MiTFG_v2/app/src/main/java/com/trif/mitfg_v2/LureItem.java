package com.trif.mitfg_v2;

public class LureItem {
    private int id;
    private String name;
    private String description;
    private String iconName;
    private String type; // NUEVO CAMPO

    public LureItem(int id, String name, String description, String iconName, String type) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.iconName = iconName;
        this.type = type;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getIconName() { return iconName; }
    public String getType() { return type; } // NUEVO GETTER
}