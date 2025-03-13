package minesweeper;

import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Main {
	
	public static void main(String[] args) throws IOException, LineUnavailableException, UnsupportedAudioFileException {
		//start the game loop
		new Minesweeper();
	}
}
