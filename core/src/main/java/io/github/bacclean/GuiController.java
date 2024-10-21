package io.github.bacclean;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class GuiController {
    private final ShapeRenderer shapeRenderer;

    public GuiController() {
        shapeRenderer = new ShapeRenderer();
    }

    public void renderBar(float barX, float barY, float value, float maxValue, float maxBarWidth, float barHeight, String filledColor, String emptyColor, String borderColor) {
        float currentBarWidth = (value / maxValue) * maxBarWidth;

        // Get colors from string names
        Color filledBarColor = getColorFromString(filledColor);
        Color emptyBarColor = getColorFromString(emptyColor);
        Color border = getColorFromString(borderColor);

        // Start rendering shapes
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draw background (empty portion of the bar) first
        shapeRenderer.setColor(emptyBarColor);
        shapeRenderer.rect(barX, barY, maxBarWidth, barHeight);

        // Draw filled portion on top
        shapeRenderer.setColor(filledBarColor);
        shapeRenderer.rect(barX, barY, currentBarWidth, barHeight);

        // End rendering filled shapes
        shapeRenderer.end();

        // Optionally: add borders around the bar
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(border); // Use the border color variable
        shapeRenderer.rect(barX, barY, maxBarWidth, barHeight); // Draw border
        shapeRenderer.end();
    }

    private Color getColorFromString(String colorName) {
        switch (colorName.toLowerCase()) {
            case "red":
                return new Color(1, 0.04f, 0, 1); 
            case "darkred":
                return new Color(0.522f, 0.102f, 0.102f, 1);
            case "green":
                return new Color(0, 1, 0, 1);
            case "blue":
                return new Color(0, 0, 1, 1);
            case "yellow":
                return new Color(0.890f, 0.851f, 0.302f, 1);
            case "darkyellow":
                return new Color(0.522f, 0.502f, 0.259f, 1);
            case "white":
                return new Color(1, 1, 1, 1);
            case "black":
                return new Color(0, 0, 0, 1);
            case "gray":
                return new Color(0.5f, 0.5f, 0.5f, 1);
            case "orange":
                return new Color(1, 0.5f, 0, 1);
            // Add more colors as needed
            default:
                return new Color(1, 1, 1, 1); // Default to white if color not recognized
        }
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}
