package com.main.untitledantgame.pools;

import com.main.untitledantgame.components.BodyComponent;
import com.main.untitledantgame.utility.ecs.Entity;
import com.main.untitledantgame.utility.ecs.EntityFactory;
import com.main.untitledantgame.visuals.Assets;

import com.google.fpl.liquidfun.World;


import com.badlogic.androidgames.framework.Game;

public class EntityPool {

    Entity[] entities;
    EntityFactory entityFactory;

    String entity_type;
    int capacity;
    int remaining;
    ExtendPool extendPool;

    public EntityPool(int init_num_entities, String entity_t, World world, Game game)
    {


        this.entity_type = entity_t;
        entityFactory = new EntityFactory(Assets.prototypeRegistry);

        this.remaining = init_num_entities;
        this.capacity = init_num_entities;
        entities = new Entity[remaining];

        extendPool = new ExtendPool();
        extendPool.entityFactory = this.entityFactory;
        extendPool.entities = this.entities;
        extendPool.entity_type = this.entity_type;
        extendPool.world = world;
        extendPool.game = game;

        for( int i = 0; i < init_num_entities; ++i )
        {
            Entity entity = entityFactory.create(entity_type, world, game);

            entities[i] = entity;
        }     
    }

    public Entity acquire()
    {
        if (remaining <= (capacity * 0.4) && !extendPool.isActive){
            extendPool.isActive = true;
            extendPool.capacity = capacity;
            extendPool.run();
        }else if(remaining <= (capacity * 0.4) && extendPool.isEnded){
            entities = extendPool.entities;
            extendPool.isEnded = false;
            extendPool.isActive = false;
            remaining += extendPool.remaining;
            capacity = extendPool.capacity;
        }
        if (remaining ==0)
            throw new IllegalStateException();
        remaining--;
        entities[remaining].getComponent(BodyComponent.class).body.setActive(true);
        return entities[remaining];
    }

    public void release( Entity e )
    {
        if (remaining ==entities.length) return;
        entities[remaining] = e;
        entities[remaining].getComponent(BodyComponent.class).body.setActive(false);
        remaining++;
    }

}
