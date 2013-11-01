package vic.rpg.registry;

import java.util.HashMap;

public class LanguageRegistry 
{
	public static String de_DE = "de_DE";
	public static String en_GB = "en_GB";
	public static String fr_FR = "fr_FR";
	
	private static HashMap<String, HashMap<String, String>> translations = new HashMap<String, HashMap<String, String>>();
	private static String currentLanguage = en_GB;
	
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
		if(translations.get(currentLanguage).containsKey(id))
		{
			return translations.get(currentLanguage).get(id);
		}
		else return id;
	}
}
