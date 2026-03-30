package com.main.untitledantgame.utility.ecs;

import com.google.gson.JsonObject;

import com.main.untitledantgame.components.Component;
import com.main.untitledantgame.components.FoodComponent;
import com.main.untitledantgame.components.LifeComponent;
import com.main.untitledantgame.components.PositionComponent;
import com.main.untitledantgame.components.SpriteComponent;
import com.main.untitledantgame.visuals.Assets;

public class ComponentFactory {

    public static Component create(String name, JsonObject data) {

        switch (name) {

            case "food":
                FoodComponent food = new FoodComponent();

                food.capacity = (int)data.get("capacity").getAsFloat();
                food.consuming_timer = (int)data.get("consuming_timer").getAsFloat();
                food.capacity = data.get("hp").getAsFloat();
                
                return food;

            case "life":
                LifeComponent life = new LifeComponent();
                life.hp = data.get("hp").getAsFloat();
                return life;

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
