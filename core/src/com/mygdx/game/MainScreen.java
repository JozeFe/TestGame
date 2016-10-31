package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.entities.Ball;
import com.mygdx.game.entities.Fruit;
import com.mygdx.game.entities.Gash;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Created by JozeFe on 10/26/16.
 */

public class MainScreen implements com.badlogic.gdx.Screen, InputProcessor {

    // Logic screen units in aspect ratio 3:2 used in all devices
    public static final float LOGIC_WIDTH = 480;
    public static final float LOGIC_HEIGHT = LOGIC_WIDTH *2/3;
    // max logic widht units is calculate for aspect ratio 16:9
    public static final float MAX_LOGIC_WIDTH = LOGIC_HEIGHT*16/9;
    // max logic height units is calculate for aspect ratio 4:3
    public static final float MAX_LOGIC_HEIGHT = LOGIC_WIDTH*3/4;
    // Real logic game width and height for specific screen resolution
    public static float realLogicWidth;
    public static float realLogicHeight;
    private float widthRatio;
    private float heightRatio;

    private final OrthographicCamera camera;
    private SpriteBatch batch;
    private Viewport viewport;
    private ShapeRenderer shapeRenderer;

    private final Ball ball;
    private boolean dragging;
    private Sprite sprite;
    private Gash gash;
    private List<Fruit> fruits;

    public MainScreen() {
        this.calculateRealLogicParameters(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        camera = new OrthographicCamera();
        // Extend viewport have min, max width and height parameters defiend min and max logical screen units to show
        // then he used black bars. This will allways show logical area 3:2 for any aspect ratio
        viewport = new ExtendViewport(LOGIC_WIDTH, LOGIC_HEIGHT, MAX_LOGIC_WIDTH, MAX_LOGIC_HEIGHT, camera);
        viewport.apply();
        // center camera positon to mid-point of logical screen
        camera.position.set(LOGIC_WIDTH /2, -LOGIC_HEIGHT /2,0);

        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(camera.combined);

        sprite = new Sprite(new Texture(Gdx.files.internal("grid.jpg")));
        //sprite.setPosition(-45, -20);
        //sprite.setSize(570,360);
        sprite.setPosition((LOGIC_WIDTH-MAX_LOGIC_WIDTH)/2, (LOGIC_HEIGHT-MAX_LOGIC_HEIGHT)/2);
        sprite.setSize(MAX_LOGIC_WIDTH,MAX_LOGIC_HEIGHT);
        ball = new Ball();
        gash = new Gash();
        dragging = false;
        fruits = new ArrayList<Fruit>();
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();

        for (Fruit fruit : fruits){
            fruit.update(delta);
        }
        for (Iterator<Fruit> i = fruits.iterator(); i.hasNext();) {
            Fruit fruit = i.next();
            if (fruit.getY() < -50){
                i.remove();
            }
        }
        Random r = new Random();
        if (r.nextInt(100) > 95) {
            fruits.add(new Fruit(r.nextInt(100)-50, -40, r.nextInt(50) + 400, 250, r.nextInt(25)+ 50));
        }

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        sprite.draw(batch);
        batch.end();

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        ball.render(shapeRenderer);
        shapeRenderer.setColor(Color.YELLOW);
        shapeRenderer.rect(0,0, 30, 30);
        shapeRenderer.rect(450,0, 30, 30);
        shapeRenderer.rect(0,290, 30, 30);
        shapeRenderer.rect(450,290, 30, 30);
        shapeRenderer.setColor(Color.RED);
        Vector2 vector1 = gash.getFirst();
        if (vector1 != null) {
            for (Vector2 vector : gash.getVectors()) {
                shapeRenderer.rectLine(vector1, vector, 5);
                vector1 = vector;
            }
        }
        shapeRenderer.setColor(Color.BLUE);
        for (Fruit fruit: fruits) {
            shapeRenderer.circle(fruit.getX(), fruit.getY(), 10);
        }
        shapeRenderer.end();

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(LOGIC_WIDTH /2, LOGIC_HEIGHT /2,0);

        this.calculateRealLogicParameters(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        sprite.getTexture().dispose();

    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.LEFT) {
            camera.translate(-50f, 0f);
        }
        if (keycode == Input.Keys.RIGHT) {
            camera.translate(50f, 0f);
        }
        if (keycode == Input.Keys.UP) {
            camera.translate(0f, 50f);
        }
        if (keycode == Input.Keys.DOWN) {
            camera.translate(0f, -50f);
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button != Input.Buttons.LEFT || pointer > 0) return false;
        setBall(screenX,screenY);
        dragging = true;
        gash.reset();
        gash.add(getLogicX(screenX), getLogicY(screenY));
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button != Input.Buttons.LEFT || pointer > 0) return false;
        dragging = false;
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (!dragging) return false;
        setBall(screenX,screenY);
        gash.add(getLogicX(screenX), getLogicY(screenY));
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    public void setBall(int screenX, int screenY){
        float tmpX, tmpY;
        tmpX = screenX / widthRatio - (realLogicWidth - LOGIC_WIDTH)/2;
        tmpY = ((float) Gdx.graphics.getHeight() - screenY) / heightRatio - (realLogicHeight - LOGIC_HEIGHT) / 2;
        ball.setVector(tmpX, tmpY);
    }

    public float getLogicX(int screenX) {
        return screenX / widthRatio - (realLogicWidth - LOGIC_WIDTH)/2;
    }

    public float getLogicY(int screenY){
        return ((float) Gdx.graphics.getHeight() - screenY) / heightRatio - (realLogicHeight - LOGIC_HEIGHT) / 2;
    }

    private void calculateRealLogicParameters(float width, float height) {
        //if device screen aspect ratio is smaller than 3:2, calculate realLogicHeight the widtht will be same
        if ((width/height) < (LOGIC_WIDTH/LOGIC_HEIGHT)) {
            realLogicHeight = (LOGIC_WIDTH * height) / width;
            realLogicWidth = LOGIC_WIDTH;
        } else { //if device screen aspect ratio is bigger than 3:2, calculate realLogicWidth the height will be same
            realLogicHeight = LOGIC_HEIGHT;
            realLogicWidth = (LOGIC_HEIGHT * width) / height;
        }
        widthRatio = width / realLogicWidth;
        heightRatio = height / realLogicHeight;
    }

}
