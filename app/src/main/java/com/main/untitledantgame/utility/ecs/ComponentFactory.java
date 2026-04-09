package com.main.untitledantgame.utility.ecs;

import com.badlogic.androidgames.framework.Color;
import com.google.gson.JsonObject;

import com.main.untitledantgame.components.Component;
import com.main.untitledantgame.components.LifeComponent;
import com.main.untitledantgame.components.PositionComponent;
import com.main.untitledantgame.components.SpriteComponent;
import com.main.untitledantgame.components.ui.UIBarComponent;
import com.main.untitledantgame.components.ui.UIButtonComponent;
import com.main.untitledantgame.components.ui.UIElementComponent;
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
            case "uibar":
                UIBarComponent barComponent = new UIBarComponent();
                barComponent.posX = data.get("x").getAsInt();
                barComponent.posY = data.get("y").getAsInt();
                barComponent.width = data.get("width").getAsInt();
                barComponent.height = data.get("height").getAsInt();

                barComponent.background = Color.convert(data.get("bgR").getAsInt(), data.get("bgG").getAsInt(),data.get("bgB").getAsInt(), 255);
                barComponent.fill = Color.convert(data.get("fillR").getAsInt(), data.get("fillG").getAsInt(),data.get("fillB").getAsInt(), 255);

                return barComponent;
            case"uibutton":
                UIButtonComponent buttonComponent = new UIButtonComponent();
                buttonComponent.posX = data.get("x").getAsInt();
                buttonComponent.posY = data.get("y").getAsInt();
                buttonComponent.width = data.get("width").getAsInt();
                buttonComponent.height = data.get("height").getAsInt();
                buttonComponent.color = Color.convert(data.get("R").getAsInt(),data.get("G").getAsInt(),data.get("B").getAsInt(),255);
                buttonComponent.text = data.get("text").getAsString();
                buttonComponent.textColor = Color.convert(data.get("textR").getAsInt(),data.get("textG").getAsInt(),data.get("textB").getAsInt(),255);

                return buttonComponent;
            default:
                return null;
        }
    }
}
