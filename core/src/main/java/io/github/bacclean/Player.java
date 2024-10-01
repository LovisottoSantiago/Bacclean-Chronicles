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
    private final Texture attackSheet;
    private final Animation<TextureRegion> idleAnimation;
    private final Animation<TextureRegion> leftIdleAnimation;
    private final Animation<TextureRegion> walkAnimation;
    private final Animation<TextureRegion> leftWalkAnimation;
    private final Animation<TextureRegion> attackAnimation;
    private final Animation<TextureRegion> leftAttackAnimation;
    private final float idleFrameDuration = 0.2f;
    private final float walkFrameDuration = 0.05f;
    private final float attackFrameDuration = 0.02f;
    

    // A variable for tracking elapsed time for the animation
    private float stateTime;
    private final SpriteBatch spriteBatch;
    private boolean leftFlag;
    
    public PlayerState playerState;

    
    public Player(String idleSheetPath, int columnsIdleSheet, int rowsIdleSheet, String walkSheetPath, int columnsWalkSheet, int rowsWalkSheet, String attackSheetPath, int columnsAttackSheet, int rowsAttackSheet) {
        this.idleSheet = new Texture(Gdx.files.internal(idleSheetPath));
        this.walkSheet = new Texture(Gdx.files.internal(walkSheetPath));
        this.attackSheet = new Texture(Gdx.files.internal(attackSheetPath));
        this.playerState = PlayerState.IDLE;
        
        idleAnimation = createAnimation(idleSheetPath, columnsIdleSheet, rowsIdleSheet, idleFrameDuration, false);
        leftIdleAnimation = createAnimation(idleSheetPath, columnsIdleSheet, rowsIdleSheet, idleFrameDuration, true);

        walkAnimation = createAnimation(walkSheetPath, columnsWalkSheet, rowsWalkSheet, walkFrameDuration, false);
        leftWalkAnimation = createAnimation(walkSheetPath, columnsWalkSheet, rowsWalkSheet, walkFrameDuration, true);

        attackAnimation = createAnimation(attackSheetPath, columnsAttackSheet, rowsAttackSheet, attackFrameDuration, false);
        leftAttackAnimation = createAnimation(attackSheetPath, columnsAttackSheet, rowsAttackSheet, attackFrameDuration, true);



        //* Instantiate a SpriteBatch for drawing and reset the elapsed animation
		// time to 0
		spriteBatch = new SpriteBatch();
		stateTime = 0f;
    }


    public enum PlayerState{
        IDLE, RUNNING, ATTACKING;
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


    private Animation<TextureRegion> createAnimation(String sheetPath, int columns, int rows, float frameDuration, boolean flip) {
        Texture sheet = new Texture(Gdx.files.internal(sheetPath));
        if (flip) {
            TextureRegion[] frames = AnimationMaker(sheet, columns, rows);
            for (int i = 0; i < frames.length; i++) {
                frames[i].flip(true, false); // Flip horizontally
            }
            return new Animation<>(frameDuration, frames);
        }
        return new Animation<>(frameDuration, AnimationMaker(sheet, columns, rows));
    }



    // Method to move the player
    public void playerMove(float speed, float delta) {
        // Handle attack input
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            // Set the attacking state and reset the state time
            playerState = PlayerState.ATTACKING;
            stateTime = 0; // Reset state time for attack animation
        } 
        else {
            // Handle movement input
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                this.translateX(-speed * delta); // Move left
                leftFlag = true;
                playerState = PlayerState.RUNNING;
            } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                this.translateX(speed * delta); // Move right
                leftFlag = false;
                playerState = PlayerState.RUNNING;
            } else {
                // If no movement and not attacking, set to idle
                if (playerState != PlayerState.ATTACKING) {
                    playerState = PlayerState.IDLE;
                } else {
                    // Check if attack animation is finished
                    if (stateTime >= attackAnimation.getAnimationDuration()) {
                        playerState = PlayerState.IDLE; // Transition to IDLE after attack
                    }
                }
            }
        }

    }
    

    public TextureRegion getCurrentFrame() {
        stateTime += Gdx.graphics.getDeltaTime();
    
        switch (playerState) {
            case ATTACKING:
                // Play attack animation without looping
                if (leftFlag) {
                    return leftAttackAnimation.getKeyFrame(stateTime, false); // Don't loop for attack
                } else {
                    return attackAnimation.getKeyFrame(stateTime, false); // Don't loop for attack
                }
            case RUNNING:
                if (leftFlag) {
                    return leftWalkAnimation.getKeyFrame(stateTime, true); // Loop for left walk animation
                } else {
                    return walkAnimation.getKeyFrame(stateTime, true); // Loop for right walk animation
                }
            case IDLE:
            default:
                if (leftFlag) {
                    return leftIdleAnimation.getKeyFrame(stateTime, true); // Loop for left idle animation
                } else {
                    return idleAnimation.getKeyFrame(stateTime, true); // Loop for right idle animation
                }
        }
    }
    

    

    

	public void dispose() { // SpriteBatches and Textures must always be disposed
		spriteBatch.dispose();
        idleSheet.dispose();
		walkSheet.dispose();
        attackSheet.dispose();
	}


}
