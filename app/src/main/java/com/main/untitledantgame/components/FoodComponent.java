package com.main.untitledantgame.components;

public class FoodComponent implements Component {
    public int capacity;
    public int food;
    public float consuming_timer;
    public float last_consumed = 0;


    public boolean isFoodEnded(float deltaTime) {

        this.last_consumed += deltaTime;

        if( this.last_consumed > this.consuming_timer ){
            this.last_consumed -= this.consuming_timer;
            if(this.food == 0) return true;
        }

        return false;

    }

}
