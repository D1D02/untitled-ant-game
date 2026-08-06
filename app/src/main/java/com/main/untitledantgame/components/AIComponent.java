package com.main.untitledantgame.components;

import android.util.Log;

import java.util.*;

import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.Joint;
import com.google.fpl.liquidfun.RevoluteJointDef;
import com.google.fpl.liquidfun.Vec2;

import com.main.untitledantgame.map.GameWorld;
import com.main.untitledantgame.screen.GameScreen;
import com.main.untitledantgame.utility.ecs.Entity;

enum Behaviour {
    WANDERING,
    FOLLOWING,
    RETURNING,
    DRAGGING,
    WAITING
}


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



    private static final float REACHED_RADIUS = 8f;
    private static final float STEERING = 1.5f;



    public float updatePosition(
            Body antBody,
            float force,
            GameScreen screen,
            float deltaTime
    ){

        switch(state){

            case WANDERING:

                return updateWandering(
                        antBody,
                        force,
                        screen,
                        deltaTime
                );


            case RETURNING:

                return followTrail(
                        antBody,
                        force,
                        screen
                );


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


    public float followTrail( Body antBody, float force, GameScreen screen ) {

        Entity pheromone = screen.nextTrailPheromone( path_id, inc_id);

        Log.d("TRAIL", "Path_id:"+path_id+" Inc_id:"+inc_id);

        Vec2 antPos = antBody.getPosition();
        Vec2 pheromonePos = pheromone.getComponent(BodyComponent.class).body.getPosition();
        Vec2 d = new Vec2((pheromonePos.getX() - antPos.getX()), pheromonePos.getY() - antPos.getY());

        if(inc_id == 0 ) {
            state = Behaviour.WAITING;
            return 1;
        }
        else if(d.getX() < 0.01f && d.getY() < 0.01f ) inc_id--;

        Vec2.normalize(d);

        d.setX(d.getX() * 10000);
        d.setY(d.getY() * 10000);

        //Log.d("AI_Normalized_Vector", "x: " + d.getX() + " y:"+ d.getY());

        antBody.applyForceToCenter( d, true );

        antBody.setLinearDamping(0.5f);


        return 2;
    }


    public void handleCollision(
            Entity ant,
            Entity b,
            GameWorld world
    ){


        if(
                state == Behaviour.WANDERING &&
                        Objects.equals(b.type,"Food")
        ){


            RevoluteJointDef def =
                    new RevoluteJointDef();



            def.setBodyA(
                    ant.getComponent(BodyComponent.class).body
            );


            def.setBodyB(
                    b.getComponent(BodyComponent.class).body
            );



            def.setLocalAnchorA(0,0);
            def.setLocalAnchorB(0,0);



            //carryJoint = world.physicsWorld.createJoint(def);



            carriedFood = b;



            def.delete();



            state = Behaviour.RETURNING;

            //world.removeEntity(b);

            Log.d(
                    "AI",
                    "Food grabbed"
            );

        }

    }














    private void generatePheromone(
            Body antBody,
            GameScreen screen,
            float deltaTime
    ){


        time_spent+=deltaTime;



        if(time_spent>=max_time){



            float x =
                    antBody.getPositionX();


            float y =
                    antBody.getPositionY();




            inc_id++;



            path_id =
                    screen.generatePheromone(
                            x,
                            y,
                            path_id,
                            inc_id
                    );



            time_spent-=max_time;

        }

    }

}