package io.github.bacclean;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class SoundController {
    private final List<Sound> attackSounds;
    private final Random random;

    public SoundController() {
        attackSounds = new ArrayList<>();
        random = new Random();
        loadSounds();
    }

    private void loadSounds() {
        attackSounds.add(Gdx.audio.newSound(Gdx.files.internal("attack_sounds/1.mp3")));
        attackSounds.add(Gdx.audio.newSound(Gdx.files.internal("attack_sounds/2.mp3")));
        attackSounds.add(Gdx.audio.newSound(Gdx.files.internal("attack_sounds/3.mp3")));
        attackSounds.add(Gdx.audio.newSound(Gdx.files.internal("attack_sounds/4.mp3")));
    }

    public Sound getAttackSound() {
        if (attackSounds.isEmpty()) {
            return null;
        }
        int index = random.nextInt(attackSounds.size());
        return attackSounds.get(index);
    }

    public void dispose() {
        for (Sound sound : attackSounds) {
            sound.dispose();
        }
    }
}