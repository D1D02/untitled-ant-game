package com.main.untitledantgame.pools;

import com.main.untitledantgame.utility.ecs.Entity;
import com.main.untitledantgame.utility.ecs.EntityFactory;
import com.main.untitledantgame.visuals.Assets;

import com.google.fpl.liquidfun.World;

import java.lang.reflect.Array;
import java.util.ArrayList;

import com.badlogic.androidgames.framework.Game;

public class EntityPool {

    Entity[] entities;
    EntityFactory entityFactory;

    String entity_type;

    int num_entities;

    public EntityPool(int init_num_entities, String entity_t, World world, Game game)
    {
        this.entity_type = entity_t;
        entityFactory = new EntityFactory(Assets.prototypeRegistry);

        this.num_entities = init_num_entities;

        entities = new Entity[num_entities];

        for( int i = 0; i < init_num_entities; ++i )
        {
            Entity entity = entityFactory.create(entity_type, world, game);

            entity.pool_id = i;

            if (entity.hasComponent(com.main.untitledantgame.components.BodyComponent.class))
            {
                entity.getComponent(com.main.untitledantgame.components.BodyComponent.class).body.setActive(false);
            }

            entities[i] = entity;
        }     
    }

    public Entity acquire()
    {
        if (num_entities==0)
            throw new IllegalStateException();
        num_entities--;
        return entities[num_entities];
    }

    public void release( Entity e )
    {
        if (num_entities==entities.length) return;
        entities[num_entities] = e;
        num_entities++;
    }

}
