package com.example.smarttourismapp.ui.onboarding;

public class OnboardingItem {
    private final int imageId;
    private final String title;
    private final String description;

    public OnboardingItem(int imageId, String title, String description){
        this.imageId =  imageId;
        this.title = title;
        this.description = description;
    }


    public int getImageId () {return imageId;}
    public String getTitle () {return title;}
    public String getDescription () {return description;}
}
