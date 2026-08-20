package com.main.untitledantgame.components.ui;

import com.badlogic.androidgames.framework.Graphics;
import com.badlogic.androidgames.framework.Pixmap;

public class UIAnimatedSpriteComponent extends UIElementComponent{

    public Pixmap sprite;
    public int windowX = 0;
    public int windowY = 0;
    public int windowWidth;
    public int windowHeight;
    public float frameDuration = 1;
    public float frameTimer = 0;

    public void update(float deltaTime){
        frameTimer += deltaTime;
        while(frameTimer > frameDuration){
            frameTimer -= frameDuration;
            windowX = (windowX+windowWidth)%sprite.getWidth();
        }
    }
    @Override
    public void draw(Graphics g) {
        int relativeX = getRelativeX(g),
                relativeY = getRelativeY(g),
                relativeHeight = getRelativeHeight(g),
                relativeWidth = getRelativeWidth(g);

        g.drawPixmap(sprite,relativeX, relativeY, windowX, windowY, windowWidth, windowHeight, relativeWidth, relativeHeight);
    }
}
