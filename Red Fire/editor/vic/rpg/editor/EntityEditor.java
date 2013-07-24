package vic.rpg.editor;

import javax.swing.JFrame;

import vic.rpg.level.Entity;

public class EntityEditor 
{
	public JFrame frame;
	
	public EntityEditor()
	{
		
	}
	
	public void show(Entity e)
	{
		frame = new JFrame();	

		frame.setSize(800, 600);
		frame.setLocationRelativeTo(Editor.instance.frame);
		frame.setTitle("Entity Editor");
		
		frame.setVisible(true);
	}
	
	public void hide()
	{
		frame.setVisible(false);
	}
}
