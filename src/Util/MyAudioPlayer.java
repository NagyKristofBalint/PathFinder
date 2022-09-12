package Util;

import Window.Table;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class MyAudioPlayer {
    private Clip traceClip = null;
    private Table table;

    public MyAudioPlayer(Table table) {
        this.table = table;
        resetAudio();
    }

    public void resetAudio() {
        try {
            traceClip = AudioSystem.getClip();
            traceClip.open(AudioSystem.getAudioInputStream(new File("res/traceSound.wav")
                    .getAbsoluteFile()));
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            System.out.println("Error at sound" + e.getMessage());
        }
    }

    public void playTraceSound() {
        traceClip.start();
    }

    public void stopTraceSound() {
        traceClip.stop();
    }

    public boolean isRunning() {
        return traceClip.isRunning();
    }
}
