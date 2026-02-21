package com.main.untitledantgame.screen;

import android.content.Context;
import android.util.Log;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Graphics;
import com.badlogic.androidgames.framework.Screen;
import com.google.gson.JsonArray;
import com.main.untitledantgame.components.LifeComponent;
import com.main.untitledantgame.map.Generator;
import com.main.untitledantgame.utility.GeneratorLoader;
import com.main.untitledantgame.utility.ecs.Entity;
import com.main.untitledantgame.utility.ecs.EntityFactory;
import com.main.untitledantgame.utility.ecs.PrototypeRegistry;
import com.main.untitledantgame.visuals.Assets;
import com.main.untitledantgame.utility.JsonLoader;

import java.util.List;

public class LoadingScreen extends Screen {
    public LoadingScreen(Game game) {
        super(game);
    }

    @Override
    public void update(float deltaTime) {
        JsonArray json = JsonLoader.loadJsonArray((Context) game, "Entities/entities.json");
        if (json == null) {
            android.util.Log.d("ECS", "JSON NON caricato!");
        } else {
            android.util.Log.d("ECS", "JSON caricato! Elementi: " + json.size());
        }
        Assets.prototypeRegistry = new PrototypeRegistry();
        Assets.prototypeRegistry.load(json);

        Log.d("ECS", "---- TEST ECS ----");

        EntityFactory factory = new EntityFactory(Assets.prototypeRegistry);

        Entity trial = factory.create("Trial");

        if (trial != null) {
            Log.d("ECS", "TRIAL OK, ID = " + trial.getId() + ", HP = " + trial.getComponent(LifeComponent.class).getHp());
        } else {
            Log.d("ECS", "TRIAL NULL");
        }

        List<Generator> generators =
                GeneratorLoader.loadGenerators((Context) game, "Entities/generator.json");

        if (generators != null) {
            for (Generator g : generators) {
                android.util.Log.d("GEN", "Loaded: " + g.name +
                        " start_delay=" + g.start_delay);
            }
        }

        Graphics g = game.getGraphics();
        Assets.background = g.newPixmap("background.png", Graphics.PixmapFormat.RGB565);
        game.setScreen(new MainMenuScreen(game));
   }

    @Override
    public void present(float deltaTime) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
