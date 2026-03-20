package com.main.untitledantgame.components;

import com.google.fpl.liquidfun.Body;
import com.main.untitledantgame.components.Component;

enum Behaviour {
  WANDERING,
  FOLLOWING,
  RETURNING,
  DRAGGING
}

public class AIComponent implements Component {
    public Behaviour state = Behaviour.WANDERING;
    float wanderAngle = 0f;
    float maxSpeed = 5.0f;

    public Behaviour getState() {
        return state;
    }

    public void setState(Behaviour new_state) {
        this.state = new_state;
    }

    public void updateWandering(Body antBody, float force) {
        wanderAngle += (Math.random() - 0.5f) * 0.5f; 

        float fx = (float) Math.cos(antBody.getAngle() + wanderAngle) * force;
        float fy = (float) Math.sin(antBody.getAngle() + wanderAngle) * force;

        antBody.applyForceToCenter(new Vec2(fx, fy), true);

        Vec2 currentVel = antBody.getLinearVelocity();
        if (currentVel.length() > maxSpeed) {
            currentVel.normalize();
            antBody.setLinearVelocity(new Vec2(currentVel.getX() * maxSpeed, currentVel.getY() * maxSpeed));
        }

        generatePheromone(antBody);
    }

    private void returnHome(Body antBody) {
        Entity targetPheromone = null;
        int minId = Integer.MAX_VALUE;

        for (Entity entity : entitiesInRange) {
            IdentifierComponent idComp = entity.getComponent(IdentifierComponent.class);
            
            if (idComp != null) {
                if (idComp.getInc_id() < minId) {
                    minId = idComp.getInc_id();
                    targetPheromone = entity;
                }
            }
        }

        if (targetPheromone != null) {
            Body targetBody = targetPheromone.getComponent(BodyComponent.class).body;
            Vec2 direction = new Vec2(
                targetBody.getPosition().getX() - antBody.getPosition().getX(),
                targetBody.getPosition().getY() - antBody.getPosition().getY()
            );
            direction.normalize();
            
            float steeringForce = 8.0f; 
            antBody.applyForceToCenter(new Vec2(direction.getX() * steeringForce, direction.getY() * steeringForce), true);
        }
    }

    private void generatePheromone(Body antBody) {

    }

    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }
}
