package vic.rpg.editor.tiles;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jnbt.CompoundTag;
import org.jnbt.IntTag;
import org.jnbt.ListTag;
import org.jnbt.NBTInputStream;
import org.jnbt.NBTOutputStream;
import org.jnbt.StringTag;
import org.jnbt.Tag;

import vic.rpg.utils.Direction;
import vic.rpg.utils.Utils;

public class TileMaterial 
{
	protected HashMap<String, ArrayList<Point>> map;
	protected String name;
	
	public TileMaterial(HashMap<String, ArrayList<Point>> map, String name)
	{
		this.map = map;
		this.name = name;
	}
	
	public Point getTextureCoord(TileMaterial outer, Direction direction)
	{
		return map.get(outer.getName()).get(direction.getID());
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	private static HashMap<String, TileMaterial> materials = new HashMap<String, TileMaterial>();
	
	public static TileMaterial getMaterial(String name)
	{
		return materials.get(name);
	}
	
	/*public static void test()
	{
		HashMap<String, ArrayList<Point>> m1map = new HashMap<String, ArrayList<Point>>(); 
		m1map.put("test2", new ArrayList<Point>(Arrays.asList(new Point[]{new Point(1, 2), new Point(3, 4), new Point(5, 6), new Point(7, 8), new Point(9, 10), new Point(11, 12), new Point(13, 14), new Point(15, 16)})));		
		TileMaterial m1 = new TileMaterial(m1map, "test1");
		
		HashMap<String, ArrayList<Point>> m2map = new HashMap<String, ArrayList<Point>>(); 
		m1map.put("test1", new ArrayList<Point>(Arrays.asList(new Point[]{new Point(1, 2), new Point(3, 4), new Point(5, 6), new Point(7, 8), new Point(9, 10), new Point(11, 12), new Point(13, 14), new Point(15, 16)})));		
		TileMaterial m2 = new TileMaterial(m2map, "test2");
		
		materials.put(m1.name, m1);
		materials.put(m2.name, m2);
		
		System.out.println(m1.getTextureCoord(m2, Direction.EAST));
		
		saveMaterials();
	}*/
	
	public static void saveMaterials()
	{
		for(TileMaterial tm : materials.values())
		{
			try {
				HashMap<String, Tag> materialTag = new HashMap<String, Tag>();
				materialTag.put("name", new StringTag("name", tm.name));
				
				ArrayList<Tag> subMaterialsList = new ArrayList<Tag>();
				for(String name : tm.map.keySet())
				{
					ArrayList<Point> al1 = tm.map.get(name);
					HashMap<String, Tag> hs1 = new HashMap<String, Tag>();
					hs1.put("name", new StringTag("name", name));
					for(int i = 0; i < al1.size(); i++)
					{
						Point p = al1.get(i);
						IntTag x = new IntTag("x" + i, p.x);
						IntTag y = new IntTag("y" + i, p.y);
						hs1.put("x" + i, x);
						hs1.put("y" + i, y);
					}
					subMaterialsList.add(new CompoundTag("material", hs1)); 
				}
				
				materialTag.put("subMaterials", new ListTag("subMaterials", CompoundTag.class, subMaterialsList));
				NBTOutputStream nbtOut = new NBTOutputStream(new FileOutputStream(Utils.getOrCreateFile(Utils.getAppdata() + "/resources/materials/" + tm.name + ".nbt")));		
				nbtOut.writeTag(new CompoundTag("material", materialTag));
				nbtOut.close();
			} catch (Exception e) {
				System.err.println("Failed to save material \"" + tm.name + "\"!");
				continue;
			}
		}
	}
	
	public static void loadMaterials()
	{
		try {
			File files = Utils.getOrCreateFile(Utils.getAppdata() + "/resources/materials/");
			
			for(File f : files.listFiles(new FilenameFilter() 
			{		
				@Override
				public boolean accept(File dir, String name) 
				{
					return name.endsWith(".nbt");
				}
			}))
			{
				try {
					NBTInputStream nbtIN = new NBTInputStream(new FileInputStream(f));
					Map<String, Tag> materialMap = ((CompoundTag)nbtIN.readTag()).getValue();
					
					String name = (String)materialMap.get("name").getValue();
					HashMap<String, ArrayList<Point>> mats = new HashMap<String, ArrayList<Point>>();
					
					List<Tag> subMaterialsList = ((ListTag)materialMap.get("subMaterials")).getValue();
					for(Tag t : subMaterialsList)
					{
						Map<String, Tag> subMaterialMap = ((CompoundTag)t).getValue();
						ArrayList<Point> subMaterialSides = new ArrayList<Point>();
						
						for(int i = 0; i < 8; i++)
						{
							int x = (Integer) subMaterialMap.get("x" + i).getValue();
							int y = (Integer) subMaterialMap.get("y" + i).getValue();
							subMaterialSides.add(new Point(x, y));
						}
						
						String subName = (String) subMaterialMap.get("name").getValue();
						mats.put(subName, subMaterialSides);
					}
					
					nbtIN.close();			
					materials.put(name, new TileMaterial(mats, name));
				} catch (Exception e) {
					System.err.println("Failed loading material \"" + f.getName() + "\"!");
					continue;
				}
			}
				
		} catch (Exception e) {
			System.err.println("Failed loading materials. Abortinig...");
		}
	}
	
	public static void editMaterials()
	{
		
	}
}