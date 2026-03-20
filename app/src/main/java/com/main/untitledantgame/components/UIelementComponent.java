package com.main.untitledantgame.components;

import com.badlogic.androidgames.framework.Graphics;

public abstract class UIelement implements Component{

    int posX = 0, posY = 0;
    int width = 100, height = 20;
    public abstract void draw(Graphics g);
}
