package com.main.untitledantgame.components;

public class AnimatedSpriteComponent extends SpriteComponent{
    public float frameDuration = 1;
    public float frameTimer = 0;

    public void update(float deltaTime){
        frameTimer += deltaTime;
        while(frameTimer > frameDuration){
            frameTimer -= frameDuration;
            windowX = (windowX+windowWidth)%sprite.getWidth();
        }
    }
}
