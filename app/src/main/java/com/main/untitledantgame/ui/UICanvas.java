package com.main.untitledantgame.ui;

import com.main.untitledantgame.utility.ecs.Entity;

import java.util.ArrayList;
import java.util.List;

public class UICanvas {
    public List<Entity> elements; //il public è più efficiente rispetto a getter e setter

    public UICanvas(){
        elements = new ArrayList<>();
    }

    public void addElement(Entity e){
        elements.add(e);
    }
}
