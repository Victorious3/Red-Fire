package vic.rpg.level.entity.living;

import java.awt.Point;
import java.awt.geom.Area;
import java.util.HashMap;
import java.util.Map;

import org.jnbt.CompoundTag;
import org.jnbt.IntTag;
import org.jnbt.Tag;

import vic.rpg.gui.GuiIngame;
import vic.rpg.level.Editable;
import vic.rpg.level.Entity;
import vic.rpg.level.entity.EntityEvent;
import vic.rpg.level.entity.living.EntityController.HealthChangeEvent;
import vic.rpg.level.path.Node;
import vic.rpg.level.path.Path;
import vic.rpg.render.TextureFX;
import vic.rpg.render.TextureLoader;
import vic.rpg.server.Server;
import vic.rpg.server.packet.Packet9EntityMoving;
import vic.rpg.utils.Utils;
import vic.rpg.utils.Utils.Side;

import com.jogamp.opengl.util.texture.Texture;

public class EntityLiving extends Entity
{
	public static Texture sprite_unknown = TextureLoader.requestTexture(Utils.readImageFromJar("/vic/rpg/resources/character/unknown.png"));
	
	@Editable public int rotation = 0;
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
	
	public Inventory inventory = new Inventory(this);
	
	public EntityLiving(int width, int height) 
	{
		super(width, height);
	}

	public void setX(int x)
	{
		this.xCoord = x;
	}
	
	@Override
	public void onMouseClicked(int x, int y, EntityPlayer entity, int mouseEvent) 
	{
		GuiIngame.focusedEntity = this;
	}

	public void walkTo(int x, int y, double maxCost) 
	{
		if(Utils.getSide() == Side.CLIENT) return;
		Node begin = Node.fromPoint(new Point(this.xCoord, this.yCoord));
		Node end = Node.fromPoint(new Point(x, y));
		this.curPath = Server.server.serverLoop.pathServer.create(this.levelObj.nodeMap, begin, end, maxCost);
		this.walk = true;		
	}
	
	public void setWalking(boolean isWalking)
	{
		if(Utils.getSide() == Side.SERVER)
		{		
			Server.server.broadcast(new Packet9EntityMoving(this));
		}
		this.isWalking = isWalking;
	}
	
	public boolean isWalking()
	{
		return isWalking;
	}
	
	public Inventory getInventory()
	{
		return inventory;
	}
	
	@Override
	public void readFromNBT(CompoundTag tag, Object... args) 
	{
		super.readFromNBT(tag);
		Map<String, Tag> map = tag.getValue();
		this.rotation = (Integer) map.get("rotation").getValue();
		this.lp = (Integer) map.get("lp").getValue();
		this.max_lp = (Integer) map.get("max_lp").getValue();
		inventory.readFromNBT(tag);
	}

	@Override
	public CompoundTag writeToNBT(CompoundTag tag, Object... args) 
	{
		tag = super.writeToNBT(tag);
		IntTag rotation = new IntTag("rotation", this.rotation);
		IntTag lp = new IntTag("lp", this.lp);
		IntTag max_lp = new IntTag("max_lp", this.max_lp);
		Map<String, Tag> map = tag.getValue();
		Map<String, Tag> map2 = new HashMap<String, Tag>();
		map2.putAll(map);		
		map2.put("rotation", rotation);
		map2.put("lp", lp);
		map2.put("max_lp", max_lp);
		
		tag = new CompoundTag(tag.getName(), map2);
		tag = inventory.writeToNBT(tag);
		
		return tag;
	}
	
	public void formatInventory(){};

	public void setY(int y)
	{
		this.yCoord = y;
	}
	
	public void setSprite(TextureFX fx)
	{
		this.sprite = fx;
		this.setTexture(fx);
	}
	
	public void setRotation(int rotation)
	{
		if(Utils.getSide() == Side.CLIENT) this.setSprite(rotatedSprites[rotation]);
		this.rotation = rotation;
	}
	
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
			
			if(nextX < xCoord) setRotation(1);
			else if(nextX > xCoord) setRotation(2);
			else if(nextY < yCoord) setRotation(3);
			else if(nextY > yCoord) setRotation(0);

			if(nextX > xCoord - speed && nextX < xCoord + speed && nextY > yCoord - speed && nextY < yCoord + speed)
			{
				xCoord = nextX; yCoord = nextY;
				this.walkNow = false; isWalking = false;
			}
			
			Server.server.broadcast(new Packet9EntityMoving(this));
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
	
	public Texture getShortcutImage()
	{
		return sprite_unknown;
	}
	
	@Override
	public void onEventReceived(EntityEvent e) 
	{
		if(e instanceof HealthChangeEvent)
		{
			int lp = (int) e.getData("health");
			this.lp = lp < 0 ? 0 : lp > this.max_lp ? this.max_lp : lp;
		}
		
		super.onEventReceived(e);
	}
}
