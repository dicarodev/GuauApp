package com.guauapp.model;
import java.io.Serializable;
import java.util.List;
public class Dog implements Serializable {
        private String id;
        private String dog_name;
        private String owner_name;
        private String breed;
        private String province;
        private String location;
        private List<String> tags;
        private String description;

        public Dog() {
        }

        public Dog(String id, String dog_name, String owner_name, String breed, String province, String location, List<String> tags, String description) {
                this.id = id;
                this.dog_name = dog_name;
                this.owner_name = owner_name;
                this.breed = breed;
                this.province = province;
                this.location = location;
                this.tags = tags;
                this.description = description;
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

        public List<String> getTags() {
                return tags;
        }

        public void setTags(List<String> tags) {
                this.tags = tags;
        }

        public String getDescription() {
                return description;
        }

        public void setDescription(String description) {
                this.description = description;
        }
}
