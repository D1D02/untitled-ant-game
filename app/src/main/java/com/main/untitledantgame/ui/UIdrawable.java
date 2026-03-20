package com.main.untitledantgame.ui;

import com.badlogic.androidgames.framework.Graphics;

abstract public class UIdrawable {

    int posX = 0, posY = 0;
    int width = 100, height = 20;
    public abstract void draw(Graphics g);
}
