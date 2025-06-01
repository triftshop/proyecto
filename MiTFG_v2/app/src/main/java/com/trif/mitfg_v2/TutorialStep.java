package com.trif.mitfg_v2;

public class TutorialStep {
    private String imageName;
    private String description;

    public TutorialStep(String imageName, String description) {
        this.imageName = imageName;
        this.description = description;
    }

    public String getImageName() {
        return imageName;
    }

    public String getDescription() {
        return description;
    }
}