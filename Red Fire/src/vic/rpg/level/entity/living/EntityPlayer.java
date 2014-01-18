package vic.rpg.level.entity.living;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Area;

import org.jnbt.CompoundTag;

import vic.rpg.item.ItemApple;
import vic.rpg.level.Editable;
import vic.rpg.render.LightSource;
import vic.rpg.render.TextureFX;
import vic.rpg.render.TextureLoader;
import vic.rpg.server.Server;
import vic.rpg.server.packet.Packet20Chat;
import vic.rpg.utils.Direction;
import vic.rpg.utils.Utils;
import vic.rpg.utils.Utils.Side;

import com.jogamp.opengl.util.texture.Texture;

public class EntityPlayer extends EntityLiving 
{	
	public int step = 0;
	@Editable public String username = "NO_USERNAME";
	public boolean isWalkingBlocked = false;
	
	public static TextureFX[] sprites = TextureFX.createTextureFXArray("/vic/rpg/resources/character/character.png", 70, 70, 8, 8, 0, 0, 10);	
	public EntityPlayer() 
	{
		super(70, 70);
		this.zLevel = -1;
		if(Utils.getSide() == Side.CLIENT) this.initRender();
	}
	
	@Override
	public void formatInventory() 
	{
		inventory = new Inventory(this);
		
		inventory.add(0, 12, 8);
		inventory.add(1);
		inventory.add(2);
		inventory.add(3);
		inventory.add(4);
		inventory.add(5);
		inventory.add(6);
		inventory.add(7);
		inventory.add(8);
		
		//Quickbar
		inventory.add(9);
		inventory.add(10);
		inventory.add(11);
		inventory.add(12);
		inventory.add(13);
		inventory.add(14);
		inventory.add(15);
		inventory.add(16);
		inventory.add(17);
		inventory.add(18);
		
		//TODO addToInventory sends Entity!
		inventory.addItem(2, new ItemApple());
		inventory.addItem(9, new ItemApple());
	}

	@Override
	public Area getCollisionBoxes(Area area) 
	{
		Point p = Utils.convCartToIso(new Point(xCoord, yCoord));
		area.add(new Area(new Rectangle(p.x + 27, p.y + 53, 16, 15)));
		return area;
	}

	public static Texture portrait = TextureLoader.requestTexture(Utils.readImageFromJar("/vic/rpg/resources/character/portrait.png"));
	
	@Override
	public Texture getShortcutImage() 
	{
		return portrait;
	}
	
	@Override
	public String getName() 
	{
		return username;
	}
	
	public int tickCounter = 0;	
	
	public void tick() 
	{	
		super.tick();
	}
	
	public void sendChatMessage(String message, String username)
	{
		if(Utils.getSide() == Side.SERVER)
		{
			Server.connections.get(this.username).packetHandler.addPacketToSendingQueue(new Packet20Chat(message, username));
		}
	}

	@Override
	public void readFromNBT(CompoundTag tag, Object... args) 
	{
		super.readFromNBT(tag);
		this.username = tag.getString("username", this.username);
	}

	public void initRender() 
	{
		this.lightSources.add(new LightSource(500, 1.0F, Color.orange, true));
		super.rotatedSprites = Utils.cloneArray(EntityPlayer.sprites, TextureFX.class);
		this.setRotation(Direction.NORTH);
	}

	@Override
	public Point getLightPosition(LightSource l) 
	{
		if(l == lightSources.get(0))
		{
			return new Point(this.xCoord + getWidth() / 2, this.yCoord);
		}
		return null;
	}

	@Override
	public CompoundTag writeToNBT(CompoundTag tag, Object... args) 
	{
		tag = super.writeToNBT(tag);	
		tag.putString("username", username);	
		return tag;
	}	
}
