package io.github.bacclean;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Player extends Sprite {

    // Objects used
    private final Texture idleSheet;
    private final Texture walkSheet;
    private final Animation<TextureRegion> idleAnimation;
    private final Animation<TextureRegion> walkAnimation;

    // A variable for tracking elapsed time for the animation
    private float stateTime;
    private final SpriteBatch spriteBatch;

    public Player(String idleSheetPath, int columnsIdleSheet, int rowsIdleSheet, String walkSheetPath, int columnsWalkSheet, int rowsWalkSheet) {
        
        //! IDLE ANIMATION -
        idleSheet = new Texture(Gdx.files.internal(idleSheetPath));
        if (idleSheet == null) {
            Gdx.app.log("Player", "Idle sheet not loaded!");
        } else {
            Gdx.app.log("Player", "Idle sheet loaded successfully: " + idleSheetPath);
        }
        TextureRegion[] idleFrames = AnimationMaker(idleSheet, columnsIdleSheet, rowsIdleSheet);
        Gdx.app.log("Player", "Idle frames count: " + idleFrames.length);
        idleAnimation = new Animation<>(0.025f, idleFrames);

    
        //! WALK ANIMATION -
        walkSheet = new Texture(Gdx.files.internal(walkSheetPath));
        if (walkSheet == null) {
            Gdx.app.log("Player", "Walk sheet not loaded!");
        } else {
            Gdx.app.log("Player", "Walk sheet loaded successfully: " + walkSheetPath);
        }
        TextureRegion[] walkFrames = AnimationMaker(walkSheet, columnsWalkSheet, rowsWalkSheet);
        Gdx.app.log("Player", "Walk frames count: " + walkFrames.length);
        walkAnimation = new Animation<>(0.025f, walkFrames);


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


    public TextureRegion getCurrentFrame() {
        stateTime += Gdx.graphics.getDeltaTime();
        return idleAnimation.getKeyFrame(stateTime, true);
    }

    

	public void dispose() { // SpriteBatches and Textures must always be disposed
		spriteBatch.dispose();
        idleSheet.dispose();
		walkSheet.dispose();
	}


}
