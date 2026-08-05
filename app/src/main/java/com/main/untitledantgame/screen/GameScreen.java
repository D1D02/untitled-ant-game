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
import com.google.fpl.liquidfun.RevoluteJoint;
import com.google.fpl.liquidfun.RevoluteJointDef;
import com.google.fpl.liquidfun.Vec2;
import com.google.fpl.liquidfun.World;
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
import com.main.untitledantgame.pools.EntityPool;
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
    List<Entity> pendingEntities = new ArrayList<>();
    List<Entity> generators = new ArrayList<>(5);
    List<Entity> mortalEntities = new ArrayList<>(100);

    EntityPool pheromones_pool;

    Entity[][] trails;

    int curr_path_id = 0;

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
        //handleCollisions(contactListener.getCollisions());
        dispatchCollisions();

        checkTTL(deltaTime);
        //GENERATORI
        generateItems(deltaTime);

        for(Entity e : pendingEntities){
            world.addEntity(e);
        }

        pendingEntities.clear();
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
            float agentAngle = agent.updatePosition(e.getComponent(BodyComponent.class).body, 5000f, this, deltaTime);
        }
    }

    void drawWorld(){
        Graphics g = game.getGraphics();

        //g.drawRect(0,0, 480, 320, Color.convert(120,210,50,255)); //sfondo provvisorio
        g.drawPixmap(Assets.background, xOffset-500, yOffset-500);


        List<Entity> entities = world.entities;
        for(int i = 0; i < entities.size();i++){ //l'enhanced for è meno efficiente perchè alloca memoria per la variabile di scorrimento
            Entity e = entities.get(i);
            if(!e.hasComponent(SpriteComponent.class) || !e.hasComponent(BodyComponent.class))
                continue;
            SpriteComponent sprite = e.getComponent(SpriteComponent.class);
            BodyComponent body = e.getComponent(BodyComponent.class);
            int posX = (int)body.body.getPositionX() + g.getWidth()/2 - sprite.width/2 + xOffset;
            int posY = (int)body.body.getPositionY() + g.getHeight()/2 - sprite.height/2 + yOffset;
            g.drawPixmap(sprite.sprite,posX,posY, 0, 0, sprite.sprite.getWidth(), sprite.sprite.getHeight(), sprite.width, sprite.height);

        }

        List<Entity> uiElements = canvas.elements;
        for(int i = 0; i < uiElements.size(); i++){
            Entity e = uiElements.get(i);
            UIElementComponent ui = e.getWithSuperComponent(UIElementComponent.class);
            if(ui.enabled)
                ui.draw(g);
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
        anthill = new Anthill(0, 0, foodBarEntity.getComponent(UIBarComponent.class), pheroBarEntity.getComponent(UIBarComponent.class), expBarEntity.getComponent(UIBarComponent.class), world.physicsWorld, game);

        world.addEntity(anthill);

        //PULSANTE SPEDIZIONE
        Entity exploration = entityFactory.create("ExplorationButton", world.physicsWorld, game);
        UIButtonComponent.setOnClickEvent(exploration, ()->{
            if(anthill.consumeFood(10))
                spawnEntity("Ant", 0, 0);
        });
        canvas.addElement(exploration);

        //ENTITA' INIZIALI
        for(int i = 0; i < 10; i++){
            GeneratorComponent gen = food_generator.getComponent(GeneratorComponent.class);
            spawnEntity("Food", gen.getRandomPosition(), gen.getRandomPosition());
        }

        pheromones_pool = new EntityPool(1000, "Pheromone", world.physicsWorld, game);
        trails = new Entity[1000][1000];

    }
    void generateItems( float deltaTime ) {
        for(Entity generator : generators ) {
            if(generator.getComponent(GeneratorComponent.class).canBeGenerated(deltaTime)) {
                GeneratorComponent gen = generator.getComponent(GeneratorComponent.class);
                float x = gen.getRandomPosition();
                float y = gen.getRandomPosition();
                spawnEntity(generator.getComponent(GeneratorComponent.class).entity_name, x, y);
            }
        }
    }
    void spawnEntity(String name, float x, float y){
        Entity newEntity = entityFactory.create(name, world.physicsWorld, game);
        newEntity.getComponent(BodyComponent.class).body.setTransform(x, y, 0);
        world.addEntity(newEntity);
    }

    public int generatePheromone(float x, float y, int path_id, int inc_id){

        if(path_id == -1 ) path_id = this.curr_path_id++;


        Entity newPheromone = pheromones_pool.acquire();

        newPheromone.getComponent(BodyComponent.class)
                .body.setTransform(x,y,0);

        newPheromone.getComponent(IdentifierComponent.class).inc_id = inc_id;

        trails[path_id][inc_id] = newPheromone;



        mortalEntities.add(newPheromone);

        pendingEntities.add(newPheromone);
        return  path_id;
    }

    public void dispatchCollisions() {
        Collection<Collision> collisions = contactListener.getCollisions();

        for(Collision event : collisions) {
            if( !event.sensor ) {
                if(Objects.equals(event.a.type, "Ant")) event.a.getComponent(AIComponent.class).handleCollision( event.a, event.b, world);
                else if(Objects.equals(event.b.type, "Ant")) event.b.getComponent(AIComponent.class).handleCollision( event.b, event.a, world);
            }
            else{
                if(Objects.equals(event.a.type, "Ant")) event.a.getComponent(AIComponent.class).handleSensorCollision( event.a, event.b, world);
                else if(Objects.equals(event.b.type, "Ant")) event.b.getComponent(AIComponent.class).handleSensorCollision( event.b, event.a, world);
            }
        }
    }

    void checkTTL(float deltaTime) {
        Iterator<Entity> iterator = mortalEntities.iterator();

        while (iterator.hasNext()) {
            Entity mortalEntity = iterator.next();

            if (mortalEntity.getComponent(AliveComponent.class).isDead(deltaTime)) {
                iterator.remove();

                world.removeEntity(mortalEntity);
                if (Objects.equals(mortalEntity.type, "Pheromone")) {
                    IdentifierComponent id = mortalEntity.getComponent(IdentifierComponent.class);
                    /*if (trails[id.path_id][id.inc_id] == mortalEntity) {
                        trails[id.path_id][id.inc_id] = null;
                    }*/
                    pheromones_pool.release(mortalEntity.pool_id);
                }
            }
        }
    }


    public Entity nextTrailPheromone(int path_id, int inc_id) {
        // Controllo di sicurezza sui limiti delle matrici
        if (path_id < 0 || path_id >= 1000 || inc_id < 0 || inc_id >= 1000) {
            return null;
        }
        return trails[path_id][inc_id];
    }

    public int fun() {
        Entity newPheromone = pheromones_pool.acquire();

        newPheromone.getComponent(BodyComponent.class)
                .body.setTransform(0,0,0);

        return 1;
    }

    public Vec2 getAnthillPosition() {
        return new Vec2(0, 0); // il formicaio viene creato in (0,0) in initWorld()
    }

    public void destroyJoint(Joint joint) {
        if (joint != null) world.physicsWorld.destroyJoint(joint);
    }

    public void removeEntity(Entity e) {
        if (e != null) world.removeEntity(e);
    }

    public void depositFood(int amount) {
        anthill.addFood(amount); // adatta al nome reale del metodo nella tua classe Anthill
    }
}