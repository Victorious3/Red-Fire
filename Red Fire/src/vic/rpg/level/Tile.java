package vic.rpg.level;

import vic.rpg.render.Render;

public class Tile
{	
	public int data = 0;
	public int id = 0;
	public Render render = new Render(Level.CELL_SIZE, Level.CELL_SIZE);
	
	public Render getRender(int x, int y, int data){return render;};	
	public void tick(int x, int y, int data){};
	
	//Editor stuff
	public String getDescription(){return "MISSING_DESCRIPTION";}
	
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
