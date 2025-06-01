package com.trif.mitfg_v2;

public class KnotItem {
    private int id;
    private String name;
    private String description;
    private String iconName;

    public KnotItem(int id, String name, String description, String iconName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.iconName = iconName;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getIconName() { return iconName; }
}