package vic.rpg.utils;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
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

/**
 * Utils is a class that is used almost everywhere. It contains a
 * set of methods for every taste.
 * @author Victorious3
 */
public class Utils 
{
	public static GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	public static GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
	public static GraphicsConfiguration gConfig = defaultScreen.getDefaultConfiguration();
	
	public static enum Side
	{
		CLIENT("CLIENT"), SERVER("SERVER"), BOTH("CLIENT/SERVER"), OTHER_SIDE("NONE");

		@Override
		public String toString() 
		{
			return super.toString();
		}
		
		private final String name;
		private Side(String name)
		{
			this.name = name;
		}
	}
	
	/**
	 * Returns a new {@link InputStream} from a {@link File} inside the jar.
	 * @param s
	 * @return InputStream
	 */
	public static InputStream getStreamFromJar(String s)
	{
		return Game.class.getResourceAsStream(s);		
	}
	
	/**
	 * Self explaining.
	 * @param s
	 * @return File
	 */
	public static File getFileFromJar(String s)
	{
		try {
			return new File(Game.class.getResource(s).toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Returns %APPDATA% + ./RedFire
	 * @return
	 */
	public static String getAppdata()
	{
		String s =  System.getenv("APPDATA") + "/.RedFire";
		s = replaceBackslashes(s);
		return s;
	}
	
	/**
	 * Replaces any backslash inside a string with forward slashes.
	 * @param s
	 * @return
	 */
	public static String replaceBackslashes(String s)
	{
		return s.replaceAll("\\\\", "/");
	}
	
	/**
	 * Searches for a given File or Directory and if it doesn't exist,
	 * it's automatically created.
	 * @param s
	 * @return
	 */
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
	
	/**
	 * Returns the current side the active {@link Thread} is running on.
	 * Can either be {@link Side#SERVER} or {@link Side#CLIENT}.
	 * The Editor counts as Client.
	 * @return
	 */
	public static Side getSide()
	{
		Thread thr = Thread.currentThread();
		if(thr.getName().contains("Server"))
		{
			return Side.SERVER;
		}
		return Side.CLIENT;
	}
	
	/**
	 * Parse a {@link BufferedImage} from inside the Jar or from elsewhere.
	 * @param s
	 * @return
	 */
	public static BufferedImage readImage(String s)
	{
		if(s.startsWith("/"))
		{
			try {
				InputStream in = getStreamFromJar(s);
				BufferedImage img = ImageIO.read(in);
				in.close();
				return img;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else
		{
			try {
				return ImageIO.read(new File(s));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static Point conv1Dto2DPoint(int data, double width)
	{
		int xCoord = (int)((double)data % width);
		int yCoord = (int)((double)data / width);

		return new Point(xCoord, yCoord);
	}
	
	public static int conv2Dto1Dint(int x, int y, double width)
	{
		return (int)((double)x + (double)y * width);
	}
	
	public static Dimension conv1Dto2DDim(int data, double width)
	{
		Point p = conv1Dto2DPoint(data, width);
		return new Dimension(p.x, p.y);	
	}
	
	/**
	 * Used reflection to set a field inside a given Object.
	 * @param fieldName
	 * @param value
	 * @param object
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws NumberFormatException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
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
	
	/**
	 * Strips the extension of a file name.
	 * "test.test" becomes "test".
	 * @param str
	 * @return
	 */
	public static String stripExtension(String str) 
	{
        if (str == null) return null;
        int pos = str.lastIndexOf(".");
        if (pos == -1) return str;
        return str.substring(0, pos);
    }
	
	/**
	 * This is a very tricky method. It only returns {@code true}
	 * if you are lucky enough to hit the specified chance.
	 * @param percent
	 * @return
	 */
	public static boolean withChance(int percent)
	{
		Random rand = new Random();
		int chance = rand.nextInt(percent);
		
		if(chance == 0) return true;
		return false;
	}
	
	/**
	 * Returns the {@link Exception#printStackTrace()} inside a handy string.
	 * @param e
	 * @return
	 */
	public static String getStackTrace(Exception e)
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}
	
	/**
	 * Performs a clone of every Object inside an array.
	 * @param source
	 * @param type
	 * @return
	 */
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
	
	/**
	 * Returns the current CPU usage in percent.
	 * @return
	 */
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
	
	/**
	 * Returns the amount of RAM installed. 
	 * @return
	 */
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
	
	/**
	 * Rounds to i2
	 * @param i1
	 * @param i2
	 * @return
	 */
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
	
	/**
	 * Converts an Isometric coordinate to a Cartesian coordinate.
	 * @param p
	 * @return
	 */
	public static Point convIsoToCart(Point p)
	{
		return new Point((2 * p.y + p.x) / 2, (2 * p.y - p.x) / 2);
	}
	
	/**
	 * Converts a Cartesian coordinate to an Isometric coordinate.
	 * @param p
	 * @return
	 */
	public static Point convCartToIso(Point p)
	{
		return new Point(p.x - p.y, (p.x + p.y) / 2);
	}

	public static void copyFileFromJar(String from, String to) throws IOException
	{
		InputStream stream = getStreamFromJar(from);
		FileOutputStream fos = new FileOutputStream(new File(to));
		byte[] buf = new byte[2048];
		int r = stream.read(buf);
		while(r != -1)
		{
			fos.write(buf, 0, r);
			r = stream.read(buf);
		}
		stream.close();
		fos.close();
	}
}
