package com.mygdx.game;

import com.mygdx.game.entities.Ball;
import com.mygdx.game.entities.Fruit;
import com.mygdx.game.entities.Gash;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JozeFe on 11/1/16.
 * @author Jozef Krcho
 */

public class FingerKnife {

    private Ball ball;
    private Gash gash;
    private List<Fruit> fruits;

    public FingerKnife() {
        ball = new Ball();
        gash = new Gash();
        fruits = new ArrayList<Fruit>();
    }

    public void dispose() {

    }
}
