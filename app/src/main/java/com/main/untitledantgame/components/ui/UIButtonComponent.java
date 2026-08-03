package com.main.untitledantgame.components.ui;

import com.badlogic.androidgames.framework.Color;
import com.badlogic.androidgames.framework.Graphics;
import com.badlogic.androidgames.framework.Input;
import com.main.untitledantgame.utility.ecs.Entity;

public class UIButtonComponent extends UIElementComponent{

    OnClick onClickEvent;
    public int color = Color.convert(100, 100, 100, 255);
    public String text = "BUTTON";
    public int textColor = Color.convert(0, 0, 0, 255);

    @Override
    public void draw(Graphics g) {
        int relativeX = getRelativeX(g),
                relativeY = getRelativeY(g),
                relativeHeight = getRelativeHeight(g),
                relativeWidth = getRelativeWidth(g);

        g.drawRect(relativeX, relativeY,relativeWidth, relativeHeight, color);
        g.drawText(text, relativeX, relativeY+(relativeHeight/2), textColor);
    }

    public static void setOnClickEvent(Entity e, OnClick event){
        e.getComponent(UIButtonComponent.class).onClickEvent = event;
    }

    public static void check(Entity e, Input.TouchEvent event, Graphics g) {
        if(!e.hasComponent(UIButtonComponent.class))
            return;

        UIButtonComponent button = e.getComponent(UIButtonComponent.class);

        if(!button.enabled)
            return;

        int relativeX = button.getRelativeX(g), relativeY = button.getRelativeY(g);
        if(event.x > relativeX && event.x < relativeX + button.getRelativeWidth(g) - 1 &&
                event.y > relativeY && event.y < relativeY + button.getRelativeHeight(g) - 1)
            button.onClickEvent.run();
    }
}
