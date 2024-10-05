package io.github.bacclean;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class Player extends Sprite {

    // Textures for different player states
    private final Texture idleSheet;
    private final Texture walkSheet;
    private final Texture attackSheet;

    // Animations for the player
    private final Animation<TextureRegion> idleAnimation;
    private final Animation<TextureRegion> leftIdleAnimation;
    private final Animation<TextureRegion> walkAnimation;
    private final Animation<TextureRegion> leftWalkAnimation;
    private final Animation<TextureRegion> attackAnimation;
    private final Animation<TextureRegion> leftAttackAnimation;

    // Animation frame durations
    private final float idleFrameDuration = 0.2f;
    private final float walkFrameDuration = 0.08f;
    private final float attackFrameDuration = 0.05f;


    // State properties
    private float stateTime;
    private final SpriteBatch spriteBatch;
    private boolean leftFlag;
    public PlayerState playerState;
    public float speed;

    // Stamina properties
    private float stamina = 100;
    private float staminaRegenTime = 0f;
    private final float regenInterval = 1f;
    private final float regenAmount = 10f;
    private final float staminaCost = 20;

    // Shape renderer for stamina bar
    private final ShapeRenderer staminaBarRenderer;
    
    // Collision properties
    public Rectangle movementBounds;
    public Rectangle attackBounds; 
    public int movementBoundsWidth = 42;
    public int movementBoundsHeight = 42;
    public int attackBoundsWidth = 60;
    public int attackBoundsHeight= 42;
    
    // Enum to define player states
    public enum PlayerState {
        IDLE, RUNNING, ATTACKING
    }


    public Player(
                    String idleSheetPath, int columnsIdleSheet, int rowsIdleSheet, 
                    String walkSheetPath, int columnsWalkSheet, int rowsWalkSheet, 
                    String attackSheetPath, int columnsAttackSheet, int rowsAttackSheet
    ) {
        // Load textures
        this.idleSheet = new Texture(Gdx.files.internal(idleSheetPath));
        this.walkSheet = new Texture(Gdx.files.internal(walkSheetPath));
        this.attackSheet = new Texture(Gdx.files.internal(attackSheetPath));

        // Initialize animations
        idleAnimation = AnimationController.createAnimation(idleSheetPath, columnsIdleSheet, rowsIdleSheet, idleFrameDuration, false);
        leftIdleAnimation = AnimationController.createAnimation(idleSheetPath, columnsIdleSheet, rowsIdleSheet, idleFrameDuration, true);
        walkAnimation = AnimationController.createAnimation(walkSheetPath, columnsWalkSheet, rowsWalkSheet, walkFrameDuration, false);
        leftWalkAnimation = AnimationController.createAnimation(walkSheetPath, columnsWalkSheet, rowsWalkSheet, walkFrameDuration, true);
        attackAnimation = AnimationController.createAnimation(attackSheetPath, columnsAttackSheet, rowsAttackSheet, attackFrameDuration, false);
        leftAttackAnimation = AnimationController.createAnimation(attackSheetPath, columnsAttackSheet, rowsAttackSheet, attackFrameDuration, true);

        // Initialize shape renderer and sprite batch
        staminaBarRenderer = new ShapeRenderer();
        this.spriteBatch = new SpriteBatch();
        this.stateTime = 0f;
        this.playerState = PlayerState.IDLE;

        // Initialize collision bounds
        movementBounds = new Rectangle(getX() + (getWidth() - movementBoundsWidth) / 2, getY() + (getHeight() - movementBoundsHeight) / 2, movementBoundsWidth, movementBoundsHeight);
        attackBounds = new Rectangle(getX() + (getWidth() - attackBoundsWidth) / 2, getY() + (getHeight() - attackBoundsHeight) / 2, attackBoundsWidth, attackBoundsHeight);        
    }


    //? Stamina methods
    public void renderStaminaBar() {
        // Set up bar dimensions
        float maxBarWidth = 100; // Maximum width of the stamina bar
        float barHeight = 6; // Height of the stamina bar
        float barX = 20;
        float barY = 520; // Position the bar slightly above the player's head
    
        // Calculate the current width of the stamina bar based on the player's stamina
        float currentBarWidth = (stamina / 100) * maxBarWidth;
    
        // Begin drawing the shapes
        staminaBarRenderer.begin(ShapeRenderer.ShapeType.Filled);        
        // Draw the background of the stamina bar (empty portion)
        staminaBarRenderer.setColor(0.522f, 0.502f, 0.259f, 1); // #858042 for the empty bar
        staminaBarRenderer.rect(barX, barY, maxBarWidth, barHeight);        
        // Draw the current stamina (filled portion)
        staminaBarRenderer.setColor(0.890f, 0.851f, 0.302f, 1); // #e3d94d for the filled bar
        staminaBarRenderer.rect(barX, barY, currentBarWidth, barHeight);    
        staminaBarRenderer.end();
    }

    public void logStamina(float value){
        Gdx.app.log("Stamina", "Stamina: " + value);
    }

    public void decreaseStamina(float value) {
        stamina -= value;
        stamina = Math.max(0, stamina); // Ensure stamina doesn't go below 0
        logStamina(stamina);
    }

    public void regenerateStamina(float delta) {
        staminaRegenTime += delta; // Increment the time by the delta time        
        // Only regenerate if enough time has passed (e.g., 1 second)
        if (staminaRegenTime >= regenInterval) {
            stamina += regenAmount; // Regenerate stamina
            stamina = Math.min(100, stamina); // Ensure stamina doesn't exceed 100
            staminaRegenTime = 0; // Reset the timer
            logStamina(stamina);
        }
    }


    //! Input and movement
    public void playerMove(float delta) {        
        // Handle attack input
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            performAttack();
        }

        if (playerState == PlayerState.ATTACKING) {
            if (stateTime >= attackAnimation.getAnimationDuration()) {
                attackBounds.setSize(0, 0);
                stateTime = 0; // Reset stateTime after attack completes
                playerState = PlayerState.IDLE; // Return to idle state
            }
        } else {
            regenerateStamina(delta); // Time-based regeneration            
            sideMovement(delta);
            movementBounds.setPosition(getX() + (getWidth() - movementBoundsWidth) / 2, getY()); // Update bounds position
            attackBounds.setPosition(1500, 1500); //avoid attack bound appears on screen
        }
    }

    // Attack movement and logic
    public void performAttack() {
        if (stamina > staminaCost) {
            playerState = PlayerState.ATTACKING;
            stateTime = 0; 
            decreaseStamina(staminaCost);
            // Set the attack bounds based on the player direction
            attackBounds.setPosition(getX() + (getWidth() - attackBoundsWidth) / 2 + (leftFlag ? -10 : 10), getY());
            attackBounds.setSize(attackBoundsWidth, attackBoundsHeight); // Set the size of the attack bounds
        } else {
            Gdx.app.log("Stamina", "Not enough stamina to perform attacks.");
        }
    }

    // WASD movement and logic
    public void sideMovement(float delta) {
        speed = 200f;
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && stamina > 5){
            speed += 200f;
            decreaseStamina(0.3f);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            this.translateX(-speed * delta); // Move left
            leftFlag = true;
            playerState = PlayerState.RUNNING;
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            this.translateX(speed * delta); // Move right
            leftFlag = false;
            playerState = PlayerState.RUNNING;
        } else {
            // Set to idle and regenerate stamina slowly
            playerState = PlayerState.IDLE;
        }
    }


    public TextureRegion getCurrentFrame() {
        stateTime += Gdx.graphics.getDeltaTime();        
        switch (playerState) {
            case ATTACKING:
                if (leftFlag) {
                    return leftAttackAnimation.getKeyFrame(stateTime, false); // Don't loop for attack
                } 
                else {
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
