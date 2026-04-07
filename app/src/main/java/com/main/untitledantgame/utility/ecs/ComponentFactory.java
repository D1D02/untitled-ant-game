package com.main.untitledantgame.utility.ecs;

import android.util.Log;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Graphics;
import com.badlogic.androidgames.framework.Pixmap;

import com.google.fpl.liquidfun.*;

import com.google.gson.JsonObject;

import com.main.untitledantgame.components.*;
import com.main.untitledantgame.utility.ecs.Entity;

public class ComponentFactory {

    public static Component create(String name, JsonObject data, 
                                   World world, Game game, Entity entity) {

        switch (name) {

            case "ai":
                AIComponent ai = new AIComponent();
                ai.wanderAngle = data.get("wander_angle").getAsFloat();
                ai.maxSpeed = data.get("max_speed").getAsFloat();
                return ai;

            case "alive":
                AliveComponent al = new AliveComponent();
                al.ttl = data.get("ttl").getAsFloat();
                return al;

            case "body":

                BodyComponent bc = new BodyComponent();
                BodyDef bDef = new BodyDef();

                String typeStr = data.get("type").getAsString();
                bDef.setType(typeStr.equals("dynamicBody") ?
                        BodyType.dynamicBody : BodyType.staticBody);

                if (data.has("position")) {
                    JsonObject pos = data.getAsJsonObject("position");
                    bDef.setPosition(
                            pos.get("x").getAsFloat(),
                            pos.get("y").getAsFloat()
                    );
                }

                bc.body = world.createBody(bDef);

                bc.body.setUserData(entity);

                if (data.has("fixture") && data.has("polygon_shape")) {

                    JsonObject fixData = data.getAsJsonObject("fixture");
                    JsonObject poly = data.getAsJsonObject("polygon_shape");

                    PolygonShape shape = new PolygonShape();
                    shape.setAsBox(
                            poly.get("dimension_1").getAsFloat(),
                            poly.get("dimension_2").getAsFloat()
                    );

                    FixtureDef fDef = new FixtureDef();
                    fDef.setShape(shape);
                    fDef.setDensity(fixData.get("density").getAsFloat());
                    fDef.setFriction(fixData.get("friction").getAsFloat());
                    fDef.setRestitution(fixData.get("restitution").getAsFloat());

                    bc.body.createFixture(fDef);

                    shape.delete();
                    fDef.delete();
                }

                bDef.delete();
                return bc;

            case "food":
                FoodComponent food = new FoodComponent();
                food.capacity = data.get("capacity").getAsInt();
                food.food = data.get("food").getAsInt();
                food.consuming_timer = data.get("consuming_timer").getAsInt();
                food.last_consumed = data.get("last_consumed").getAsInt();
                return food;

            case "identifier":
                IdentifierComponent id = new IdentifierComponent();
                id.inc_id = data.get("inc_id").getAsInt();
                return id;

            case "life":
                LifeComponent life = new LifeComponent();
                life.hp = data.get("hp").getAsFloat();
                return life;

            case "sprite":
                SpriteComponent sprite = new SpriteComponent();
                String path = data.get("pathname").getAsString();

                try {
                    Pixmap pixmap = game.getGraphics()
                            .newPixmap(path, Graphics.PixmapFormat.ARGB8888);
                    sprite.sprite = pixmap;
                } catch (Exception e) {
                    Log.e("ECS", "Errore sprite: " + path);
                }

                return sprite;

            default:
                return null;
        }
    }
}