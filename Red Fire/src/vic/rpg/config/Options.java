package vic.rpg.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import vic.rpg.registry.LanguageRegistry;
import vic.rpg.utils.Utils;

/**
 * Options handle all the configurable parts of the Game.
 * @author Victorious3
 */
public class Options 
{
	public static String LANGUAGE = LanguageRegistry.en_GB;
	public static boolean LIGHTING = true;
	
	/**
	 * Loads all the config options from a file called "%APPDATA%/.RedFire/config/config.cf". It is using the
	 * {@link Properties} API.
	 */
	public static void load() 
	{
		File file = Utils.getOrCreateFile(Utils.getAppdata() + "/config/config.cf");
		Properties prop = new Properties();	
		
		try {
			prop.load(new FileInputStream(file));		
			LANGUAGE = prop.getProperty("lang", LanguageRegistry.en_GB);
			LIGHTING = Boolean.parseBoolean(prop.getProperty("lighting", String.valueOf(true)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Saves all the config options to a file called "%APPDATA%/.RedFire/config/config.cf". It is using the
	 * {@link Properties} API.
	 */
	public static void safe() 
	{
		File file = Utils.getOrCreateFile(Utils.getAppdata() + "/config/config.cf");
		Properties prop = new Properties();	
		
		try {	
			prop.setProperty("lang", LANGUAGE);
			prop.setProperty("lighting", String.valueOf(LIGHTING));
			prop.store(new FileOutputStream(file), "Options");			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
}
