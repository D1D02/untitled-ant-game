package com.main.untitledantgame.utility.ecs;

import com.badlogic.androidgames.framework.Game;
import com.google.fpl.liquidfun.World;
import com.google.gson.JsonObject;
import com.main.untitledantgame.components.Component;

import java.util.Map;

public class EntityFactory {

    private PrototypeRegistry registry;

    public EntityFactory(PrototypeRegistry registry) {
        this.registry = registry;
    }

    public Entity create(String prototypeName, World world, Game game) {

        JsonObject proto = registry.getPrototype(prototypeName);
        if (proto == null) return null;

        Entity entity = new Entity();

        JsonObject components = proto.getAsJsonObject("components");

        for (Map.Entry<String, com.google.gson.JsonElement> entry : components.entrySet()) {

            String compName = entry.getKey();
            JsonObject compData = entry.getValue().getAsJsonObject();

            Component component = ComponentFactory.create(compName, compData, world, game, entity, prototypeName);

            if (component != null) {
                entity.addComponent(component);
            }
        }

        return entity;
    }
}


