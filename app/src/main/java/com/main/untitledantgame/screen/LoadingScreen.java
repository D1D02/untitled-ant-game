package com.main.untitledantgame.screen;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Graphics;
import com.badlogic.androidgames.framework.Pixmap;
import com.badlogic.androidgames.framework.Screen;
import com.badlogic.androidgames.framework.impl.AndroidAudio;
import com.google.gson.JsonArray;
import com.main.untitledantgame.utility.Quiz;
import com.main.untitledantgame.utility.ecs.PrototypeRegistry;
import com.main.untitledantgame.visuals.Assets;
import com.main.untitledantgame.utility.JsonLoader;

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
            Log.d("ECS", "Entities not loaded!");
        } else {
            Log.d("ECS", "Entities loaded! Elements: " + entitiesJson.size());
        }

        Assets.prototypeRegistry = new PrototypeRegistry();
        Assets.prototypeRegistry.load(entitiesJson);


        JsonArray uiJson = JsonLoader.loadJsonArray((Context) game, "Entities/ui.json");
        if (uiJson == null) {
            Log.d("ECS", "UI not loaded!");
        } else {
            Log.d("ECS", "UI loaded! Elements: " + uiJson.size());
        }

        Assets.prototypeRegistry.load(uiJson);


        JsonArray generators = JsonLoader.loadJsonArray((Context) game, "Entities/generator.json");

         if (generators == null) {
            Log.d("ECS", "Generators not loaded!");
        } else {
            Log.d("ECS", "Generators loaded! Elements: " + uiJson.size());
        }
        
        Assets.prototypeRegistry.load(generators);


        g = game.getGraphics();

        screenW = g.getWidth();
        screenH = g.getHeight();

        assetManager = ((Context) game).getAssets();

        Assets.anthill = loadSprite("anthill.png", game);
        Assets.background = loadSprite("grass.png", game);
        Assets.button = loadSprite("button.png", game);

        AndroidAudio audio = new AndroidAudio((Activity)game);
        Assets.soundtrack = audio.newMusic("soundtrack.mp3");
        Assets.eatSound = audio.newSound("eat.mp3");
        Assets.levelUpSound = audio.newSound("level_up.mp3");
        Assets.buttonSound = audio.newSound("button.mp3");
        Assets.pickupSound = audio.newSound("pickup.mp3");
        Assets.boardSound = audio.newSound("board.mp3");
        Assets.correctSound = audio.newSound("correct.mp3");
        Assets.wrongSound = audio.newSound("wrong.mp3");
        //AudioManager.initialize(((Context) game).getApplicationContext());
        // Passo al menu principale
        game.setScreen(new MainMenuScreen(game));

        Quiz.init((Context) game);
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
    Pixmap loadSprite(String sprite, Game game){
        try {
            return game.getGraphics()
                    .newPixmap(sprite, Graphics.PixmapFormat.ARGB8888);
        } catch (Exception e) {
            Log.e("ECS", "Errore sprite");
        }
        return null;
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
