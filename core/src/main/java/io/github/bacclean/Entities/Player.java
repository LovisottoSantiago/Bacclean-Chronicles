package io.github.bacclean.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import io.github.bacclean.Controllers.AnimationController;
import io.github.bacclean.Controllers.GuiController;
import io.github.bacclean.Controllers.SoundController;
import io.github.bacclean.Entities.Player.PlayerState;
import io.github.bacclean.Entities.Skeleton.EnemyState;




@SuppressWarnings("unused")
public class Player extends Sprite {
    private final GuiController gui;

    // Textures for different player states
    private final Texture attackSheet;
    private final Texture idleSheet;
    private final Texture jumpingDownSheet;
    private final Texture jumpingUpSheet;
    private final Texture walkSheet;
    
    // Animations for the player
    private final Animation<TextureRegion> attackAnimation;
    private final Animation<TextureRegion> idleAnimation;
    private final Animation<TextureRegion> jumpingDownAnimation;
    private final Animation<TextureRegion> jumpingUpAnimation;
    private final Animation<TextureRegion> leftAttackAnimation;
    private final Animation<TextureRegion> leftIdleAnimation;
    private final Animation<TextureRegion> leftJumpingDownAnimation;
    private final Animation<TextureRegion> leftJumpingUpAnimation;
    private final Animation<TextureRegion> leftWalkAnimation;
    private final Animation<TextureRegion> walkAnimation;

    // Animation frame durations
    private final float attackFrameDuration = 0.08f;
    private final float idleFrameDuration = 0.2f;
    private final float jumpingDownFrameDuration = 0.05f;
    private final float jumpingUpFrameDuration = 0.05f;
    private final float walkFrameDuration = 0.1f;


    // State properties
    private boolean leftFlag;
    private final SpriteBatch spriteBatch;
    public float speed;
    public float stateTime;
    public PlayerState playerState;
    public PlayerState previousState = PlayerState.IDLE;

    // Render bar
    float maxBarWidth = 80; 
    float barHeight = 3; 
    float borderThickness = 1;
    float barX = 640 - (maxBarWidth / 2);
    
    // Stamina properties
    private float stamina;
    private final float maxStamina = 100;
    private float staminaRegenTime = 0f;
    private final float regenInterval = 1f;
    private final float regenAmount = 10f;
    private final float staminaCost = 20;

    // Life properties
    private float life;
    public float maxLife = 100;    

    
    // Collision properties
    public int attackBoundsHeight= 45;
    public int attackBoundsWidth = 90;
    public int playerBoundsHeight = 45;
    public int playerBoundsWidth = 22;
    public Rectangle attackBounds; 
    public Rectangle playerBounds;


    // Jump
    public final float jumpVelocity = 150f; 
    public final float gravity = -98f; // Gravity effect (how fast the player falls back down)
    public float verticalVelocity = 0; // Current vertical speed
    public float groundValue;
    public boolean isFloating = true;
    
    // Attack 
    SoundController playerSounds;
    SoundController enemySounds;
    Sound attackSound;
    Sound enemyHurtSound;
    public float playerPower = 20;
    
    // Enemy
    public boolean enemyDamaged = false; // Flag to track if the enemy is already damaged
    

    // Enum to define player states
    public enum PlayerState {
        IDLE, RUNNING, ATTACKING, JUMPING, FALLING
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
        gui = new GuiController();
        stamina = maxStamina;
        this.spriteBatch = new SpriteBatch();
        this.stateTime = 0f;
        this.playerState = PlayerState.IDLE;

        // Initialize collision bounds
        playerBounds = new Rectangle(getX() + (getWidth() - playerBoundsWidth) / 2, getY() + (getHeight() - playerBoundsHeight) / 2, playerBoundsWidth, playerBoundsHeight);
        attackBounds = new Rectangle(getX() + (getWidth() - attackBoundsWidth) / 2, getY() + (getHeight() - attackBoundsHeight) / 2, attackBoundsWidth, attackBoundsHeight);        

        // Attack sounds
        playerSounds = new SoundController();
        enemySounds = new SoundController();
    }


    //? Stamina methods
    public void renderStaminaBar() {
        float barY = this.getHeight() + 10;
        gui.renderBar(barX, barY, stamina, maxStamina, maxBarWidth, barHeight, "yellow", "darkYellow", "gray");
    }

    public void renderLifeBar() {
        float barY = this.getHeight() + 20;
        gui.renderBar(barX, barY, life, maxLife, maxBarWidth, barHeight, "red", "darkRed", "gray");
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

    public void increaseLife(int life){
        maxLife += life;
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
        gravityLogic(delta);
    }
    
}

    
    // Helper method to update movement bounds
    private void updateMovementBounds() {
        playerBounds.setPosition(getX() + (getWidth() - playerBoundsWidth) / 2, getY());
    }
    

    // Attack movement and logic (no dmg)
    public void performAttack() {
        if (stamina > staminaCost) {
            playerState = PlayerState.ATTACKING;
            stateTime = 0; 
            decreaseStamina(staminaCost);
            // Set the attack bounds based on the player direction
            attackBounds.setPosition(getX() + (getWidth() - attackBoundsWidth) / 2 + (leftFlag ? -35 : 35), getY());
            attackBounds.setSize(attackBoundsWidth, attackBoundsHeight); // Set the size of the attack bounds
            
            attackSound = playerSounds.getAttackSound();
            if (attackSound != null) {
                attackSound.play();
                attackSound.setVolume(2, 1f);
            }

        } else {
            Gdx.app.log("Stamina", "Not enough stamina to perform attacks.");
        }
    }


    public void damageEnemy(Rectangle enemyBound) {
        if (attackBounds.overlaps(enemyBound) && !enemyDamaged) {
            Gdx.app.log("Attack alert: ", "enemy has been damaged by player.");
            enemyHurtSound = enemySounds.getEnemyHurtSound();
            if (enemyHurtSound != null) {
                long soundId = enemyHurtSound.play();
                enemyHurtSound.setVolume(soundId, 0.8f);                 
            }

            Skeleton.enemyState = EnemyState.HIT;
            enemyDamaged = true; // Mark as damaged

        }

        // Reset the flag if there's no overlap, allowing damage again in the future
        if (!attackBounds.overlaps(enemyBound)) {
            enemyDamaged = false;
        }
    }


    public boolean isJumping() {
        return playerState == PlayerState.JUMPING || playerState == PlayerState.FALLING;
    }


    public void sideMovement(float delta) {
        speed = 200f;
        if ((Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && stamina > 5) && playerState != PlayerState.JUMPING && playerState != PlayerState.FALLING) {
            speed += 200f;
            decreaseStamina(0.3f);
        }
    
        if (getY() < groundValue) {
            playerState = PlayerState.IDLE;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            this.translateX(-speed * delta);
            leftFlag = true;
            if (!isJumping()) {
                playerState = PlayerState.RUNNING;
            }                
        } 
        else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            this.translateX(speed * delta);
            leftFlag = false;
            if (playerState != PlayerState.RUNNING && !isJumping()) {
                playerState = PlayerState.RUNNING;
            }
        }
        else if (!isJumping()) { //! bug solution
            playerState = PlayerState.IDLE;
        }
        
        // JUMP
        if (Gdx.input.isKeyPressed(Input.Keys.W) && (playerState == PlayerState.IDLE || playerState == PlayerState.RUNNING)) {
            playerState = PlayerState.JUMPING;
            jump();            
        }
    
        if (playerState == PlayerState.JUMPING || playerState == PlayerState.FALLING) {
            gravityLogic(delta);
        }
    }

    public void jump() {
        verticalVelocity = jumpVelocity; // Apply initial jump velocity
    }

    public void gravityLogic(float delta) {        
        verticalVelocity += gravity * delta; // Increase downward speed
        setPosition(getX(), getY() + verticalVelocity * delta); // Update the player position
        //Gdx.app.log("alert", "a: " + verticalVelocity);

        // Assuming getY() <= ground level is a condition for landing
        if (getY() <= groundValue && playerState != PlayerState.RUNNING && !isFloating) {
            verticalVelocity = 0;
            setPosition(getX(), groundValue);
            playerState = PlayerState.IDLE;
        }          
        if (verticalVelocity < -10 && playerState == PlayerState.RUNNING){ //! bug solution
            playerState = PlayerState.FALLING;
        }
        else if (verticalVelocity < 0 && playerState == PlayerState.JUMPING){ //! bug solution
            playerState = PlayerState.FALLING;
        }
        
    }


    public void checkGroundCollision(java.util.List<com.badlogic.gdx.math.Rectangle> groundTileRectangles) {        
        for (Rectangle tile : groundTileRectangles) {
            isFloating = false;
            if (playerBounds.overlaps(tile)) {
                    if (playerBounds.y > tile.getY() && playerBounds.y <= tile.getY() + tile.getHeight() && verticalVelocity < 0) {
                    setPosition(getX(), tile.getY() + tile.getHeight());
                    groundValue = playerBounds.y;
                    verticalVelocity = 0;
                    return;
                }
                // Hitting the bottom of the tile
                else if (playerBounds.y + playerBounds.height >= tile.getY() && playerBounds.y + playerBounds.height <= tile.getY() + 10 && verticalVelocity > 0) {
                    setPosition(getX(), tile.getY() - playerBounds.height);
                    verticalVelocity = -1; // Apply a small downward force
                    playerState = PlayerState.FALLING;
                    System.out.println("Player hit the bottom of the tile. New Y Position: " + getY());
                    return;
                }
            } groundValue = 64;
        }       
            int leftLimit = 1000;
            if (playerBounds.x < leftLimit) {
                setPosition(leftLimit, getY());
                playerState = PlayerState.IDLE;
            }
            if (!isFloating && playerState != PlayerState.ATTACKING) {
                gravityLogic(Gdx.graphics.getDeltaTime());
            }
    }    
    

    private Animation<TextureRegion> getCurrentAnimation() {
        switch (playerState) {
            case ATTACKING:
                return leftFlag ? leftAttackAnimation : attackAnimation;
            case RUNNING:
                return leftFlag ? leftWalkAnimation : walkAnimation;
            case JUMPING:
                return leftFlag ? leftJumpingUpAnimation : jumpingUpAnimation;
            case FALLING:
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
        spriteBatch.dispose();
        attackSound.dispose();
        gui.dispose();
    }
    


}