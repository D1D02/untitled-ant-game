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

    Graphics g;
    int screenW;
    int screenH;
    android.content.res.AssetManager assetManager;
    public LoadingScreen(Game game) {

        super(game);
    }

    @Override
    public void update(float deltaTime) {
        System.loadLibrary("liquidfun");
        System.loadLibrary("liquidfun_jni");

        JsonArray entitiesJson = JsonLoader.loadJsonArray((Context) game, "Entities/entities.json");
        if (entitiesJson == null) {
            Log.d("ECS", "JSON NON caricato!");
        } else {
            Log.d("ECS", "JSON caricato! Elementi: " + entitiesJson.size());
        }

        Assets.prototypeRegistry = new PrototypeRegistry();
        Assets.prototypeRegistry.load(entitiesJson);

        JsonArray uiJson = JsonLoader.loadJsonArray((Context) game, "Entities/ui.json");
        if (uiJson == null) {
            Log.d("ECS", "JSON NON caricato!");
        } else {
            Log.d("ECS", "JSON caricato! Elementi: " + uiJson.size());
        }

        Assets.prototypeRegistry.load(uiJson);

        List<Generator> generators =
                GeneratorLoader.loadGenerators((Context) game, "Entities/generator.json");

        if (generators != null) {
            Assets.generators = generators;
        }

        g = game.getGraphics();

        screenW = g.getWidth();
        screenH = g.getHeight();

        assetManager = ((Context) game).getAssets();

        Assets.anthill = loadSprite("anthill.png", 0.25f);

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

    com.badlogic.androidgames.framework.impl.AndroidPixmap loadSprite(String sprite, float scale) {
        try {
            java.io.InputStream in =
                    assetManager.open(sprite);

            android.graphics.Bitmap original =
                    android.graphics.BitmapFactory.decodeStream(in);

            in.close();

            android.graphics.Bitmap scaled =
                    android.graphics.Bitmap.createScaledBitmap(
                            original,
                            (int)(screenW*scale), //dimensione calcolata in proporzione allo schermo, per ora
                            (int)(screenW*scale),
                            true
                    );

            android.graphics.Bitmap converted =
                    scaled.copy(Bitmap.Config.RGB_565, true);

            original.recycle();
            return new com.badlogic.androidgames.framework.impl.AndroidPixmap(
                    converted,
                    Graphics.PixmapFormat.RGB565
            );


        } catch (Exception e) {
            Log.d("ECS", e.getMessage());
        }
        return null;
    }
}
