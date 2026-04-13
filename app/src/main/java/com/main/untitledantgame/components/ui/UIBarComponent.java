package com.main.untitledantgame.components.ui;

import com.badlogic.androidgames.framework.Color;
import com.badlogic.androidgames.framework.Graphics;

public class UIBarComponent extends UIElementComponent {
    public int background = Color.convert(150, 150,150, 255), fill = Color.convert(255, 255,255, 255);
    public float maxValue = 100, value = 50;

    @Override
    public void draw(Graphics g) {
        g.drawRect(getRelativeX(g), getRelativeY(g),getRelativeWidth(g), getRelativeHeight(g), background);
        int fillWidth = (int)((float)getRelativeWidth(g) * (value/maxValue));
        g.drawRect(getRelativeX(g), getRelativeY(g),fillWidth,getRelativeHeight(g), fill);
    }
}
