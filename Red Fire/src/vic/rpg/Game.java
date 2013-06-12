package vic.rpg;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.UIManager;

import vic.rpg.client.net.NetHandler;
import vic.rpg.client.packet.PacketHandlerSP;
import vic.rpg.config.Options;
import vic.rpg.gui.Gui;
import vic.rpg.gui.GuiMain;
import vic.rpg.gui.controls.GSlider;
import vic.rpg.level.Level;
import vic.rpg.level.entity.living.EntityPlayer;
import vic.rpg.registry.GameRegistry;
import vic.rpg.registry.LevelRegistry;
import vic.rpg.registry.RenderRegistry;
import vic.rpg.render.Gif;
import vic.rpg.render.Screen;
import vic.rpg.render.TextureFX;
import vic.rpg.server.Server;
import vic.rpg.utils.Utils;

public class Game extends Canvas implements Runnable 
{
	public static String USERNAME = "victorious3";
	
	public static JFrame frame = new JFrame();

	public static final int HEIGHT = 600;
	public static final int WIDTH = 800;
	
	public static int RES_HEIGHT = 600;
	public static int RES_WIDTH = 800;
	
	public static NetHandler netHandler = new NetHandler();
	public static PacketHandlerSP packetHandler = new PacketHandlerSP();
	public static Game game = new Game();	
	public Screen screen;
	
	public BufferedImage img;
		
	private Thread thread;
	public boolean isRunning = false;
	public static int frames = 0;
	public static int fps = 0;
	
	//Game Objects
	public static EntityPlayer thePlayer;
	public static Level level;
	
    public Game()
    {
    	RenderRegistry.bufferImages();
        RenderRegistry.setup();
        new LevelRegistry();
        
        Options.load();
    	
    	screen = new Screen(WIDTH, HEIGHT);
        img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

        USERNAME = String.valueOf(new Random().nextLong());
    }
	
    public void stopGame()
    {
    	System.err.println("STOPPING!");
    	Options.safe();
    	
    	if(Utils.getSide().equals(Utils.SIDE_CLIENT))
		{
			RenderRegistry.stop();
		}
    	
    	System.exit(0);
    }
    
    private void render()
    {
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null) {
            createBufferStrategy((int)GSlider.returnValue(1, 10, Options.RENDER_PASSES));
            return;
        }

        Graphics g = bs.getDrawGraphics();
        Graphics2D g2d = (Graphics2D)g;
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, Options.ANTIALASING);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, Options.COLOR_RENDER);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, Options.INTERPOLATION);
        
        g2d.scale((double)RES_WIDTH / (double)WIDTH, (double)RES_HEIGHT / (double)HEIGHT);
        
        g2d.drawImage(img, 0, 0, WIDTH, HEIGHT, null);
        
        if(Gui.currentGui == null)
        {
        	screen.render(g2d); 
        }
        else if(!Gui.currentGui.pauseGame)
        {
        	screen.render(g2d);
        }
        
        g2d.drawImage(screen.img, 0, 0, WIDTH, HEIGHT, null);
              
        screen.postRender(g2d);
        
        g2d.dispose();
        bs.show();
    }
    
	private void tick() 
	{
		screen.tick();
		
		if(Gui.currentGui != null)
        {
			Gui.currentGui.updateGui();
        }
		if(level != null)
		{
			level.tick();
		}		
		if(Gui.currentGui != null)
		{
			if(!Gui.currentGui.pauseGame) TextureFX.tickAll();		
			if(Gui.currentGui.pauseGame) Gif.tickAll();
		}
	}
	
    public void start()
	{
		if(isRunning) return;
		isRunning = true;
		thread = new Thread(this);
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
	
	public static void main(String[] args)
	{		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		game.addMouseListener(GameRegistry.mouse);
		game.addMouseMotionListener(GameRegistry.mouse);
		game.addKeyListener(GameRegistry.key);
		game.addComponentListener(GameRegistry.window);
		
		frame.add(game);
		frame.setSize(WIDTH, HEIGHT);
		frame.setLocationRelativeTo(null);
		frame.setTitle("Red Fire Alpha " + GameRegistry.VERSION);
		
		frame.addWindowListener(new WindowListener() 
		{
			@Override
			public void windowActivated(WindowEvent e) {}

			@Override
			public void windowClosed(WindowEvent e) {}

			@Override
			public void windowClosing(WindowEvent e) 
			{
				if(netHandler.IS_SINGLEPLAYER) Server.server.inputHandler.handleCommand("stop", null);				
				Game.game.stopGame();
			}

			@Override
			public void windowDeactivated(WindowEvent e) {}

			@Override
			public void windowDeiconified(WindowEvent e) {}

			@Override
			public void windowIconified(WindowEvent e) {}

			@Override
			public void windowOpened(WindowEvent e) {}	      
			
		});
		
		frame.setVisible(true);
		
		game.requestFocus();
		game.start();
		
		Gui.setGui(new GuiMain());
	}

	@Override
	public void run() 
	{	
		frames = 0;
		double unprocessedSeconds = 0;
		long previousTime = System.nanoTime();
		double secondsPerTick = 1 / 60.0;
		int tickCount = 0;
		boolean ticked = false;
		
		while(isRunning)
		{
			long currentTime = System.nanoTime();
			long passedTime = currentTime - previousTime;
			previousTime = currentTime;
			unprocessedSeconds += passedTime / 1000000000.0;
			
			while(unprocessedSeconds > secondsPerTick)
			{
				tick();
				unprocessedSeconds -= secondsPerTick;
				ticked = true;
				tickCount++;
				if(tickCount % 60 == 0)
				{
					previousTime += 1000;
					fps = frames;
					frames = 0;
				}
			}
			if(ticked)
			{
				frames++;
				render();
			}
			frames++;
			render();
		}		
	}
}
