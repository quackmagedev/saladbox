package com.mygdx.game;

import com.badlogic.gdx.Gdx;

public class ScreenBounds {
    int xMin;
    int xMax;
    int yMin;
    int yMax;

    public ScreenBounds(int radius) {
        this.xMax = Gdx.graphics.getWidth() - radius;
        this.xMin = radius;
        this.yMax = Gdx.graphics.getHeight() - radius;
        this.yMin = radius;
    }
}
