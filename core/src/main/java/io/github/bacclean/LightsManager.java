package io.github.bacclean;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;

import box2dLight.PointLight;
import box2dLight.RayHandler;

public class LightsManager {
    private final RayHandler rayHandler;
    private final PointLight pointLight;


    public LightsManager(OrthographicCamera camera) {
        rayHandler = new RayHandler(null);
        rayHandler.setAmbientLight(0.2f);
        rayHandler.setShadows(true);

        pointLight = new PointLight(rayHandler, 500, new Color(1, 1, 1, 0.70f), 300, 0, 0);
        pointLight.setSoft(true);
    }

    public void update(float x, float y){
        pointLight.setPosition(x, y);
    }

    public void render(OrthographicCamera camera) {
        // Ensure the RayHandler updates with the latest camera position
        rayHandler.setCombinedMatrix(camera);
        rayHandler.updateAndRender();
    }

    public void dispose(){
        rayHandler.dispose();
    }


}
