package com.mygdx.game.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;


/**
 * Created by JozeFe on 10/26/16.
 */

public class Ball {
    private Vector2 vector;
    private float radius;

    public Ball() {
        vector = new Vector2();
        vector.add(20, 20);
        radius = 50;
    }

    public void add(float x, float y) {
        vector.add(x, y);
    }

    public void setVector(float x, float y){
        vector.add(-vector.x + x , -vector.y + y);
    }

    public Vector2 getVector() {
        return vector;
    }

    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.GREEN);
        //shapeRenderer.circle(vector.x, vector.y, radius);
        shapeRenderer.rect(vector.x, vector.y, 15,15);

    }


}
