package com.mygdx.game.entities;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by JozeFe on 10/27/16.
 */

public class Gash {
    private List<Vector2> vectors;
    private float minDistance = 100;

    public Gash() {
        //vectors = new ArrayList<Vector2>();
        vectors = new LinkedList<Vector2>();
        //vectors.
    }

    public void add(float x, float y){
        if (vectors.isEmpty()) {
            vectors.add(new Vector2(x, y));
        }

        if (vectors.get(vectors.size()-1).dst2(x,y) > minDistance) {
            vectors.add(new Vector2(x, y));
        }
    }

    public List<Vector2> getVectors(){
        return vectors;
    }

    public void reset() {
        vectors.clear();
    }

    public Vector2 getFirst() {
        if (vectors.isEmpty())
            return null;
        return vectors.get(0);
    }
}
