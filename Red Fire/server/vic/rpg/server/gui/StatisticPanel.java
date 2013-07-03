package vic.rpg.server.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;

import vic.rpg.utils.Utils;

public class StatisticPanel extends JPanel
{
	public static StatisticPanel instance = new StatisticPanel();
	
	private StatisticPanel(){}
	private ArrayList<Integer[]> values = new ArrayList<Integer[]>();

	@Override
	protected void paintComponent(Graphics g) 
	{
		super.paintComponent(g);
		
		Integer[] i = new Integer[3];
		
		i[0] = (int)(Utils.getCPUUsage() * 100);
		i[1] = (int)(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);
		i[2] = (int)((double)(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / Runtime.getRuntime().maxMemory() * 100);
		
		g.setColor(Color.black);
		g.fillRect(21, 0, 300, 101);
		g.setColor(new Color(0, 120, 10));
		
		for(int i4 = 0; i4 < 27; i4++)
		{
			g.drawLine(i4 * 10 + 30, 0, i4 * 10 + 30, 100);
		}
		
		for(int i4 = 0; i4 < 10; i4++)
		{
			g.drawLine(20, i4 * 10, 300, i4 * 10);
		}
		
		Integer[] cValue = new Integer[]{0, 0, 0};
		
		for(int i2 = values.size() - 300; i2 < values.size(); i2++)
		{
			if(i2 > 0)
			{
				Integer[] i3 = values.get(i2);
				g.setColor(Color.red);
				g.drawLine(i2 - values.size() + 299, 100 - cValue[0], i2 - values.size() + 300, 100 - i3[0]);
				g.setColor(Color.blue);
				g.drawLine(i2 - values.size() + 299, 100 - cValue[2], i2 - values.size() + 300, 100 - i3[2]);
				cValue = i3;
			}
		}
		
		g.setColor(Color.white);
		g.drawLine(0, 100, 300, 100);
		
		g.drawString("CPU: " + i[0] + "%", 25, 20);
		g.drawString("use: " + i[1] + " MB", 25, 30);
		g.drawString("max: " + Runtime.getRuntime().maxMemory() / (1024 * 1024) + " MB", 25, 40);
		g.drawString("all: " + Utils.getDeviceMemory() / (1024 * 1024) + " MB", 25, 50);

		g.setColor(Color.white);
		g.fillRect(0, 0, 20, 101);
		g.setColor(Color.black);
		g.drawString("100", 0, 10);
		g.drawString("0", 12, 100);
		
		g.setColor(Color.red);
		g.drawString("CPU (%)", 200, 20);
		g.setColor(Color.blue);
		g.drawString("RAM (%)", 250, 20);
		
		if(values.size() > 300) values.remove(0);
		values.add(i);
	}
}
