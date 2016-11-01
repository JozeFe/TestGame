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
 * @author Jozef Krcho
 */

public class MainScreen implements com.badlogic.gdx.Screen, InputProcessor {

    // Logic screen units in aspect ratio 3:2 used in all devices
    private static final float LOGIC_WIDTH = 480;
    private static final float LOGIC_HEIGHT = LOGIC_WIDTH *2/3;
    // max logic widht units is calculate for aspect ratio 16:9
    private static final float MAX_LOGIC_WIDTH = LOGIC_HEIGHT*16/9;
    // max logic height units is calculate for aspect ratio 4:3
    private static final float MAX_LOGIC_HEIGHT = LOGIC_WIDTH*3/4;
    // Real logic game width and height for specific screen resolution
    private static float realLogicWidth;
    private static float realLogicHeight;
    private float widthRatio;
    private float heightRatio;

    private final OrthographicCamera camera;
    private final Viewport viewport;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;

    private FingerKnife game;
    private Sprite background;

    private final Ball ball;
    private boolean dragging;

    private Gash gash;
    private List<Fruit> fruits;

    public MainScreen() {
        this.calculateRealLogicParameters(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera = new OrthographicCamera();
        /*
            ExtendeViewport have min, max width and height parameters, this configuration
            is set to be used on device with min aspect ratio 16:9 to 4:3 and
            will always show center of screen with ratio 3:2 on any kind of device screen aspect ratio.
            If device have more extream aspect ratio then black bars will be add.
         */
        viewport = new ExtendViewport(LOGIC_WIDTH, LOGIC_HEIGHT, MAX_LOGIC_WIDTH, MAX_LOGIC_HEIGHT, camera);
        viewport.apply();
        // center camera positon to mid-point of logical screen ( center of 3:2 part of screen )
        camera.position.set(LOGIC_WIDTH /2, -LOGIC_HEIGHT /2,0);
        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(camera.combined);

        // background set up
        background = new Sprite(new Texture(Gdx.files.internal("grid.jpg")));
        // TODO refactor this comment
        // set Position to bottom left corner
        background.setPosition((LOGIC_WIDTH-MAX_LOGIC_WIDTH)/2, (LOGIC_HEIGHT-MAX_LOGIC_HEIGHT)/2);
        // set size to maximum logical visible size (same for every device)
        background.setSize(MAX_LOGIC_WIDTH,MAX_LOGIC_HEIGHT);

        Gdx.input.setInputProcessor(this);
        game = new FingerKnife();

        ball = new Ball();
        gash = new Gash();
        dragging = false;
        fruits = new ArrayList<Fruit>();

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
        background.draw(batch);
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
        background.getTexture().dispose();

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
        ball.setVector(getLogicX(screenX), getLogicY(screenY));
        gash.reset();
        gash.add(getLogicX(screenX), getLogicY(screenY));
        dragging = true;
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
        ball.setVector(getLogicX(screenX), getLogicY(screenY));
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

    /**
     * Transform screenX position, (it's calculate on concrete device from left side to right)
     * to LogicalScreen X position units, (it's calculate from left to right)
     * @param screenX real X position on screen (concrete pixel)
     * @return logical X postion on screen (screen logic unit)
     */
    public float getLogicX(int screenX) {
        return screenX / widthRatio - (realLogicWidth - LOGIC_WIDTH)/2;
    }

    /**
     * Transform screenY position, (it's calculate on concrete device from top to bottom)
     * to LogicalScreen Y position units, (it's calculate from bottom to top)
     * @param screenY real Y position on screen (concrete pixel)
     * @return logical Y postion on screen (screen logic unit)
     */
    public float getLogicY(int screenY){
        return ((float) Gdx.graphics.getHeight() - screenY) / heightRatio - (realLogicHeight - LOGIC_HEIGHT) / 2;
    }

    /**
     * Calculate realLogical values that depends on device screen aspect ratio
     * if device aspect ratio is bigger than 3:2, e.g. 4:3 realLogicHeight will be bigger than LogicHeight
     * if device aspect ratio is smaller than 3:2, e.g 16:9 realLogicWidth will be bigger than LogicWidth.
     * @param width real device widht
     * @param height real device height
     */
    private void calculateRealLogicParameters(float width, float height) {
        //if device screen aspect ratio is smaller than 3:2, calculate realLogicHeight the width will be same
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
