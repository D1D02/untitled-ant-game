package com.main.untitledantgame.utility.ecs;

import android.util.Log;

import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.FixtureDef;
import com.google.fpl.liquidfun.PolygonShape;
import com.google.fpl.liquidfun.World;
import com.main.untitledantgame.components.AIComponent;
import com.main.untitledantgame.components.AliveComponent;
import com.main.untitledantgame.components.BodyComponent;
import com.main.untitledantgame.components.ui.UIButtonComponent;
import com.main.untitledantgame.components.ui.UIBarComponent;
import com.main.untitledantgame.components.FoodComponent;
import com.main.untitledantgame.components.IdentifierComponent;
import com.main.untitledantgame.components.GeneratorComponent;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Graphics;
import com.badlogic.androidgames.framework.Pixmap;

import com.badlogic.androidgames.framework.Color;
import com.google.gson.JsonObject;

import com.main.untitledantgame.components.*;
import com.main.untitledantgame.components.ui.UILabelComponent;

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
            case "sensor":
                BodyComponent bodyComp = entity.getComponent(BodyComponent.class);
                if (bodyComp == null || bodyComp.body == null) {
                    return null;
                }

                SensorComponent sc = new SensorComponent();
                JsonObject polySensor = data.getAsJsonObject("polygon_shape");

                PolygonShape sensorShape = new PolygonShape();
                sensorShape.setAsBox(
                        polySensor.get("dimension_1").getAsFloat(),
                        polySensor.get("dimension_2").getAsFloat()
                );

                FixtureDef sfDef = new FixtureDef();
                sfDef.setShape(sensorShape);
                sfDef.setDensity(0f);
                sfDef.setFriction(0f);
                sfDef.setRestitution(0f);

                sc.sensorFixture = bodyComp.body.createFixture(sfDef);

                sensorShape.delete();
                sfDef.delete();

                return sc;
            case "food":
                FoodComponent food = new FoodComponent();
                if(data.get("capacity") != null) food.capacity = data.get("capacity").getAsInt();
                food.food = data.get("food").getAsInt();
                if(data.get("consuming_timer") != null) food.consuming_timer = data.get("consuming_timer").getAsInt();
                if(data.get("last_consumed") != null) food.last_consumed = data.get("last_consumed").getAsInt();
                return food;

            case "identifier":
                IdentifierComponent id = new IdentifierComponent();
                id.inc_id = data.get("inc_id").getAsInt();
                return id;

            case "generator":
                GeneratorComponent generator = new GeneratorComponent();
                generator.start_delay = data.get("start_delay").getAsInt();
                generator.ratio = data.get("ratio").getAsInt();
                generator.time_spent = data.get("time_spent").getAsInt();
                generator.min_distance = data.get("min_distance").getAsInt();
                generator.max_distance = data.get("max_distance").getAsInt();
                generator.entity_name = data.get("entity_name").getAsString();

                return generator;

            case "life":
                LifeComponent life = new LifeComponent();
                life.hp = data.get("hp").getAsFloat();
                return life;

            case "sprite":
                SpriteComponent sprite = new SpriteComponent();

                sprite.width = data.get("width").getAsInt();
                sprite.height = data.get("height").getAsInt();
                String path = data.get("pathname").getAsString();

                try {
                    Pixmap pixmap = game.getGraphics()
                            .newPixmap(path, Graphics.PixmapFormat.ARGB8888);
                    sprite.sprite = pixmap;
                } catch (Exception e) {
                    Log.e("ECS", "Errore sprite: " + path);
                }

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
                //buttonComponent.color = Color.convert(data.get("R").getAsInt(),data.get("G").getAsInt(),data.get("B").getAsInt(),255);
                buttonComponent.text = data.get("text").getAsString();
                buttonComponent.textColor = Color.convert(data.get("textR").getAsInt(),data.get("textG").getAsInt(),data.get("textB").getAsInt(),255);

                return buttonComponent;
            case "uilabel":
                UILabelComponent labelComponent = new UILabelComponent();
                labelComponent.posX = data.get("x").getAsInt();
                labelComponent.posY = data.get("y").getAsInt();
                labelComponent.width = data.get("width").getAsInt();
                labelComponent.height = data.get("height").getAsInt();
                labelComponent.text = data.get("text").getAsString();
                labelComponent.textColor = Color.convert(data.get("textR").getAsInt(),data.get("textG").getAsInt(),data.get("textB").getAsInt(),255);

                return labelComponent;
            case "obstacle":
                ObstacleComponent oc = new ObstacleComponent();
                oc.name = data.get("name").getAsString();
                oc.weight = data.get("weight").getAsFloat();

                return oc;
            case "ant_stats":
                AntStatsComponent as = new AntStatsComponent();
                as.force = data.get("force").getAsFloat();

                return as;
            default:
                return null;
        }
    }
}