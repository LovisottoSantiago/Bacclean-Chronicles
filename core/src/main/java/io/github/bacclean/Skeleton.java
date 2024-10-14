package io.github.bacclean;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import io.github.bacclean.Skeleton.SkeletonState;

@SuppressWarnings("unused")
public class Skeleton extends Sprite{
    private float stateTime;
    private final SpriteBatch spriteBatch;
    private final Texture idleSheet;
    private final Animation<TextureRegion> idleAnimation;
    private final float idleFrameDuration = 0.2f;

    public SkeletonState skeletonState;
    public SkeletonState previousState = SkeletonState.IDLE;

    public enum SkeletonState {
        IDLE, WALKING
    }

    public Skeleton (
        String idleSheetPath, int columnsIdleSheet, int rowsIdleSheet
    ) {
        this.idleSheet = new Texture(Gdx.files.internal(idleSheetPath));

        idleAnimation = AnimationController.createAnimation(idleSheetPath, columnsIdleSheet, rowsIdleSheet, idleFrameDuration, false);
    
        this.spriteBatch = new SpriteBatch();
        this.stateTime = 0f;
        this.skeletonState = SkeletonState.IDLE;
    
    }



    
    private Animation<TextureRegion> getCurrentAnimation() {
        switch (skeletonState) {
            case WALKING:
                return idleAnimation;
            default:
                return idleAnimation;
        }
    }

    
    public TextureRegion getCurrentFrame() {
        if (skeletonState != previousState) {
            stateTime = 0;
            previousState = skeletonState;
        }
    
        stateTime += Gdx.graphics.getDeltaTime();
        return getCurrentAnimation().getKeyFrame(stateTime, true);
    }
        
    
    public void dispose() {
        idleSheet.dispose();
        spriteBatch.dispose();
    }
    


    
}
