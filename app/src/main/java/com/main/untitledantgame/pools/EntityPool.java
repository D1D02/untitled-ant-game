package com.main.untitledantgame.pools;

import com.main.untitledantgame.utility.ecs.Entity;
import com.main.untitledantgame.utility.ecs.EntityFactory;
import com.main.untitledantgame.visuals.Assets;

import com.google.fpl.liquidfun.World;

import java.lang.reflect.Array;
import java.util.ArrayList;

import com.badlogic.androidgames.framework.Game;

public class EntityPool {

    ArrayList<Entity> entities = new ArrayList<>();
    EntityFactory entityFactory;

    String entity_type;
    int curr = -1;    
    int rel = -1;

    int num_entities;

    public EntityPool(int init_num_entities, String entity_t, World world, Game game)
    {
        this.entity_type = entity_t;
        entityFactory = new EntityFactory(Assets.prototypeRegistry);

        this.num_entities = init_num_entities;

        for( int i = 0; i < init_num_entities; ++i )
        {
            Entity entity = entityFactory.create(entity_type, world, game);

            if (entity.hasComponent(com.main.untitledantgame.components.BodyComponent.class))
            {
                entity.getComponent(com.main.untitledantgame.components.BodyComponent.class).body.setActive(false);
            }

            entities.add(entity);
        }     
    }

    public Entity acquire()
    {
        curr++;
        if( curr == num_entities && rel != 0 ) curr = 0;
        Entity entity = entities.get(curr);
        if (entity.hasComponent(com.main.untitledantgame.components.BodyComponent.class))
        {
            entity.getComponent(com.main.untitledantgame.components.BodyComponent.class).body.setActive(true);
        }
        return entity;
    }

    public void release( int entity_id ) 
    {
        if (entity_id >= rel || rel == num_entities - 1) 
        {
            Entity entity = entities.get(entity_id);
            if (entity.hasComponent(com.main.untitledantgame.components.BodyComponent.class))
            {
                entity.getComponent(com.main.untitledantgame.components.BodyComponent.class).body.setActive(false);
            }
            rel = entity_id;
        }
    }

    public Entity getEntity( int id )
    {
        return entities.get(id);
    }

}
