package vic.rpg.level.entity.living;

import java.awt.Rectangle;
import java.awt.geom.Area;
import java.util.HashMap;
import java.util.Map;

import org.jnbt.CompoundTag;
import org.jnbt.StringTag;
import org.jnbt.Tag;

import vic.rpg.item.ItemApple;
import vic.rpg.item.ItemShield;
import vic.rpg.item.ItemSword;
import vic.rpg.level.Editable;
import vic.rpg.render.TextureFX;
import vic.rpg.render.TextureLoader;
import vic.rpg.server.Server;
import vic.rpg.server.packet.Packet20Chat;
import vic.rpg.utils.Utils;

import com.jogamp.opengl.util.texture.Texture;

public class EntityPlayer extends EntityLiving 
{	
	public int step = 0;
	@Editable public String username = "NO_USERNAME";
	public boolean isWalkingBlocked = false;
	
	public static TextureFX[] sprites = new TextureFX[]{new TextureFX("/vic/rpg/resources/character/player_main_4.gif", 10), new TextureFX("/vic/rpg/resources/character/player_main_3.gif", 10), new TextureFX("/vic/rpg/resources/character/player_main_2.gif", 10), new TextureFX("/vic/rpg/resources/character/player_main_1.gif", 10)};	
	public EntityPlayer() 
	{
		super(33, 32);
		this.zLevel = -1;
		if(Utils.getSide().equals(Utils.SIDE_CLIENT)) this.initRender();
	}
	
	@Override
	public void formatInventory() 
	{
		inventory.add(0, 12, 8);
		inventory.add(1);
		inventory.add(2);
		inventory.add(3);
		inventory.add(4);
		inventory.add(5);
		inventory.add(6);
		inventory.add(7);
		inventory.add(8);
		
		inventory.addItem(2, new ItemApple());
		inventory.addToInventory(new ItemSword());
		inventory.addToInventory(new ItemSword());
		inventory.addToInventory(new ItemSword());
		inventory.addToInventory(new ItemShield());
	}

	@Override
	public Area getCollisionBoxes(Area area) 
	{
		area.add(new Area(new Rectangle(xCoord, yCoord, getWidth(), getHeight())));
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
		if(Utils.getSide().equals(Utils.SIDE_SERVER))
		{
			Server.connections.get(this.username).packetHandler.addPacketToSendingQueue(new Packet20Chat(message, username));
		}
	}

	@Override
	public void readFromNBT(CompoundTag tag) 
	{
		super.readFromNBT(tag);
		Map<String, Tag> map = tag.getValue();
		this.username = (String) map.get("username").getValue();
	}

	public void initRender() 
	{
		super.rotatedSprites = Utils.cloneArray(EntityPlayer.sprites, TextureFX.class);
		this.setRotation(0);
	}

	@Override
	public CompoundTag writeToNBT(CompoundTag tag) 
	{
		tag = super.writeToNBT(tag);
		
		StringTag username = new StringTag("username", this.username);
		Map<String, Tag> map = tag.getValue();
		Map<String, Tag> map2 = new HashMap<String, Tag>();
		map2.putAll(map);		
		map2.put("username", username);
		
		tag = new CompoundTag(tag.getName(), map2);	
		return tag;
	}	
}
