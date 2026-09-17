package com.main.untitledantgame.screen;

import android.content.Context;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Graphics;
import com.badlogic.androidgames.framework.Input;
import com.badlogic.androidgames.framework.Screen;
import com.main.untitledantgame.components.ui.UIButtonComponent;
import com.main.untitledantgame.components.ui.UIElementComponent;
import com.main.untitledantgame.components.ui.UILabelComponent;
import com.main.untitledantgame.map.Statistics;
import com.main.untitledantgame.ui.UICanvas;
import com.main.untitledantgame.utility.JsonSaver;
import com.main.untitledantgame.utility.ecs.Entity;
import com.main.untitledantgame.utility.ecs.EntityFactory;
import com.main.untitledantgame.visuals.Assets;

import java.util.List;

public class StatisticsScreen extends Screen {
    UICanvas canvas;
    public StatisticsScreen(Game game) {
        super(game);
        initUI();
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

        Entity mainMenu = factory.create("CustMainMenu", null, null);
        UIButtonComponent.setOnClickEvent(mainMenu, ()->{
            game.setScreen(new MainMenuScreen(game));
        });

        Entity statsLabel = factory.create("StatisticsLabel", null, null);

        Entity antsSpawnedLabel = factory.create("AntsSpawnedLabel", null, null);
        Entity pheromonesSpawnedLabel = factory.create("PheromonesSpawnedLabel", null, null);
        Entity foodPickedLabel = factory.create("FoodPickedLabel", null, null);
        Entity obstaclesRemovedLabel = factory.create("ObstaclesRemovedLabel", null, null);
        Entity enemiesKilledLabel = factory.create("EnemiesKilledLabel", null, null);
        Entity levelReachedLabel = factory.create("LevelReachedLabel", null, null);
        Entity coinsEarnedLabel = factory.create("CoinsEarnedLabel", null, null);


        canvas.addElement(mainMenu);
        canvas.addElement(statsLabel);
        canvas.addElement(antsSpawnedLabel);
        canvas.addElement(pheromonesSpawnedLabel);
        canvas.addElement(foodPickedLabel);
        canvas.addElement(obstaclesRemovedLabel);
        canvas.addElement(enemiesKilledLabel);
        canvas.addElement(levelReachedLabel);
        canvas.addElement(coinsEarnedLabel);

        Statistics stats = JsonSaver.getHighestLevelGame((Context) game);
        if(stats == null)
            return;
        antsSpawnedLabel.getComponent(UILabelComponent.class).text = stats.ants_spawned + "";
        pheromonesSpawnedLabel.getComponent(UILabelComponent.class).text = stats.pheromones_spawned + "";
        foodPickedLabel.getComponent(UILabelComponent.class).text = stats.picked_food +"";
        obstaclesRemovedLabel.getComponent(UILabelComponent.class).text = stats.obstacle_moved + "";
        enemiesKilledLabel.getComponent(UILabelComponent.class).text = stats.enemies_killed + "";
        levelReachedLabel.getComponent(UILabelComponent.class).text = stats.level + "";
        coinsEarnedLabel.getComponent(UILabelComponent.class).text = stats.coin + "";
    }
}
