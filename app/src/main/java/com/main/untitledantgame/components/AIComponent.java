package com.main.untitledantgame.components;

import android.util.Log;

import java.util.*;

import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.RevoluteJointDef;
import com.google.fpl.liquidfun.Vec2;

import com.badlogic.androidgames.framework.Screen;

import com.main.untitledantgame.map.GameWorld;
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

    private Entity last_pheromone = null;
    private int lastPheromoneId = -1;

    private float time_spent = 0;
    private float max_time = 0.3f;

    public float updatePosition(Body antBody, float force, GameScreen screen, float deltaTime) {
        switch (state) {
            case WANDERING:
                return updateWandering(antBody, force, screen, deltaTime);
            case RETURNING:
                return followTrail(antBody, force);
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

    public void handleCollision(Entity ant, Entity b, GameWorld world) {
        if(state == Behaviour.WANDERING && Objects.equals(b.type, "Food")){
            RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
            revoluteJointDef.setBodyA(ant.getComponent(BodyComponent.class).body);
            revoluteJointDef.setBodyB(b.getComponent(BodyComponent.class).body);
            revoluteJointDef.setLocalAnchorA(0, 0);
            revoluteJointDef.setLocalAnchorB(0, 0);
            world.physicsWorld.createJoint(revoluteJointDef);
            revoluteJointDef.delete();

            state = Behaviour.RETURNING;

            this.lastPheromoneId = this.inc_id;
}
    }

    public void handleSensorCollision(Entity ant, Entity b, GameWorld world) {
        if(Objects.equals(b.type, "Pheromone")){
            if(!path.contains(b)) {
                path.add(b);
            }
        }
    }

    private float followTrail(Body antBody, float force) {


        if (last_pheromone == null) {
            int targetId = this.lastPheromoneId - 1;

            
        }

        Vec2 pheromonePos = last_pheromone.getComponent(BodyComponent.class).body.getPosition();
        Vec2 antPos = antBody.getPosition();

        float dx = pheromonePos.getX() - antPos.getX();
        float dy = pheromonePos.getY() - antPos.getY();

        if (Math.abs(dx) < 0.4f && Math.abs(dy) < 0.4f) {
            last_pheromone = null;
            return directionAngle;
        }

        directionAngle = (float) Math.atan2(dy, dx);

        float fx = (float) Math.cos(directionAngle) * force;
        float fy = (float) Math.sin(directionAngle) * force;

        antBody.applyForceToCenter(new Vec2(fx, fy), true);
        antBody.setLinearDamping(4.0f);

        antBody.setTransform(antBody.getPosition(), directionAngle);

        return directionAngle;
    }

    private float walkTowards(Vec2 target, Body antBody, float force) {
        Vec2 antPos = antBody.getPosition();
        float dx = target.getX() - antPos.getX();
        float dy = target.getY() - antPos.getY();
        directionAngle = (float) Math.atan2(dy, dx);

        antBody.applyForceToCenter(new Vec2((float) Math.cos(directionAngle) * force, (float) Math.sin(directionAngle) * force), true);
        antBody.setLinearDamping(4.0f);
        antBody.setTransform(antBody.getPosition(), directionAngle);
        return directionAngle;
    }

    private void generatePheromone(Body antBody, GameScreen screen, float deltaTime) {
        time_spent += deltaTime;

        if( time_spent >= max_time ) {
            float x = antBody.getPosition().getX();
            float y = antBody.getPosition().getY();

            this.inc_id++;
            if( path_id == -1 ) this.path_id = UUID_Int.getUUIDInt();
            screen.generatePheromone(x, y, this.path_id, this.inc_id);
            time_spent -= max_time;
        }
    }
}