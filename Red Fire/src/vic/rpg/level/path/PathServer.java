package vic.rpg.level.path;

import java.util.ArrayList;

public class PathServer extends Thread
{
	private ArrayList<Path> processingPaths = new ArrayList<Path>();
	
	public PathServer()
	{
		this.setName("Server PathThread");
		this.setDaemon(true);
		this.start();
	}
	
	public Path create(NodeMap nodeMap, Node begin, Node end, double maxCost)
	{
		Path p = new Path(nodeMap, begin, end, maxCost);
		if(begin.isBlocked || end.isBlocked)
		{
			p.isReady = true;
			p.isPossible = false;
		}
		else processingPaths.add(p);
		return p;
	}
	
	public boolean isRunning = true;
	
	@Override
	public void run() 
	{
		while(isRunning)
		{
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if(processingPaths.size() > 0)
			{
				Path p = processingPaths.get(0);
				processingPaths.remove(0);
				p.isPossible = p.compute();
				p.isReady = true;
			}
		}
	}
}
