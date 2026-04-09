package com.main.untitledantgame.components.ui;

import com.badlogic.androidgames.framework.Graphics;
import com.main.untitledantgame.components.Component;

public abstract class UIElementComponent implements Component {

    public int posX = 0, posY = 0;
    public int width = 100, height = 20;

    public abstract void draw(Graphics g);

    public int getRelativeWidth(Graphics g){
        return (int)((float)g.getWidth() * width /100);
    }
    public int getRelativeHeight(Graphics g){
        return (int)((float)g.getHeight() * height /100);
    }

    public int getRelativeX(Graphics g){
        return (int)((float)g.getWidth() * posX/100);
    }

    public int getRelativeY(Graphics g){
        return (int)((float)g.getHeight() * posY/100);
    }
}
