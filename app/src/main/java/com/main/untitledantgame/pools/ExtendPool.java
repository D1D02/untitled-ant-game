package com.main.untitledantgame.pools;

import com.badlogic.androidgames.framework.Game;
import com.google.fpl.liquidfun.World;
import com.main.untitledantgame.utility.ecs.Entity;
import com.main.untitledantgame.utility.ecs.EntityFactory;

public class ExtendPool implements Runnable{

    public boolean isActive = false;
    public boolean isEnded = false;
    public EntityFactory entityFactory;

    public Entity[] entities;

    public int capacity;

    public int remaining;

    public String entity_type;
    public World world;
    public Game game;

    @Override
    public void run() {

        int new_num_entities = (int) (this.capacity * 0.4);
        int new_capacity = new_num_entities + this.capacity;
        Entity[] new_entities = new Entity[new_capacity];

        for( int i = 0; i < new_num_entities; i++ )
        {
            Entity entity = entityFactory.create(entity_type, world, game);
            new_entities[i] = entity;
        }

        System.arraycopy(entities, 0, new_entities, new_num_entities, capacity);

        capacity = new_capacity;
        entities = new_entities;

        remaining = new_num_entities;
        this.isEnded = true;
        return;
    }


}
