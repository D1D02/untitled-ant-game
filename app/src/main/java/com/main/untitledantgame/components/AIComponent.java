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
import com.main.untitledantgame.visuals.Assets;

public class AIComponent implements Component {

    public Behaviour state = Behaviour.WANDERING;

    public float wanderAngle = 0f;

    private float w_time_spent = 0;
    private float w_max_time = 1.5f;

    public float maxSpeed = 5.0f;

    public int path_id = -1;
    public int inc_id = -1;

    private Entity carriedFood = null;
    private Joint carryJoint = null;
    public int carriedRock = -1;
    private Joint carryJointRock = null;

    private final float p_max_time = 1.5f;
    private float p_time_spent = p_max_time;


    public float updatePosition( Body antBody, float force, GameScreen screen, float deltaTime ){

        switch(state){
            case WANDERING:
                return updateWandering( antBody, force, screen, deltaTime );

            case RETURNING:
                return followTrail( antBody, force, screen, false );

            case FOLLOWING:
                return followTrail( antBody, force, screen, true );

            case DRAGGING:
                return dragRock( antBody, force, screen );

            case WAITING:
                return 2;
            default:
                return antBody.getAngle();
        }
    }

    public float updateWandering( Body antBody, float force, GameScreen screen, float deltaTime ){

        w_time_spent+=deltaTime;

        if(w_time_spent>=w_max_time){

            wanderAngle += (float)((Math.random()-0.5f)*0.5f);
            w_time_spent-=w_max_time;

        }

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

        Vec2 antPos = antBody.getPosition();
        //Log.d("TRAIL", "Path_id:"+path_id+" Inc_id:"+inc_id);
        Vec2 d_spawn = new Vec2((0 - antPos.getX()), 0 - antPos.getY());
        if ( !forward  && ( ( Math.abs(d_spawn.getX()) < 20.1f && Math.abs(d_spawn.getY()) < 20.1f )  ) ) {
            Entity ant = (Entity)antBody.getUserData();
            if (carryJoint != null && carriedFood != null)
            {

                if(carriedFood.getComponent(BodyComponent.class).body != null){
                    Log.d("AI Component", "Food dropped in anthill");
                    screen.anthill.addFood(carriedFood.getComponent(FoodComponent.class).food + ant.getComponent(FoodComponent.class).food);
                    screen.anthill.addExp(25);
                    screen.destroyJoint(carryJoint);
                    screen.world.removeEntity(carriedFood);
                    screen.anthill.game_stats.picked_food += 1;
                }


                carryJoint = null;
                carriedFood = null;
            }else screen.anthill.addFood(ant.getComponent(FoodComponent.class).food);
            screen.world.removeEntity(ant);

            return 1;
        }


        if(pheromone != null) {
            try {
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
            } catch (Exception e) {
                state = Behaviour.WAITING;
                antBody.setAngularVelocity(10);
            }

        }
        else if( carriedRock != -1 )
        {
            Entity ant = (Entity) antBody.getUserData();
            Entity rock = screen.obstacles.get(carriedRock);
            carryJointRock = screen.createJoint(ant, rock);
            state = Behaviour.DRAGGING;
        }
        return wanderAngle;
    }

    public float dragRock( Body antBody, float force, GameScreen screen )
    {
        Entity pheromone = screen.nextTrailPheromone( path_id, inc_id - 1);
        //Log.d("DragRock", "Path_id: "+path_id+" Inc_id: "+inc_id);
        if(carryJointRock == null || carriedRock == -1) state = Behaviour.RETURNING;

        try {
            if (pheromone != null) {
                Vec2 antPos = antBody.getPosition();
                Vec2 pheromonePos = pheromone.getComponent(BodyComponent.class).body.getPosition();
                Vec2 d = new Vec2((pheromonePos.getX() - antPos.getX()), pheromonePos.getY() - antPos.getY());
                if ((d.getY() * d.getY() + d.getX() * d.getX()) > 3500) {
                    if (carryJointRock != null && carriedRock != -1) {
                        screen.destroyObstacle(carriedRock, carryJointRock);
                        carriedRock = -1;
                        carryJointRock = null;
                        carriedFood = screen.spawnEntity("Food", antPos.getX(), antPos.getY());
                        carryJoint = screen.createJoint((Entity) antBody.getUserData(), carriedFood);

                    }
                    inc_id--;


                    state = Behaviour.RETURNING;
                } else {
                    wanderAngle = (float) Math.atan2(-d.getY(), -d.getX());
                    //Log.d("DragRock", "Angle: "+wanderAngle);

                    Vec2.normalize(d);

                    d.setX(-d.getX() * force);
                    d.setY(-d.getY() * force);


                    antBody.applyForceToCenter(d, true);

                    antBody.setLinearDamping(0.5f);
                }
            }
        }catch(Exception e){
            state = Behaviour.WAITING;
            antBody.setAngularVelocity(2);
        }


        return wanderAngle;
    }

    public void handleCollision( Entity ant, Entity b, GameWorld world, GameScreen screen ){

        if( state == Behaviour.WANDERING && Objects.equals(b.type,"Food") && carriedRock ==-1 )
        {
            carryJoint = screen.createJoint(ant, b);
            carriedFood = b;

            state = Behaviour.RETURNING;


            Log.d( "AI", "Food grabbed" );
            Assets.pickupSound.play(1);

        }
        else if( state == Behaviour.WANDERING && Objects.equals(b.type,"Rock") )
        {
            screen.addToBoard(b, path_id);
            state = Behaviour.RETURNING;
            b.getComponent(BodyComponent.class).body.setLinearVelocity(new Vec2(0f, 0f));
            b.getComponent(BodyComponent.class).body.setAngularVelocity(0);
            Log.d( "AI", "Rock detected" );
        }
        else if( state == Behaviour.FOLLOWING && Objects.equals(b.type,"Rock") )
        {
            carryJointRock = screen.createJoint(ant, b);


            state = Behaviour.DRAGGING;


            Log.d( "AI", "Food grabbed" );
        }
    }

    private void generatePheromone( Body antBody, GameScreen screen, float deltaTime ){

        p_time_spent+=deltaTime;

        if(p_time_spent>=p_max_time){

            float x = antBody.getPositionX();
            float y = antBody.getPositionY();

            inc_id++;
            path_id = screen.generatePheromone( x, y, path_id, inc_id );
            antBody.setAngularVelocity(0);
            p_time_spent-=p_max_time;

        }

    }

}