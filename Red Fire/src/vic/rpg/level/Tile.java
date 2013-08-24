package vic.rpg.level;

import vic.rpg.render.Drawable;

public class Tile
{	
	public int data = 0;
	public int id = 0;
	public Drawable drawable = new Drawable(Level.CELL_SIZE, Level.CELL_SIZE);
	
	public Drawable getDrawable(int x, int y, int data){return drawable;};	
	public void tick(int x, int y, int data){};
	
	//Editor stuff
	public String getDescription(){return "MISSING_DESCRIPTION";}
	
	public double getMovementCost(){return 0;}
	public boolean isWalkingPermitted(){return true;}
	
	@Override
	public Tile clone() 
	{
		try {
			return (Tile) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}		
}
