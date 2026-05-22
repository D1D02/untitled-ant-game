package com.main.untitledantgame.map;

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
import com.main.untitledantgame.utility.ecs.ComponentFactory;
import com.main.untitledantgame.utility.ecs.Entity;
import com.main.untitledantgame.visuals.Assets;

public class Anthill extends Entity {
    UIBarComponent foodBar, pheroBar, expbar;
    int food = 100, phero = 0, exp = 0;
    int maxFood = 100, maxPhero = 100, maxExp = 100;
    int level = 1;

    public Anthill(int x, int y, UIBarComponent foodBar, UIBarComponent pheroBar, UIBarComponent expbar, World world, Game game){
        BodyComponent body = generateBody(x, y, world);

        addComponent(body);
        SpriteComponent sprite = new SpriteComponent();

        sprite.width = 100;
        sprite.height = 100;

        sprite.sprite = Assets.anthill;

        addComponent(sprite);

        this.foodBar = foodBar;
        this.pheroBar = pheroBar;
        this.expbar = expbar;
        foodBar.maxValue = maxFood;
        foodBar.value = food;
        pheroBar.maxValue = maxPhero;
        pheroBar.value = phero;
        expbar.maxValue = maxExp;
        expbar.value = exp;
    }

    private BodyComponent generateBody(float x, float y, World world){
        BodyComponent bc = new BodyComponent();
        BodyDef bDef = new BodyDef();
        bDef.setType(BodyType.staticBody);
        bDef.setPosition(x, y);

        bc.body = world.createBody(bDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(20, 20);

        FixtureDef fDef = new FixtureDef();
        fDef.setShape(shape);
        fDef.setDensity(1f);
        fDef.setFriction(1f);
        fDef.setRestitution(0f);

        //bc.body.createFixture(fDef);
        shape.delete();
        fDef.delete();

        shape.delete();
        bDef.delete();
        return bc;
    }

    public boolean consumeFood(int amount){
        if(amount > food)
            return false;
        food -= amount;
        foodBar.value = food;
        return true;
    }

    public void addFood(int amount){
        food = Math.min(food+amount, maxFood);
        foodBar.value = food;
    }

    public boolean consumePhero(int amount){
        if(amount > phero)
            return false;
        phero -= amount;
        pheroBar.value = phero;
        return true;
    }

    public void addPhero(int amount){
        phero = Math.min(phero + amount, maxPhero);
        pheroBar.value = phero;
    }

    public void addExp(int amount){
        exp += amount;
        while(exp >= maxExp){
            exp -= maxExp;
            levelUp();
        }
        expbar.value = exp;
    }

    void levelUp(){
        level++;
        maxExp += 25;
        expbar.maxValue = maxExp;
    }
}
