package io.github.bacclean;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import io.github.bacclean.Enemy.EnemyState;

@SuppressWarnings("unused")
public class Enemy extends Sprite{
    private float stateTime;
    private final SpriteBatch spriteBatch;
    private final Vector2 position;

    private final Texture idleSheet;
    private final Texture hitSheet;

    private final Animation<TextureRegion> idleAnimation;
    private final Animation<TextureRegion> hitAnimation;

    private final float idleFrameDuration = 0.2f;
    private final float hitFrameDuration = 0.08f;

    // Collisions
    public Rectangle enemyBounds;
    public int enemyBoundsWidth = 22;
    public int enemyBoundsHeight = 54;


    public static EnemyState enemyState;
    public EnemyState previousState = EnemyState.IDLE;

    public static enum EnemyState {
        IDLE, HIT, WALKING
    }

    public Enemy (
        String idleSheetPath, int columnsIdleSheet, int rowsIdleSheet,
        String hitSheetPath, int columnsHitSheet, int rowsHitSheet
    ) {
        this.idleSheet = new Texture(Gdx.files.internal(idleSheetPath));
        this.hitSheet = new Texture(Gdx.files.internal(hitSheetPath));

        idleAnimation = AnimationController.createAnimation(idleSheetPath, columnsIdleSheet, rowsIdleSheet, idleFrameDuration, false);
        hitAnimation = AnimationController.createAnimation(hitSheetPath, columnsHitSheet, rowsHitSheet, hitFrameDuration, false);
    
        this.spriteBatch = new SpriteBatch();
        this.stateTime = 0f;
        Enemy.enemyState = EnemyState.IDLE;    
        this.position = new Vector2(0,0);

        enemyBounds = new Rectangle(getX() + (getWidth() - enemyBoundsWidth) / 2, getY() + (getHeight() - enemyBoundsHeight) / 2, enemyBoundsWidth, enemyBoundsHeight);

    }

    public void updateEnemyBounds(){
        enemyBounds.setPosition(getX() + (getWidth() - enemyBoundsWidth) / 2, getY());
    }

    
    private Animation<TextureRegion> getCurrentAnimation() {
        switch (enemyState) {
            case WALKING:
                return idleAnimation;
            case HIT:
                return hitAnimation;
            default:
                return idleAnimation;
        }
    }

    
    public TextureRegion getCurrentFrame() {
        if (enemyState != previousState) {
            stateTime = 0;
            previousState = enemyState;
        }
    
        stateTime += Gdx.graphics.getDeltaTime();

        if (enemyState == EnemyState.HIT && hitAnimation.isAnimationFinished(stateTime)){
            position.x -= 15 * (Gdx.graphics.getDeltaTime() / hitAnimation.getAnimationDuration());
            enemyState = EnemyState.IDLE;
        }

        return getCurrentAnimation().getKeyFrame(stateTime, true);
    }



        
    
    public void dispose() {
        idleSheet.dispose();
        spriteBatch.dispose();
    }
    


    
}
