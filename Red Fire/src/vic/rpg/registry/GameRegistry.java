package vic.rpg.registry;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;

import vic.rpg.listener.Key;
import vic.rpg.listener.Mouse;
import vic.rpg.listener.Window;
import vic.rpg.utils.Utils;

/**
 * Contains the version and some other useful things.
 * @author Victorious3
 */
public class GameRegistry 
{
	public static final String VERSION = "0.5r12";
	
	public static Mouse mouse = new Mouse();
	public static Key key = new Key();
	public static Window window = new Window();
	
	public static Cursor CURSOR_DRAG = Toolkit.getDefaultToolkit().createCustomCursor(Utils.readImage("/vic/rpg/resources/drag.gif"), new Point(0, 0), "CUSTOM_DRAG");
	public static Cursor CURSOR_DROP = Toolkit.getDefaultToolkit().createCustomCursor(Utils.readImage("/vic/rpg/resources/drop.gif"), new Point(0, 0), "CUSTOM_DROP");
}
