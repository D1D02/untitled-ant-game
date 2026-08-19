package com.main.untitledantgame.visuals;

import com.badlogic.androidgames.framework.Audio;
import com.badlogic.androidgames.framework.Music;
import com.badlogic.androidgames.framework.Pixmap;
import com.badlogic.androidgames.framework.Sound;
import com.badlogic.androidgames.framework.impl.AndroidAudio;
import com.badlogic.androidgames.framework.impl.AndroidMusic;
import com.main.untitledantgame.map.GameWorld;
import com.main.untitledantgame.map.Generator;
import com.main.untitledantgame.pools.EntityPool;
import com.main.untitledantgame.utility.ecs.PrototypeRegistry;

import java.util.List;

public class Assets {
    public static Pixmap background;
    public static Pixmap anthill;
    public static Pixmap loading;
    public static Pixmap game_over;
    public static Pixmap button;

    public static Pixmap[] antSkins;
    public static PrototypeRegistry prototypeRegistry;
    public static List<Generator> generators;

    public static EntityPool pheromones_pool;

    public static GameWorld world;

    public static Music soundtrack;
    public static Sound eatSound;

    public static Sound levelUpSound;

    public static Sound buttonSound;

    public static Sound pickupSound;

    public static Sound boardSound;

    public static Sound correctSound;

    public static Sound wrongSound;
}