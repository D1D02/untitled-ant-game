package com.main.untitledantgame.components;

import java.util.*;

import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.Vec2;

import com.badlogic.androidgames.framework.Screen;

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

    public void updatePosition(Body antBody, float force, Screen screen) {

        switch (state) {
            case WANDERING:
                updateWandering(antBody, force);
                break;

            case FOLLOWING:
                followTrail(antBody, force);
                break;

            case RETURNING:
                //returnHome(antBody, force);
                break;

            case WAITING:
                break;
            default:
                break;
        }
    }

    public void updateWandering(Body antBody, float force, Screen screen) {

        wanderAngle += (Math.random() - 0.5f) * 0.5f;

        float fx = (float) Math.cos(antBody.getAngle() + wanderAngle) * force;
        float fy = (float) Math.sin(antBody.getAngle() + wanderAngle) * force;

        antBody.applyForceToCenter(new Vec2(fx, fy), true);
        antBody.setTransform(antBody.getPosition(), wanderAngle);
        
        antBody.setLinearDamping(2.0f);

        generatePheromone(antBody, screen);
    }


    public void handleCollision(Entity b, Screen screen) {
        if(b.type == "pheromones" && ( state == Behaviour.FOLLOWING || state == Behaviour.RETURNING ) ) {
            followTrail(b);
        }
        else if(b.type == "food") {
            state = Behaviour.RETURNING;
            screen.attachFood(this, b);
        }           
    }

    private void followTrail(Entity pheromone) {
        
        if (pheromone != last_pheromone) {
            last_pheromone = pheromone;
            
            Vec2 pheromonePos = last_pheromone.getComponent("BodyComponent").body.getPosition();
            Vec2 antPos = antBody.getPosition();
            
            float dx = pheromonePos.x - antPos.x;
            float dy = pheromonePos.y - antPos.y;
            
            directionAngle = (float) Math.atan2(dy, dx);
        }
        
        float fx = (float) Math.cos(directionAngle) * force;
        float fy = (float) Math.sin(directionAngle) * force;

        antBody.applyForceToCenter(new Vec2(fx, fy), true);
        antBody.setLinearDamping(2.0f);
        antBody.setTransform(antBody.getPosition(), directionAngle);
    }
    

    private void generatePheromone(Body antBody, Screen screen) {
        float x = antBody.getPosition().getX();
        float y = antBody.getPosition().getY();

        screen.generatePheromone(x, y);
    }

}


