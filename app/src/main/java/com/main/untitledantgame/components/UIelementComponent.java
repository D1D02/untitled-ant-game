package com.main.untitledantgame.components;

import com.badlogic.androidgames.framework.Graphics;
import com.main.untitledantgame.ui.UIdrawable;

public class UIelementComponent implements Component{

    UIdrawable drawable;

    public UIelementComponent(UIdrawable drawable) {
        this.drawable = drawable;
    }

    public void draw(Graphics g){
        drawable.draw(g);
    }
}
