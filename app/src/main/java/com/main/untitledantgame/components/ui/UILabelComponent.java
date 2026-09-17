package com.main.untitledantgame.components.ui;

import com.badlogic.androidgames.framework.Color;
import com.badlogic.androidgames.framework.Graphics;
import com.main.untitledantgame.visuals.Assets;

public class UILabelComponent extends UIElementComponent{
    public String text = "BUTTON";
    public int textColor;

    @Override
    public void draw(Graphics g) {
        int relativeX = getRelativeX(g),
                relativeY = getRelativeY(g),
                relativeHeight = getRelativeHeight(g),
                relativeWidth = getRelativeWidth(g);

        //g.drawRect(relativeX, relativeY,relativeWidth, relativeHeight, color);
        g.drawPixmap(Assets.button, relativeX, relativeY, 0, 0, Assets.button.getWidth(), Assets.button.getHeight(), relativeWidth, relativeHeight);
        g.drawText(text, relativeX+(relativeWidth/6), relativeY+(relativeHeight/2)+10, textColor);
    }
}
