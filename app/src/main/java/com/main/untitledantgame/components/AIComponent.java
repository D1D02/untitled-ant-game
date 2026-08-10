package com.main.untitledantgame.components;

import android.util.Log;

import java.util.*;

import com.badlogic.androidgames.framework.Screen;
import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.Joint;
import com.google.fpl.liquidfun.RevoluteJointDef;
import com.google.fpl.liquidfun.Vec2;

import com.main.untitledantgame.map.GameWorld;
import com.main.untitledantgame.screen.GameScreen;
import com.main.untitledantgame.utility.ecs.Entity;

public class AIComponent implements Component {

    public Behaviour state = Behaviour.WANDERING;

    public float wanderAngle = 0f;
    public float directionAngle = 0f;

    public float maxSpeed = 5.0f;

    public int path_id = -1;
    public int inc_id = -1;

    private Entity carriedFood = null;
    private Joint carryJoint = null;


    private float time_spent = 0;
    private float max_time = 0.3f;


    public float updatePosition( Body antBody, float force, GameScreen screen, float deltaTime ){

        switch(state){
            case WANDERING:
                return updateWandering( antBody, force, screen, deltaTime );

            case RETURNING:
                return followTrail( antBody, force, screen, false );

            case FOLLOWING:
                return followTrail( antBody, force, screen, true );

            case WAITING:
                return 2;
            default:
                return antBody.getAngle();
        }
    }

    public float updateWandering( Body antBody, float force, GameScreen screen, float deltaTime ){


        wanderAngle += (float)((Math.random()-0.5f)*0.5f);

        float angle = antBody.getAngle()+wanderAngle;

        float fx = (float)Math.cos(angle)*force;


        float fy = (float)Math.sin(angle)*force;


        antBody.applyForceToCenter( new Vec2(fx,fy), true );
        //Log.d("WANDERING", "x: " + fx + " y:"+ fy);

        antBody.setLinearDamping(1.5f);

        generatePheromone( antBody, screen, deltaTime );

        return angle;
    }

    public float followTrail( Body antBody, float force, GameScreen screen, Boolean forward ) {

        Entity pheromone = screen.nextTrailPheromone( path_id, inc_id);

        //Log.d("TRAIL", "Path_id:"+path_id+" Inc_id:"+inc_id);

        if( !forward && carriedFood != null && inc_id == 0 ) {

            if (carryJoint != null)
            {
                screen.destroyJoint(carryJoint);
                Entity ant = (Entity)antBody.getUserData();
                screen.anthill.addFood(carriedFood.getComponent(FoodComponent.class).food + ant.getComponent(FoodComponent.class).food);
                screen.anthill.addExp(25);
                screen.world.removeEntity(carriedFood);
                screen.world.removeEntity(ant);
                carryJoint = null;
                carriedFood = null;
                ant = null;
            }

            return 1;
        }

        Vec2 antPos = antBody.getPosition();

        if(pheromone != null) {
            Vec2 pheromonePos = pheromone.getComponent(BodyComponent.class).body.getPosition();
            Vec2 d = new Vec2((pheromonePos.getX() - antPos.getX()), pheromonePos.getY() - antPos.getY());
            wanderAngle = (float) Math.atan2(d.getY(), d.getX());
            if (Math.abs(d.getX()) < 10.1f && Math.abs(d.getY()) < 10.1f) {
                if (forward) inc_id++;
                else inc_id--;
                antBody.setLinearVelocity(new Vec2(0f, 0f));
            }
            Vec2.normalize(d);

            d.setX(d.getX() * force);
            d.setY(d.getY() * force);

            //Log.d("AI_Normalized_Vector", "x: " + d.getX() + " y:"+ d.getY());

            antBody.applyForceToCenter(d, true);

            antBody.setLinearDamping(0.5f);
        }
        else
        {
            state = Behaviour.WAITING;
        }
        return wanderAngle;
    }


    public void handleCollision( Entity ant, Entity b, GameWorld world, GameScreen screen ){

        if( state == Behaviour.WANDERING && Objects.equals(b.type,"Food") )
        {
            RevoluteJointDef def = new RevoluteJointDef();

            def.setBodyA( ant.getComponent(BodyComponent.class).body );

            def.setBodyB( b.getComponent(BodyComponent.class).body );

            def.setLocalAnchorA(0,0);
            def.setLocalAnchorB(0,0);

            carryJoint = world.physicsWorld.createJoint(def);

            carriedFood = b;

            def.delete();

            state = Behaviour.RETURNING;


            Log.d( "AI", "Food grabbed" );

        }
        else if( state == Behaviour.WANDERING && Objects.equals(b.type,"Rock") )
        {
            screen.addToBoard(b, path_id);
            state = Behaviour.RETURNING;


            Log.d( "AI", "Rock detected" );
        }

    }

    private void generatePheromone( Body antBody, GameScreen screen, float deltaTime ){

        time_spent+=deltaTime;

        if(time_spent>=max_time){

            float x = antBody.getPositionX();
            float y = antBody.getPositionY();

            inc_id++;
            path_id = screen.generatePheromone( x, y, path_id, inc_id );

            time_spent-=max_time;

        }

    }

}