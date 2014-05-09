package vic.rpg.server.permission;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * <b>Permissions API</b>
 * </br></br>
 * Create an empty permission tree with {@link #createRoot(boolean)}.
 * To add new permissions to the tree, call {@link #add(String...)}.
 * @author Victorious3
 */
public class Permission 
{
	private boolean access = false;
	private ArrayList<Permission> sub = new ArrayList<Permission>();
	private final String name;
	private Permission parent;
	
	/**
	 * Creates a permission tree.
	 * @param perm
	 */
	private Permission(String perm, boolean rootAccess)
	{
		boolean access = perm.startsWith("+");
		access = !perm.startsWith("-");
		
		perm = perm.replaceAll("[-+]", "");
		
		if(perm.length() == 0)
		{
			this.name = "";
			this.access = access;
		}
		else if(perm.contains("."))
		{
			this.name = perm.split("\\.")[0];
			this.access = rootAccess;
			perm = perm.substring(this.name.length() + 1);
			if(!access) perm = "-" + perm;
			Permission p = new Permission(perm, rootAccess);
			p.parent = this;
			sub.add(p);
		}
		else 
		{
			this.access = access;
			this.name = perm;
		}
	}
	
	/**
	 * Creates an empty permission tree.
	 * @param access
	 */
	private Permission(boolean access)
	{
		this.name = "";
		this.access = access;
	}
	
	@Override
	public boolean equals(Object obj) 
	{
		return obj instanceof Permission && ((Permission)obj).name.equals(this.name);
	}

	/**
	 * Adds one or more permissions to this permission tree.
	 * @param permString
	 */
	public void add(String... permString)
	{
		for(String s : permString)
		{
			add(new Permission(s, access));
		}
	}
	
	private void add(Permission perm)
	{
		if(perm.name == "" && this.name == "")
		{
			this.access = perm.access;
			sub.clear();
		}
		else if(perm.equals(this))
		{
			for(Permission perm2 : perm.sub)
			{
				if(sub.contains(perm2)) 
				{
					perm2.parent = this;
					sub.get(sub.indexOf(perm2)).add(perm2);
				}
				else 
				{
					perm2.parent = this;
					sub.add(perm);
				}
			}
		}
		else if(sub.contains(perm))
		{
			if(perm.sub.size() > 0)
			{
				for(Permission perm2 : perm.sub)
				{
					perm2.parent = this;
					sub.get(sub.indexOf(perm)).add(perm2);
				}
			}
			else 
			{
				perm.parent = this;
				sub.set(sub.indexOf(perm), perm);
			}
		}
		else 
		{
			perm.parent = this;
			sub.add(perm);
		}
	}
	
	/**
	 * Checks the permission tree if the given permissions are all set.
	 * @param permString
	 * @return
	 */
	public boolean hasPermission(String... permString)
	{
		boolean access = true;
		for(String s : permString)
		{
			if(!hasPermission(new Permission(s, access))) access = false;
		}
		return access;
	}
	
	private boolean hasPermission(Permission perm)
	{
		if(perm.equals(this))
		{
			if(perm.sub.size() > 0)
			{
				if(sub.contains(perm.sub.get(0)))
				{
					return sub.get(sub.indexOf(perm.sub.get(0))).hasPermission(perm.sub.get(0));
				}
			}
			return access;
		}
		else if(sub.contains(perm))
		{
			return(sub.get(sub.indexOf(perm)).hasPermission(perm));
		}
		return access;
	}

	@Override
	public String toString() 
	{
		String out = "";
		out += access ? "" : "-";
		out += this.name == "" ? "ROOT" : this.name;
		if(sub.size() > 0)
		{
			out += "[";
			Iterator<Permission> iter = sub.iterator();
			while(iter.hasNext())
			{
				out += iter.next().toString();
				out += iter.hasNext() ? ", " : "";
			}
			out += "]";
		}
		return out;
	}
	
	/**
	 * Returns a String containing every permission set in the permission tree.
	 * @return
	 */
	public String getAllPermissions()
	{
		String out = "";
		out += !access ? "-\n" : "+\n";
		for(Permission perm : sub)
		{
			out += perm.getSubPermissions("");
		}
		return out.substring(0, out.length() - 1);
	}
	
	private String getSubPermissions(String permString)
	{
		if(sub.size() > 0)
		{
			if(parent != null && parent.access != this.access) permString += getPermissionName("", true) + "\n";
			for(Permission perm : sub)
			{
				permString = perm.getSubPermissions(permString);
			}
		}
		else
		{
			boolean valid = false;
			Permission perm = this;
			while(perm.parent != null)
			{
				System.out.println(perm + " " + perm.parent);
				if(perm.access != perm.parent.access)
				{
					valid = true;
					break;
				}
				perm = perm.parent;
			}
			if(valid) permString += getPermissionName("", false) + "\n";
		}
		return permString;
	}
	
	private String getPermissionName(String in, boolean force)
	{
		if(parent != null) 
		{
			in = parent.getPermissionName(in, false) + in;
		}
		return ((sub.size() == 0 || force) && !access ? "-" : "") + in + (parent != null && parent.name != "" ? "." : "") + name;
	}

	/**
	 * Returns the state of this permission.
	 * @return
	 */
	public boolean isAccess() 
	{
		return access;
	}
	
	/**
	 * Creates an empty permission tree.
	 * @param access
	 * @return Permission
	 */
	public static Permission createRoot(boolean access)
	{
		return new Permission(access);
	}
}
