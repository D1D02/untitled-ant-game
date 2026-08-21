package com.main.untitledantgame.map;

import com.google.fpl.liquidfun.World;
import com.main.untitledantgame.components.BodyComponent;
import com.main.untitledantgame.utility.ecs.Entity;

import java.util.ArrayList;

import java.util.List;


public class GameWorld {
    public List<Entity> entities; //il public è più efficiente rispetto a getter e setter

    public GameWorld(){
        entities = new ArrayList<>();
    }

    public World physicsWorld = new World(0, 0);
    public void addEntity(Entity e){
        entities.add(e);
    }
    public void removeEntity(Entity e){
        if(e.hasComponent(BodyComponent.class))
        {
            BodyComponent bodyComp = e.getComponent(BodyComponent.class);
            physicsWorld.destroyBody(bodyComp.body);
            bodyComp.body.delete();
            bodyComp.body = null;
        }
        entities.remove(e);
    }

    public void removePooledEntity(Entity e ){
        entities.remove(e);
    }

}
