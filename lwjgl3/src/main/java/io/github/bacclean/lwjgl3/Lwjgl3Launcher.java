package io.github.bacclean.lwjgl3;

import java.util.Random;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import io.github.bacclean.Main;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {

    private static final String[] TitlesStrings = {
        "Bacclean Chronicles: ¡Las aventuras de Lauty en Miramar!",
        "Bacclean Chronicles: ¡Quiero 5 litros de suavizante!",
        "Bacclean Chronicles: ¡Oh no! ¡Se llevan en cana a Mr. Bacclean!",
        "Bacclean Chronicles: ¿Cuántos son? ¿Son 7?",
        "Bacclean Chronicles: Colgando el Ford Ka en 2 ruedas.",
        "Bacclean Chronicles: Hazlo, perra.",
    };


    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new Main(), getDefaultConfiguration());
    }


    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle(getRandomTitle());
        //// Vsync limits the frames per second to what your hardware can display, and helps eliminate
        //// screen tearing. This setting doesn't always work on Linux, so the line after is a safeguard.
        configuration.useVsync(true);
        //// Limits FPS to the refresh rate of the currently active monitor, plus 1 to try to match fractional
        //// refresh rates. The Vsync setting above should limit the actual FPS to match the monitor.
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);
        //// If you remove the above line and set Vsync to false, you can get unlimited FPS, which can be
        //// useful for testing performance, but can also be very stressful to some hardware.
        //// You may also need to configure GPU drivers to fully disable Vsync; this can cause screen tearing.
        configuration.setWindowedMode(1024, 576);
        //// You can change these files; they are in lwjgl3/src/main/resources/ .
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
        return configuration;
    }


    // Method to get a random title from the array
    private static String getRandomTitle() {
        Random random = new Random();
        return TitlesStrings[random.nextInt(TitlesStrings.length)];
    }

}