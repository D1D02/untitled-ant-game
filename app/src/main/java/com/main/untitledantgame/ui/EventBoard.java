package com.main.untitledantgame.ui;

import com.main.untitledantgame.components.ui.UIButtonComponent;

public class EventBoard {
    UIButtonComponent toggleButton;
    UIButtonComponent b1;
    UIButtonComponent b2;
    UIButtonComponent b3;

    public EventBoard(UIButtonComponent toggleButton, UIButtonComponent b1,
                      UIButtonComponent b2, UIButtonComponent b3){
        this.toggleButton = toggleButton;
        this.b1 = b1;
        this.b2 = b2;
        this.b3 = b3;
    }

    public void toggle(boolean value){
        toggleButton.enabled = value;
        b1.enabled = value;
        b2.enabled = value;
        b3.enabled = value;
    }
}
