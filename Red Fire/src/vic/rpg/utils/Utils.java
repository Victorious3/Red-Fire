package vic.rpg.utils;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Random;

import javax.imageio.ImageIO;

import vic.rpg.Game;

public class Utils 
{

	public static GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	public static GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
	public static GraphicsConfiguration gConfig = defaultScreen.getDefaultConfiguration();
	
	public static InputStream getStreamFromString(String s)
	{
		return Game.class.getResourceAsStream(s);		
	}
	
	public static String getAppdata()
	{
		String s =  System.getenv("APPDATA") + "/.RedFire";
		s = s.replaceAll("\\\\", "/");
		return s;
	}
	
	public static File getOrCreateFile(String s)
	{
		File file = new File(s);
		if(!file.exists())
		{
			if(s.lastIndexOf("/") != s.length() - 1 && s.contains("."))
			{
				if(s.contains("/"))
				{
					String[] s1 = s.split("/");
					String s2 = s1[s1.length - 1];
					String s3 = s.replace(s2, "");
					s2 = s;
					
					File f1 = new File(s3);
					File f2 = new File(s2);
					
					f1.mkdirs();
					try {
						f2.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				else
				{
					try {
						file.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			else
			{
				file.mkdirs();
			}	
		}
		return file;
	}
	
	public static final String SIDE_SERVER = "server";
	public static final String SIDE_CLIENT = "client";
	
	public static String getSide()
	{
		Thread thr = Thread.currentThread();
		if(thr.getName().contains("Server"))
		{
			return SIDE_SERVER;
		}
		return SIDE_CLIENT;
	}
	
	public static Image readImageFromJar(String s)
	{
		try {
			InputStream in = getStreamFromString(s);
			Image img = ImageIO.read(in);
			in.close();
			return img;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void setFiled(String fieldName, Object value, String type, Object object) throws NoSuchFieldException, SecurityException, NumberFormatException, IllegalArgumentException, IllegalAccessException
	{
		Field field = object.getClass().getField(fieldName);
    	
    	if(field != null)
    	{
        	field.setAccessible(true);
        	
        	switch(type)
        	{
        	case "int" : field.setInt(object, Integer.parseInt(value.toString())); break;
        	case "float" : field.setFloat(object, Float.parseFloat(value.toString())); break;
        	case "boolean" : field.setBoolean(object, Boolean.parseBoolean(value.toString())); break;
        	case "double" : field.setDouble(object, Double.parseDouble(value.toString())); break;
        	case "long" : field.setLong(object, Long.parseLong(value.toString())); break;
        	case "byte" : field.setByte(object, Byte.parseByte(value.toString())); break;
        	case "char" : field.setChar(object, (value.toString().charAt(0))); break;
        	case "short" : field.setShort(object, Short.parseShort(value.toString())); break;
        	default : field.set(object, value);
        	}	        	
    	}
	}
	
	public static String stripExtension(String str) 
	{
        if (str == null) return null;
        int pos = str.lastIndexOf(".");
        if (pos == -1) return str;
        return str.substring(0, pos);
    }
	
	public boolean withChance(int percent)
	{
		Random rand = new Random();
		int chance = rand.nextInt(percent);
		
		if(chance == 0) return true;
		return false;
	}
}
