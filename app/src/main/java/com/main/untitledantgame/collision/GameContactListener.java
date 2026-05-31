package com.main.untitledantgame.collision;

import com.google.fpl.liquidfun.*;

import java.util.Collection;
import java.util.HashSet;

import com.main.untitledantgame.collision.Collision;
import com.main.untitledantgame.components.BodyComponent;
import com.main.untitledantgame.utility.ecs.Entity;

public class GameContactListener extends ContactListener {

    private final Collection<Collision> cache = new HashSet<>();

    public Collection<Collision> getCollisions() {
        Collection<Collision> result = new HashSet<>(cache);
        cache.clear();
        return result;
    }

    @Override
    public void beginContact(Contact contact) {

        Body ba = contact.getFixtureA().getBody();
        Body bb = contact.getFixtureB().getBody();

        Object ua = ba.getUserData();
        Object ub = bb.getUserData();

        if (!(ua instanceof Entity) || !(ub instanceof Entity)) return;

        Entity a = (Entity) ua;
        Entity b = (Entity) ub;

        if( a.getComponent(BodyComponent.class).isSensor || b.getComponent(BodyComponent.class).isSensor ) {
            Collision collision = new Collision(a, b);
            collision.sensor = true;
            cache.add(collision);
        }
        else    cache.add(new Collision(a, b));
    }
}