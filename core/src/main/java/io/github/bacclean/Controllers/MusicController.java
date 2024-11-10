package io.github.bacclean.Controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class MusicController {
    private Music currentMusic;
    private final Array<String> musicFiles;

    public MusicController(){
        musicFiles = new Array<>();
        musicFiles.add("music/1_DavidKBD.ogg");
        musicFiles.add("music/2_DavidKBD.ogg");
        musicFiles.add("music/3_DavidKBD.ogg");
        musicFiles.add("music/4_DavidKBD.ogg");
        musicFiles.add("music/5_DavidKBD.ogg");
        musicFiles.add("music/6_DavidKBD.ogg");
        musicFiles.add("music/7_DavidKBD.ogg");

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
        currentMusic.setVolume(0.09f);
        currentMusic.play();
    }

    public void dispose() {
        if (currentMusic != null) {
            currentMusic.dispose();
        }
    }



}
