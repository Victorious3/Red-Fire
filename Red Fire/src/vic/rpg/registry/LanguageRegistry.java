package vic.rpg.registry;

import java.util.ArrayList;
import java.util.HashMap;

import vic.rpg.Init;
import vic.rpg.config.Options;
import vic.rpg.utils.Utils.Side;

public class LanguageRegistry 
{
	public static String de_DE = "Deutsch";
	public static String en_GB = "English";
	public static String fr_FR = "Francais";
	
	private static ArrayList<String> languages = new ArrayList<String>();
	private static HashMap<String, HashMap<String, String>> translations = new HashMap<String, HashMap<String, String>>();
	
	@Init(side = Side.BOTH)
	public static void init()
	{
		addLanguage(de_DE);
		addLanguage(en_GB);
		addLanguage(fr_FR);
	}
	
	public static ArrayList<String> getLanguages()
	{
		return languages;
	}
	
	public static void addLanguage(String language)
	{
		translations.put(language, new HashMap<String, String>());
		languages.add(language);
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
