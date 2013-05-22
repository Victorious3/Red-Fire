package vic.rpg.sound;

import java.io.BufferedInputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;

import vic.rpg.utils.Utils;

public class SoundPlayer 
{
	public static Clip clip = null;
	
	public static void playSound(String path)
	{
		AudioInputStream as1;
		if(clip != null) clip.close();
		
		try {
			as1 = AudioSystem.getAudioInputStream(Utils.getStreamFromString(path));		

	        clip = AudioSystem.getClip();
        	clip.open(as1);
        	clip.start();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void playSoundLoop(String path)
	{
		AudioInputStream as1;
		if(clip != null) clip.close();
		
		try {
			as1 = AudioSystem.getAudioInputStream(new BufferedInputStream(Utils.getStreamFromString(path)));		
	        
			clip = AudioSystem.getClip();   
        	clip.open(as1);
        	clip.loop(Clip.LOOP_CONTINUOUSLY);
        	clip.start();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void stop()
	{
		clip.stop();
		clip.close();
		
		try {
			clip = AudioSystem.getClip();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}
}
