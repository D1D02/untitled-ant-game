package com.main.untitledantgame.screen;

import android.content.Context;
import android.util.Log;

import com.badlogic.androidgames.framework.Color;
import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Graphics;
import com.badlogic.androidgames.framework.Input;
import com.badlogic.androidgames.framework.Screen;
import com.main.untitledantgame.components.ui.UIAnimatedSpriteComponent;
import com.main.untitledantgame.components.ui.UIButtonComponent;
import com.main.untitledantgame.components.ui.UIElementComponent;
import com.main.untitledantgame.components.ui.UILabelComponent;
import com.main.untitledantgame.ui.UICanvas;
import com.main.untitledantgame.utility.JsonSaver;
import com.main.untitledantgame.utility.ecs.Entity;
import com.main.untitledantgame.utility.ecs.EntityFactory;
import com.main.untitledantgame.visuals.Assets;

import java.util.List;

public class CustomizationScreen extends Screen {
    UICanvas canvas;
    public static int antSkin = 0;

    int RED = Color.convert(255, 0, 0, 255);
    int BLACK = Color.convert(0, 0, 0, 255);
    int BLUE = Color.convert(0, 0, 255, 255);

    UIButtonComponent[] skinButtons = new UIButtonComponent[3];
    UIAnimatedSpriteComponent[] skinSprites = new UIAnimatedSpriteComponent[3];
    int[] skinPrices = {0, 100, 200};

    UILabelComponent coinsLabel;


    public CustomizationScreen(Game game) {
        super(game);

        int[] selectedSkin = new int[1];
        skinPrices = JsonSaver.getSkins((Context)game, selectedSkin);
        antSkin = selectedSkin[0];
        Log.d("SKINS", ""+antSkin);
        Log.d("SKINS", skinPrices[0] + " " + skinPrices[1] + " " + skinPrices[2]);

        initUI();
        selectSkin(antSkin);
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

        for(int i = 0; i < skinSprites.length; i++)
            skinSprites[i].update(deltaTime);
    }

    @Override
    public void present(float deltaTime) {
        Graphics g = game.getGraphics();
        g.drawPixmap(Assets.background, 0, 0);
        List<Entity> uiElements = canvas.elements;
        for(int i = 0; i < uiElements.size(); i++){
            Entity e = uiElements.get(i);
            e.getWithSuperComponent(UIElementComponent.class).draw(g);}
    }

    @Override
    public void pause() {
        Assets.soundtrack.pause();
    }

    @Override
    public void resume() {
        Assets.soundtrack.play();
    }

    @Override
    public void dispose() {

    }

    void initUI(){
        EntityFactory factory = new EntityFactory(Assets.prototypeRegistry);
        this.canvas = new UICanvas();

        Entity coinsLabel = factory.create("CoinsLabel", null, null);
        this.coinsLabel = coinsLabel.getComponent(UILabelComponent.class);
        this.coinsLabel.text = "COINS: "+JsonSaver.getCoins((Context) game);

        Entity skin1 = factory.create("SkinButton1", null, null);
        UIButtonComponent.setOnClickEvent(skin1, ()->{
            selectSkin(0);
        });
        skinButtons[0] = skin1.getComponent(UIButtonComponent.class);

        Entity skin2 = factory.create("SkinButton2", null, null);
        UIButtonComponent.setOnClickEvent(skin2, ()->{
            selectSkin(1);
        });
        skinButtons[1] = skin2.getComponent(UIButtonComponent.class);

        Entity skin3 = factory.create("SkinButton3", null, null);
        UIButtonComponent.setOnClickEvent(skin3, ()->{
            selectSkin(2);
        });
        skinButtons[2] = skin3.getComponent(UIButtonComponent.class);

        Entity mainMenu = factory.create("CustMainMenu", null, null);
        UIButtonComponent.setOnClickEvent(mainMenu, ()->{
            game.setScreen(new MainMenuScreen(game));
        });

        Entity skinSprite1 = factory.create("SkinSprite1", null, null);
        Entity skinSprite2 = factory.create("SkinSprite2", null, null);
        Entity skinSprite3 = factory.create("SkinSprite3", null, null);
        skinSprites[0] = skinSprite1.getComponent(UIAnimatedSpriteComponent.class);
        skinSprites[1] = skinSprite2.getComponent(UIAnimatedSpriteComponent.class);
        skinSprites[2] = skinSprite3.getComponent(UIAnimatedSpriteComponent.class);
        for(int i = 0; i < skinSprites.length; i++){
            skinSprites[i].sprite = Assets.antSkins[i];
            if(skinPrices[i] > 0){
                skinButtons[i].text = ""+skinPrices[i];
                skinButtons[i].textColor = BLUE;
            }
            else if(antSkin == i){
                skinButtons[0].text = "SELECTED";
                skinButtons[0].textColor = RED;
            }
            else{
                skinButtons[0].text = "SELECT";
                skinButtons[0].textColor = BLACK;
            }
        }

        canvas.addElement(skin1);
        canvas.addElement(skin2);
        canvas.addElement(skin3);
        canvas.addElement(mainMenu);
        canvas.addElement(skinSprite1);
        canvas.addElement(skinSprite2);
        canvas.addElement(skinSprite3);
        canvas.addElement(coinsLabel);
    }

    void selectSkin(int index){
        if(skinPrices[index] > 0){
            if(JsonSaver.spendCoins((Context)game,skinPrices[index])){
                skinPrices[index] = 0;
                antSkin = index;
                this.coinsLabel.text = "COINS: "+JsonSaver.getCoins((Context) game);
            }
        }else{
            antSkin = index;
        }

        for(int i = 0; i < Assets.antSkins.length; i++){
            if(skinPrices[i]>0)
                continue;
            if (i == antSkin){
                skinButtons[i].text = "SELECTED";
                skinButtons[i].textColor = RED;
            }
            else{
                skinButtons[i].text = "SELECT";
                skinButtons[i].textColor = BLACK;
            }
        }
        JsonSaver.setSkins((Context)game, skinPrices, antSkin);
    }
}
