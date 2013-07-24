package vic.rpg.editor.listener;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import vic.rpg.editor.Editor;

public class ZoomListener implements ActionListener 
{
	public Color color;
	
	private int maxZoom = 500;
	private int minZoom = 10;
	
	@Override
	public void actionPerformed(ActionEvent arg0) 
	{
		JComboBox<?> box = (JComboBox<?>) arg0.getSource();	
		String s = box.getSelectedItem().toString();	
		
		try{
			int i = Integer.parseInt(s);
			if(i > maxZoom) i = maxZoom;		
			if(i < minZoom) i = minZoom;
 			s = i + "%";
			box.setSelectedItem(s);
			
		} catch (Exception e) {
			if(!s.matches("[0-9]+%$"))
			{
				box.setSelectedItem("????");
			}
			else
			{
				int i = Integer.parseInt(s.substring(0, s.length() - 1));
				if(i > maxZoom) i = maxZoom;		
				if(i < minZoom) i = minZoom;
	 			s = i + "%";
				box.setSelectedItem(s);
			}
		}
		
		Editor.instance.labelLevel.scale(getZoom(box, Editor.instance.labelLevel.getScale()));
	}
	
	public static float getZoom(JComboBox<?> box, float zoom)
	{
		String s = box.getSelectedItem().toString();
		
		s = s.substring(0, s.length() - 1);
		
		try
		{
			return Integer.parseInt(s) / 100.0F;
		} catch (Exception e) {}
		
		return zoom;
	}
	
	public static void setZoom(JComboBox<?> box, float zoom)
	{
		int i = (int)(zoom * 100);
		String s = i + "%";
		box.setSelectedItem(s);
	}
}


