package com.main.untitledantgame.components;

import com.badlogic.androidgames.framework.Screen;
import com.main.untitledantgame.screen.GameScreen;

public class GeneratorComponent implements Component {
    public int start_delay = 0;
    public float ratio = 10;
    public float time_spent = 0;
    public float min_distance = 10;
    public float max_distance = 100;
    public String entity_name;

    public Boolean canBeGenerated( float time_passed ) {
        this.time_spent += time_passed;
    
        if( time_spent >= ratio ){ time_spent -= ratio; return true; }
        else{ return false; }
    }

    public float getRandomPosition(GameScreen game){
        float distance = ((max_distance - min_distance) * game.rand.nextFloat()) + min_distance;
        if(Math.random() < 0.5) distance = -distance;
        return distance;
    }
}
