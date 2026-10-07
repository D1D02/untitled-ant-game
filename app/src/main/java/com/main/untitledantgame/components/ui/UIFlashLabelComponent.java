package com.main.untitledantgame.components.ui;

import com.badlogic.androidgames.framework.Color;
import com.badlogic.androidgames.framework.Graphics;
import com.main.untitledantgame.visuals.Assets;

public class UIFlashLabelComponent extends UILabelComponent{
    float alpha = 0;
    int flashPhase = 0;

    public boolean play = false;

    public UIFlashLabelComponent(){
        enabled = false;
    }
    @Override
    public void draw(Graphics g) {
        int relativeX = getRelativeX(g),
                relativeY = getRelativeY(g),
                relativeHeight = getRelativeHeight(g),
                relativeWidth = getRelativeWidth(g);

        //g.drawRect(relativeX, relativeY,relativeWidth, relativeHeight, color);
        g.drawPixmap(Assets.button, relativeX, relativeY, 0, 0, Assets.button.getWidth(), Assets.button.getHeight(), relativeWidth, relativeHeight, (int)Math.min(255, alpha));
        g.drawText(text, relativeX+(relativeWidth/6), relativeY+(relativeHeight/2)+10, textColor);
    }
    public void update(float deltaTime){
        if(!play)
            return;
        else if(!enabled)
            enabled = true;

        if(flashPhase == 0){
            alpha += 500 * deltaTime;
            if(alpha>=350)
                flashPhase = 1;
        }else if(flashPhase == 1){
            alpha -= 200 * deltaTime;
            if(alpha<0){
                alpha = 0;
                play = false;
                flashPhase = 0;
                enabled = false;
            }
        }

        textColor = Color.convert(255, 255, 0, (int)Math.min(alpha, 255));
    }


}
