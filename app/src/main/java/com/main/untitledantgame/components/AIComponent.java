package com.main.untitledantgame.components;

import android.util.Log;

import java.util.*;

import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.Fixture;
import com.google.fpl.liquidfun.Joint;
import com.google.fpl.liquidfun.Vec2;

import com.main.untitledantgame.collision.Sensor;
import com.main.untitledantgame.map.GameWorld;
import com.main.untitledantgame.screen.GameScreen;
import com.main.untitledantgame.utility.ecs.Entity;
import com.main.untitledantgame.visuals.Assets;

public class AIComponent implements Component {

    public Behaviour state = Behaviour.WANDERING;

    public float wanderAngle = 0f;
    public static final float angle_inc = (float) (3.14 * ( (float) 1 / 16));


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

    public Vec2 sense_distance = new Vec2(50, 50);
    public Sensor sensor = new Sensor();

    public float attack_damage = 1f;

    public float attack_time = 0.3f;
    public float last_attack = 0f;

    public Vec2 attack_range = new Vec2(25,25);

    public float updatePosition( Body antBody, float force, GameScreen screen, float deltaTime ){

        switch(state){
            case WANDERING:
                return updateWandering( antBody, force, screen, deltaTime, false );

            case RETURNING:
                return followTrail( antBody, force, screen, false );

            case FOLLOWING:
                return followTrail( antBody, force, screen, true );

            case DRAGGING:
                return dragRock( antBody, force, screen );

            case PATROLLING:
                return patrolArea( antBody, force, screen, deltaTime );

            case WAITING:
                return 2;

            default:
                return antBody.getAngle();
        }
    }

    public float updateWandering( Body antBody, float force, GameScreen screen, float deltaTime, boolean isWasp ){

        w_time_spent+=deltaTime;

        Vec2 ant_pos = antBody.getPosition();

        if(!isWasp && w_time_spent>=w_max_time){

            wanderAngle += (float)((Math.random()-0.5f)*0.5f);
            w_time_spent-=w_max_time;

        } else if (isWasp && w_time_spent>=w_max_time) {
            wanderAngle += (float)(Math.random()*3.14f);
            w_time_spent-=w_max_time;
        }

        float angle = antBody.getAngle()+wanderAngle;
        float fx = (float)Math.cos(angle)*force;
        float fy = (float)Math.sin(angle)*force;

        List<Fixture> results = sensor.findInArea( screen.world.physicsWorld,
                ant_pos.getX() - sense_distance.getX(),
                ant_pos.getY() - sense_distance.getX(),
                ant_pos.getX() + sense_distance.getX(),
                ant_pos.getY() + sense_distance.getX() );

        boolean ant_found = false;
        for (Fixture fixture : results) {
            Body body = fixture.getBody();
            Entity entity = (Entity) body.getUserData();
            //if (!ant.equals(entity)) Log.d("AiComponent", "Detected: " + entity.type);

            if(!isWasp) {
                if (!"Ant".equals(entity.type) ) {
                    if (entity.hasComponent(ObstacleComponent.class) && entity.getComponent(ObstacleComponent.class).spotted)
                        break;
                    Vec2 en = entity.getComponent(BodyComponent.class).body.getPosition();
                    Vec2 d = new Vec2((en.getX() - ant_pos.getX()), en.getY() - ant_pos.getY());
                    fx = d.getX() * force;
                    fy = d.getY() * force;
                    if("Wasp".equals(entity.type) && checkInRange(ant_pos, body.getPosition(), attack_range)){
                        Log.d("AiComponent", "Ant: " );
                        Entity ant = (Entity) antBody.getUserData();
                        if(last_attack > 0) last_attack -= deltaTime;
                        else {
                            last_attack = attack_time;
                            entity.getComponent(LifeComponent.class).hp -= attack_damage;

                        }
                        AIComponent waspAI = entity.getComponent(AIComponent.class);
                        if(waspAI.last_attack > 0 ) waspAI.last_attack -= deltaTime;
                        else{
                            waspAI.last_attack = waspAI.attack_time;
                            ant.getComponent(LifeComponent.class).hp -= waspAI.attack_damage;
                            Log.d("AiComponent", "Ant life: " + ant.getComponent(LifeComponent.class).hp);

                        }
                        if(entity.getComponent(LifeComponent.class).hp < 0) {
                            screen.world.removePooledEntity(entity);
                            state = Behaviour.WANDERING;
                        }
                        if(ant.getComponent(LifeComponent.class).hp < 0) {
                            screen.ants_pool.release(ant);
                            screen.world.removePooledEntity(ant);
                            Log.d("AiComponent", "Ant died. ");
                            waspAI.state = Behaviour.PATROLLING;
                        }
                    }
                    break;
                }

            }
            else {
                if ("Ant".equals(entity.type)) {
                    Vec2 en = entity.getComponent(BodyComponent.class).body.getPosition();
                    Vec2 d = new Vec2((en.getX() - ant_pos.getX()), en.getY() - ant_pos.getY());
                    fx = d.getX() * force;
                    fy = d.getY() * force;
                    ant_found = true;
                    break;
                }
            }
        }


        if(isWasp && !ant_found) state = Behaviour.PATROLLING;
        antBody.applyForceToCenter( new Vec2(fx,fy), true );
        //Log.d("WANDERING", "x: " + fx + " y:"+ fy);

        antBody.setLinearDamping(0.5f);

        if(!isWasp) generatePheromone( antBody, screen, deltaTime );

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
                    Log.d("AI Component", " Food dropped in anthill");
                    screen.anthill.addFood(carriedFood.getComponent(FoodComponent.class).food + ant.getComponent(FoodComponent.class).food);
                    screen.anthill.addExp(25);
                    screen.destroyJoint(carryJoint);
                    screen.world.removePooledEntity(carriedFood);
                    screen.anthill.game_stats.picked_food += 1;
                }


                carryJoint = null;
                carriedFood = null;
            }else screen.anthill.addFood(ant.getComponent(FoodComponent.class).food);
            screen.world.removePooledEntity(ant);

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
            try{

                Entity rock = screen.obstacles.get(carriedRock);
                carryJointRock = screen.createJoint(ant, rock);
                state = Behaviour.DRAGGING;

            } catch (Exception e) {
                state = Behaviour.RETURNING;
            }
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
                if (carryJointRock == null || carriedRock == -1) return wanderAngle;
                Vec2 obstacle = screen.obstacles.get(carriedRock).getComponent(BodyComponent.class).body.getPosition();
                Vec2 antPos = antBody.getPosition();
                Vec2 pheromonePos = pheromone.getComponent(BodyComponent.class).body.getPosition();
                Vec2 d = new Vec2((pheromonePos.getX() - obstacle.getX()), pheromonePos.getY() - obstacle.getY());
                if ((d.getY() * d.getY() + d.getX() * d.getX()) > 20000) {
                     {
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

    public float patrolArea( Body antBody, float force, GameScreen screen, float deltaTime )
    {
        this.updateWandering(antBody, force, screen, deltaTime, true);
        try {


        }catch(Exception e){
            //state = Behaviour.WAITING;
            antBody.setAngularVelocity(2);
        }


        return wanderAngle;
    }

    public void handleCollision( Entity ant, Entity b, GameWorld world, GameScreen screen, float deltaTime ){

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
            b.getComponent(ObstacleComponent.class).spotted = true;
            Log.d( "AI", "Rock detected" );
        }
        else if( state == Behaviour.FOLLOWING && Objects.equals(b.type,"Rock") )
        {
            carryJointRock = screen.createJoint(ant, b);


            state = Behaviour.DRAGGING;


            Log.d( "AI", "Food grabbed" );
        }
        else if(Objects.equals(b.type,"Wasp")){

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
    
    private boolean checkInRange( Vec2 pos1, Vec2 pos2, Vec2 range ) {
        return (abs(pos1.getX() - pos2.getX()) < range.getX()) && (abs(pos1.getY() - pos2.getY()) < range.getY());
    }

    private float abs(float v) {
        return v > 0 ? v : -v;
    }

}