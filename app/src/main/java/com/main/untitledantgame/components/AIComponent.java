package com.main.untitledantgame.components;

import java.util.*;

import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.Vec2;

import com.badlogic.androidgames.framework.Screen;

import com.main.untitledantgame.screen.GameScreen;
import com.main.untitledantgame.utility.ecs.Entity;
import com.main.untitledantgame.components.Component;
import com.main.untitledantgame.utility.UUID_Int;


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

    private float pheromoneDropDistance = 0.2f;
    private Vec2 lastDropPosition = null;

    private Entity last_pheromone = null;

    private float time_spent = 0;
    private float max_time = 0.3f;

    public float updatePosition(Body antBody, float force, GameScreen screen, float deltaTime) {

        switch (state) {
            case WANDERING:
                return updateWandering(antBody, force, screen, deltaTime);

            case FOLLOWING: return 0f;
            case RETURNING:
                inc_id = -1;
                return followTrail(last_pheromone, antBody, force);

            case WAITING:
                inc_id = -1;
                return 0f;
            default:
                return 0f;
        }
    }

    public float updateWandering(Body antBody, float force, GameScreen screen, float deltaTime) {

        wanderAngle += (float) ((Math.random() - 0.5f) * 0.5f);

        float fx = (float) Math.cos(antBody.getAngle() + wanderAngle) * force;
        float fy = (float) Math.sin(antBody.getAngle() + wanderAngle) * force;

        antBody.applyForceToCenter(new Vec2(fx, fy), true);
        antBody.setTransform(antBody.getPosition(), wanderAngle);
        
        antBody.setLinearDamping(2.0f);

        generatePheromone(antBody, screen, deltaTime);

        return wanderAngle;
    }


    public void handleCollision(Entity ant, Entity b, GameScreen screen) {
        if(b.type.equals("pheromones") && ( state != Behaviour.WANDERING ) ) {
            int dxPheromone = b.getComponent(IdentifierComponent.class).inc_id - last_pheromone.getComponent(IdentifierComponent.class).inc_id;

            if(dxPheromone > 0 && state == Behaviour.FOLLOWING) last_pheromone = b;
            else if(dxPheromone <= 0 && state == Behaviour.RETURNING) last_pheromone = b;

        }
        else if(b.type.equals("food")) {
            state = Behaviour.RETURNING;
            //screen.attachFood(ant, b);
        }           
    }

    private float followTrail(Entity pheromone, Body antBody, float force) {
        
        if (pheromone != last_pheromone) {
            last_pheromone = pheromone;
            
            Vec2 pheromonePos = last_pheromone.getComponent(BodyComponent.class).body.getPosition();
            Vec2 antPos = antBody.getPosition();
            
            float dx = pheromonePos.getX() - antPos.getX();
            float dy = pheromonePos.getY() - antPos.getY();
            
            directionAngle = (float) Math.atan2(dy, dx);
        }
        
        float fx = (float) Math.cos(directionAngle) * force;
        float fy = (float) Math.sin(directionAngle) * force;

        antBody.applyForceToCenter(new Vec2(fx, fy), true);
        antBody.setLinearDamping(2.0f);
        antBody.setTransform(antBody.getPosition(), directionAngle);

        return directionAngle;
    }


    private void generatePheromone(Body antBody, GameScreen screen, float deltaTime) {

        time_spent += deltaTime;;

        if( time_spent >= max_time ) {
            float x = antBody.getPosition().getX();
            float y = antBody.getPosition().getY();

            screen.generatePheromone(x, y, UUID_Int.getUUIDInt());
            time_spent -= max_time;
        }
    }

}


