package io.github.bacclean;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Player extends Sprite {
    private final Texture walkSheet;
    private final TextureRegion[] walkFrames;
    private final TextureRegion[] walkFramesLeft;

    private final Texture stanceSheet;
    private final TextureRegion[] stanceFrames;
    private final TextureRegion[] stanceFramesLeft;

    private float stateTime;
    private int currentFrame;
    private boolean facingLeft; // Track the player's facing direction

    public Player(String walkSheetPath, int walkFrNumber, String stanceSheetPath, int stanceFrNumber) {
        // Load the texture sheets
        walkSheet = new Texture(Gdx.files.internal(walkSheetPath));
        stanceSheet = new Texture(Gdx.files.internal(stanceSheetPath));


        // WALK
        TextureRegion[][] tmpWalk = TextureRegion.split(walkSheet, walkSheet.getWidth() / walkFrNumber, walkSheet.getHeight());
        walkFrames = new TextureRegion[walkFrNumber]; // Assuming 3 frames
        System.arraycopy(tmpWalk[0], 0, walkFrames, 0, walkFrNumber); // Assuming 1 row of animation frames

        walkFramesLeft = new TextureRegion[walkFrNumber]; // Assuming 3 frames
        for (int i = 0; i < walkFramesLeft.length; i++) {
            walkFramesLeft[i] = new TextureRegion(walkFrames[i]); // Copy the frame
            walkFramesLeft[i].flip(true, false); // Flip it in the X-axis
        }



        // STANCE
        TextureRegion[][] tmpStance = TextureRegion.split(stanceSheet, stanceSheet.getWidth() / stanceFrNumber, stanceSheet.getHeight());
        stanceFrames = new TextureRegion[stanceFrNumber]; // Assuming 3 frames for stance
        System.arraycopy(tmpStance[0], 0, stanceFrames, 0, stanceFrNumber);

        stanceFramesLeft = new TextureRegion[stanceFrNumber];
        for (int i = 0; i < stanceFramesLeft.length; i++) {
            stanceFramesLeft[i] = new TextureRegion(stanceFrames[i]);
            stanceFramesLeft[i].flip(true, false);
        }




        // Set the player's initial texture (default stance)
        //setRegion(stanceFrames[0]);
        facingLeft = false; // Initially facing right

        // Initialize animation variables
        stateTime = 0f;
        currentFrame = 0;
    }

    // Method to move the player
    public void playerMove(float speed, float delta) {
        // Handle movement input
        boolean isMoving = false;
        boolean movingLeft = false;

        // Handle movement input
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            //*  jump logic here */
            
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            this.translateX(-speed * delta); // Move left
            movingLeft = true; // Set movingLeft to true
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            this.translateX(speed * delta); // Move right
            isMoving = true;
        }

        if (isMoving) {
            // Update facing direction based on movement
            if (movingLeft) {
                facingLeft = true; // Set facing left
            } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                facingLeft = false; // Set facing right
            }
            updateAnimation(delta, movingLeft);
        } else {
            // Reset to idle stance if not moving
            currentFrame = 0; // Reset to the first idle frame
            if (facingLeft) {
                setRegion(stanceFramesLeft[currentFrame]);
            } else {
                setRegion(stanceFrames[currentFrame]);
            }
        }
    }

    // Method to draw the player using a SpriteBatch
    public void draw(SpriteBatch batch) {
        super.draw(batch); // Calls Sprite's draw method to render the texture
    }

    private void updateAnimation(float delta, boolean movingLeft) {
        // Update current frame for walking animation
        stateTime += delta;
        if (stateTime >= 0.1f) { // Update frame every 0.1 seconds
            currentFrame = (currentFrame + 1) % walkFrames.length;

            // Set the region based on direction
            if (movingLeft) {
                setRegion(walkFramesLeft[currentFrame]); // Use left-facing frames if moving left
            } else {
                setRegion(walkFrames[currentFrame]); // Use right-facing frames
            }

            stateTime = 0f;
        }
    }

    // Method to dispose resources
    public void dispose() {
        walkSheet.dispose();
        stanceSheet.dispose();
    }


    // ! TO DO:
    // ! fix stance
}
