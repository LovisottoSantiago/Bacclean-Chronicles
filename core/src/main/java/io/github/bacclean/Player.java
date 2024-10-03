package io.github.bacclean;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Player extends Sprite {

    // Objetos y variables
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

    // Tiempo de animación
    private float stateTime;
    private final SpriteBatch spriteBatch;
    private boolean leftFlag;

    // Stamina
    private float stamina = 100;
    private float staminaRegenTime = 0f;
    private final float regenInterval = 1f;
    private final float regenAmount = 10f;
    private final float staminaUsed = 20;
    private final ShapeRenderer shapeRenderer;

    public PlayerState playerState;

    public Player(String idleSheetPath, int columnsIdleSheet, int rowsIdleSheet, String walkSheetPath, int columnsWalkSheet, int rowsWalkSheet, String attackSheetPath, int columnsAttackSheet, int rowsAttackSheet) {
        this.idleSheet = new Texture(Gdx.files.internal(idleSheetPath));
        this.walkSheet = new Texture(Gdx.files.internal(walkSheetPath));
        this.attackSheet = new Texture(Gdx.files.internal(attackSheetPath));
        this.playerState = PlayerState.IDLE;
        shapeRenderer = new ShapeRenderer();

        // Usar AnimationController para crear las animaciones
        idleAnimation = AnimationController.createAnimation(idleSheetPath, columnsIdleSheet, rowsIdleSheet, idleFrameDuration, false);
        leftIdleAnimation = AnimationController.createAnimation(idleSheetPath, columnsIdleSheet, rowsIdleSheet, idleFrameDuration, true);

        walkAnimation = AnimationController.createAnimation(walkSheetPath, columnsWalkSheet, rowsWalkSheet, walkFrameDuration, false);
        leftWalkAnimation = AnimationController.createAnimation(walkSheetPath, columnsWalkSheet, rowsWalkSheet, walkFrameDuration, true);

        attackAnimation = AnimationController.createAnimation(attackSheetPath, columnsAttackSheet, rowsAttackSheet, attackFrameDuration, false);
        leftAttackAnimation = AnimationController.createAnimation(attackSheetPath, columnsAttackSheet, rowsAttackSheet, attackFrameDuration, true);

        spriteBatch = new SpriteBatch();
        stateTime = 0f;
    }


    public enum PlayerState{
        IDLE, RUNNING, ATTACKING;
    }

    public void renderStaminaBar() {
        // Set up bar dimensions
        float maxBarWidth = 100; // Maximum width of the stamina bar
        float barHeight = 6; // Height of the stamina bar
        float barX = 460;
        float barY = 300; // Position the bar slightly above the player's head

    
        // Calculate the current width of the stamina bar based on the player's stamina
        float currentBarWidth = (stamina / 100) * maxBarWidth;
    
        // Begin drawing the shapes
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        // Draw the background of the stamina bar (empty portion)
        shapeRenderer.setColor(0.522f, 0.502f, 0.259f, 1); // #858042 for the empty bar
        shapeRenderer.rect(barX, barY, maxBarWidth, barHeight);
        
        // Draw the current stamina (filled portion)
        shapeRenderer.setColor(0.890f, 0.851f, 0.302f, 1); // #e3d94d for the filled bar
        shapeRenderer.rect(barX, barY, currentBarWidth, barHeight);
    
        shapeRenderer.end();
    }
    
    

    // Method to decrease stamina
    public void useStamina(float value) {
        stamina -= value;
        stamina = Math.max(0, stamina); // Ensure stamina doesn't go below 0
        Gdx.app.log("Stamina", "Stamina decreased: " + stamina);
    }

    // Method to regenerate stamina over time
    public void regenerateStamina(float delta) {
        staminaRegenTime += delta; // Increment the time by the delta time
        
        // Only regenerate if enough time has passed (e.g., 1 second)
        if (staminaRegenTime >= regenInterval) {
            stamina += regenAmount; // Regenerate stamina
            stamina = Math.min(100, stamina); // Ensure stamina doesn't exceed 100
            staminaRegenTime = 0; // Reset the timer
            Gdx.app.log("Stamina", "Stamina regenerated: " + stamina);
        }
    }


    public void playerMove(float speed, float delta) {
        
        //! Handle attack input
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (stamina > staminaUsed) {
                playerState = PlayerState.ATTACKING;
                stateTime = 0; 
                useStamina(staminaUsed);
            } else {
                Gdx.app.log("Stamina", "Not enough stamina to perform attacks.");
                return; // Exit early if not enough stamina
            }
        }

        if (playerState == PlayerState.ATTACKING) {
            if (stateTime >= attackAnimation.getAnimationDuration()) {
                playerState = PlayerState.IDLE;
            }
        } 
        
        else {
            regenerateStamina(delta); // Pass delta to ensure time-based regeneration
            
            //! Handle movement input
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                this.translateX(-speed * delta); // Move left
                leftFlag = true;
                playerState = PlayerState.RUNNING;
            } 
            else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                this.translateX(speed * delta); // Move right
                leftFlag = false;
                playerState = PlayerState.RUNNING;
            } 
            else {
                // Set to idle and regenerate stamina slowly
                playerState = PlayerState.IDLE;
            }
        }

    }
    
    

    public TextureRegion getCurrentFrame() {
        stateTime += Gdx.graphics.getDeltaTime();
        
        switch (playerState) {
            case ATTACKING:
            // Only return the attack animation frame; stamina check is handled in playerMove()
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
