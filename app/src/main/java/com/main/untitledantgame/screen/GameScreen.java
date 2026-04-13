package com.main.untitledantgame.screen;

import com.badlogic.androidgames.framework.Color;
import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Graphics;
import com.badlogic.androidgames.framework.Input;
import com.badlogic.androidgames.framework.Pixmap;
import com.badlogic.androidgames.framework.Screen;
import com.google.fpl.liquidfun.DistanceJoint;
import com.google.fpl.liquidfun.DistanceJointDef;
import com.google.fpl.liquidfun.Joint;
import com.google.fpl.liquidfun.JointDef;
import com.google.fpl.liquidfun.RevoluteJoint;
import com.google.fpl.liquidfun.RevoluteJointDef;
import com.main.untitledantgame.components.BodyComponent;
import com.main.untitledantgame.components.PositionComponent;
import com.main.untitledantgame.components.SpriteComponent;
import com.main.untitledantgame.components.ui.UIBarComponent;
import com.main.untitledantgame.components.ui.UIButtonComponent;
import com.main.untitledantgame.components.ui.UIElementComponent;
import com.main.untitledantgame.map.Anthill;
import com.main.untitledantgame.map.GameWorld;
import com.main.untitledantgame.ui.UICanvas;
import com.main.untitledantgame.utility.ecs.Entity;
import com.main.untitledantgame.utility.ecs.EntityFactory;
import com.main.untitledantgame.visuals.Assets;

import java.util.List;

public class GameScreen extends Screen {

    int xOffset=0, yOffset=0, lastDragX, lastDragY; //servono per lo scorrimento della visuale
    float camVelocityX = 0, camVelocityY = 0, camSpeed = 120;

    boolean dragged = false;
    GameWorld world;
    UICanvas canvas;

    Anthill anthill;

    //GameContactListener contactListener;

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

        dragged = false;

        //GESTIONE EVENTI
        for(int i = 0; i < len; i++) {
            Input.TouchEvent event = touchEvents.get(i);
            if(event.type == Input.TouchEvent.TOUCH_DOWN) {
                lastDragX = event.x;
                lastDragY = event.y;
            }
            else if(event.type == Input.TouchEvent.TOUCH_DRAGGED) {
                xOffset += event.x - lastDragX;
                yOffset += event.y - lastDragY;
                lastDragX = event.x;
                lastDragY = event.y;
            }
            else if(event.type == Input.TouchEvent.TOUCH_UP){
                List<Entity> uiElements = canvas.elements;
                for(int j = 0; j < uiElements.size(); j++){
                    Entity e = uiElements.get(j);
                    UIButtonComponent.check(e, event, g);
                }
            }
        }
        //FISICA
        world.physicsWorld.step(deltaTime, 8, 3, 3);
        handleCollisions();
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
            e.getWithSuperComponent(UIElementComponent.class).draw(g);
        }
    }

    void initWorld(){
        EntityFactory factory = new EntityFactory(Assets.prototypeRegistry);

        //WORLD
        world = new GameWorld();
        Graphics g = game.getGraphics();

        //contactListener = new GameContactListener();
        //world.setContactlistener(contactListener);

        //UI
        canvas = new UICanvas();
        Entity foodBarEntity = factory.create("FoodBar");
        Entity pheroBarEntity = factory.create("PheroBar");
        Entity expBarEntity = factory.create("ExpBar");
        canvas.addElement(foodBarEntity);
        canvas.addElement(pheroBarEntity);
        canvas.addElement(expBarEntity);

        Entity reset = factory.create("ResetCameraButton");
        UIButtonComponent.setOnClickEvent(reset, ()->{xOffset = 0; yOffset = 0;});

        canvas.addElement(reset);

        //FORMICAIO

        int w = g.getWidth(), h = g.getHeight();
        anthill = new Anthill(w/2-(w/8), h/2-(h/8), foodBarEntity.getComponent(UIBarComponent.class), pheroBarEntity.getComponent(UIBarComponent.class), expBarEntity.getComponent(UIBarComponent.class));

        world.addEntity(anthill);

        //PULSANTE SPEDIZIONE
        Entity exploration = factory.create("ExplorationButton");
        UIButtonComponent.setOnClickEvent(exploration, ()->{
            if(anthill.consumeFood(10))
                spawnAnt();
        });
    }

    void spawnAnt(){
        return;
    }

    void handleCollisions(){
        /*List collisions = contactListener.getCollisions();
        for(int i = 0; i < collisions.size; i++){
            Collision event = collisions[i];
            if(event.a.type.equals("ant")){
                event.a.getComponent("AI").handleCollision(event.b, this);
            elseif(event.b.type.equals("ant")){
                event.a.getComponent("AI").handleCollision(event.a, this);
            }
        }

         */
    }

    public void attachFood(Entity a, Entity b) {
        DistanceJointDef jointDef = new DistanceJointDef();

        jointDef.setBodyA(a.getComponent(BodyComponent.class).body);
        jointDef.setBodyB(b.getComponent(BodyComponent.class).body);
        jointDef.setLocalAnchorA(a.getComponent(BodyComponent.class).body.getWorldCenter());
        jointDef.setLocalAnchorA(b.getComponent(BodyComponent.class).body.getWorldCenter());

        DistanceJoint joint = (DistanceJoint) world.physicsWorld.createJoint(jointDef);
    }
}