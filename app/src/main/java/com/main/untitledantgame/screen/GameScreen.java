package com.main.untitledantgame.screen;

import com.badlogic.androidgames.framework.Color;
import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Graphics;
import com.badlogic.androidgames.framework.Input;
import com.badlogic.androidgames.framework.Pixmap;
import com.badlogic.androidgames.framework.Screen;
import com.google.fpl.liquidfun.DistanceJoint;
import com.google.fpl.liquidfun.DistanceJointDef;
import com.google.fpl.liquidfun.RevoluteJoint;
import com.google.fpl.liquidfun.RevoluteJointDef;
import com.main.untitledantgame.collision.Collision;
import com.main.untitledantgame.collision.GameContactListener;
import com.main.untitledantgame.components.AIComponent;
import com.main.untitledantgame.components.AliveComponent;
import com.main.untitledantgame.components.BodyComponent;
import com.main.untitledantgame.components.GeneratorComponent;
import com.main.untitledantgame.components.IdentifierComponent;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class GameScreen extends Screen {

    int xOffset=0, yOffset=0, lastDragX, lastDragY; //servono per lo scorrimento della visuale
    float camVelocityX = 0, camVelocityY = 0, camSpeed = 120;

    boolean dragged = false;
    GameWorld world;
    UICanvas canvas;

    Anthill anthill;

    GameContactListener contactListener;

    EntityFactory entityFactory;

    List<Entity> generators = new ArrayList<>(5);
    List<Entity> mortalEntities = new ArrayList<>(100);

    public GameScreen(Game game) {
        super(game);
        entityFactory = new EntityFactory(Assets.prototypeRegistry);
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
        moveAnts(deltaTime);
        world.physicsWorld.step(deltaTime, 8, 3, 3);
        handleCollisions(contactListener.getCollisions());
<<<<<<< HEAD


        checkTTL(deltaTime);
=======
        //GENERATORI
        generateItems(deltaTime);
>>>>>>> 9871365bb8474d8ab557efe16f87f278a2d01308
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

    void moveAnts(float deltaTime){
        List<Entity> entities = world.entities;
        for(int i = 0; i < entities.size();i++){ //l'enhanced for è meno efficiente perchè alloca memoria per la variabile di scorrimento
            Entity e = entities.get(i);
            if(!e.hasComponent(AIComponent.class))
                continue;
            AIComponent agent = e.getComponent(AIComponent.class);
            agent.updatePosition(e.getComponent(BodyComponent.class).body, 1000f, this, deltaTime);
        }
    }

    void drawWorld(){
        Graphics g = game.getGraphics();

        g.drawRect(0,0, 480, 320, Color.convert(120,210,50,255)); //sfondo provvisorio

        List<Entity> entities = world.entities;
        for(int i = 0; i < entities.size();i++){ //l'enhanced for è meno efficiente perchè alloca memoria per la variabile di scorrimento
            Entity e = entities.get(i);
            if(!e.hasComponent(SpriteComponent.class) || !e.hasComponent(BodyComponent.class))
                continue;
            SpriteComponent sprite = e.getComponent(SpriteComponent.class);
            BodyComponent body = e.getComponent(BodyComponent.class);
            int posX = (int)body.body.getPositionX() + xOffset;
            int posY = (int)body.body.getPositionY() + yOffset;
            g.drawPixmap(sprite.sprite,posX,posY, 0, 0, sprite.sprite.getWidth(), sprite.sprite.getHeight(), sprite.width, sprite.height);

        }

        List<Entity> uiElements = canvas.elements;
        for(int i = 0; i < uiElements.size(); i++){
            Entity e = uiElements.get(i);
            e.getWithSuperComponent(UIElementComponent.class).draw(g);
        }
    }

    void initWorld(){
        //WORLD
        world = new GameWorld();
        Graphics g = game.getGraphics();

        contactListener = new GameContactListener();
        world.physicsWorld.setContactListener(contactListener);

        //UI
        canvas = new UICanvas();
        Entity foodBarEntity = entityFactory.create("FoodBar", world.physicsWorld, game);
        Entity pheroBarEntity = entityFactory.create("PheroBar", world.physicsWorld, game);
        Entity expBarEntity = entityFactory.create("ExpBar", world.physicsWorld, game);
        canvas.addElement(foodBarEntity);
        canvas.addElement(pheroBarEntity);
        canvas.addElement(expBarEntity);

        Entity reset = entityFactory.create("ResetCameraButton", world.physicsWorld, game);
        UIButtonComponent.setOnClickEvent(reset, ()->{xOffset = 0; yOffset = 0;});

        canvas.addElement(reset);

        //GENERATORI
        Entity food_generator = entityFactory.create("FoodGenerator", world.physicsWorld, game);
        //Entity obstacle_generator = entityFactory.create("ObstacleGenerator", world.physicsWorld, game);
        generators.add(food_generator);
        //generators.add(obstacle_generator);

        //FORMICAIO

        int w = g.getWidth(), h = g.getHeight();
        anthill = new Anthill(w/2-(w/8), h/2-(h/8), foodBarEntity.getComponent(UIBarComponent.class), pheroBarEntity.getComponent(UIBarComponent.class), expBarEntity.getComponent(UIBarComponent.class), world.physicsWorld);

        world.addEntity(anthill);

        //PULSANTE SPEDIZIONE
        Entity exploration = entityFactory.create("ExplorationButton", world.physicsWorld, game);
        UIButtonComponent.setOnClickEvent(exploration, ()->{
            if(anthill.consumeFood(10))
                spawnEntity("Ant", 0, 0);
        });
        canvas.addElement(exploration);

        //ENTITA' INIZIALI
        for(int i = 0; i < 10; i++)
            spawnEntity("Food",
                        (float)(Math.random() * (food_generator.getComponent(GeneratorComponent.class).max_distance - food_generator.getComponent(GeneratorComponent.class).min_distance) + food_generator.getComponent(GeneratorComponent.class).min_distance),
                    (float)Math.random() * (food_generator.getComponent(GeneratorComponent.class).max_distance - food_generator.getComponent(GeneratorComponent.class).min_distance) + food_generator.getComponent(GeneratorComponent.class).min_distance);
    }

    void generateItems( float deltaTime ) {
        for(Entity generator : generators ) {
            if(generator.getComponent(GeneratorComponent.class).canBeGenerated(deltaTime)) {
                float x = (float)(Math.random() * (generator.getComponent(GeneratorComponent.class).max_distance - generator.getComponent(GeneratorComponent.class).min_distance) + generator.getComponent(GeneratorComponent.class).min_distance);
                float y = (float)(Math.random() * (generator.getComponent(GeneratorComponent.class).max_distance - generator.getComponent(GeneratorComponent.class).min_distance) + generator.getComponent(GeneratorComponent.class).min_distance);
                spawnEntity(generator.getComponent(GeneratorComponent.class).entity_name, x, y);
            }
        }
    }
    void spawnEntity(String name, float x, float y){
        Entity newEntity = entityFactory.create(name, world.physicsWorld, game);
        newEntity.getComponent(BodyComponent.class).body.setTransform(x, y, 0);
        world.addEntity(newEntity);
    }

    public void generatePheromone(float x, float y, int uuid){
        Entity newPheromone = entityFactory.create("Pheromone", world.physicsWorld, game);
        newPheromone.getComponent(BodyComponent.class).body.setTransform(x, y, 0);
        newPheromone.getComponent(IdentifierComponent.class).uuid = uuid;
        mortalEntities.add(newPheromone);
        world.addEntity(newPheromone);
    }

    void handleCollisions(Collection<Collision> collisions){
        for(Collision event : collisions){
            if(event.a.type.equals("ant")){
                event.a.getComponent(AIComponent.class).handleCollision(event.a, event.b, this);
            }
            else if(event.b.type.equals("ant")){
                event.a.getComponent(AIComponent.class).handleCollision(event.b, event.a, this);
            }
        }
    }

    public void attachFood(Entity a, Entity b) {
        DistanceJointDef jointDef = new DistanceJointDef();

        jointDef.setBodyA(a.getComponent(BodyComponent.class).body);
        jointDef.setBodyB(b.getComponent(BodyComponent.class).body);
        jointDef.setLocalAnchorA(a.getComponent(BodyComponent.class).body.getWorldCenter().getX(), a.getComponent(BodyComponent.class).body.getWorldCenter().getY());
        jointDef.setLocalAnchorB(b.getComponent(BodyComponent.class).body.getWorldCenter().getX(), b.getComponent(BodyComponent.class).body.getWorldCenter().getY());

        DistanceJoint joint = (DistanceJoint) world.physicsWorld.createJoint(jointDef);
    }

    public void dispatchCollisions() {
        Collection<Collision> collisions = contactListener.getCollisions();

        for(Collision event : collisions) {
            if(Objects.equals(event.a.type, "food") && Objects.equals(event.b.type, "ant") ||
            Objects.equals(event.b.type, "food") && Objects.equals(event.a.type, "ant")) {
                RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
                revoluteJointDef.setBodyA(event.a.getComponent(BodyComponent.class).body);
                revoluteJointDef.setBodyB(event.b.getComponent(BodyComponent.class).body);
                revoluteJointDef.setLocalAnchorA(event.a.getComponent(BodyComponent.class).body.getWorldCenter().getX(), event.a.getComponent(BodyComponent.class).body.getWorldCenter().getY());
                revoluteJointDef.setLocalAnchorB(event.b.getComponent(BodyComponent.class).body.getWorldCenter().getX(), event.b.getComponent(BodyComponent.class).body.getWorldCenter().getY());
                world.physicsWorld.createJoint(revoluteJointDef);

                RevoluteJoint revoluteJoint = (RevoluteJoint) world.physicsWorld.createJoint(revoluteJointDef);
                revoluteJointDef.delete();

            }
        }
    }
}