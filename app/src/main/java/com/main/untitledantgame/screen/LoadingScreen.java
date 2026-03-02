package com.main.untitledantgame.screen;

import android.content.Context;
import android.graphics.Bitmap;
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
        System.loadLibrary("liquidfun");
        System.loadLibrary("liquidfun_jni");

        JsonArray json = JsonLoader.loadJsonArray((Context) game, "Entities/entities.json");
        if (json == null) {
            Log.d("ECS", "JSON NON caricato!");
        } else {
            Log.d("ECS", "JSON caricato! Elementi: " + json.size());
        }

        Assets.prototypeRegistry = new PrototypeRegistry();
        Assets.prototypeRegistry.load(json);

        List<Generator> generators =
                GeneratorLoader.loadGenerators((Context) game, "Entities/generator.json");

        if (generators != null) {
            Assets.generators = generators;
        }

        Graphics g = game.getGraphics();

        int screenW = g.getWidth();
        int screenH = g.getHeight();

        try {
            android.content.res.AssetManager assetManager =
                    ((Context) game).getAssets();

            java.io.InputStream in =
                    assetManager.open("background.png");

            android.graphics.Bitmap original =
                    android.graphics.BitmapFactory.decodeStream(in);

            in.close();

            android.graphics.Bitmap scaled =
                    android.graphics.Bitmap.createScaledBitmap(
                            original,
                            screenW,
                            screenH,
                            true
                    );

            android.graphics.Bitmap converted =
                    scaled.copy(Bitmap.Config.RGB_565, true);

            Assets.background =
                    new com.badlogic.androidgames.framework.impl.AndroidPixmap(
                            converted,
                            Graphics.PixmapFormat.RGB565
                    );

            original.recycle();

        } catch (Exception e) {
            Log.d("ECS", e.getMessage());
        }

        // Passo al menu principale
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
