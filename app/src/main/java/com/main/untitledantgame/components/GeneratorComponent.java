package com.main.untitledantgame.components;

public class GeneratorComponent implements Component {
    int start_delay = 0;
    float ratio = 10;
    float time_spent = 0;
    float min_distance = 10;
    float max_distance = 100;

    public Boolean canBeGenerated( float time_passed ) {
        this.time_spent += time_passed;
    
        if( time_spent >= ratio ){ time_spent -= ratio; return true; }
        else{ return false; }
    }

}
