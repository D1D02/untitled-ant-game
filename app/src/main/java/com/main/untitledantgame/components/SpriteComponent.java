package com.main.untitledantgame.components;

public class SpriteComponent implements Component{
    String path;

    public SpriteComponent(){}

    public String getPath(){
        return this.path;
    }

    public void setPath(String new_path){
        this.path = new_path;
    }
}
