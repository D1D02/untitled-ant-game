package com.main.untitledantgame.utility.ecs;

import com.google.gson.JsonObject;

import com.main.untitledantgame.components.Component;
import com.main.untitledantgame.components.LifeComponent;
import com.main.untitledantgame.components.PositionComponent;
import com.main.untitledantgame.components.SpriteComponent;
import com.main.untitledantgame.visuals.Assets;

public class ComponentFactory {

    public static Component create(String name, JsonObject data) {

        switch (name) {

            case "life":
                LifeComponent life = new LifeComponent();
                life.setHp(data.get("hp").getAsFloat());
                return life;

            case "position":
                PositionComponent pos = new PositionComponent();
                pos.setX(data.get("x").getAsFloat());
                pos.setY(data.get("y").getAsFloat());
                return pos;

            case "sprite":
                SpriteComponent sprite = new SpriteComponent();
                if(data.get("pathname").getAsString().equals("anthill.png"))
                    sprite.setSprite(Assets.anthill);
                return sprite;

            default:
                return null;
        }
    }
}
