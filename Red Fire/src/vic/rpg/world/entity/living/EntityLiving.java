package vic.rpg.world.entity.living;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Area;

import javax.media.opengl.GL2;

import org.jnbt.CompoundTag;

import vic.rpg.client.render.DrawUtils;
import vic.rpg.client.render.Screen;
import vic.rpg.client.render.TextureFX;
import vic.rpg.client.render.TextureLoader;
import vic.rpg.event.Event;
import vic.rpg.gui.GuiIngame;
import vic.rpg.server.Server;
import vic.rpg.server.packet.Packet9EntityMoving;
import vic.rpg.utils.Direction;
import vic.rpg.utils.Utils;
import vic.rpg.utils.Utils.Side;
import vic.rpg.world.Editable;
import vic.rpg.world.entity.Entity;
import vic.rpg.world.entity.living.EntityController.HealthChangeEvent;
import vic.rpg.world.path.Node;
import vic.rpg.world.path.Path;
import vic.rpg.world.path.PathServer;

import com.jogamp.opengl.util.texture.Texture;

/**
 * EntityLivig is every Entity that can move around.
 * @author Victorious3
 */
public abstract class EntityLiving extends Entity
{
	public static Texture sprite_unknown = TextureLoader.requestTexture(Utils.readImage("/vic/rpg/resources/character/unknown.png"));
	
	/**
	 * The rotation of this EntityLiving.
	 */
	public Direction rotation = Direction.NORTH;
	
	@Editable public int lp = 100;
	@Editable public int max_lp = 100;
	
	public TextureFX sprite;
	public TextureFX[] rotatedSprites;
	
	protected boolean isWalking = false;
	protected float speed = 2;
	protected Path curPath;
	
	public boolean walk = false;
	public boolean walkNow = false; //TODO Makes sense, uh?
	private int nextX;
	private int nextY;
	
	public Inventory inventory;
	
	protected EntityLiving(int width, int height) 
	{
		super(width, height);
		inventory = new Inventory(this);
	}

	public void setX(int x)
	{
		this.xCoord = x;
	}
	
	public void setY(int y)
	{
		this.yCoord = y;
	}
	
	@Override
	public void onMouseClicked(int x, int y, EntityPlayer entity, int mouseEvent) 
	{
		GuiIngame.focusedEntity = this;
	}
	
	/**
	 * See {@link #walkTo(int, int, double)}
	 */
	public void walkTo(Point coord, double maxCost)
	{
		walkTo(coord.x, coord.y, maxCost);
	}

	/**
	 * Walk to the given Cartesian Coordinates using the {@link PathServer}. {@code maxCost} indicates
	 * how long the path can be before its getting aborted.
	 * @param x
	 * @param y
	 * @param maxCost
	 */
	public void walkTo(int x, int y, double maxCost) 
	{
		if(Utils.getSide() == Side.CLIENT) return;
		if(x < 0 || y < 0 || x >= mapObj.getWidth() || y >= mapObj.getHeight()) return;
		Node begin = Node.fromPoint(new Point(this.xCoord, this.yCoord));
		Node end = Node.fromPoint(new Point(x, y));
		this.curPath = Server.server.serverLoop.pathServer.create(this.mapObj.nodeMap, begin, end, maxCost);
		this.walk = true;		
	}
	
	public void setWalking(boolean isWalking)
	{
		if(Utils.getSide() == Side.SERVER)
		{		
			Server.server.broadcastLocally(dimension, new Packet9EntityMoving(this));
		}
		this.isWalking = isWalking;
	}
	
	public boolean isWalking()
	{
		return isWalking;
	}
	
	/**
	 * Returns the {@link Inventory} of this EntityLiving.
	 * @return Inventory
	 */
	public Inventory getInventory()
	{
		return inventory;
	}
	
	@Override
	public void readFromNBT(CompoundTag tag, Object... args) 
	{
		super.readFromNBT(tag);
		this.rotation = Direction.getDirection((Integer) tag.getInt("rotation", 0));
		this.lp = tag.getInt("lp", lp);
		this.max_lp = tag.getInt("max_lp", max_lp);
		inventory.readFromNBT(tag);
	}

	@Override
	public CompoundTag writeToNBT(CompoundTag tag, Object... args) 
	{
		tag = super.writeToNBT(tag);
		
		tag.putInt("rotation", this.rotation.getID());
		tag.putInt("lp", this.lp);
		tag.putInt("max_lp", this.max_lp);

		tag = inventory.writeToNBT(tag);
		
		return tag;
	}
	
	/**
	 * Called when a new EntityLiving is created for the first time. Used to add Fields to the {@link Inventory}.
	 */
	public void formatInventory()
	{

	}
	
	/**
	 * Sets the currently rendered to new {@link TextureFX}.
	 * @param fx
	 */
	public void setSprite(TextureFX fx)
	{
		this.sprite = fx;
		this.setTexture(fx);
	}
	
	/**
	 * Sets the rotation to the given {@link Direction}.
	 * @param rotation
	 */
	public void setRotation(Direction rotation)
	{
		if(Utils.getSide() == Side.CLIENT) this.setSprite(rotatedSprites[rotation.getID()]);
		this.rotation = rotation;
	}
	
	@Override
	public void tick()
	{
		if(walk && !walkNow && curPath.isReady)
		{
			if(curPath.isPossible && curPath.hasNext())
			{
				Point p = curPath.next().toPoint();
				this.nextX = p.x;
				this.nextY = p.y;
				this.walkNow = true;
			}
			else
			{
				walk = false;
			}
		}
		
		if(walkNow && Utils.getSide() == Side.SERVER)
		{	
			isWalking = true;
			
			if(nextX < xCoord) setX((int)(xCoord - speed));
			if(nextX > xCoord) setX((int)(xCoord + speed));
			if(nextY < yCoord) setY((int)(yCoord - speed));
			if(nextY > yCoord) setY((int)(yCoord + speed));
			
			if(nextX < xCoord && nextY < yCoord) setRotation(Direction.NORTH);
			else if(nextX < xCoord && nextY > yCoord) setRotation(Direction.WEST);
			else if(nextX > xCoord && nextY < yCoord) setRotation(Direction.EAST);
			else if(nextX > xCoord && nextY > yCoord) setRotation(Direction.SOUTH);
			else if(nextX < xCoord) setRotation(Direction.NORTH_WEST);
			else if(nextX > xCoord) setRotation(Direction.SOUTH_EAST);
			else if(nextY < yCoord) setRotation(Direction.NORTH_EAST);
			else if(nextY > yCoord) setRotation(Direction.SOUTH_WEST);

			if(nextX > xCoord - speed && nextX < xCoord + speed && nextY > yCoord - speed && nextY < yCoord + speed)
			{
				xCoord = nextX; yCoord = nextY;
				this.walkNow = false; isWalking = false;
			}
			
			Server.server.broadcastLocally(dimension, new Packet9EntityMoving(this));
		}
		
		if(Utils.getSide() == Side.CLIENT)
		{
			if(isWalking())
			{
				this.sprite.start();
			}
			else this.sprite.stop();
		}
	}
	
	@Override
	public Area getCollisionBoxes(Area area) 
	{
		return area;
	}
	
	/**
	 * Gets the shortcut texture of this EntityLiving that will be displayed when this Entity has focus.
	 * @return Texture
	 */
	public Texture getShortcutImage()
	{
		return sprite_unknown;
	}
	
	@Override
	public Event onEventReceived(Event e) 
	{
		if(e instanceof HealthChangeEvent)
		{
			int lp = (int)e.getData("health");
			this.lp = lp < 0 ? 0 : lp > this.max_lp ? this.max_lp : lp;
		}
		
		return super.onEventReceived(e);
	}

	@Override
	public void postRender(GL2 gl2) 
	{
		super.postRender(gl2);
		DrawUtils.setGL(gl2);
		Point p = Utils.convCartToIso(new Point(xCoord + Screen.xOffset, yCoord + Screen.yOffset));
		DrawUtils.fillRect(p.x - 40, p.y - 77, 80, 3, Color.red);
		DrawUtils.fillRect(p.x - 40, p.y - 77, (int)(80 * ((float)lp / (float)max_lp)), 3, Color.green);
	}		
}
