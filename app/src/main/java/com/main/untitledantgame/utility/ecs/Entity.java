package com.main.untitledantgame.utility.ecs;

import com.main.untitledantgame.components.Component;

import java.util.HashMap;
import java.util.Map;

//Definisce come è fatta una Entity, a cui bisogna aggiungere i components
public class Entity {

    private static long NEXT_ID = 0;
    private long id;
    public String type;

    private Map<Class<? extends Component>, Component> components = new HashMap<>();

    public Entity() {
        this.id = NEXT_ID++;
    }

    public long getId() {
        return id;
    }

    public <T extends Component> void addComponent(T component) {
        components.put(component.getClass(), component);
    }

    public <T extends Component> T getComponent(Class<T> type) {
        return type.cast(components.get(type));
    }

    public <T extends Component> boolean hasComponent(Class<T> type) {
        return components.containsKey(type);
    }

    public <T extends Component> T getWithSuperComponent(Class<T> type) {
        for (Component comp : components.values()) {
            if (type.isAssignableFrom(comp.getClass())) {
                return type.cast(comp);
            }
        }
        return null;
    }
}

