package ISU;

import java.io.*;
import javax.sound.sampled.*;
public class ParallelAudio { // allows me to play sounds in parallel as many times as i want
    private int currentStream;
    private Clip[] streams;
    public void play() {
        streams[currentStream].setFramePosition(0);
        streams[currentStream].start();
        currentStream++;
        currentStream %= streams.length;
    }
    public ParallelAudio(File f, int s) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        streams = new Clip[s];
        for (int i = 0; i < s; i++) {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(f);
            Clip c = (Clip)AudioSystem.getLine(new DataLine.Info(Clip.class, audioStream.getFormat()));
            c.addLineListener(new LineListener() {
                public void update(LineEvent e) {}
            });
            c.open(audioStream);
            streams[i] = c;
        }
        currentStream = 0;
    }
}
