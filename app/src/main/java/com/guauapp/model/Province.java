package com.guauapp.model;

import java.util.List;

public class Province {
    private String province;
    private List<String> cities;

    public Province() {
    }

    public Province(String province, List<String> cities) {
        this.province = province;
        this.cities = cities;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public List<String> getCities() {
        return cities;
    }

    public void setCities(List<String> cities) {
        this.cities = cities;
    }

    @Override
    public String toString() {
        return province;
    }
}
