package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by JozeFe on 10/26/16.
 */

public class Main extends Game {

      @Override
    public void create() {
        this.setScreen(new MainScreen());

    }

    @Override
    public void render() {
        super.render();

    }

    @Override
    public void dispose() {

    }
}
