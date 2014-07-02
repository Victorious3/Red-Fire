package vic.rpg.utils;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Date;

import vic.rpg.Init;
import vic.rpg.utils.Utils.Side;

public class Logger 
{
	private static PrintWriter writer;
	
	@Init(side = Side.BOTH)
	public static void init()
	{
		try {
			writer = new PrintWriter(Utils.getOrCreateFile(Utils.getAppdata() + "/log.txt"));
			writer.write("");
			writer.append("<<<RedFire log of " + new Date() + ">>>");
			writer.flush();
			log(LogLevel.FINEST, "Log file sucsessfully created!");
		} catch (FileNotFoundException e) {
			log(LogLevel.SEVERE, "Log file could not be created!");
		}
	}
	
	public static enum LogLevel
	{
		FINE("INFO"), FINEST("FINEST"), WARNING("WARNING"), SEVERE("SEVERE"), DEBUG("DEBUG");
		
		private String name;
		private LogLevel(String name)
		{
			this.name = name;
		}
		
		@Override
		public String toString() 
		{
			return name;
		}
	}
	
	public static void log()
	{
		log(LogLevel.FINE, "");
	}
	
	public static void log(LogLevel level)
	{
		log(level, "");
	}
	
	public static void log(String log)
	{
		log(LogLevel.FINE, log);
	}
	
	public static void log(LogLevel level, String log)
	{
		log = "[" + Utils.getSide() + "][" + level + "]:" + log;
		if(level == LogLevel.SEVERE) System.err.println(log);
		else System.out.println(log);
		if(level != LogLevel.DEBUG && writer != null)
		{
			writer.append(log + "\n");
			writer.flush();
		}
	}
}
