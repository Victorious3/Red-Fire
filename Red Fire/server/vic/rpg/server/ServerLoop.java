package vic.rpg.server;

import vic.rpg.level.Level;

public class ServerLoop implements Runnable
{		
	private Thread thread;
	public boolean isRunning = false;
	public static Level level;
	
	public ServerLoop()
	{
		thread = new Thread(this);		
		thread.start();
		
		level = new Level(100, 100, "Test level");
		level.populate();
	}
	
	private void tick() 
	{
		level.tick();
	}
	
    public void start()
	{
		if(isRunning) return;
		isRunning = true;
		thread = new Thread(this);
		thread.setName("Server GameLoop");
		thread.start();
	}
	
	public void stop()
	{
		if(!isRunning) return;
		isRunning = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public void halt()
	{
		if(isRunning)
		{
			isRunning = false;
		}
		else
		{
			isRunning = true;
		}
	}

	@Override
	public void run() 
	{	
		double unprocessedSeconds = 0;
		long previousTime = System.nanoTime();
		double secondsPerTick = 1 / 60.0;
		int tickCount = 0;
		
		while(isRunning && Server.server.isRunning)
		{
			long currentTime = System.nanoTime();
			long passedTime = currentTime - previousTime;
			previousTime = currentTime;
			unprocessedSeconds += passedTime / 1000000000.0;
			
			while(unprocessedSeconds > secondsPerTick)
			{
				tick();
				unprocessedSeconds -= secondsPerTick;
				tickCount++;
				if(tickCount % 60 == 0)
				{
					previousTime += 1000;
				}
			}
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}		
	}
}
