package com.main.untitledantgame.map;

public class Statistics {
    public int ants_spawned;
    public int pheromones_spawned;
    public int picked_food;
    public int obstacle_moved;
    public int enemies_killed;
    public int level;
    public int coin;


    public void reset() {
        this.ants_spawned = 0;
        this.pheromones_spawned = 0;
        this.picked_food = 0;
        this.obstacle_moved = 0;
        this.enemies_killed = 0;
        this.level = 1;
        this.coin = 0;
    }

}
