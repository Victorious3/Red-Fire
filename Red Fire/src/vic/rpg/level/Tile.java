package vic.rpg.level;

import vic.rpg.render.Render;

public abstract class Tile
{	
	public int data = 0;
	public int id = 0;
	public Render render = new Render(Level.CELL_SIZE, Level.CELL_SIZE);
	
	public abstract Render getRender(int x, int y, int data);	
	public abstract void tick(int x, int y, int data);
}
