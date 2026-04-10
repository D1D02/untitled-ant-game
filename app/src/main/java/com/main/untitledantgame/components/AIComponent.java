package com.main.untitledantgame.components;

import java.util.*;

import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.Vec2;

import com.main.untitledantgame.utility.ecs.Entity;
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
    public float maxSpeed = 5.0f;

    public int path_id = -1;
    public int inc_id = -1;

    public List<Entity> entitiesInRange = new ArrayList<>();

    private float pheromoneDropDistance = 0.2f;
    private Vec2 lastDropPosition = null;

    private int nextIncId = 0;

    public void updatePosition(Body antBody, float force) {

        switch (state) {
            case WANDERING:
                updateWandering(antBody, force);
                break;

            case FOLLOWING:
                //followTrail(antBody, force);
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

    public void updateWandering(Body antBody, float force) {

        wanderAngle += (Math.random() - 0.5f) * 0.5f;

        float fx = (float) Math.cos(antBody.getAngle() + wanderAngle) * force;
        float fy = (float) Math.sin(antBody.getAngle() + wanderAngle) * force;

        antBody.applyForceToCenter(new Vec2(fx, fy), true);

       // generatePheromone(antBody);
    }



    private void generatePheromone(Body antBody) {

    }

}