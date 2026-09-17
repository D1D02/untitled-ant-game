package com.main.untitledantgame.collision;

import com.google.fpl.liquidfun.Fixture;
import com.google.fpl.liquidfun.QueryCallback;
import com.google.fpl.liquidfun.World;

import java.util.ArrayList;
import java.util.List;

public class Sensor extends QueryCallback {

    private final List<Fixture> detectedFixtures = new ArrayList<>();

    @Override
    public boolean reportFixture(Fixture fixture) {
        detectedFixtures.add(fixture);

        return true;
    }

    public List<Fixture> findInArea(World world, float xmin, float ymin, float xmax, float ymax) {
        detectedFixtures.clear();

        world.queryAABB(this, xmin, ymin, xmax, ymax);

        return detectedFixtures;
    }

    public List<Fixture> getDetectedFixtures() {
        return detectedFixtures;
    }

}
