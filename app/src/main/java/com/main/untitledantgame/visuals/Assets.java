package com.main.untitledantgame.visuals;

import com.badlogic.androidgames.framework.Audio;
import com.badlogic.androidgames.framework.Music;
import com.badlogic.androidgames.framework.Pixmap;
import com.badlogic.androidgames.framework.Sound;
import com.badlogic.androidgames.framework.impl.AndroidAudio;
import com.badlogic.androidgames.framework.impl.AndroidMusic;
import com.main.untitledantgame.map.Generator;
import com.main.untitledantgame.utility.ecs.PrototypeRegistry;

import java.util.List;

public class Assets {
    public static Pixmap background;
    public static Pixmap anthill;
    public static PrototypeRegistry prototypeRegistry;
    public static List<Generator> generators;

    public static Music soundtrack;
    public static Sound eatSound;
}