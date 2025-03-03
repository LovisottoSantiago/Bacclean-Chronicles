package entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.Input;

import java.util.HashMap;
import java.util.Map;

public class Player {

    public enum State { IDLE, RUNNING, ATTACKING, JUMPING, FALLING }

    private State state;
    private final Vector2 position;
    private float stateTime;
    private final Map<State, Animation<TextureRegion>> animations;
    private boolean facingRight = true;


    public Player() {
        state = State.IDLE;
        position = new Vector2(100, 100);
        stateTime = 0;
        animations = new HashMap<>();

        animations.put(State.IDLE, loadAnimation("player/idle.png", 6, 0.1f));
        animations.put(State.RUNNING, loadAnimation("player/running.png", 8, 0.08f));
        animations.put(State.ATTACKING, loadAnimation("player/idle.png", 5, 0.1f));
        animations.put(State.JUMPING, loadAnimation("player/idle.png", 2, 0.15f));
        animations.put(State.FALLING, loadAnimation("player/idle.png", 2, 0.15f));
    }

    // Basics methods
    public void update(float delta) {
        stateTime += delta;
        playerMovement(delta);
    }

    public void setState(State newState) {
        if (state != newState) {
            state = newState;
            stateTime = 0;
        }
    }

    public void render(SpriteBatch batch) {
        TextureRegion currentFrame = animations.get(state).getKeyFrame(stateTime, true);
        float width = currentFrame.getRegionWidth() * 2.0f;
        float height = currentFrame.getRegionHeight() * 2.0f;
        batch.draw(animations.get(state).getKeyFrame(stateTime, true), position.x, position.y, width, height);
    }


    // Animations
    private Animation<TextureRegion> loadAnimation(String path, int cols, float frameDuration) {
        Texture sheet = new Texture(Gdx.files.internal(path));
        TextureRegion[][] tmp = TextureRegion.split(sheet, sheet.getWidth() / cols, sheet.getHeight());
        TextureRegion[] frames = new TextureRegion[cols];

        int index = 0;
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < cols; j++) {
                frames[index] = tmp[i][j];
                index++;
            }
        }
        return new Animation<>(frameDuration, frames);
    }

    private void flipAnimations() {
        for (Animation<TextureRegion> animation : animations.values()) {
            for (TextureRegion frame : animation.getKeyFrames()) {
                frame.flip(true, false);
            }
        }
    }


    // Movements
    public void playerMovement(float delta){
        float horizontalVelocity = 180;

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            setState(State.RUNNING);
            position.x += horizontalVelocity * delta;

            if (!facingRight) {
                flipAnimations();
                facingRight = true;
            }
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            setState(State.RUNNING);
            position.x -= horizontalVelocity * delta;

            if (facingRight) {
                flipAnimations();
                facingRight = false;
            }
        }
        else {
            setState(State.IDLE);
        }
    }






}
