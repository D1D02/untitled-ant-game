package com.main.untitledantgame.components;

public class PositionComponent implements Component{
    float x;
    float y;

    public PositionComponent(){}

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }
}
