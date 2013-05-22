package vic.rpg.config;

import java.awt.RenderingHints;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import vic.rpg.utils.Utils;

public class Options 
{
	public static float RENDER_PASSES = 0.2F;
	
	public static Object ANTIALASING = RenderingHints.VALUE_ANTIALIAS_DEFAULT;
	public static Object COLOR_RENDER = RenderingHints.VALUE_COLOR_RENDER_DEFAULT;
	public static Object INTERPOLATION = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
		
	public static void load() 
	{
		File file = Utils.getOrCreateFile(Utils.getAppdata() + "/config/config.cf");
		Properties prop = new Properties();	
		
		try {
			prop.load(new FileInputStream(file));
			
			RENDER_PASSES = Float.parseFloat(prop.getProperty("renderpasses", Float.toString(0.2F)));
			
			switch(Integer.parseInt(prop.getProperty("antialasing", "0")))
			{
			case 0: ANTIALASING = RenderingHints.VALUE_ANTIALIAS_DEFAULT; break;
			case 1: ANTIALASING = RenderingHints.VALUE_ANTIALIAS_ON; break;
			case 2: ANTIALASING = RenderingHints.VALUE_ANTIALIAS_OFF; break;
			}
			
			switch(Integer.parseInt(prop.getProperty("colorrender", "0")))
			{
			case 0: COLOR_RENDER = RenderingHints.VALUE_COLOR_RENDER_DEFAULT; break;
			case 1: COLOR_RENDER = RenderingHints.VALUE_COLOR_RENDER_SPEED; break;
			case 2: COLOR_RENDER = RenderingHints.VALUE_COLOR_RENDER_QUALITY; break;
			}
			
			switch(Integer.parseInt(prop.getProperty("interpolation", "0")))
			{
			case 0: INTERPOLATION = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR; break;
			case 1: INTERPOLATION = RenderingHints.VALUE_INTERPOLATION_BILINEAR; break;
			case 2: INTERPOLATION = RenderingHints.VALUE_INTERPOLATION_BICUBIC; break;
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void safe() 
	{
		File file = Utils.getOrCreateFile(Utils.getAppdata() + "/config/config.cf");
		Properties prop = new Properties();	
		
		try {
			prop.setProperty("renderpasses", String.valueOf(RENDER_PASSES));
			
			if(ANTIALASING == RenderingHints.VALUE_ANTIALIAS_DEFAULT) prop.setProperty("antialasing", "0");
			else if(ANTIALASING == RenderingHints.VALUE_ANTIALIAS_ON) prop.setProperty("antialasing", "1");
			else if(ANTIALASING == RenderingHints.VALUE_ANTIALIAS_OFF) prop.setProperty("antialasing", "2");
			
			if(COLOR_RENDER == RenderingHints.VALUE_COLOR_RENDER_DEFAULT) prop.setProperty("colorrender", "0");
			else if(COLOR_RENDER == RenderingHints.VALUE_COLOR_RENDER_SPEED) prop.setProperty("colorrender", "1");
			else if(COLOR_RENDER == RenderingHints.VALUE_COLOR_RENDER_QUALITY) prop.setProperty("colorrender", "2");
			
			if(INTERPOLATION == RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR) prop.setProperty("interpolation", "0");
			else if(INTERPOLATION == RenderingHints.VALUE_INTERPOLATION_BILINEAR) prop.setProperty("interpolation", "1");
			else if(INTERPOLATION == RenderingHints.VALUE_INTERPOLATION_BICUBIC) prop.setProperty("interpolation", "2");
			
			prop.store(new FileOutputStream(file), "Options");
				
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
}
