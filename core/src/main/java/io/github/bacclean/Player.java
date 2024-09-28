package io.github.bacclean;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Player extends Sprite { 
    Texture walkSheet;
    TextureRegion[] walkFrames;
    
    Texture stanceSheet;
    TextureRegion[] stanceFrames;

    // Variables to manage animation states
    private float stateTime;
    private int currentFrame;

    public Player(String walkSheetPath, String stanceSheetPath) {
        // Load the texture sheets
        walkSheet = new Texture(Gdx.files.internal(walkSheetPath));
        stanceSheet = new Texture(Gdx.files.internal(stanceSheetPath));

        // Split the walk sheet into frames
        TextureRegion[][] tmpWalk = TextureRegion.split(walkSheet, walkSheet.getWidth() / 3, walkSheet.getHeight());
        walkFrames = new TextureRegion[3]; // Assuming 3 frames
        for (int i = 0; i < 3; i++) {
            walkFrames[i] = tmpWalk[0][i];  // Assuming 1 row of animation frames
        }

        // Split the stance sheet into frames
        TextureRegion[][] tmpStance = TextureRegion.split(stanceSheet, stanceSheet.getWidth() / 3, stanceSheet.getHeight());
        stanceFrames = new TextureRegion[3]; // Assuming 3 frames for stance
        for (int i = 0; i < 3; i++) {
            stanceFrames[i] = tmpStance[0][i];
        }

        // Set the player's initial texture (e.g., standing position)
        setRegion(stanceFrames[0]);
        
        // Initialize animation variables
        stateTime = 0f;
        currentFrame = 0;
    }



    // Method to move the player
    public void playerMove(float speed, float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            this.translateY(speed * delta); // Moves player vertically
            // Update current frame for animation
            stateTime += delta;
            if (stateTime >= 0.1f) { // Update frame every 0.1 seconds
                currentFrame = (currentFrame + 1) % walkFrames.length;
                setRegion(walkFrames[currentFrame]);
                stateTime = 0f;
            }
        } 
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            this.translateX(-speed * delta); // Moves player horizontally
            // Update current frame for animation
            stateTime += delta;
            if (stateTime >= 0.1f) { // Update frame every 0.1 seconds
                currentFrame = (currentFrame + 1) % walkFrames.length;
                setRegion(walkFrames[currentFrame]);
                stateTime = 0f;
            }
        } 
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            this.translateY(-speed * delta); // Moves player vertically
            // Update current frame for animation
            stateTime += delta;
            if (stateTime >= 0.1f) { // Update frame every 0.1 seconds
                currentFrame = (currentFrame + 1) % walkFrames.length;
                setRegion(walkFrames[currentFrame]);
                stateTime = 0f;
            }
        } 
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            this.translateX(speed * delta); // Moves player horizontally
            // Update current frame for animation
            stateTime += delta;
            if (stateTime >= 0.1f) { // Update frame every 0.1 seconds
                currentFrame = (currentFrame + 1) % walkFrames.length;
                setRegion(walkFrames[currentFrame]);
                stateTime = 0f;
            }
        } 
        else {
            // If no key is pressed, switch back to stance frames
            setRegion(stanceFrames[0]); // Default stance
        }
    }




    // Method to draw the player using a SpriteBatch
    public void draw(SpriteBatch batch) {
        super.draw(batch);  // This calls Sprite's draw method to render the texture
    }


    public void update(float delta) {
        playerMove(100, delta); // Example speed
        // Update animation frames if needed
    }


}
