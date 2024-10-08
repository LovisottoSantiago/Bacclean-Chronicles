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
    private final Texture jumpingUpSheet;
    private final Texture jumpingDownSheet;

    // Animations for the player
    private final Animation<TextureRegion> idleAnimation;
    private final Animation<TextureRegion> leftIdleAnimation;
    private final Animation<TextureRegion> walkAnimation;
    private final Animation<TextureRegion> leftWalkAnimation;
    private final Animation<TextureRegion> attackAnimation;
    private final Animation<TextureRegion> leftAttackAnimation;
    private final Animation<TextureRegion> jumpingUpAnimation;
    private final Animation<TextureRegion> leftJumpingUpAnimation;
    private final Animation<TextureRegion> jumpingDownAnimation;
    private final Animation<TextureRegion> leftJumpingDownAnimation;

    // Animation frame durations
    private final float idleFrameDuration = 0.2f;
    private final float walkFrameDuration = 0.08f;
    private final float attackFrameDuration = 0.05f;
    private final float jumpingUpFrameDuration = 0.05f;
    private final float jumpingDownFrameDuration = 0.05f;


    // State properties
    private float stateTime;
    private final SpriteBatch spriteBatch;
    private boolean leftFlag;
    public PlayerState playerState;
    public PlayerState previousState = PlayerState.IDLE;
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

    // Jump
    public final float jumpVelocity = 400; 
    public final float gravity = 500f; // Gravity effect (how fast the player falls back down)
    public float verticalVelocity = 0f; // Current vertical speed
    
    // Enum to define player states
    public enum PlayerState {
        IDLE, RUNNING, ATTACKING, JUMPING_UP, JUMPING_DOWN
    }


    public Player(
                    String idleSheetPath, int columnsIdleSheet, int rowsIdleSheet, 
                    String walkSheetPath, int columnsWalkSheet, int rowsWalkSheet, 
                    String attackSheetPath, int columnsAttackSheet, int rowsAttackSheet,
                    String jumpingUpSheetPath, int columnsjumpingUpSheet, int rowsjumpingUpSheet,
                    String jumpingDownSheetPath, int columnsjumpingDownSheet, int rowsjumpingDownSheet
    ) {
        // Load textures
        this.idleSheet = new Texture(Gdx.files.internal(idleSheetPath));
        this.walkSheet = new Texture(Gdx.files.internal(walkSheetPath));
        this.attackSheet = new Texture(Gdx.files.internal(attackSheetPath));
        this.jumpingUpSheet = new Texture(Gdx.files.internal(jumpingUpSheetPath));
        this.jumpingDownSheet = new Texture(Gdx.files.internal(jumpingDownSheetPath));

        // Initialize animations
        idleAnimation = AnimationController.createAnimation(idleSheetPath, columnsIdleSheet, rowsIdleSheet, idleFrameDuration, false);
        leftIdleAnimation = AnimationController.createAnimation(idleSheetPath, columnsIdleSheet, rowsIdleSheet, idleFrameDuration, true);
        walkAnimation = AnimationController.createAnimation(walkSheetPath, columnsWalkSheet, rowsWalkSheet, walkFrameDuration, false);
        leftWalkAnimation = AnimationController.createAnimation(walkSheetPath, columnsWalkSheet, rowsWalkSheet, walkFrameDuration, true);
        attackAnimation = AnimationController.createAnimation(attackSheetPath, columnsAttackSheet, rowsAttackSheet, attackFrameDuration, false);
        leftAttackAnimation = AnimationController.createAnimation(attackSheetPath, columnsAttackSheet, rowsAttackSheet, attackFrameDuration, true);
        jumpingUpAnimation = AnimationController.createAnimation(jumpingUpSheetPath, columnsjumpingUpSheet, rowsjumpingUpSheet, jumpingUpFrameDuration, false);
        leftJumpingUpAnimation = AnimationController.createAnimation(jumpingUpSheetPath, columnsjumpingUpSheet, rowsjumpingUpSheet, jumpingUpFrameDuration, true);
        jumpingDownAnimation = AnimationController.createAnimation(jumpingDownSheetPath, columnsjumpingDownSheet, rowsjumpingDownSheet, jumpingDownFrameDuration, false);
        leftJumpingDownAnimation = AnimationController.createAnimation(jumpingDownSheetPath, columnsjumpingDownSheet, rowsjumpingDownSheet, jumpingDownFrameDuration, true);

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
        //logStamina(stamina);
    }

    public void regenerateStamina(float delta) {
        staminaRegenTime += delta; // Increment the time by the delta time        
        // Only regenerate if enough time has passed (e.g., 1 second)
        if (staminaRegenTime >= regenInterval) {
            stamina += regenAmount; // Regenerate stamina
            stamina = Math.min(100, stamina); // Ensure stamina doesn't exceed 100
            staminaRegenTime = 0; // Reset the timer
            //logStamina(stamina);
        }
    }


    public void playerMove(float delta) {
        // Handle attack input
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && !isJumping()) {
            performAttack();
        }
    
        // Handle state transitions
        if (playerState == PlayerState.ATTACKING) {
            stateTime += delta; // Increment stateTime while attacking
            if (stateTime >= attackAnimation.getAnimationDuration()) {
                attackBounds.setSize(0, 0); // Reset attack bounds
                playerState = PlayerState.IDLE; // Return to idle state after attack
                stateTime = 0; // Reset stateTime only after finishing the attack
            }
        } else {
            regenerateStamina(delta); // Time-based regeneration            
            sideMovement(delta);
            updateMovementBounds();
            attackBounds.setSize(0, 0); // Ensure attack bounds are not visible when not attacking
        }
        
        jumpLogic(delta);
    }
    
    
    // Helper method to update movement bounds
    private void updateMovementBounds() {
        movementBounds.setPosition(getX() + (getWidth() - movementBoundsWidth) / 2, getY());
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


    private boolean isJumping() {
        return playerState == PlayerState.JUMPING_UP || playerState == PlayerState.JUMPING_DOWN;
    }


    public void sideMovement(float delta) {
        speed = 200f;
        if ((Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && stamina > 5) && playerState != PlayerState.JUMPING_UP && playerState != PlayerState.JUMPING_DOWN) {
            speed += 200f;
            decreaseStamina(0.3f);
        }
        // Handle horizontal movement
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            this.translateX(-speed * delta);
            leftFlag = true;
            if (playerState != PlayerState.RUNNING && !isJumping()) {
                playerState = PlayerState.RUNNING;
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            this.translateX(speed * delta);
            leftFlag = false;
            if (playerState != PlayerState.RUNNING && !isJumping()) {
                playerState = PlayerState.RUNNING;
            }
        } else if (playerState != PlayerState.IDLE && !isJumping()) {
            playerState = PlayerState.IDLE;
        }
        // JUMP
        if (Gdx.input.isKeyPressed(Input.Keys.W) && playerState != PlayerState.JUMPING_UP && playerState != PlayerState.JUMPING_DOWN) {
            playerState = PlayerState.JUMPING_UP;
            verticalVelocity = jumpVelocity;
        }
    }


    public void jumpLogic(float delta) {
        if (playerState == PlayerState.JUMPING_UP || playerState == PlayerState.JUMPING_DOWN) {
            verticalVelocity -= gravity * delta; // Apply gravity
            this.translateY(verticalVelocity * delta);
            if (verticalVelocity > 0) {
                playerState = PlayerState.JUMPING_UP; // Ascending
            } else {
                playerState = PlayerState.JUMPING_DOWN; // Descending
            }
        }        
        if (playerState == PlayerState.JUMPING_DOWN && verticalVelocity == 0) {
            playerState = PlayerState.IDLE; // Reset to IDLE if not falling anymore
        }
        
    }
    
    

    private Animation<TextureRegion> getCurrentAnimation() {
        switch (playerState) {
            case ATTACKING:
                return leftFlag ? leftAttackAnimation : attackAnimation;
            case RUNNING:
                return leftFlag ? leftWalkAnimation : walkAnimation;
            case JUMPING_UP:
                return leftFlag ? leftJumpingUpAnimation : jumpingUpAnimation;
            case JUMPING_DOWN:
                return leftFlag ? leftJumpingDownAnimation : jumpingDownAnimation;
            default:
                return leftFlag ? leftIdleAnimation : idleAnimation;
        }
    }

    
    public TextureRegion getCurrentFrame() {
        if (playerState != previousState) {
            stateTime = 0;
            previousState = playerState;
        }
    
        stateTime += Gdx.graphics.getDeltaTime();
        return getCurrentAnimation().getKeyFrame(stateTime, true);
    }
        
    
    public void dispose() {
        idleSheet.dispose();
        walkSheet.dispose();
        attackSheet.dispose();
        jumpingUpSheet.dispose();
        jumpingDownSheet.dispose();
        staminaBarRenderer.dispose();
        spriteBatch.dispose();
    }
    


}