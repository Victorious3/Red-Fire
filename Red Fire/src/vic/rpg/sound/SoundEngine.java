package vic.rpg.sound;

import java.util.Arrays;
import java.util.HashMap;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Control;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.FloatControl.Type;

import vic.rpg.Game;
import vic.rpg.utils.Utils;

public class SoundEngine 
{
	private static HashMap<String, Clip> clips = new HashMap<String, Clip>();
	public static float MASTER_VOLUME = 1.0F;
	
	public static void loadClip(String path, String name)
	{
		if(clips.containsKey(name)) return;
		AudioInputStream as1;
		Clip clip;
		
		try {
			as1 = AudioSystem.getAudioInputStream(Utils.getStreamFromJar(path));		

	        clip = AudioSystem.getClip();
        	clip.open(as1);
        	clips.put(name, clip);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void playClip(String name)
	{
		playClip(name, false);
	}
	
	public static void playClip(String name, boolean loop)
	{
		playClip(name, loop, MASTER_VOLUME);
	}
	
	public static void playClip(String name, boolean loop, float volume)
	{
		Clip clip = clips.get(name);
		if(clip == null) throw new NullPointerException("Clip " + name + " does not exist!");
    	gainClip(name, volume);
    	if(loop) clip.loop(Clip.LOOP_CONTINUOUSLY);
    	clip.start();
	}
	
	public static void balanceClip(String name, float balance)
	{
		((FloatControl) getClipControl(name, Type.BALANCE)).setValue(balance);
	}
	
	public static void gainClip(String name, float volume)
	{
		float gain = (float) ((Math.log((volume != 0.0) ? volume : 1.0E-4) / Math.log(10.0)) * 20.0);
		((FloatControl) getClipControl(name, Type.MASTER_GAIN)).setValue(gain);
	}
	
	public static Control getClipControl(String name, Type control)
	{
		Clip clip = clips.get(name);
		if(clip == null) throw new NullPointerException("Clip " + name + " does not exist!");
		return clip.getControl(control);
	}
	
	//TODO Finish that up
	public static void adjustClipRelativeToPlayer(String name, int x, int y, float range)
	{
		Clip clip = clips.get(name);
		if(clip == null) throw new NullPointerException("Clip " + name + " does not exist!");
		
		float diffX = Game.thePlayer.xCoord - x;
		float diffY = Game.thePlayer.yCoord - y;

		float maxDiffX = Game.WIDTH / 2;
		float maxDiffY = Game.WIDTH / 2;

		float balanceX = 1 - (diffX / maxDiffX);
		float balanceY = 1 - (diffY / maxDiffY);

		float volume = balanceX * range;
		
		if(balanceX > 1F) balanceX = 1F;
		if(balanceX < -1F) balanceX = -1F;
		balanceClip(name, balanceX);
	}
	
	public static void stopAll(String... except)
	{
		for(String name : clips.keySet())
		{
			if(!Arrays.asList(except).contains(name)) stopClip(name);
		}
	}
	
	public static void closeAll(String... except)
	{
		for(String name : clips.keySet())
		{
			if(!Arrays.asList(except).contains(name)) closeClip(name);
		}
	}
	
	public static void stopClip(String name)
	{
		Clip clip = clips.get(name);
		if(clip == null) throw new NullPointerException("Clip " + name + " does not exist!");
		if(clip.isRunning())
		{
			clip.setFramePosition(0);
			clip.stop();
		}
	}
	
	public static void closeClip(String name)
	{
		stopClip(name);
		Clip clip = clips.get(name);
		clip.close();
	}
}
