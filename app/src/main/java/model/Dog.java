package model;

import java.util.ArrayList;

public class Dog {
    private int id;
    private String dog_name;
    private String owmer_name;
    private String breed;
    private String province;
    private String location;
    private String description;
    private ArrayList<String> tags;

    public Dog() {
    }

    public Dog(int id, String dog_name, String owmer_name, String breed, String province, String location,ArrayList<String> tags, String description) {
        this.id = id;
        this.dog_name = dog_name;
        this.owmer_name = owmer_name;
        this.breed = breed;
        this.province = province;
        this.location = location;
        this.description = description;
        this.tags = tags;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDog_name() {
        return dog_name;
    }

    public void setDog_name(String dog_name) {
        this.dog_name = dog_name;
    }

    public String getOwmer_name() {
        return owmer_name;
    }

    public void setOwmer_name(String owmer_name) {
        this.owmer_name = owmer_name;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }
}
