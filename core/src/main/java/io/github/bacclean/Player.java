package io.github.bacclean;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Player extends Sprite {

    // Objects used
    private final Texture idleSheet;
    private final Texture walkSheet;
    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> walkAnimation;

    // A variable for tracking elapsed time for the animation
    private float stateTime;

    private SpriteBatch spriteBatch;


    public Player(String idleSheetPath, int columnsIdleSheet, int rowsIdleSheet, String walkSheetPath, int columnsWalkSheet, int rowsWalkSheet) {
        
        //*  Player class logic (Stance{sheet path, number of frames} | Walk{sheet path, number of frames})                
        
        // Load the texture sheets
        idleSheet = new Texture(Gdx.files.internal(idleSheetPath));
        walkSheet = new Texture(Gdx.files.internal(walkSheetPath));

        TextureRegion[] idleFrames = AnimationMaker(idleSheet, columnsIdleSheet, rowsIdleSheet);
        idleAnimation = new Animation<TextureRegion>(0.025f, idleFrames);

        TextureRegion[] walkFrames = AnimationMaker(walkSheet, columnsWalkSheet, rowsWalkSheet);
        walkAnimation = new Animation<TextureRegion>(0.025f, walkFrames);

        // Instantiate a SpriteBatch for drawing and reset the elapsed animation
		// time to 0
		spriteBatch = new SpriteBatch();
		stateTime = 0f;
    }


    // Method to handle animations
    public static TextureRegion[] AnimationMaker(Texture sheet, int columns, int rows){
        // Use the split utility method to create a 2D array of TextureRegions. This is
		// possible because this sprite sheet contains frames of equal size and they are
		// all aligned.
        TextureRegion[][] tmp = TextureRegion.split(sheet,
            sheet.getWidth() / columns,
            sheet.getHeight() / rows);
        
        // Place the regions into a 1D array in the correct order, starting from the top
		// left, going across first. The Animation constructor requires a 1D array.
        TextureRegion[] frames = new TextureRegion[columns * rows]; // asuming there is only 1 row
        int index = 0;
        for (int i = 0; i < rows; i++){
            for (int j= 0; j < columns; j++){
                frames[index++] = tmp[i][j];
            }
        }
        return frames;
    }



    // Method to move the player
    public void playerMove(float speed, float delta) {

        // Handle movement input
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            //*  jump logic here */
            
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            this.translateX(-speed * delta); // Move left

        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            this.translateX(speed * delta); // Move right
        }

    }

	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear screen
		stateTime += Gdx.graphics.getDeltaTime(); // Accumulate elapsed animation time


		spriteBatch.begin();

        
		spriteBatch.end();
	}

	public void dispose() { // SpriteBatches and Textures must always be disposed
		spriteBatch.dispose();
		walkSheet.dispose();
	}


}
