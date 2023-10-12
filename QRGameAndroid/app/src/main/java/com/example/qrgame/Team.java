package com.example.qrgame;

public class Team {
    private Integer score;
    private String station;
    public Team(Integer score, String station){
        this.station = station;
        this.score = score;
    }
    public Team(){ }

    public Integer getScore() {
        return score;
    }
    public void setScore(Integer value){
        this.score = value;
    }

    public String getStation() {
        return station;
    }
    public void setStation(String value){
        this.station = value;
    }
}
