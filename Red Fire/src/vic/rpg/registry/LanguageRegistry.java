package vic.rpg.registry;

import java.util.HashMap;

import vic.rpg.config.Options;

public class LanguageRegistry 
{
	public static String de_DE = "de_DE";
	public static String en_GB = "en_GB";
	public static String fr_FR = "fr_FR";
	
	private static HashMap<String, HashMap<String, String>> translations = new HashMap<String, HashMap<String, String>>();
	
	static
	{
		addLanguage(de_DE);
		addLanguage(en_GB);
		addLanguage(fr_FR);
	}
	
	public static void addLanguage(String language)
	{
		translations.put(language, new HashMap<String, String>());
	}
	
	public static void addTranslation(String language, String id, String translation)
	{
		translations.get(language).put(id, translation);
	}
	
	public static String getTranslation(String id)
	{
		if(translations.get(Options.LANGUAGE).containsKey(id))
		{
			return translations.get(Options.LANGUAGE).get(id);
		}
		else return id;
	}
}
