package com.main.untitledantgame.screen;

import com.badlogic.androidgames.framework.Color;
import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Graphics;
import com.badlogic.androidgames.framework.Input;
import com.badlogic.androidgames.framework.Pixmap;
import com.badlogic.androidgames.framework.Screen;
import com.main.untitledantgame.components.PositionComponent;
import com.main.untitledantgame.components.SpriteComponent;
import com.main.untitledantgame.components.UIelementComponent;
import com.main.untitledantgame.map.GameWorld;
import com.main.untitledantgame.ui.UICanvas;
import com.main.untitledantgame.ui.UIbar;
import com.main.untitledantgame.utility.ecs.Entity;
import com.main.untitledantgame.visuals.Assets;

import java.util.List;

public class GameScreen extends Screen {

    int xOffset=0, yOffset=0, touchX,touchY; //servono per lo scorrimento della visuale
    GameWorld world;
    UICanvas canvas;
    public GameScreen(Game game) {
        super(game);
        initWorld();
    }

    @Override
    public void update(float deltaTime) {
        Graphics g = game.getGraphics();
        List<Input.TouchEvent> touchEvents = game.getInput().getTouchEvents();
        game.getInput().getKeyEvents();
        int len = touchEvents.size();

        for(int i = 0; i < len; i++) {
            Input.TouchEvent event = touchEvents.get(i);
            if(event.type == Input.TouchEvent.TOUCH_DOWN) {
                touchX = event.x;
                touchY = event.y;
            }
            else if(event.type == Input.TouchEvent.TOUCH_DRAGGED) {
                xOffset += (event.x - touchX)/10;
                yOffset += (event.y - touchY)/10;
            }
        }
    }

    @Override
    public void present(float deltaTime) {
        drawWorld();
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

    void drawWorld(){
        Graphics g = game.getGraphics();

        g.drawRect(0,0, 480, 320, Color.convert(120,210,50,255)); //sfondo provvisorio

        List<Entity> entities = world.entities;
        for(int i = 0; i < entities.size();i++){ //l'enhanced for è meno efficiente perchè alloca memoria per la variabile di scorrimento
            Entity e = entities.get(i);
            if(!e.hasComponent(SpriteComponent.class) || !e.hasComponent(PositionComponent.class))
                continue;
            Pixmap sprite = e.getComponent(SpriteComponent.class).getSprite();
            PositionComponent pos = e.getComponent(PositionComponent.class);
            g.drawPixmap(sprite,(int)pos.getX() + xOffset,(int)pos.getY() + yOffset);
        }

        List<Entity> uiElements = canvas.elements;
        for(int i = 0; i < uiElements.size(); i++){
            Entity e = uiElements.get(i);
            if(!e.hasComponent(UIelementComponent.class))
                continue;
            e.getComponent(UIelementComponent.class).draw(g);
        }
    }

    void initWorld(){
        world = new GameWorld();
        Entity anthill = new Entity(); //sostituire con creazione attraverso EntityFactory
        anthill.addComponent(new PositionComponent(0,0)); //sostituire con approccio data-driven attraverso component factory
        anthill.addComponent(new SpriteComponent(Assets.anthill));
        world.addEntity(anthill);

        canvas = new UICanvas();
        Entity foodBar = new Entity(); //sostituire con generazione con factory...
        UIbar bar = new UIbar();
        UIelementComponent barComponent = new UIelementComponent(bar);
        foodBar.addComponent(barComponent);
        canvas.addElement(foodBar);
    }
}