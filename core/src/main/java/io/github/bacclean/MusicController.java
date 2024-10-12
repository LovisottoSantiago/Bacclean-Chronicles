package io.github.bacclean;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class MusicController {
    private Music currentMusic;
    private final Array<String> musicFiles;

    public MusicController(){
        musicFiles = new Array<>();
        musicFiles.add("music/halloweenRKT.mp3");
        //musicFiles.add("music/miedo.mp3");

    }

    public void playRandomMusic(){
        if (currentMusic != null){
            currentMusic.stop();
            currentMusic.dispose();
        }

        int randomIndex = MathUtils.random(musicFiles.size - 1);
        currentMusic = Gdx.audio.newMusic(Gdx.files.internal(musicFiles.get(randomIndex)));


        // Set a listener to play another random song when this one finishes
        currentMusic.setOnCompletionListener((Music music) -> {
            playRandomMusic(); // Play a new random song when the current one ends
        });

        currentMusic.setLooping(false);
        currentMusic.setVolume(0.6f);
        currentMusic.play();
    }

    public void dispose() {
        if (currentMusic != null) {
            currentMusic.dispose();
        }
    }



}
