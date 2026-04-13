package com.main.untitledantgame.map;

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

    public Anthill(int x, int y, UIBarComponent foodBar, UIBarComponent pheroBar, UIBarComponent expbar){
        PositionComponent pos = new PositionComponent();
        pos.setX(x);
        pos.setY(y);
        addComponent(pos);
        SpriteComponent sprite = new SpriteComponent();
        sprite.setSprite(Assets.anthill);
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
