package vic.rpg.utils;

public class Vector3
{	
	public int x;
	public int y;
	public int z;
	
	public Vector3(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public boolean equals(Object obj) 
	{
		if(obj instanceof Vector3)
		{
			Vector3 vec = (Vector3)obj;
			return vec.x == x && vec.y == y && vec.z == z;
		}
		return false;
	}
}
