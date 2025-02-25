package io.github.bacclean.Entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Item {
    private final Texture texture;
    private final Vector2 position;
    private final Rectangle bounds;

    public Item(String texturePath, float x, float y) {
        this.texture = new Texture(texturePath); 
        this.position = new Vector2(x, y);
        this.bounds = new Rectangle(position.x, position.y, texture.getWidth(), texture.getHeight());
    }

    public void draw(SpriteBatch spriteBatch) {
        spriteBatch.draw(texture, position.x, position.y); 
    }

    public Vector2 getPosition() {
        return position; // Get the position if needed
    }

    public Rectangle getBounds() {
        return bounds; // Get the bounds for collision detection
    }

    public void dispose() {
        texture.dispose(); // Dispose of the texture when no longer needed
    }

    public void itemLifeEffect(Player player){ // Fernet
        player.increaseLife(20);
        
    }

}
