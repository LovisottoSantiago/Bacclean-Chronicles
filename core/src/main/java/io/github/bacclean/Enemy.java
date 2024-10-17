package io.github.bacclean;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import io.github.bacclean.Enemy.EnemyState;

@SuppressWarnings("unused")
public class Enemy extends Sprite{
    private float stateTime;
    private final SpriteBatch spriteBatch;
    private final Texture idleSheet;
    private final Animation<TextureRegion> idleAnimation;
    private final float idleFrameDuration = 0.2f;

    // Collisions
    public Rectangle enemyBounds;
    public int enemyBoundsWidth = 22;
    public int enemyBoundsHeight = 40;


    public EnemyState enemyState;
    public EnemyState previousState = EnemyState.IDLE;

    public enum EnemyState {
        IDLE, WALKING
    }

    public Enemy (
        String idleSheetPath, int columnsIdleSheet, int rowsIdleSheet
    ) {
        this.idleSheet = new Texture(Gdx.files.internal(idleSheetPath));

        idleAnimation = AnimationController.createAnimation(idleSheetPath, columnsIdleSheet, rowsIdleSheet, idleFrameDuration, false);
    
        this.spriteBatch = new SpriteBatch();
        this.stateTime = 0f;
        this.enemyState = EnemyState.IDLE;    

        enemyBounds = new Rectangle(getX() + (getWidth() - enemyBoundsWidth) / 2, getY() + (getHeight() - enemyBoundsHeight) / 2, enemyBoundsWidth, enemyBoundsHeight);
    }

    public void updateEnemyBounds(){
        enemyBounds.setPosition(getX() + (getWidth() - enemyBoundsWidth) / 2, getY());
    }

    
    private Animation<TextureRegion> getCurrentAnimation() {
        switch (enemyState) {
            case WALKING:
                return idleAnimation;
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
        return getCurrentAnimation().getKeyFrame(stateTime, true);
    }
        
    
    public void dispose() {
        idleSheet.dispose();
        spriteBatch.dispose();
    }
    


    
}
