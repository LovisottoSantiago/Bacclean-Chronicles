package io.github.bacclean.Controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AnimationController {

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

    public static Animation<TextureRegion> createAnimation(String sheetPath, int columns, int rows, float frameDuration, boolean flip) {
        Texture sheet = new Texture(Gdx.files.internal(sheetPath));
        if (flip) {
            TextureRegion[] frames = AnimationMaker(sheet, columns, rows);
            for (TextureRegion frame : frames) {
                frame.flip(true, false); // Flip horizontally
            }
            return new Animation<>(frameDuration, frames);
        }
        return new Animation<>(frameDuration, AnimationMaker(sheet, columns, rows));
    }

    
}
