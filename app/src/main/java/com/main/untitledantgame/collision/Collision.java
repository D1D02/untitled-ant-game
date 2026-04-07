package com.main.untitledantgame.collision;

import com.main.untitledantgame.GameObject;
import com.main.untitledantgame.utility.ecs.Entity;

/** An unordered pair of game objects.
 *
 */
public class Collision {
    Entity a, b;

    public Collision(Entity a, Entity b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public int hashCode() {
        return a.hashCode() ^ b.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Collision))
            return false;
        Collision otherCollision = (Collision) other;
        return (a.equals(otherCollision.a) && b.equals(otherCollision.b)) ||
               (a.equals(otherCollision.b) && b.equals(otherCollision.a));
    }
}
    