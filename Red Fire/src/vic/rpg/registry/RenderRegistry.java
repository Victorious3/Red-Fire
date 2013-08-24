package vic.rpg.registry;

import java.awt.Font;
import java.io.InputStream;

import vic.rpg.render.TextureFX;
import vic.rpg.render.TextureLoader;
import vic.rpg.utils.Utils;

public class RenderRegistry 
{
	public static Font RPGFont;
	
	public static final String IMG_TERRAIN_WATER = "imgterrainwater";
	public static final String IMG_TERRAIN_GRASS = "imgterraingrass";
	public static final String IMG_TERRAIN_GRASS_2 = "imgterraingrass2";
	public static final String IMG_ENTITY_STATIC_HOUSE = "imgterrainentitystatichouse";
	public static final String IMG_ENTITY_STATIC_TREE = "imgterrainentitystatictree";
	public static final String IMG_ENTITY_STATIC_APLTREE = "imgterrainentitystaticapltree";
	
	public static TextureFX anim_water;
	
	public static void bufferImages()
	{
		TextureLoader.storeTexture(IMG_TERRAIN_WATER, "/vic/rpg/resources/terrain/waterfx_1.png");
		TextureLoader.storeTexture(IMG_TERRAIN_GRASS, "/vic/rpg/resources/terrain/grass.png");
		TextureLoader.storeTexture(IMG_TERRAIN_GRASS_2, "/vic/rpg/resources/terrain/grass_2.png");
		TextureLoader.storeTexture(IMG_ENTITY_STATIC_HOUSE, "/vic/rpg/resources/terrain/house.png");
		TextureLoader.storeTexture(IMG_ENTITY_STATIC_TREE, "/vic/rpg/resources/terrain/tree.png");
		TextureLoader.storeTexture(IMG_ENTITY_STATIC_APLTREE, "/vic/rpg/resources/terrain/apple_tree.png");
		
		anim_water = new TextureFX("/vic/rpg/resources/terrain/test.gif", 1.5f);
	}

	public static void setup() 
	{	
		try {
			InputStream is = Utils.getStreamFromJar("/vic/rpg/resources/allember.ttf");
			RPGFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(20f);
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void stop() 
	{
		
	}
}
	
