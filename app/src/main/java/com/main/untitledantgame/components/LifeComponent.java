package com.main.untitledantgame.components;

public class LifeComponent implements Component{
    float hp;

    public LifeComponent(){}

    public float getHp() {
        return hp;
    }

    public void setHp(float new_hp) {
        this.hp = new_hp;
    }

    public void addHp(float plus_hp) {
        this.hp = this.hp + plus_hp;
    }

    public void removeHp(float minus_hp) {
        this.hp = this.hp - minus_hp;
    }
}