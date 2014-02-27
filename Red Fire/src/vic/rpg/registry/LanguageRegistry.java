package vic.rpg.registry;

import java.util.ArrayList;
import java.util.HashMap;

import vic.rpg.Init;
import vic.rpg.config.Options;
import vic.rpg.utils.Utils.Side;

/**
 * The LanguageRegistry contains all the rendered text in three languages.
 * @author Victorious3
 */
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
	
	/**
	 * Returns an ArrayList of all languages available.
	 * @return ArrayList&ltString&gt
	 */
	public static ArrayList<String> getLanguages()
	{
		return languages;
	}
	
	/**
	 * Adds a new Language with the given name to the list of languages.
	 * @param language
	 */
	public static void addLanguage(String language)
	{
		translations.put(language, new HashMap<String, String>());
		languages.add(language);
	}
	
	/**
	 * Adds a new Translation for the given id on the given language.
	 * @param language
	 * @param id
	 * @param translation
	 */
	public static void addTranslation(String language, String id, String translation)
	{
		translations.get(language).put(id, translation);
	}
	
	/**
	 * Returns the translation saved for the given id. The language from {@link Options#LANGUAGE} is used.
	 * @param id
	 * @return String
	 */
	public static String getTranslation(String id)
	{
		if(translations.get(Options.LANGUAGE).containsKey(id))
		{
			return translations.get(Options.LANGUAGE).get(id);
		}
		else return id;
	}
}
