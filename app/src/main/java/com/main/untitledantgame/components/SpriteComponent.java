package com.main.untitledantgame.components;

import com.badlogic.androidgames.framework.Pixmap;

public class SpriteComponent implements Component{
    Pixmap sprite;

    public SpriteComponent(){}

    public SpriteComponent(Pixmap sprite){
        this.sprite = sprite;
    }

    public void setSprite(Pixmap sprite){
        this.sprite=sprite;
    }

    public Pixmap getSprite(){
        return this.sprite;
    }

}
