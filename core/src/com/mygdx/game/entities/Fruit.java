package com.mygdx.game.entities;

/**
 * Created by JozeFe on 10/27/16.
 */

public class Fruit {
    private float x0,y0;
    private float v0;
    private float t, g;
    private double alfa;

    public Fruit(float x, float y, float v, float g, double alfa) {
        this.x0 = x;
        this.y0 = y;
        this.g = g;
        this.alfa = Math.toRadians(alfa);
        this.v0 = v;
        t = 0;
    }

    public void update(float time) {
        t += time;
    }

    public float getX() {
        return (float)(x0 + v0*t*Math.cos(alfa));
    }

    public float getY(){
        return (float)(y0 + v0*t*Math.sin(alfa) - (1.0/2)*g*t*t);
    }
}
