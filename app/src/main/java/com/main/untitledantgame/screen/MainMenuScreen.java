package com.main.untitledantgame.screen;

import android.app.Activity;
import android.app.Application;
import android.widget.Toast;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Graphics;
import com.badlogic.androidgames.framework.Input;
import com.badlogic.androidgames.framework.Screen;
import com.main.untitledantgame.visuals.Assets;

import java.util.List;

public class MainMenuScreen extends Screen {
    public MainMenuScreen(Game game) {
        super(game);
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
                System.out.println(event.x + " " + event.y);
                if(inBounds(event, 200, 100, 400, 100) ) {
                    game.setScreen(new GameScreen(game));
                    System.out.println("Starting game");
                    return;
                }
                if(inBounds(event, 200, 300, 400, 100) ) {
                    //game.setScreen(new HelpScreen(game));
                    System.out.println("Selected Settings");
                    return;
                }
            }
        }
    }

    private boolean inBounds(Input.TouchEvent event, int x, int y, int width, int height) {
        if(event.x > x && event.x < x + width - 1 &&
                event.y > y && event.y < y + height - 1)
            return true;
        else
            return false;
    }

    @Override
    public void present(float deltaTime) {
        Graphics g = game.getGraphics();

        int screenW = g.getWidth();
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
                bgW, bgH);
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
