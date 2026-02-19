package com.main.untitledantgame.utility.ecs;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

//Trasforma i Json in Liste di Oggetti con chiave il nome della classe
public class PrototypeRegistry {

    private Map<String, JsonObject> prototypes = new HashMap<>();

    public void load(JsonArray jsonArray) {

        for (JsonElement element : jsonArray) {

            JsonObject obj = element.getAsJsonObject();
            String name = obj.get("name").getAsString();

            prototypes.put(name, obj);
            Log.d("ECS", "Prototype registrato: " + name);
        }
    }

    public JsonObject getPrototype(String name) {
        return prototypes.get(name);
    }

    public boolean contains(String name) {
        return prototypes.containsKey(name);
    }
}
