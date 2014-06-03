package vic.rpg.server.command;

import java.util.Arrays;

public class CommandException extends Exception 
{
	private Command command;
	private int mismatch;
	private Class<?>[] expectedArgs;
	private int expectedLength;
	private int length;
	
	private final int type;
	
	public CommandException(Command command, Class<?>[] expectedArgs, int mismatch)
	{
		this.command = command;
		this.expectedArgs = expectedArgs;
		this.mismatch = mismatch;
		this.type = 0;
	}
	
	public CommandException(Command command, int expectedLength, int length)
	{
		this.command = command;
		this.expectedLength = expectedLength;
		this.length = length;
		this.type = 1;
	}
	
	@Override
	public String getMessage() 
	{
		if(type == 0) return "Wrong command types for Command " + command.getName() + " expected " + Arrays.toString(expectedArgs) + ". Caught missmatch on argument " +  mismatch + ".";
		else return "Wrong number of Argumentsfor Command " + command.getName() + " expected " + expectedLength + ", got " + length + ".";
	}	
}
