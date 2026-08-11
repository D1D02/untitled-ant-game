package com.main.untitledantgame.screen;

import static com.main.untitledantgame.utility.Quiz.getQuestion;

import android.util.Log;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Graphics;
import com.badlogic.androidgames.framework.Input;
import com.badlogic.androidgames.framework.Screen;
import com.google.fpl.liquidfun.Joint;
import com.main.untitledantgame.collision.Collision;
import com.main.untitledantgame.collision.GameContactListener;
import com.main.untitledantgame.components.AIComponent;
import com.main.untitledantgame.components.AliveComponent;
import com.main.untitledantgame.components.AntStatsComponent;
import com.main.untitledantgame.components.Behaviour;
import com.main.untitledantgame.components.BodyComponent;
import com.main.untitledantgame.components.FoodComponent;
import com.main.untitledantgame.components.GeneratorComponent;
import com.main.untitledantgame.components.IdentifierComponent;
import com.main.untitledantgame.components.ObstacleComponent;
import com.main.untitledantgame.components.SpriteComponent;
import com.main.untitledantgame.components.ui.UIBarComponent;
import com.main.untitledantgame.components.ui.UIButtonComponent;
import com.main.untitledantgame.components.ui.UIElementComponent;
import com.main.untitledantgame.components.ui.UILabelComponent;
import com.main.untitledantgame.map.Anthill;
import com.main.untitledantgame.map.GameWorld;
import com.main.untitledantgame.pools.EntityPool;
import com.main.untitledantgame.ui.UICanvas;
import com.main.untitledantgame.utility.Quiz;
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
    public GameWorld world;
    UICanvas canvas;

    public Anthill anthill;

    GameContactListener contactListener;

    EntityFactory entityFactory;
    List<Entity> pendingEntities = new ArrayList<>();
    List<Entity> generators = new ArrayList<>(5);
    List<Entity> mortalEntities = new ArrayList<>(100);

    List<Entity> obstacles = new ArrayList<>(10);
    int obSize = 0;
    int obMax = 10;
    UIButtonComponent[] boardButtons = new UIButtonComponent[3];

    UIButtonComponent quizButton;
    float quizTimer = 0f;
    float quizWaitTime = 5f;
    UILabelComponent quizLabel;
    UIButtonComponent quizTrue;
    UIButtonComponent quizFalse;

    EntityPool pheromones_pool;

    float move_ants = 0.0f;

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

        //QUIZ
        quizTimer += deltaTime;
        if(quizTimer >= quizWaitTime)
            quizReady();
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


            if("Ant".equals(e.type))
            {
                AIComponent agent = e.getComponent(AIComponent.class);
                if(e.getComponent(FoodComponent.class).isFoodEnded(deltaTime)) world.removeEntity(e);
                Log.d("FoodComponent", "Time passed" + e.getComponent(FoodComponent.class).last_consumed);
                float rot = agent.updatePosition(e.getComponent(BodyComponent.class).body, e.getComponent(AntStatsComponent.class).force, this, deltaTime);
                e.getComponent(SpriteComponent.class).rotation =(float) Math.toDegrees(rot) + 90f;
            }
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
            //g.drawPixmap(sprite.sprite,posX,posY, 0, 0, sprite.sprite.getWidth(), sprite.sprite.getHeight(), sprite.width, sprite.height, rot);
            g.drawPixmap(sprite.sprite, posX, posY, 0, 0, sprite.sprite.getWidth(), sprite.sprite.getHeight(), sprite.width, sprite.height, sprite.rotation);
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
        Entity expBarEntity = entityFactory.create("ExpBar", world.physicsWorld, game);
        canvas.addElement(foodBarEntity);
        canvas.addElement(expBarEntity);

        Entity reset = entityFactory.create("ResetCameraButton", world.physicsWorld, game);
        UIButtonComponent.setOnClickEvent(reset, ()->{xOffset = 0; yOffset = 0;});

        canvas.addElement(reset);

        Entity board1 = entityFactory.create("BoardButton1", world.physicsWorld, game);
        Entity board2 = entityFactory.create("BoardButton2", world.physicsWorld, game);
        Entity board3 = entityFactory.create("BoardButton3", world.physicsWorld, game);
        Entity toggleBoard = entityFactory.create("ToggleBoardButton", world.physicsWorld, game);

        boardButtons[0] = board1.getComponent(UIButtonComponent.class);
        boardButtons[1] = board2.getComponent(UIButtonComponent.class);
        boardButtons[2] = board3.getComponent(UIButtonComponent.class);
        UIButtonComponent toggle = toggleBoard.getComponent(UIButtonComponent.class);

        UIButtonComponent.setOnClickEvent(board1, ()->{selectBoardEvent(0);});
        UIButtonComponent.setOnClickEvent(board2, ()->{selectBoardEvent(1);});
        UIButtonComponent.setOnClickEvent(board3, ()->{selectBoardEvent(2);});

        UIButtonComponent.setOnClickEvent(toggleBoard, ()->{
            boardButtons[0].enabled = !boardButtons[0].enabled;
            boardButtons[1].enabled = !boardButtons[1].enabled;
            boardButtons[2].enabled = !boardButtons[2].enabled;
            if(boardButtons[0].enabled) {
                toggle.text = "v";
                toggle.posY = 40;
            }
            else{
                toggle.posY = 85;
                toggle.text = "^";
            }
        });

        Entity quiz = entityFactory.create("QuizButton", world.physicsWorld, game);
        Entity quizLabel = entityFactory.create("QuizLabel", world.physicsWorld, game);
        this.quizLabel = quizLabel.getComponent(UILabelComponent.class);

        UIButtonComponent.setOnClickEvent(quiz, ()->{startQuiz();});
        quizButton = quiz.getComponent(UIButtonComponent.class);
        this.quizLabel.enabled = false;
        quizButton.enabled = false;

        Entity quizTrue = entityFactory.create("QuizTrue", world.physicsWorld, game);
        Entity quizFalse = entityFactory.create("QuizFalse", world.physicsWorld, game);
        UIButtonComponent.setOnClickEvent(quizTrue, ()->{
            quizAnswer(Quiz.answer(true)); });
        UIButtonComponent.setOnClickEvent(quizFalse, ()->{quizAnswer(Quiz.answer(false));});

        this.quizTrue = quizTrue.getComponent(UIButtonComponent.class);
        this.quizFalse = quizFalse.getComponent(UIButtonComponent.class);
        this.quizTrue.enabled = false;
        this.quizFalse.enabled = false;

        canvas.addElement(board1);
        canvas.addElement(board2);
        canvas.addElement(board3);
        canvas.addElement(toggleBoard);
        canvas.addElement(quiz);
        canvas.addElement(quizLabel);
        canvas.addElement(quizTrue);
        canvas.addElement(quizFalse);

        updateBoard();

        //GENERATORI
        Entity food_generator = entityFactory.create("FoodGenerator", world.physicsWorld, game);
        //Entity obstacle_generator = entityFactory.create("ObstacleGenerator", world.physicsWorld, game);
        generators.add(food_generator);
        //generators.add(obstacle_generator);
        Entity rock_generator = entityFactory.create("RockGenerator", world.physicsWorld, game);
        generators.add(rock_generator);

        //FORMICAIO

        anthill = new Anthill(0, 0, foodBarEntity.getComponent(UIBarComponent.class),expBarEntity.getComponent(UIBarComponent.class), world.physicsWorld, game);

        world.addEntity(anthill);

        //PULSANTE SPEDIZIONE
        Entity exploration = entityFactory.create("ExplorationButton", world.physicsWorld, game);
        UIButtonComponent.setOnClickEvent(exploration, ()->{

            if(anthill.consumeFood(10)) {
                Entity ant = spawnEntity("Ant", 0, 0);
                ant.getComponent(AIComponent.class).wanderAngle = (float)(Math.random()-1f);

            }
        });
        canvas.addElement(exploration);

        //ENTITA' INIZIALI
        for(int i = 0; i < 10; i++){
            GeneratorComponent foodGen = food_generator.getComponent(GeneratorComponent.class);
            spawnEntity("Food", foodGen.getRandomPosition(), foodGen.getRandomPosition());
            if(i < 3){
                GeneratorComponent rockGen = rock_generator.getComponent(GeneratorComponent.class);
                spawnEntity("Rock", rockGen.getRandomPosition(), rockGen.getRandomPosition());
            }
        }

        pheromones_pool = new EntityPool(1000, "Pheromone", world.physicsWorld, game);
        trails = new Entity[1000][1000];
    }

    void quizReady(){
        quizButton.enabled = true;
        quizTimer = 0;
    }

    void startQuiz(){
        quizLabel.text = Quiz.getQuestion();
        quizLabel.enabled = true;
        quizButton.enabled = false;
        quizTrue.enabled = true;
        quizFalse.enabled = true;
    }

    void quizAnswer(boolean right){
        if(right)
            Assets.pickupSound.play(10);
        else
            Assets.eatSound.play(10);
        quizLabel.enabled = false;
        quizTrue.enabled = false;
        quizFalse.enabled = false;
    }

    void selectBoardEvent(int index){
        Log.d("OBSTACLES", "Ostacoli " +obSize);
        if(obSize <= index)
            return;
        Entity e = obstacles.get(index);
        BodyComponent bc = e.getComponent(BodyComponent.class);
        ObstacleComponent oc = e.getComponent(ObstacleComponent.class);

        //Graphics g = game.getGraphics();
        xOffset = -(int)(bc.body.getPositionX());// + g.getWidth()/2f);
        yOffset = -(int)(bc.body.getPositionY());// + g.getHeight()/2f);
        //Log.d("POS_DEBUG", xOffset + " " + yOffset);
        int antAmount = 2;
        if(!anthill.consumeFood(10*antAmount))
            return;

        for(int i = 0; i < antAmount; i++) {
            Entity ant = spawnEntity("Ant", 0, 0);
            AIComponent agent = ant.getComponent(AIComponent.class);
            agent.path_id = oc.path_id;
            agent.state = Behaviour.FOLLOWING;
            agent.inc_id = 1;
        }
    }

    public void addToBoard(Entity e, int id){
        if(obSize >= obMax){
            return;
        }
        Assets.boardSound.play(1);
        ObstacleComponent oc = e.getComponent(ObstacleComponent.class);
        oc.path_id = id;
        oc.spotted = true;
        obstacles.add(e);
        obSize++;
        updateBoard();
    }

    void updateBoard(){
        for(int i = 0; i < 3; i++) {
            if(i >= obstacles.size())
                boardButtons[i].text = "";
            else{
                Entity ob = obstacles.get(i);
                boardButtons[i].text = ob.getComponent(ObstacleComponent.class).name;
            }
        }


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
    Entity spawnEntity(String name, float x, float y){
        Entity newEntity = entityFactory.create(name, world.physicsWorld, game);
        newEntity.getComponent(BodyComponent.class).body.setTransform(x, y, 0);
        world.addEntity(newEntity);

        return newEntity;
    }

    public int generatePheromone(float x, float y, int path_id, int inc_id){

        if(path_id == -1 ) path_id = this.curr_path_id++;


        Entity newPheromone = pheromones_pool.acquire();

        newPheromone.getComponent(BodyComponent.class).body.setTransform(x,y,0);

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
                if(Objects.equals(event.a.type, "Ant")) event.a.getComponent(AIComponent.class).handleCollision( event.a, event.b, world, this);
                else if(Objects.equals(event.b.type, "Ant")) event.b.getComponent(AIComponent.class).handleCollision( event.b, event.a, world, this);
            }
            else{
                //if(Objects.equals(event.a.type, "Ant")) event.a.getComponent(AIComponent.class).handleSensorCollision( event.a, event.b, world);
                //else if(Objects.equals(event.b.type, "Ant")) event.b.getComponent(AIComponent.class).handleSensorCollision( event.b, event.a, world);
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


    public void destroyJoint(Joint joint) {
        if (joint != null) world.physicsWorld.destroyJoint(joint);
    }

    public void followingPath(int path_id){
        Entity ant = spawnEntity("Ant", 0, 0);
        AIComponent ant_ai = ant.getComponent(AIComponent.class);
        ant_ai.path_id = path_id;
        ant_ai.state = Behaviour.FOLLOWING;
    }
}