package com.main.untitledantgame;

import android.os.Bundle;
import android.util.Log;

import com.badlogic.androidgames.framework.Screen;
import com.badlogic.androidgames.framework.impl.AndroidGame;
import com.main.untitledantgame.screen.LoadingScreen;

public class UntitledAntGame extends AndroidGame {

    @Override
    public Screen getStartScreen() {
        return new LoadingScreen(this);
    }
}
