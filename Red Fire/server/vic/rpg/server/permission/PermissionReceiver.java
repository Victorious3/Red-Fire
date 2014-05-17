package vic.rpg.server.permission;

import java.util.ArrayList;

public class PermissionReceiver implements Cloneable
{
	public String name;
	public Permission permission;
	public String prefix;
	public String suffix;
	public ArrayList<String> groups;
	
	public String getPrefix()
	{
		return prefix != null ? prefix : "";
	}
	
	public String getSuffix()
	{
		return suffix != null ? suffix : "";
	}

	@Override
	public PermissionReceiver clone() 
	{
		try {
			return (PermissionReceiver) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
