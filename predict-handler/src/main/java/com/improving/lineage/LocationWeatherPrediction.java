package com.improving.lineage;

import jakarta.persistence.*;

@Entity
@Table(name = "weather_prediction")
public class LocationWeatherPrediction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String city;
    private String state;
    private int currentTemp;
    private int predictLow;
    private int predictHigh;

    public LocationWeatherPrediction() {}
    public LocationWeatherPrediction(Long id, String city, String state, int currentTemp, int predictLow, int predictHigh) {
        this.id = id;
        this.city = city;
        this.state = state;
        this.currentTemp = currentTemp;
        this.predictLow = predictLow;
        this.predictHigh = predictHigh;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getCurrentTemp() {
        return currentTemp;
    }

    public void setCurrentTemp(int currentTemp) {
        this.currentTemp = currentTemp;
    }

    public int getPredictLow() {
        return predictLow;
    }

    public void setPredictLow(int predictLow) {
        this.predictLow = predictLow;
    }

    public int getPredictHigh() {
        return predictHigh;
    }

    public void setPredictHigh(int predictHigh) {
        this.predictHigh = predictHigh;
    }
}
