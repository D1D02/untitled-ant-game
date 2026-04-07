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
                followTrail(antBody, force);
                break;

            case RETURNING:
                returnHome(antBody, force);
                break;

            case WAITING:
                break;
        }
    }

    public void updateWandering(Body antBody, float force) {

        wanderAngle += (Math.random() - 0.5f) * 0.5f;

        float fx = (float) Math.cos(antBody.getAngle() + wanderAngle) * force;
        float fy = (float) Math.sin(antBody.getAngle() + wanderAngle) * force;

        antBody.applyForceToCenter(new Vec2(fx, fy), true);

        limitSpeed(antBody);

        generatePheromone(antBody);
    }

    public void followTrail(Body antBody, float force) {

        Entity best = null;
        int bestId = -1;

        for (Entity e : entitiesInRange) {
            IdentifierComponent id = e.getComponent(IdentifierComponent.class);

            if (id != null && id.inc_id > inc_id) {
                if (id.inc_id > bestId) {
                    bestId = id.inc_id;
                    best = e;
                }
            }
        }

        moveTowards(antBody, best, force);

        if (best != null) {
            inc_id = bestId;
        }
    }

    public void returnHome(Body antBody, float force) {

        Entity best = null;
        int bestId = Integer.MAX_VALUE;

        for (Entity e : entitiesInRange) {
            IdentifierComponent id = e.getComponent(IdentifierComponent.class);

            if (id != null && id.inc_id < inc_id) {
                if (id.inc_id < bestId) {
                    bestId = id.inc_id;
                    best = e;
                }
            }
        }

        moveTowards(antBody, best, force);

        if (best != null) {
            inc_id = bestId;
        }
    }

    private void moveTowards(Body antBody, Entity target, float force) {

        if (target == null) return;

        Body targetBody = target.getComponent(BodyComponent.class).body;

        Vec2 antPos = antBody.getPosition();
        Vec2 targetPos = targetBody.getPosition();

        float dx = targetPos.getX() - antPos.getX();
        float dy = targetPos.getY() - antPos.getY();

        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        if (dist > 0.01f) {
            antBody.applyForceToCenter(
                    new Vec2((dx / dist) * force, (dy / dist) * force),
                    true
            );
        }

        limitSpeed(antBody);
    }

    private void limitSpeed(Body body) {
        Vec2 v = body.getLinearVelocity();

        float len = (float) Math.sqrt(v.getX()*v.getX() + v.getY()*v.getY());

        if (len > maxSpeed) {
            float ratio = maxSpeed / len;
            body.setLinearVelocity(new Vec2(v.getX()*ratio, v.getY()*ratio));
        }
    }

    private void generatePheromone(Body antBody) {

        Vec2 pos = antBody.getPosition();

        if (lastDropPosition == null) {
            dropPheromone(pos);
            return;
        }

        float dx = pos.getX() - lastDropPosition.getX();
        float dy = pos.getY() - lastDropPosition.getY();

        float dist = (float) Math.sqrt(dx*dx + dy*dy);

        if (dist > pheromoneDropDistance) {
            dropPheromone(pos);
        }
    }

    private void dropPheromone(Vec2 pos) {

        if (path_id == -1)
            path_id = UUID_Int.getUUIDInt();

        Entity pheromone = new Entity();

        IdentifierComponent id = new IdentifierComponent();
        id.inc_id = nextIncId++;
        pheromone.addComponent(id);

        BodyComponent bc = new BodyComponent();
        pheromone.addComponent(bc);

        lastDropPosition = new Vec2(pos.getX(), pos.getY());

        spawnPheromone(pheromone, pos);
    }

    private void spawnPheromone(Entity pheromone, Vec2 pos) {
        // chiama il tuo sistema ECS / factory
        // tipo:
        // world.spawnEntity("Pheromone", pos, pheromone);
    }
}