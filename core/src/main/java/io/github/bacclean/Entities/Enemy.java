package io.github.bacclean.Entities;

import com.badlogic.gdx.Gdx;
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
    private boolean isDamaged = false;
    private float enemyLife;
    private final float maxEnemyLife = 100;

    private final Texture idleSheet;
    private final Texture hitSheet;
    private final Texture deathSheet;

    private final Animation<TextureRegion> idleAnimation;
    private final Animation<TextureRegion> hitAnimation;
    private final Animation<TextureRegion> deathAnimation;

    private final float idleFrameDuration = 0.2f;
    private final float hitFrameDuration = 0.08f;
    private final float deathFrameDuration = 0.2f;

    // Collisions
    public Rectangle enemyBounds;
    public int enemyBoundsWidth = 22;
    public int enemyBoundsHeight = 54;


    public static EnemyState enemyState;
    public EnemyState previousState = EnemyState.IDLE;

    public static enum EnemyState {
        IDLE, HIT, WALKING, DEATH
    }

    public Enemy (
        String idleSheetPath, int columnsIdleSheet, int rowsIdleSheet,
        String hitSheetPath, int columnsHitSheet, int rowsHitSheet,
        String deathSheetPath, int columnsDeathSheet, int rowsDeathSheet
    ) {
        this.idleSheet = new Texture(Gdx.files.internal(idleSheetPath));
        this.hitSheet = new Texture(Gdx.files.internal(hitSheetPath));
        this.deathSheet = new Texture(Gdx.files.internal(deathSheetPath));

        idleAnimation = AnimationController.createAnimation(idleSheetPath, columnsIdleSheet, rowsIdleSheet, idleFrameDuration, false);
        hitAnimation = AnimationController.createAnimation(hitSheetPath, columnsHitSheet, rowsHitSheet, hitFrameDuration, false);
        deathAnimation = AnimationController.createAnimation(deathSheetPath, columnsDeathSheet, rowsDeathSheet, deathFrameDuration, false);
    
        this.spriteBatch = new SpriteBatch();
        this.gui = new GuiController();
        this.stateTime = 0f;
        enemyLife = maxEnemyLife;
        Enemy.enemyState = EnemyState.IDLE;    

        enemyBounds = new Rectangle(getX() + (getWidth() - enemyBoundsWidth) / 2, getY() + (getHeight() - enemyBoundsHeight) / 2, enemyBoundsWidth, enemyBoundsHeight);

    }

    public void updateEnemyBounds(){
        enemyBounds.setPosition(getX() + (getWidth() - enemyBoundsWidth) / 2, getY());
    }

    public void reduceLife(float amount) {
        if (enemyState != EnemyState.DEATH) {
            enemyLife -= amount;
            if (enemyLife == 0) {
                enemyState = EnemyState.DEATH; 
                stateTime = 0; 
            } 
            else {
                enemyState = EnemyState.HIT;
                isDamaged = true;
            }
        }
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
                return idleAnimation;
            case HIT:
                return hitAnimation;
            case DEATH:
                return deathAnimation;
            default:
                return idleAnimation;
        }
    }


        
    
    public void dispose() {
        idleSheet.dispose();
        spriteBatch.dispose();
        hitSheet.dispose();
        deathSheet.dispose();
    }
    


    
}
