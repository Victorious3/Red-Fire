package vic.rpg.level.path;

import java.util.ArrayList;

/**
 * The PathServer generates empty {@link Path Paths} and computes them in sequence.
 * @see Path#compute()
 * @author Victorious3
 */
public class PathServer extends Thread
{
	private ArrayList<Path> processingPaths = new ArrayList<Path>();
	
	public PathServer()
	{
		this.setName("Server PathThread");
		this.setDaemon(true);
		this.start();
	}
	
	/**
	 * Create a new empty {@link Path} which is added to the queue for later processing.
	 * @param nodeMap
	 * @param begin
	 * @param end
	 * @param maxCost
	 * @return Path
	 */
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
				Path p = processingPaths.remove(0);
				if(p == null) continue;
				p.isPossible = p.compute();
				p.isReady = true;
			}
		}
	}
}
