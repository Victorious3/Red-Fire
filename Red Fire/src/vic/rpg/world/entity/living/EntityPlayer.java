package vic.rpg.world.entity.living;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Area;

import javax.media.opengl.GL2;

import org.jnbt.CompoundTag;

import vic.rpg.client.render.DrawUtils;
import vic.rpg.client.render.LightSource;
import vic.rpg.client.render.Screen;
import vic.rpg.client.render.TextureFX;
import vic.rpg.client.render.TextureLoader;
import vic.rpg.item.ItemStack;
import vic.rpg.registry.RenderRegistry;
import vic.rpg.registry.WorldRegistry;
import vic.rpg.server.Server;
import vic.rpg.server.packet.Packet20Chat;
import vic.rpg.utils.Direction;
import vic.rpg.utils.Utils;
import vic.rpg.utils.Utils.Side;
import vic.rpg.world.Editable;

import com.jogamp.opengl.util.texture.Texture;

/**
 * An EntityPlayer is the player controlled by the user.
 * @author Victorious3
 */
public class EntityPlayer extends EntityLiving 
{	
	public int step = 0;
	@Editable public String username = "NO_USERNAME";
	public boolean isWalkingBlocked = false;
	
	public static TextureFX[] sprites = TextureFX.createTextureFXArray("/vic/rpg/resources/character/character.png", 70, 70, 8, 8, 0, 0, 10);
	
	public EntityPlayer() 
	{
		super(70, 70);
		if(Utils.getSide() == Side.CLIENT) this.initRender();
	}
	
	@Override
	public void postRender(GL2 gl2) 
	{
		super.postRender(gl2);
		DrawUtils.setGL(gl2);
		Point p = Utils.convCartToIso(new Point(xCoord + Screen.xOffset, yCoord + Screen.yOffset));
		DrawUtils.setFont(RenderRegistry.RPGFont.deriveFont(18F));
		DrawUtils.drawString(p.x - DrawUtils.getFormattedStringLenght(username) / 2, p.y - getHeight() - 10, username, Color.white);
	}

	@Override
	public void onCreation() 
	{		
		super.onCreation();
		
		inventory.addItemStackGrid(0, 12, 8);
		inventory.addItemStack(1);
		inventory.addItemStack(2);
		inventory.addItemStack(3);
		inventory.addItemStack(4);
		inventory.addItemStack(5);
		inventory.addItemStack(6);
		inventory.addItemStack(7);
		inventory.addItemStack(8);
		
		//Quickbar
		inventory.addItemStack(11);
		inventory.addItemStack(12);
		inventory.addItemStack(13);
		inventory.addItemStack(14);
		inventory.addItemStack(15);
		inventory.addItemStack(16);
		inventory.addItemStack(17);
		inventory.addItemStack(18);
		
		inventory.addSkill(0);
		inventory.addSkill(1);
		inventory.addSkill(2);
		inventory.addSkill(3);
		inventory.addSkill(4);
		inventory.addSkill(5);
		inventory.addSkill(6);
		inventory.addSkill(7);
		inventory.setSkill(8, WorldRegistry.SKILL_HEAL.clone());
		inventory.setSkill(9, WorldRegistry.SKILL_HEAL.clone());
		
		inventory.setItemStack(2, new ItemStack(WorldRegistry.ITEM_APPLE, 16));
		inventory.setItemStack(3, new ItemStack(WorldRegistry.ITEM_PEER, 16));
		inventory.addToInventory(new ItemStack(WorldRegistry.ITEM_SHIELD));
		inventory.addToInventory(new ItemStack(WorldRegistry.ITEM_SWORD));
	}

	@Override
	public Area getCollisionBoxes(Area area) 
	{
		Point p = Utils.convCartToIso(new Point(xCoord, yCoord));
		area.add(new Area(new Rectangle(p.x + 27, p.y + 53, 16, 15)));
		return area;
	}

	public static Texture portrait = TextureLoader.requestTexture(Utils.readImage("/vic/rpg/resources/character/portrait.png"));
	
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
	
	@Override
	public void tick() 
	{	
		super.tick();
	}
	
	public void sendChatMessage(String message, String username)
	{
		if(Utils.getSide() == Side.SERVER)
		{
			Server.getConnections().get(this.username).packetHandler.addPacketToSendingQueue(new Packet20Chat(message, username));
		}
	}

	/**
	 * Initialize the current sprite and the rotation.
	 */
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
	public void readFromNBT(CompoundTag tag, Object... args) 
	{
		super.readFromNBT(tag);
		this.username = tag.getString("username", this.username);
	}

	@Override
	public CompoundTag writeToNBT(CompoundTag tag, Object... args) 
	{
		tag = super.writeToNBT(tag);	
		tag.putString("username", username);	
		return tag;
	}	
}
