package com.guauapp.model;
import java.io.Serializable;
import java.util.ArrayList;
public class Dog implements Serializable {
        private String id;
        private String dog_name;
        private String owner_name;
        private String breed;
        private String province;
        private String location;
        private String description;
        private String gender;
        private String age;
        private String castrated;
        private ArrayList<String> images;

        public Dog() {
        }

        public Dog(String id, String dog_name, String owner_name, String breed, String province, String location, String description, String gender, String age, String castrated, ArrayList<String> images) {
                this.id = id;
                this.dog_name = dog_name;
                this.owner_name = owner_name;
                this.breed = breed;
                this.province = province;
                this.location = location;
                this.description = description;
                this.gender = gender;
                this.age = age;
                this.castrated = castrated;
                this.images = images;
        }

        public String getId() {
                return id;
        }

        public void setId(String id) {
                this.id = id;
        }

        public String getDog_name() {
                return dog_name;
        }

        public void setDog_name(String dog_name) {
                this.dog_name = dog_name;
        }

        public String getOwner_name() {
                return owner_name;
        }

        public void setOwner_name(String owner_name) {
                this.owner_name = owner_name;
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

        public String getAge() {
                return age;
        }

        public void setAge(String age) {
                this.age = age;
        }

        public String getCastrated() {
                return castrated;
        }

        public void setCastrated(String castrated) {
                this.castrated = castrated;
        }

        public ArrayList<String> getImages() {
                return images;
        }

        public void setImages(ArrayList<String> images) {
                this.images = images;
        }

        public String getGender() {
                return gender;
        }

        public void setGender(String gender) {
                this.gender = gender;
        }
}
