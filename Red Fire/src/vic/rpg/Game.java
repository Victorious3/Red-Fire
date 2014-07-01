package vic.rpg;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLException;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;
import javax.swing.UIManager;

import vic.rpg.client.net.NetHandler;
import vic.rpg.client.net.PacketHandlerSP;
import vic.rpg.client.render.DrawUtils;
import vic.rpg.client.render.Screen;
import vic.rpg.client.render.TextureLoader;
import vic.rpg.config.Options;
import vic.rpg.gui.Gui;
import vic.rpg.gui.GuiIngame;
import vic.rpg.gui.GuiMain;
import vic.rpg.registry.GameRegistry;
import vic.rpg.registry.RenderRegistry;
import vic.rpg.server.Server;
import vic.rpg.utils.Logger;
import vic.rpg.utils.Utils;
import vic.rpg.utils.Utils.Side;
import vic.rpg.world.Map;
import vic.rpg.world.entity.living.EntityPlayer;

import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.awt.Screenshot;

/**
 * Main game class
 * @author Victorious3
 */
public class Game extends GLCanvas implements Runnable 
{
	public static String USERNAME = "victorious3";
	
	public static JFrame frame = new JFrame();

	public static final int HEIGHT = 600;
	public static final int WIDTH = 800;
	
	public static int RES_HEIGHT = 600;
	public static int RES_WIDTH = 800;

	public static GLProfile GL_PROFILE;
    public static NetHandler netHandler = new NetHandler();
	public static PacketHandlerSP packetHandler = new PacketHandlerSP();
	public static Game game;	
	public Screen screen;
	
	private Thread thread;
	public Animator GL_ANIMATOR;
	public boolean isRunning = false;
	
	public static boolean TAKE_SCREENSHOT = false;
	private static boolean isUpdating = false;
	
	//Game Objects
	public static String playerUUID;
	public static Map map;
	
	/**
	 * Returns the instance of the currently active player by using {@link #playerUUID}.
	 * If the game hasn't started yet, it returns null.
	 * @return EntityPlayer
	 */
	public static EntityPlayer getPlayer()
	{
		if(Game.map != null && playerUUID != null)
		{
			return (EntityPlayer) Game.map.entityMap.get(playerUUID);
		}
		return null;
	}
	
	/**
	 * Main constructor.
	 * @param glcapabilities
	 */
    public Game(GLCapabilities glcapabilities)
    {
    	super(glcapabilities);
    	RenderRegistry.bufferImages();
        RenderRegistry.setup();  
        Options.load();	 	
    }
    
    /**
     * Used to save the config and do some cleanup work in {@link RenderRegistry#stop()}. It does terminate the application afterwards.
     */
    public synchronized void stopGame()
    {
    	Logger.log("Stopping client...");
    	Options.safe();
    	
    	if(Utils.getSide() == Side.CLIENT)
		{
			RenderRegistry.stop();
		}
    	
    	Logger.log("Client Stopped! Thanks for using our software! (V3.inc)");
    	System.exit(0);
    }
    
    /**
     * Calls all static methods that are marked with {@link Init} and {@link PostInit}
     * to perform some inits like setting up textures.
     * Only searches in the package {@code vic.rpg}.
     */
    public static void init()
    {   	
    	List<Class<?>> cls;
		try {
			cls = ClassFinder.getClasses("vic.rpg", (String)null);
			for(Class<?> c : cls)
			{
				for(Method m : c.getDeclaredMethods())
				{
					if(m.getAnnotation(Init.class) != null && Modifier.isStatic(m.getModifiers()))
					{
						Init init = m.getAnnotation(Init.class);
						if(init.side() == Side.CLIENT || init.side() == Side.BOTH)
						{
							m.setAccessible(true);
							try {
								System.out.println("init: " + c.getName() + "." + m.getName() + "()");
								m.invoke(null, (Object[])null);
							} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
			for(Class<?> c : cls)
			{
				for(Method m : c.getDeclaredMethods())
				{
					if(m.getAnnotation(PostInit.class) != null && Modifier.isStatic(m.getModifiers()))
					{
						PostInit init = m.getAnnotation(PostInit.class);
						if(init.side() == Side.CLIENT || init.side() == Side.BOTH)
						{
							m.setAccessible(true);
							try {
								System.out.println("postinit: " + c.getName() + "." + m.getName() + "()");
								m.invoke(null, (Object[])null);
							} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * Does all the init operations involved in setting up OpenGL,<br> creates the Screen object and calls {@link #init()}
     * @param gl2
     */
    private void init(GL2 gl2)
    {
    	init();
    	
    	gl2.glEnable(GL2.GL_ALPHA_TEST);
    	gl2.glAlphaFunc(GL2.GL_GREATER, 0.1F);
    	gl2.glEnable(GL2.GL_BLEND);
    	gl2.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
    	gl2.glDisable(GL2.GL_DEPTH_TEST);
    	
    	gl2.glMatrixMode(GL2.GL_PROJECTION);
    	gl2.glLoadIdentity();
    	gl2.glViewport(0, 0, WIDTH, HEIGHT);
    	gl2.glOrtho(0, WIDTH, HEIGHT, 0, -1, 1);
    	gl2.glMatrixMode(GL2.GL_MODELVIEW);
    	
    	screen = new Screen(WIDTH, HEIGHT);
    	screen.init(gl2);
		Gui.setGui(new GuiMain());
		start();
		
		GL_ANIMATOR = new Animator();
		GL_ANIMATOR.add(this);
		GL_ANIMATOR.setUpdateFPSFrames(10, null);
		GL_ANIMATOR.start();
    }
    
    /**
     * Main render loop. It does flush the OpenGL context and pauses the 
     * Render Thread if a game tick is in progress.
     * @param gl2
     */
    private synchronized void render(GL2 gl2)
    {
    	if(isUpdating)
    	{
    		try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	
    	gl2.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
    	DrawUtils.setGL(gl2);
    	screen.render(gl2);
    	screen.postRender(gl2);
    	gl2.glFlush();
    	
    	notify();
    }
    
    /**
     * Main update loop. It updates the Screen, the currently active gui and the active map. 
     * It triggers {@link #isUpdating} to inform the Render Thread that a game tick is in progress.
     */
	private synchronized void tick() 
	{
		isUpdating = true;
		screen.tick();

		if(Gui.currentGui != null)
        {
			Gui.currentGui.updateGui();
        }
		if(map != null)
		{
			map.tick();
		}	
		isUpdating = false;
	}
	
	/**
	 * Creates and starts the Update Thread "Game".
	 */
    public void start()
	{
		if(isRunning) return;
		isRunning = true;
		thread = new Thread(this);
		thread.setName("Game");
		thread.start();
	}
	
    /**
     * Stops the Update Thread and terminates the application. <b>Not in use anymore</b>.
     */
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
	
	/**
	 * Main void for the Client. Does a lot of initialization like creating the OpenGL Animator.
	 * @param args
	 */
	public static void main(String[] args)
	{		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Welcome to the RedFire alpha! Feel free to redistribute this software in binary form.");
		System.out.println("Please report bugs to our gitHub page at https://github.com/Victorious3/Red-Fire/");
		System.out.println("WARNING: There is no guaranty that this software will run as intended on your PC. Use it on your own risk!");
		System.out.println(GLProfile.glAvailabilityToString());
		
		try {
			GL_PROFILE = GLProfile.get(GLProfile.GL2);
		} catch (GLException e) {
			System.err.println("Woops, looks like your device doesn't support GL2. Let me try something different...");
			try {
				GL_PROFILE = GLProfile.get(GLProfile.GL2GL3);
			} catch (GLException e2) {
				System.err.println("Ehm GL2GL3 is also not supported. That's a really annoying. Sorry but I can't help you.");
				System.exit(-1);
			}
		}
		
        GLCapabilities glcapabilities = new GLCapabilities(GL_PROFILE);
		
        game = new Game(glcapabilities);
        System.out.println(glcapabilities.toString());
        game.setSize(WIDTH, HEIGHT);
        game.addGLEventListener(new GLEventListener() 
        {		
			@Override
			public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {}
			
			@Override public void init(GLAutoDrawable drawable) 
			{
				game.init(drawable.getGL().getGL2());
			}	
			
			@Override public void dispose(GLAutoDrawable drawable) {}
			
			@Override
			public void display(GLAutoDrawable drawable) 
			{
				TextureLoader.setupTextures(drawable.getGL().getGL2());
				if(TAKE_SCREENSHOT)
				{
					BufferedImage screenshot = Screenshot.readToBufferedImage(Game.RES_WIDTH, Game.RES_HEIGHT);
					Date date = new Date();
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-d_HH.mm.ss");
					File file = Utils.getOrCreateFile(Utils.getAppdata() + "/screenshots/" + df.format(date) + ".jpg");
					
					try {
						ImageIO.write(screenshot, "jpg", file);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					if(Gui.currentGui instanceof GuiIngame)
					{
						((GuiIngame) Gui.currentGui).addChatMessage("Screenshot saved to " + file.getName(), "CLIENT");
					}
					TAKE_SCREENSHOT = false;
				}
				game.render(drawable.getGL().getGL2());
			}
		});
		game.addMouseListener(GameRegistry.mouse);
		game.addMouseMotionListener(GameRegistry.mouse);
		game.addMouseWheelListener(GameRegistry.mouse);
		game.addKeyListener(GameRegistry.key);
		game.addComponentListener(GameRegistry.window);
		
		frame.add(game);
		frame.setTitle("Red Fire Alpha " + GameRegistry.VERSION);
		frame.setIconImage(Utils.readImage("/vic/rpg/resources/rf_icon.png"));
		
		frame.addWindowListener(new WindowAdapter() 
		{
			@Override
			public void windowClosing(WindowEvent e) 
			{
				if(netHandler.IS_SINGLEPLAYER) Server.server.inputHandler.handleCommand("stop", null, Server.server);				
				Game.game.stopGame();
			}		
		});
		
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);		
		game.requestFocus();
		
		if(args.length > 0 && args[0] != null)
		{
			Game.USERNAME = args[0];
		}
		else Game.USERNAME = String.valueOf(new Random().nextLong());	
	}

	/**
	 * Main Update Thread. It caps the update frequency at 20TPS. <br>Calls {@link #tick()}. 
	 */
	@Override
	public void run() 
	{	
		double unprocessedSeconds = 0;
		long previousTime = System.nanoTime();
		double secondsPerTick = 1 / 60.0;
		int tickCount = 0;
		
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
				tickCount++;
				if(tickCount % 60 == 0)
				{
					previousTime += 1000;
				}
			}
		}		
	}
}