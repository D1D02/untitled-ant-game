package com.main.untitledantgame.utility.ecs;

import com.google.fpl.liquidfun.BodyDef;
import com.google.gson.JsonObject;
import com.main.untitledantgame.components.AIComponent;
import com.main.untitledantgame.components.AliveComponent;
import com.main.untitledantgame.components.Component;
import com.main.untitledantgame.components.FoodComponent;
import com.main.untitledantgame.components.IdentifierComponent;
import com.main.untitledantgame.components.LifeComponent;
import com.main.untitledantgame.components.PositionComponent;
import com.main.untitledantgame.components.SpriteComponent;
import com.main.untitledantgame.visuals.Assets;

import components.BodyComponent;

public class ComponentFactory {

    public static Component create(String name, JsonObject data) {

        switch (name) {

            case "ai":
                AIComponent ai = new AIComponent();
                ai.wanderAngle = (int)data.get("wander_angle").getAsFloat();
                ai.maxSpeed = (int)data.get("max_speed").getAsFloat();
                return ai;

            case "alive":
                AliveComponent al = new AliveComponent();
                al.ttl = (int)data.get("ttl").getAsFloat();
                return al;


            case "body":
                BodyComponent bc = new BodyComponent();
                
                BodyDef bDef = new BodyDef();
                
                String typeStr = data.get("type").getAsString();
                if (typeStr.equals("dynamicBody")) bDef.setType(BodyType.dynamicBody);
                else if (typeStr.equals("staticBody")) bDef.setType(BodyType.staticBody);
                else bDef.setType(BodyType.kinematicBody);

                if (data.has("position")) {
                    JsonObject pos = data.getAsJsonObject("position");
                    bDef.setPosition(pos.get("x").getAsFloat(), pos.get("y").getAsFloat());
                }
                
                if (data.has("angle")) bDef.setAngle(data.get("angle").getAsFloat());
                if (data.has("gravity_scale")) bDef.setGravityScale(data.get("gravity_scale").getAsFloat());
                if (data.has("fixed_rotation")) bDef.setFixedRotation(data.get("fixed_rotation").getAsBoolean());

                bc.body = world.createBody(bDef);

                PolygonShape shape = new PolygonShape();
                if (data.has("polygon_shape")) {
                    JsonObject poly = data.getAsJsonObject("polygon_shape");
                    float d1 = poly.get("dimension_1").getAsFloat();
                    float d2 = poly.get("dimension_2").getAsFloat();
                    shape.setAsBox(d1, d2);
                }

                FixtureDef fDef = new FixtureDef();
                fDef.setShape(shape);
                
                if (data.has("fixture")) {
                    JsonObject fix = data.getAsJsonObject("fixture");
                    fDef.setDensity(fix.get("density").getAsFloat());
                    fDef.setFriction(fix.get("friction").getAsFloat());
                    fDef.setRestitution(fix.get("restitution").getAsFloat());
                }

                bc.body.createFixture(fDef);

                shape.delete();
                fDef.delete();
                bDef.delete();

                return bc;
            
            case "food":
                FoodComponent food = new FoodComponent();

                food.capacity = (int)data.get("capacity").getAsFloat();
                food.consuming_timer = (int)data.get("consuming_timer").getAsFloat();
                food.capacity = data.get("hp").getAsFloat();
                
                return food;

            case "identifier":
                IdentifierComponent id = new IdentifierComponent(); 
                return id;

            case "life":
                LifeComponent life = new LifeComponent();
                life.hp = data.get("hp").getAsFloat();
                return life;

            case "sprite":
                SpriteComponent sprite = new SpriteComponent();
                String path = data.get("pathname").getAsString();

                try {
                    Pixmap pixmap = game.getGraphics().newPixmap(path, Graphics.PixmapFormat.ARGB8888);
                    sprite.setSprite(pixmap);
                } catch (Exception e) {
                    Log.e("ECS", "Impossibile caricare la pixmap dal path: " + path);
                }
                
                return sprite;

            default:
                return null;
        }
    }
}
