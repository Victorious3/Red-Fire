package vic.rpg.registry;

import java.awt.Font;
import java.io.InputStream;

import vic.rpg.render.TextureLoader;
import vic.rpg.utils.Utils;

/**
 * Was once used for saving the int[] arrays of parsed BufferedImages containing the color values for
 * a texture. Not used very often these days.
 * @author Victorious3
 *
 */
@Deprecated
public class RenderRegistry 
{
	public static Font RPGFont;
	
	public static final String IMG_ENTITY_STATIC_HOUSE = "imgterrainentitystatichouse";
	public static final String IMG_ENTITY_STATIC_TREE = "imgterrainentitystatictree";
	public static final String IMG_ENTITY_STATIC_APLTREE = "imgterrainentitystaticapltree";
		
	public static void bufferImages()
	{
		TextureLoader.storeTexture(IMG_ENTITY_STATIC_HOUSE, "/vic/rpg/resources/terrain/house.png");
		TextureLoader.storeTexture(IMG_ENTITY_STATIC_TREE, "/vic/rpg/resources/terrain/tree.png");
		TextureLoader.storeTexture(IMG_ENTITY_STATIC_APLTREE, "/vic/rpg/resources/terrain/apple_tree.png");
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
	
