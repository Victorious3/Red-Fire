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
	protected HashMap<String, Point[]> map;
	protected String name;
	
	public TileMaterial(HashMap<String, Point[]> map, String name)
	{
		this.map = map;
		this.name = name;
	}
	
	public TileMaterial(String name)
	{
		this.map = new HashMap<String, Point[]>();
		this.name = name;
	}
	
	public HashMap<String, Point[]> getSubMaterials()
	{
		return map;
	}
	
	public void addSubMaterial(TileMaterial mat)
	{
		Point[] al = new Point[Direction.getAmount()];
		this.map.put(mat.getName(), al);
	}
	
	public void setTextureCoord(TileMaterial mat, Point texCoord, Direction dir)
	{
		if(!map.containsKey(mat.name))
		{
			addSubMaterial(mat);
		}
		Point[] al = map.get(mat.name);
		al[dir.getID()] = texCoord;
	}
	
	public Point getTextureCoord(TileMaterial outer, Direction direction)
	{
		if(!map.containsKey(outer.getName())) return null;
		return map.get(outer.getName())[direction.getID()];
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
	
	public static HashMap<String, TileMaterial> getMaterials()
	{
		return materials;
	}
	
	public static TileMaterial getMaterial(String name)
	{
		return materials.get(name);
	}
	
	public static void addMaterial(TileMaterial material)
	{
		materials.put(material.getName(), material);
	}
	
	public static void saveMaterials()
	{
		try {
			File dir = Utils.getOrCreateFile(Utils.getAppdata() + "/resources/materials/");
			for(File file: dir.listFiles()) file.delete();
		} catch (Exception e) {
			
		}
		
		for(TileMaterial tm : materials.values())
		{
			try {
				HashMap<String, Tag> materialTag = new HashMap<String, Tag>();
				materialTag.put("name", new StringTag("name", tm.name));
				
				ArrayList<Tag> subMaterialsList = new ArrayList<Tag>();
				for(String name : tm.map.keySet())
				{
					Point[] al1 = tm.map.get(name);
					HashMap<String, Tag> hs1 = new HashMap<String, Tag>();
					hs1.put("name", new StringTag("name", name));
					for(int i = 0; i < al1.length; i++)
					{
						Point p = al1[i];
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
					HashMap<String, Point[]> mats = new HashMap<String, Point[]>();
					
					List<Tag> subMaterialsList = ((ListTag)materialMap.get("subMaterials")).getValue();
					for(Tag t : subMaterialsList)
					{
						Map<String, Tag> subMaterialMap = ((CompoundTag)t).getValue();
						Point[] subMaterialSides = new Point[Direction.getAmount()];
						
						for(int i = 0; i < Direction.getAmount(); i++)
						{
							int x = (Integer) subMaterialMap.get("x" + i).getValue();
							int y = (Integer) subMaterialMap.get("y" + i).getValue();
							subMaterialSides[i] = new Point(x, y);
						}
						
						String subName = (String) subMaterialMap.get("name").getValue();
						mats.put(subName, subMaterialSides);
					}
					
					nbtIN.close();
					System.out.println("Loaded material \"" + name  + "\"");
					materials.put(name, new TileMaterial(mats, name));
				} catch (Exception e) {
					System.err.println("Failed loading material \"" + f.getName() + "\"!");
					e.printStackTrace();
					continue;
				}
			}
				
		} catch (Exception e) {
			System.err.println("Failed loading materials. Aborting...");
		}
	}
}