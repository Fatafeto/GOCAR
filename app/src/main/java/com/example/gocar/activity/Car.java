package com.example.gocar.activity;

public class Car {

    String name;
    int production_year;
    int fuel_level;
    double longitude;
    double latitude;
    String image_path;

    public Car(String name , int production_year , int fuel_level , double longitude , double latitude , String image_path) {
        this.name = name;
        this.production_year = production_year;
        this.fuel_level = fuel_level;
        this.longitude = longitude;
        this.latitude = latitude;
        this.image_path = image_path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProduction_year() {
        return production_year;
    }

    public void setProduction_year(int production_year) {
        this.production_year = production_year;
    }

    public int getFuel_level() {
        return fuel_level;
    }

    public void setFuel_level(int fuel_level) {
        this.fuel_level = fuel_level;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }
}
