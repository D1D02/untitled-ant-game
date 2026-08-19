package com.main.untitledantgame.screen;

import android.app.Activity;
import android.app.Application;
import android.widget.Toast;

import com.badlogic.androidgames.framework.Color;
import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Graphics;
import com.badlogic.androidgames.framework.Input;
import com.badlogic.androidgames.framework.Screen;
import com.main.untitledantgame.components.ui.UIButtonComponent;
import com.main.untitledantgame.components.ui.UIElementComponent;
import com.main.untitledantgame.ui.UICanvas;
import com.main.untitledantgame.utility.ecs.Entity;
import com.main.untitledantgame.utility.ecs.EntityFactory;
import com.main.untitledantgame.visuals.Assets;

import java.util.List;

public class MainMenuScreen extends Screen {

    UICanvas canvas;

    boolean gameStarted = false;
    public MainMenuScreen(Game game) {
        super(game);
        initUI();
        Assets.soundtrack.play();
    }

    @Override
    public void update(float deltaTime) {
        Graphics g = game.getGraphics();
        List<Input.TouchEvent> touchEvents = game.getInput().getTouchEvents();
        game.getInput().getKeyEvents();
        int len = touchEvents.size();

        for(int i = 0; i < len; i++) {
            Input.TouchEvent event = touchEvents.get(i);
            if(event.type == Input.TouchEvent.TOUCH_UP) {
                List<Entity> uiElements = canvas.elements;
                for(int j = 0; j < uiElements.size(); j++){
                    Entity e = uiElements.get(j);
                    UIButtonComponent.check(e, event, g);
                }
            }
        }
    }

    @Override
    public void present(float deltaTime) {
        Graphics g = game.getGraphics();

        /*int screenW = g.getWidth();
        int screenH = g.getHeight();

        int bgW = Assets.background.getWidth();
        int bgH = Assets.background.getHeight();

        float scale = Math.max(
                (float) screenW / bgW,
                (float) screenH / bgH
        );

        int scaledW = (int)(bgW * scale);
        int scaledH = (int)(bgH * scale);

        int x = (screenW - scaledW) / 2;
        int y = (screenH - scaledH) / 2;

        g.drawPixmap(Assets.background,
                x, y,
                0, 0,
                bgW, bgH);*/
        //g.drawRect(0,0, 480, 320, Color.convert(120,210,50,255)); //sfondo provvisorio
        g.drawPixmap(Assets.background, 0, 0);
        List<Entity> uiElements = canvas.elements;
        for(int i = 0; i < uiElements.size(); i++){
            Entity e = uiElements.get(i);
            e.getWithSuperComponent(UIElementComponent.class).draw(g);}

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

    void initUI(){
        EntityFactory factory = new EntityFactory(Assets.prototypeRegistry);
        this.canvas = new UICanvas();

        Entity play = factory.create("PlayButton", null, null);
        UIButtonComponent.setOnClickEvent(play, ()->{
            if(gameStarted)
                return;
            gameStarted = true;
            game.setScreen(new GameScreen(game));
        });
        Entity customize = factory.create("CustomizationButton", null, null);
        UIButtonComponent.setOnClickEvent(customize, ()->{
            if(gameStarted)
                return;
            game.setScreen(new CustomizationScreen(game));
        });

        canvas.addElement(play);
        canvas.addElement(customize);
    }
}
