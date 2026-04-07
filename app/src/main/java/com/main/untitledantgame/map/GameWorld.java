package com.main.untitledantgame.map;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.util.Log;

import com.badlogic.androidgames.framework.Input;
import com.badlogic.androidgames.framework.Sound;
import com.badlogic.androidgames.framework.impl.TouchHandler;
import com.google.fpl.liquidfun.ContactListener;
import com.google.fpl.liquidfun.ParticleSystem;
import com.google.fpl.liquidfun.ParticleSystemDef;
import com.google.fpl.liquidfun.World;
import com.main.untitledantgame.GameObject;
import com.main.untitledantgame.TouchConsumer;
import com.main.untitledantgame.collision.Collision;
import com.main.untitledantgame.collision.CollisionSounds;
import com.main.untitledantgame.collision.GameContactListener;
import com.main.untitledantgame.utility.ecs.Entity;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class GameWorld {
    public List<Entity> entities; //il public è più efficiente rispetto a getter e setter

    public GameWorld(){
        entities = new ArrayList<>();
    }

    public void addEntity(Entity e){
        entities.add(e);
    }
}
