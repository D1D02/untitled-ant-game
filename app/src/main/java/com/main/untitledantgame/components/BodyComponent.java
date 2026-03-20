package com.main.untitledantgame.components;

import com.google.fpl.liquidfun.Body;

public class BodyComponent implements Component {
    Body body;

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }
}
