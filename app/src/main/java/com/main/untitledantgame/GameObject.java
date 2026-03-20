package com.main.untitledantgame;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.fpl.liquidfun.Body;
import com.main.untitledantgame.components.Component;
import com.main.untitledantgame.map.Box;
import com.main.untitledantgame.map.GameWorld;

import java.util.ArrayList;
import java.util.List;

public class GameObject {
    String name;
    List<Component> components;

    public GameObject(String name)
    {
        this.name = name;
        this.components = new ArrayList<Component>();
    }

    public void addComponent(Component c){
        components.add(c);
    }

}
