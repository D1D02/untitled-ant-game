package com.main.untitledantgame.components;

public class AliveComponent implements Component {
    public float ttl;

    public boolean isDead( float deltaTime ) {
        this.ttl -= deltaTime;
        return this.ttl <= 0 ? true : false;        
    }
}
