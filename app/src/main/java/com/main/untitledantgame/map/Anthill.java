package com.main.untitledantgame.map;

import android.content.Context;
import android.util.Log;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Graphics;
import com.badlogic.androidgames.framework.Pixmap;
import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.FixtureDef;
import com.google.fpl.liquidfun.PolygonShape;
import com.google.fpl.liquidfun.World;
import com.google.gson.JsonObject;
import com.main.untitledantgame.components.BodyComponent;
import com.main.untitledantgame.components.PositionComponent;
import com.main.untitledantgame.components.SpriteComponent;
import com.main.untitledantgame.components.ui.UIBarComponent;
import com.main.untitledantgame.components.ui.UIFlashLabelComponent;
import com.main.untitledantgame.components.ui.UILabelComponent;
import com.main.untitledantgame.utility.JsonSaver;
import com.main.untitledantgame.utility.ecs.ComponentFactory;
import com.main.untitledantgame.utility.ecs.Entity;
import com.main.untitledantgame.visuals.Assets;

public class Anthill extends Entity {
    UIBarComponent foodBar, expbar;
    UIFlashLabelComponent levelUpLabel;

    UILabelComponent levelLabel;

    UILabelComponent gameOverTimer;
    public int food = 100, exp = 0;
    int maxFood = 200,maxExp = 100;
    int level = 1;
    public Statistics game_stats;
    Context ctx;

    public Anthill(int x, int y, UIBarComponent foodBar, UIBarComponent expbar, World world,
                   UIFlashLabelComponent levelUpLabel, UILabelComponent levelLabel, UILabelComponent gameOverTimer,
                   Context ctx){
        BodyComponent body = generateBody(x, y, world);

        addComponent(body);
        SpriteComponent sprite = new SpriteComponent();

        sprite.sprite = Assets.anthill;
        sprite.width = 100;
        sprite.height = 100;
        sprite.windowX = 0;
        sprite.windowY = 0;
        sprite.windowWidth = sprite.sprite.getWidth();
        sprite.windowHeight = sprite.sprite.getHeight();
        this.ctx = ctx;

        addComponent(sprite);

        this.foodBar = foodBar;
        this.expbar = expbar;
        foodBar.maxValue = maxFood;
        foodBar.value = food;
        expbar.maxValue = maxExp;
        expbar.value = exp;

        this.levelUpLabel = levelUpLabel;
        this.levelLabel = levelLabel;
        this.gameOverTimer = gameOverTimer;
    }

    private BodyComponent generateBody(float x, float y, World world){
        BodyComponent bc = new BodyComponent();
        BodyDef bDef = new BodyDef();
        bDef.setType(BodyType.staticBody);
        bDef.setPosition(x, y);

        bc.body = world.createBody(bDef);

        bDef.delete();
        return bc;
    }

    public boolean consumeFood(int amount){
        if(food == 0)
            return false;
        food = Math.max(food-amount, 0);
        foodBar.value = food;
        if(food == 0)
            gameOverTimer.enabled = true;
        return true;
    }

    public void addFood(int amount){
        food = Math.min(food+amount, maxFood);
        foodBar.value = food;
        Assets.eatSound.play(1);
        gameOverTimer.enabled = false;
    }


    public void addExp(int amount){
        exp += amount;
        while(exp >= maxExp){
            exp -= maxExp;
            levelUp(ctx);
        }
        expbar.value = exp;
    }

    void levelUp(Context ctx){
        Assets.levelUpSound.play(1);
        levelUpLabel.play = true;
        level++;
        JsonSaver.addCoins(ctx, 10,game_stats);
        maxExp += (int) (Math.exp(level + 1) * 5);
        expbar.maxValue = maxExp;
        game_stats.level += 1;
        levelLabel.text = level + "";
    }
}
