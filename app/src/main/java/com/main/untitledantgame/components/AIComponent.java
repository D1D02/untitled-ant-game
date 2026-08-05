package com.main.untitledantgame.components;

import android.util.Log;

import java.util.*;

import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.Joint;
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

    private Entity carriedFood = null;
    private Joint carryJoint = null;

    private float time_spent = 0;
    private float max_time = 0.3f;

    private static final float REACHED_RADIUS = 15.0f;
    private static final float SLOWING_RADIUS = 45.0f;

    public float updatePosition(Body antBody, float force, GameScreen screen, float deltaTime) {
        switch (state) {
            case WANDERING:
                return updateWandering(antBody, force, screen, deltaTime);
            case RETURNING:
                return followTrail(antBody, force, screen);
            case WAITING:
                return arriveAtNest(antBody, force, screen);
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

            carryJoint = world.physicsWorld.createJoint(revoluteJointDef);
            carriedFood = b;

            revoluteJointDef.delete();

            state = Behaviour.RETURNING;
        }
    }

    public void handleSensorCollision(Entity ant, Entity b, GameWorld world) {
        if(Objects.equals(b.type, "Pheromone")){

        }
    }

    public float followTrail(Body antBody, float force, GameScreen screen) {

        if(path_id < 0){
            state = Behaviour.WANDERING;
            return antBody.getAngle();
        }


        if(inc_id < 0){
            state = Behaviour.WAITING;
            return antBody.getAngle();
        }


        Entity pheromone = screen.nextTrailPheromone(path_id, inc_id);


        while(pheromone == null && inc_id >= 0){

            inc_id--;
            pheromone = screen.nextTrailPheromone(path_id, inc_id);
        }


        if(pheromone == null){
            state = Behaviour.WAITING;
            return antBody.getAngle();
        }



        Body pheroBody =
                pheromone.getComponent(BodyComponent.class).body;

        Vec2 pheroPos = pheroBody.getPosition();

        float[] result = arriveSteering(antBody, pheroPos.getX(), pheroPos.getY(), force);
        float distance = result[3];


        if (distance < REACHED_RADIUS) {

            Log.d("RETURN", "Reached pheromone " + inc_id + " dist=" + distance);

            inc_id--;

            return antBody.getAngle();
        }

        antBody.applyForceToCenter(new Vec2(result[0], result[1]), true);
        antBody.setLinearDamping(0.5f);

        float angle = (float) Math.atan2(pheroPos.getY() - antBody.getPositionY(), pheroPos.getX() - antBody.getPositionX());
        antBody.setTransform(antBody.getPosition(), angle);

        Log.d("RETURN",
                "target=" + inc_id +
                        " dist=" + distance +
                        " speedFactor=" + result[2]);

        return angle;
    }


    private float arriveAtNest(Body antBody, float force, GameScreen screen) {

        Vec2 nestPos = screen.getAnthillPosition();

        float[] result = arriveSteering(antBody, nestPos.getX(), nestPos.getY(), force);
        float distance = result[3];

        if (distance < REACHED_RADIUS) {

            Log.d("RETURN", "Arrivata al nido, consegna cibo, dist=" + distance);

            screen.destroyJoint(carryJoint);
            screen.removeEntity(carriedFood);
            screen.depositFood(10);

            resetForNewTrip(antBody);
            return antBody.getAngle();
        }

        antBody.applyForceToCenter(new Vec2(result[0], result[1]), true);
        antBody.setLinearDamping(0.5f);

        float angle = (float) Math.atan2(nestPos.getY() - antBody.getPositionY(), nestPos.getX() - antBody.getPositionX());
        antBody.setTransform(antBody.getPosition(), angle);

        return angle;
    }


    private float[] arriveSteering(Body antBody, float targetX, float targetY, float force) {

        Vec2 antPos = antBody.getPosition();

        float dx = targetX - antPos.getX();
        float dy = targetY - antPos.getY();
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance < 0.0001f) {
            return new float[]{0f, 0f, 0f, distance};
        }

        float dirX = dx / distance;
        float dirY = dy / distance;

        float speedFactor = Math.min(1.0f, distance / SLOWING_RADIUS);
        float desiredSpeed = maxSpeed * speedFactor;

        float desiredX = dirX * desiredSpeed;
        float desiredY = dirY * desiredSpeed;

        Vec2 vel = antBody.getLinearVelocity();

        float fx = (desiredX - vel.getX()) * antBody.getMass() * 2.0f;
        float fy = (desiredY - vel.getY()) * antBody.getMass() * 2.0f;

        float magnitude = (float) Math.sqrt(fx * fx + fy * fy);
        if (magnitude > force) {
            fx = fx / magnitude * force;
            fy = fy / magnitude * force;
        }

        return new float[]{fx, fy, speedFactor, distance};
    }

    private void resetForNewTrip(Body antBody) {
        path_id = -1;
        inc_id = -1;
        carriedFood = null;
        carryJoint = null;
        time_spent = 0;
        state = Behaviour.WANDERING;
        antBody.setLinearDamping(2.0f);
    }

    private void applyBrakingForce(Body body, float maxForce) {

        Vec2 velocity = body.getLinearVelocity();

        float brakingX = -velocity.getX() * body.getMass() * 5.0f;
        float brakingY = -velocity.getY() * body.getMass() * 5.0f;

        float magnitudeSquared =
                brakingX * brakingX +
                        brakingY * brakingY;

        float maxForceSquared = maxForce * maxForce;

        if (magnitudeSquared > maxForceSquared) {
            float magnitude = (float) Math.sqrt(magnitudeSquared);

            brakingX = brakingX / magnitude * maxForce;
            brakingY = brakingY / magnitude * maxForce;
        }

        body.applyForceToCenter(
                new Vec2(brakingX, brakingY),
                true
        );

        body.setLinearDamping(1.0f);
    }

    private void generatePheromone(Body antBody, GameScreen screen, float deltaTime) {
        time_spent += deltaTime;

        if( time_spent >= max_time ) {
            float x = antBody.getPosition().getX();
            float y = antBody.getPosition().getY();

            this.inc_id++;
            this.path_id = screen.generatePheromone(x, y, this.path_id, this.inc_id);
            time_spent -= max_time;
        }
    }
}