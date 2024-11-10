package io.github.bacclean.Controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class SoundController {
    private final List<Sound> attackSounds;
    private final List<Sound> enemySounds;
    private final List<Sound> getItemSounds;
    private final Random random;

    public SoundController() {
        attackSounds = new ArrayList<>();
        enemySounds = new ArrayList<>();
        getItemSounds = new ArrayList<>();
        random = new Random();
        loadSounds();
    }

    private void loadSounds() {
        attackSounds.add(Gdx.audio.newSound(Gdx.files.internal("attack_sounds/1.mp3")));
        attackSounds.add(Gdx.audio.newSound(Gdx.files.internal("attack_sounds/2.mp3")));
        attackSounds.add(Gdx.audio.newSound(Gdx.files.internal("attack_sounds/3.mp3")));
        attackSounds.add(Gdx.audio.newSound(Gdx.files.internal("attack_sounds/4.mp3")));
        enemySounds.add(Gdx.audio.newSound(Gdx.files.internal("enemies/skeleton/sounds/hurt_1.mp3")));
        enemySounds.add(Gdx.audio.newSound(Gdx.files.internal("enemies/skeleton/sounds/hurt_2.mp3")));
        enemySounds.add(Gdx.audio.newSound(Gdx.files.internal("enemies/skeleton/sounds/hurt_3.mp3")));
        enemySounds.add(Gdx.audio.newSound(Gdx.files.internal("enemies/skeleton/sounds/hurt_4.mp3")));
        getItemSounds.add(Gdx.audio.newSound(Gdx.files.internal("getItemsSounds/audio1.mp3")));
        getItemSounds.add(Gdx.audio.newSound(Gdx.files.internal("getItemsSounds/audio2.mp3")));
        getItemSounds.add(Gdx.audio.newSound(Gdx.files.internal("getItemsSounds/audio3.mp3")));
    }

    public Sound getAttackSound() {
        if (attackSounds.isEmpty()) {
            return null;
        }
        int index = random.nextInt(attackSounds.size());
        return attackSounds.get(index);
    }

    public Sound getEnemyHurtSound() {
        if (enemySounds.isEmpty()) {
            return null;
        }
        int index = random.nextInt(enemySounds.size());
        return enemySounds.get(index);
    }

    public Sound getItemSound() {
        if (getItemSounds.isEmpty()) {
            return null;
        }
        int index = random.nextInt(getItemSounds.size());
        return getItemSounds.get(index);
    }

    public void dispose() {
        for (Sound sound : attackSounds) {
            sound.dispose();
        }
        for (Sound sound : enemySounds) {
            sound.dispose();
        }
        for (Sound sound : getItemSounds) {
            sound.dispose();
        }
    }
}
