package com.example.weatherforecastapp;

public class Details {
    String attribute;
    String icon;
    String value;

    public Details(String attri,String icon, String value) {
        this.attribute = attri;
        this.icon = icon;
        this.value = value;
    }

    public String getAttri() {
        return attribute;
    }

    public void setAttri(String attri) {
        this.attribute = attri;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
