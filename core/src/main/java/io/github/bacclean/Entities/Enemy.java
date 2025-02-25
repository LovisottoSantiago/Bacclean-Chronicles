package io.github.bacclean.Entities;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import io.github.bacclean.Controllers.AnimationController;
import io.github.bacclean.Controllers.GuiController;
import io.github.bacclean.Entities.Enemy.EnemyState;

@SuppressWarnings("unused")
public class Enemy extends Sprite{
    private float stateTime;
    private final SpriteBatch spriteBatch;
    private final GuiController gui;

    private boolean hitDisplacementApplied = false;
    public boolean isDamaged = false;
    public float damageCooldown = 0f;
    private float enemyLife;
    private final float maxEnemyLife = 100;

    private final Texture idleSheet;
    private final Texture walkSheet;
    private final Texture hitSheet;
    private final Texture deathSheet;

    private final Animation<TextureRegion> idleAnimation;
    private final Animation<TextureRegion> leftIdleAnimation;
    private final Animation<TextureRegion> walkAnimation;
    private final Animation<TextureRegion> leftWalkAnimation;
    private final Animation<TextureRegion> hitAnimation;
    private final Animation<TextureRegion> leftHitAnimation;
    private final Animation<TextureRegion> deathAnimation;
    private final Animation<TextureRegion> leftDeathAnimation;

    private final float idleFrameDuration = 0.2f;
    private final float walkFrameDuration = 0.08f;
    private final float hitFrameDuration = 0.08f;
    private final float deathFrameDuration = 0.2f;

    // Collisions
    public Rectangle enemyBounds;
    public int enemyBoundsWidth = 22;
    public int enemyBoundsHeight = 54;
    public Rectangle enemyVision;
    public int enemyVisionWidth = 300;
    public int enemyVisionHeight = enemyBoundsHeight;

    
    // Gravity fields
    public final float jumpVelocity = 150f; 
    public final float gravity = -98f;  
    public float verticalVelocity = 0; 
    public float groundValue;
    public boolean isFloating = true;
    public float speed;


    public boolean leftFlag;

    public EnemyState enemyState;
    public EnemyState previousState = EnemyState.IDLE;

    public enum EnemyState {
        IDLE, HIT, WALKING, DEATH
    }

    public Enemy (
        String idleSheetPath, int columnsIdleSheet, int rowsIdleSheet,
        String walkSheetPath, int columnsWalkSheet, int rowsWalkSheet,
        String hitSheetPath, int columnsHitSheet, int rowsHitSheet,
        String deathSheetPath, int columnsDeathSheet, int rowsDeathSheet
    ) {
        this.idleSheet = new Texture(Gdx.files.internal(idleSheetPath));
        this.walkSheet = new Texture(Gdx.files.internal(walkSheetPath));
        this.hitSheet = new Texture(Gdx.files.internal(hitSheetPath));
        this.deathSheet = new Texture(Gdx.files.internal(deathSheetPath));

        idleAnimation = AnimationController.createAnimation(idleSheetPath, columnsIdleSheet, rowsIdleSheet, idleFrameDuration, false);
        leftIdleAnimation = AnimationController.createAnimation(idleSheetPath, columnsIdleSheet, rowsIdleSheet, idleFrameDuration, true);
        walkAnimation = AnimationController.createAnimation(walkSheetPath, columnsWalkSheet, rowsWalkSheet, walkFrameDuration, false);
        leftWalkAnimation = AnimationController.createAnimation(walkSheetPath, columnsWalkSheet, rowsWalkSheet, walkFrameDuration, true);
        hitAnimation = AnimationController.createAnimation(hitSheetPath, columnsHitSheet, rowsHitSheet, hitFrameDuration, false);
        leftHitAnimation = AnimationController.createAnimation(hitSheetPath, columnsHitSheet, rowsHitSheet, hitFrameDuration, true);
        deathAnimation = AnimationController.createAnimation(deathSheetPath, columnsDeathSheet, rowsDeathSheet, deathFrameDuration, false);
        leftDeathAnimation = AnimationController.createAnimation(deathSheetPath, columnsDeathSheet, rowsDeathSheet, deathFrameDuration, true);
    
        this.spriteBatch = new SpriteBatch();
        this.gui = new GuiController();
        this.stateTime = 0f;
        enemyLife = maxEnemyLife;
        this.enemyState = EnemyState.IDLE;    

        enemyBounds = new Rectangle(getX() + (getWidth() - enemyBoundsWidth) / 2, getY() + (getHeight() - enemyBoundsHeight) / 2, enemyBoundsWidth, enemyBoundsHeight);
        enemyVision = new Rectangle(getX() + (getWidth() - enemyVisionWidth) / 2, getY() + (getHeight() - enemyVisionHeight) / 2, enemyVisionWidth, enemyVisionHeight);
    }


    public void updateEnemyBounds(){
        enemyBounds.setPosition(getX() + (getWidth() - enemyBoundsWidth) / 2, getY());
        enemyVision.setPosition(getX() + (getWidth() - enemyVisionWidth) / 2, getY());
    }


    public void applyGravity(float deltaTime) {
        if (isFloating) {
            verticalVelocity += gravity * deltaTime;
            setPosition(getX(), getY() + verticalVelocity * deltaTime);
        }
    }


    public void checkGroundCollision(java.util.List<com.badlogic.gdx.math.Rectangle> groundTileRectangles) {        
        isFloating = true; 
        for (Rectangle tile : groundTileRectangles) {
            if (enemyBounds.overlaps(tile)) {
                // Check if landing on the top of the tile
                if (enemyBounds.y > tile.getY() && enemyBounds.y <= tile.getY() + tile.getHeight() && verticalVelocity < 0) {
                    setPosition(getX(), tile.getY() + tile.getHeight());
                    groundValue = tile.getY() + tile.getHeight();
                    verticalVelocity = 0;
                    isFloating = false;
                    return;
                }
            } 
        }
        int leftLimit = 1000;
        if (enemyBounds.x < leftLimit) {
            setPosition(leftLimit, getY());
            enemyState = EnemyState.IDLE;
        }
    }


    public void reduceLife(float amount) {
        if (enemyState != EnemyState.DEATH) { 
            enemyLife -= amount;
    
            if (enemyLife <= 0) {
                enemyState = EnemyState.DEATH;
                stateTime = 0; 
            } else {
                enemyState = EnemyState.HIT;
            }
        }
    }
    

    public void rightMovement(float delta) {
        speed = 50f;
        this.translateX(speed * delta);
            leftFlag = false;
            enemyState = EnemyState.WALKING;
    }

    public void leftMovement(float delta) {
        speed = 50f;
        this.translateX(-speed * delta);
            leftFlag = true;
            enemyState = EnemyState.WALKING;
    }
            
    public TextureRegion getCurrentFrame(float playerX, float playerPower) {
        if (enemyState != previousState) {
            stateTime = 0;
            previousState = enemyState;
    
            // Handle HIT state specific logic
            if (enemyState == EnemyState.HIT && !hitDisplacementApplied) {
                float displacement = (getX() < playerX) ? 8 : -8;
                setPosition(getX() - displacement, getY());
                this.reduceLife(playerPower);
                Gdx.app.log("Enemy life: ", "life is " + enemyLife);
                hitDisplacementApplied = true;
            }
        }

        stateTime += Gdx.graphics.getDeltaTime();
    
        if (enemyState == EnemyState.HIT && hitAnimation.isAnimationFinished(stateTime)) {
            enemyState = EnemyState.IDLE;  
            hitDisplacementApplied = false;
            isDamaged = false;
        }

        if (enemyState == EnemyState.DEATH) {
            if (deathAnimation.isAnimationFinished(stateTime)) {
                setPosition(-1000, -1000);              
                idleSheet.dispose();
                hitSheet.dispose();
                deathSheet.dispose(); 
            }
        }
    
        return getCurrentAnimation().getKeyFrame(stateTime, true);
    }
    

    private Animation<TextureRegion> getCurrentAnimation() {
        switch (enemyState) {
            case WALKING:
                return leftFlag ? leftWalkAnimation : walkAnimation;
            case HIT:
                return leftFlag ? leftHitAnimation : hitAnimation;
            case DEATH:
                return leftFlag ? leftDeathAnimation : deathAnimation;
            default:
                return leftFlag ? leftIdleAnimation : idleAnimation;
        }
    }
        

    public void update(float deltaTime, java.util.List<com.badlogic.gdx.math.Rectangle> groundTileRectangles) {
        if (damageCooldown > 0) {
            damageCooldown -= deltaTime; // Decrease the cooldown time
        }

        if (Gdx.input.isKeyPressed(Input.Keys.O)) {
            rightMovement(deltaTime);
        } else if (Gdx.input.isKeyPressed(Input.Keys.I)) {
            leftMovement(deltaTime);
        } else {
            if (enemyState == EnemyState.WALKING) {
                enemyState = EnemyState.IDLE; 
            }
        }

        
        applyGravity(deltaTime); // Apply gravity each frame
        checkGroundCollision(groundTileRectangles); // Check for ground collision
        updateEnemyBounds(); // Update collision bounds
    }
    

    public void dispose() {
        idleSheet.dispose();
        spriteBatch.dispose();
        hitSheet.dispose();
        deathSheet.dispose();
    }
    


    
}
