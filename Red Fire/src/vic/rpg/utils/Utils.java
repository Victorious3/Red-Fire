package vic.rpg.utils;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.Random;

import javax.imageio.ImageIO;

import vic.rpg.Game;

public class Utils 
{
	public static GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	public static GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
	public static GraphicsConfiguration gConfig = defaultScreen.getDefaultConfiguration();
	
	public static InputStream getStreamFromJar(String s)
	{
		return Game.class.getResourceAsStream(s);		
	}
	
	public static File getFileFromJar(String s)
	{
		try {
			return new File(Game.class.getResource(s).toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getAppdata()
	{
		String s =  System.getenv("APPDATA") + "/.RedFire";
		s = replaceBackslashes(s);
		return s;
	}
	
	public static String replaceBackslashes(String s)
	{
		return s.replaceAll("\\\\", "/");
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
	
	public static BufferedImage readImageFromJar(String s)
	{
		try {
			InputStream in = getStreamFromJar(s);
			BufferedImage img = ImageIO.read(in);
			in.close();
			return img;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void setField(String fieldName, Object value, Object object) throws NoSuchFieldException, SecurityException, NumberFormatException, IllegalArgumentException, IllegalAccessException
	{
		Field field = object.getClass().getField(fieldName);
    	String type = field.getType().getName();
    	
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
	
	public static boolean withChance(int percent)
	{
		Random rand = new Random();
		int chance = rand.nextInt(percent);
		
		if(chance == 0) return true;
		return false;
	}
	
	public static String getStackTrace(Exception e)
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Object> T[] cloneArray(Object[] source, Class<T> type)
	{
		if(source == null)
		{
			throw new NullPointerException();
		}
		T[] clone = (T[]) Array.newInstance(type, source.length);
		for(int i = 0; i < source.length; i++)
		{
			try {
				Method cloneMethod = source[i].getClass().getMethod("clone");
				cloneMethod.setAccessible(true);
				clone[i] = (T) cloneMethod.invoke(source[i]);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				throw new IllegalArgumentException("Object of type " + clone[i].getClass().getSimpleName() + " can't be cloned!");
			}
		}
		return clone;
	}
	
	private static OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
	
	public static double getCPUUsage()
	{	
		try {
			Method m = osBean.getClass().getDeclaredMethod("getSystemCpuLoad");
			m.setAccessible(true);
			double d = (double) m.invoke(osBean);
			if(d < 0) d = 0.0D;
			return d;
		} catch (Exception e){}
		return -1.0D;
	}
	
	public static long getDeviceMemory()
	{	
		try {
			Method m = osBean.getClass().getDeclaredMethod("getTotalPhysicalMemorySize");
			m.setAccessible(true);
			return (long) m.invoke(osBean);
		} catch (Exception e){}
		return -1;
	}
	
	public static int lcm(int... a) 
	{
		for(int m = 1;; m++) 
		{
			int n = a.length;
			for(int i : a) 
			{
				if(i == 0) break;
				if(m % i != 0) 
				{
					break;
				}
				if(--n == 0) 
				{
					return m;
				}
			}
		}
	}
	
	public static int rnd(int i1, int i2) 
	{
		if(i1 % i2 == 0) return i1;
		else if(i1 > i2)
		{
			int i3 = i1 / i2;
			return i2 * i3;
		}
		return 0;
	}
}
