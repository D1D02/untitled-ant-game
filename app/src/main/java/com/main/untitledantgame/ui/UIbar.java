package com.main.untitledantgame.ui;

import com.badlogic.androidgames.framework.Color;
import com.badlogic.androidgames.framework.Graphics;

public class UIbar extends UIdrawable{
    int background = Color.convert(150, 150,150, 255), fill = Color.convert(255, 255,255, 255);
    float maxValue = 100, value = 50;
    public void draw(Graphics g){
        g.drawRect(posX, posY,width, height, background);
        int fillWidth = (int)((float)width * (value/maxValue));
        g.drawRect(posX, posY,fillWidth,height, fill);
    }

    public void setBackground(int c){
        background = c;
    }

    public void setFill(int c){
        fill = c;
    }
}
