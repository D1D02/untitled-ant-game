package com.main.untitledantgame.components;

public class AntStatsComponent implements Component{
    float force;
    float max_distance;
    float range;

    public void setForce(float force) {
        this.force = force;
    }

    public float getForce() {
        return force;
    }

    public void setMax_distance(float max_distance) {
        this.max_distance = max_distance;
    }
    
    public float getMax_distance() {
        return max_distance;
    }

    public void setRange(float range) {
        this.range = range;
    }

    public float getRange() {
        return range;
    }
}
