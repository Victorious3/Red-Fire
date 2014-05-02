package vic.rpg.render;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.NoSuchElementException;

import javax.media.opengl.GL2;

import vic.rpg.Game;
import vic.rpg.Init;
import vic.rpg.registry.LanguageRegistry;
import vic.rpg.utils.Utils.Side;

import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.texture.Texture;

/**
 * DrawUtils is where all the OpenGl related methods are located used for
 * drawing onto the screen.
 * @author Victorious3
 */
public class DrawUtils 
{
	private static GL2 gl2;
	private static float LINE_WIDTH = 1.0F;
	private static Font FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 10); 	
	private static HashMap<Font, TextRenderer> textRenderers = new HashMap<Font, TextRenderer>();
	private static HashMap<String, StringControl> stringControls = new HashMap<String, StringControl>();

	@Init(side = Side.CLIENT)
	public static void init()
	{
		registerStringControl(new StringControl() 
		{		
			@Override
			public String getName() 
			{			
				return "color";
			}
			
			@Override
			public Color format(String argument, String string, Color color, int x, int y) 
			{
				return new Color(Integer.parseInt(argument.split(",")[0]), Integer.parseInt(argument.split(",")[1]), Integer.parseInt(argument.split(",")[2]));
			}
		});
	}
	
	public static void registerStringControl(StringControl con)
	{
		stringControls.put(con.getName(), con);
	}
	
	/**
	 * Sets the reference to a {@link GL2} context. Should always be called before using DrawUtils.
	 * @param gl2
	 */
	public static void setGL(GL2 gl2)
	{
		DrawUtils.gl2 = gl2;
	}
	
	/**
	 * Sets the thickness of the border.
	 * @param width
	 */
	public static void setLineWidth(float width)
	{
		LINE_WIDTH = width;
	}
	
	/**
	 * Sets the currently used {@link Font} and creates a new {@link TextRenderer}
	 * if necessary.
	 * @param f
	 */
	public static void setFont(Font f)
	{
		FONT = f;
		if(!textRenderers.containsKey(FONT))
		{
			textRenderers.put(FONT, new TextRenderer(FONT));
		}
	}
	
	/**
	 * Draws a solid rectangle with the given color at x|y.
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param color
	 */
	public static void fillRect(int x, int y, int width, int height, Color color)
	{
		gl2.glPushMatrix();
		gl2.glColor4f(getR(color), getG(color), getB(color), getA(color));
		gl2.glBegin(GL2.GL_QUADS);
		gl2.glVertex2i(x, y);
		gl2.glVertex2i(x + width, y);
		gl2.glVertex2i(x + width, y + height);
		gl2.glVertex2i(x, y + height);
		gl2.glEnd();
		gl2.glPopMatrix();
	}
	
	/**
	 * Draws a hollow rectangle with the given color at x|y.
	 * @see #setLineWidth(float)
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param color
	 */
	public static void drawRect(int x, int y, int width, int height, Color color)
	{
		gl2.glPushMatrix();
		gl2.glColor4f(getR(color), getG(color), getB(color), getA(color));
		gl2.glLineWidth(LINE_WIDTH);
		gl2.glBegin(GL2.GL_LINE_LOOP);
		gl2.glVertex2i(x, y);
		gl2.glVertex2i(x + width, y);
		gl2.glVertex2i(x + width, y + height);
		gl2.glVertex2i(x, y + height);
		gl2.glEnd();		
		gl2.glPopMatrix();
	}
	
	/**
	 * Draws a line with from coordinate 1 to coordinate 2 with the given color.
	 * @see #setLineWidth(float)
	 * @param x
	 * @param y
	 * @param x2
	 * @param y2
	 * @param color
	 */
	public static void drawLine(int x, int y, int x2, int y2, Color color)
	{
		gl2.glPushMatrix();
		gl2.glColor4f(getR(color), getG(color), getB(color), getA(color));
		gl2.glLineWidth(LINE_WIDTH);
		gl2.glBegin(GL2.GL_LINES);
		gl2.glVertex2i(x, y);
		gl2.glVertex2i(x2, y2);			
		gl2.glEnd();
		gl2.glPopMatrix();
	}
	
	/**
	 * Draws a simple dot at x|y with the given color.
	 * @see #setLineWidth(float)
	 * @param x
	 * @param y
	 * @param color
	 */
	public static void drawPoint(int x, int y, Color color)
	{
		gl2.glPushMatrix();
		gl2.glColor4f(getR(color), getG(color), getB(color), getA(color));
		gl2.glBegin(GL2.GL_POINTS);
		gl2.glVertex2i(x, y);
		gl2.glEnd();
		gl2.glPopMatrix();
	}
	
	/**
	 * Draws a hollow circle with a given radius and the given color at x|y.
	 * x|y is the center of the circle.
	 * @see #setLineWidth(float)
	 * @param x
	 * @param y
	 * @param radius
	 * @param color
	 */
	public static void drawCircle(int x, int y, int radius, Color color)
	{
		gl2.glPushMatrix();
		gl2.glColor4f(getR(color), getG(color), getB(color), getA(color));
		gl2.glLineWidth(LINE_WIDTH);
		gl2.glBegin(GL2.GL_LINE_LOOP);
		for(int i = 0; i <= 360; i++)
		{
			gl2.glVertex2d(x + Math.sin(i) * radius, y + Math.cos(i) * radius);
		}
		gl2.glEnd();
		gl2.glPopMatrix();
	}
	
	/**
	 * Draws a solid circle with a given radius and the given color at x|y.
	 * x|y is the center of the circle.
	 * @see #setLineWidth(float)
	 * @param x
	 * @param y
	 * @param radius
	 * @param color
	 */
	public static void fillCircle(int x, int y, int radius, Color color)
	{
		gl2.glPushMatrix();
		gl2.glColor4f(getR(color), getG(color), getB(color), getA(color));
		gl2.glBegin(GL2.GL_TRIANGLE_FAN);
		for(int i = 0; i <= 360; i++)
		{
			gl2.glVertex2d(x + Math.sin(i) * radius, y + Math.cos(i) * radius);
		}
		gl2.glEnd();
		gl2.glPopMatrix();
	}
	
	/**
	 * Draws a given {@link Texture} at x|y. The Texture has to be bound to the
	 * OpenGL context to display anything with {@link TextureLoader#requestTexture(java.awt.image.BufferedImage)}
	 * @see TextureLoader#requestTexture(java.awt.image.BufferedImage)
	 * @param x
	 * @param y
	 * @param tex
	 */
	public static void drawTexture(int x, int y, Texture tex)
	{
		if(tex != null && tex.getWidth() > 0 && tex.getHeight() > 0)
		{
			gl2.glEnable(GL2.GL_TEXTURE_2D);
			gl2.glPushMatrix();
			gl2.glColor3f(1.0F, 1.0F, 1.0F);
			gl2.glBindTexture(GL2.GL_TEXTURE_2D, tex.getTextureObject(gl2));
			gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
			gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
			gl2.glBegin(GL2.GL_QUADS);
			gl2.glNormal3i(0, 0, 1);
	        gl2.glTexCoord2i(0, 0);
	        gl2.glVertex2i(x, y);
	        gl2.glTexCoord2i(1, 0);
	        gl2.glVertex2i(x + tex.getWidth(), y);
	        gl2.glTexCoord2i(1, 1);
	        gl2.glVertex2i(x + tex.getWidth(), y + tex.getHeight());
	        gl2.glTexCoord2i(0, 1);
	        gl2.glVertex2i(x, y + tex.getHeight());
	        gl2.glEnd();
			gl2.glPopMatrix();
			gl2.glDisable(GL2.GL_TEXTURE_2D);
		}
	}
	
	/**
	 * Draws only a part of a {@link Texture}. The offset and bounds can be specified. The Texture has to be bound to the
	 * OpenGL context to display anything with {@link TextureLoader#requestTexture(java.awt.image.BufferedImage)}
	 * @see TextureLoader#requestTexture(java.awt.image.BufferedImage)
	 * @param x
	 * @param y
	 * @param texX
	 * @param texY
	 * @param width
	 * @param height
	 * @param tex
	 */
	public static void drawTextureWithOffset(int x, int y, int texX, int texY, int width, int height, Texture tex)
	{
		if(tex != null && tex.getWidth() > 0 && tex.getHeight() > 0)
		{			
			double texX2 = (double)texX / (double)tex.getWidth();
			double texY2 = (double)texY / (double)tex.getHeight();
			
			double texX3 = texX2 + (double)width / (double)tex.getWidth();
			double texY3 = texY2 + (double)height / (double)tex.getHeight();
			
			gl2.glEnable(GL2.GL_TEXTURE_2D);
			gl2.glPushMatrix();
			gl2.glColor3f(1.0F, 1.0F, 1.0F);
			gl2.glBindTexture(GL2.GL_TEXTURE_2D, tex.getTextureObject(gl2));
			gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
			gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
			gl2.glBegin(GL2.GL_QUADS);
			gl2.glNormal3i(0, 0, 1);
			gl2.glTexCoord2d(texX2, texY2);
			gl2.glVertex2i(x, y);
			gl2.glTexCoord2d(texX3, texY2);
			gl2.glVertex2i(x + width, y);
			gl2.glTexCoord2d(texX3, texY3);
			gl2.glVertex2i(x + width, y + height);
			gl2.glTexCoord2d(texX2, texY3);
			gl2.glVertex2i(x, y + height);
			gl2.glEnd();
			gl2.glPopMatrix();
			gl2.glDisable(GL2.GL_TEXTURE_2D);
		}
	}
	
	/**
	 * Draws a {@link Texture} or a {@link TextureFX}.
	 * @see #drawTexture(int, int, Texture)
	 * @see TextureFX#draw(GL2, int, int)
	 * @param x
	 * @param y
	 * @param tex
	 */
	public static void drawTexture(int x, int y, Object tex)
	{
		if(tex instanceof Texture)
		{
			drawTexture(x, y, (Texture)tex);
		}
		else if(tex instanceof TextureFX)
		{
			((TextureFX)tex).draw(gl2, x, y);
		}
	}
	
	/**
	 * Draws a simple line of text without any formatation in the given color.
	 * @param x
	 * @param y
	 * @param string
	 * @param color
	 */
	public static void drawUnformattedString(int x, int y, String string, Color color) 
	{
		TextRenderer tr = textRenderers.get(FONT);
		if(tr == null) throw new NoSuchElementException("No TextRenderer for the font " + FONT.getName() + "! Create one using setFont(FONT)");
		tr.setColor(color);
		tr.beginRendering(Game.WIDTH, Game.HEIGHT);
		tr.draw(string, x, -y + Game.HEIGHT);
		tr.endRendering();
	}
	
	public static interface StringControl
	{
		public Color format(String argument, String string, Color color, int x, int y);
		
		public String getName();
	}
	
	/**
	 * Draws a simple line of text that can be formatted in the given color.
	 * 
	 * Possible formats are:
	 * <ul>
	 * <li>& + int from 1 to 9: Set the color</li>
	 * <li>& + f: <b>fat</b></li>
	 * <li>& + i: <i>italics</i></li>
	 * <li>& + k: plain</li>
	 * <li>&color=red,green,blue#: Set a special color</li>
	 * <li>&string#: Translates the given string to the current language</li>
	 * </ul>
	 * @param x
	 * @param y
	 * @param string
	 * @param color
	 */
	public static void drawString(int x, int y, String string, Color color)
	{
		drawString(x, y, string, color, true, -1);
	}
	
	private static RenderedText drawString(int preX, int x, int y, String string, Color color, int maxWidth, boolean draw, boolean split)
	{
		int x2 = x;
		String finalString = "";
		for(String subString : string.split("(?<=\\s+|,\\s)"))
		{
			double width = FONT.getStringBounds(subString, getTextRenderer().getFontRenderContext()).getWidth();
			if(x2 + width > maxWidth && split)
			{
				x2 = preX;
				y += FONT.getSize();
				if(draw) drawUnformattedString(x2, y, subString, color);
				x2 += width;
				finalString += "\n" + subString;
			}
			else
			{
				if(draw) drawUnformattedString(x2, y, subString, color);
				x2 += width;
				finalString += subString;
			}
		}
		return new RenderedText(x2, y, finalString);
	}
	
	private static Color fontColor = Color.white;
	private static boolean fontLock = false;
	
	/**
	 * Used to lock the color and the font style for the text rendering.
	 * @param lockFont
	 */
	public static void lockFont(boolean lockFont)
	{
		fontLock = lockFont;
	}
	
	private static RenderedText drawString(int x, int y, String string, Color color, boolean draw, int maxWidth) 
	{		
		int x2 = x;
		int y2 = y;
		String finalString = "";
		
		if(fontLock)
		{
			color = fontColor;
		}
		
		if(string.contains("&"))
		{
			String[] subStrings = string.split("&");	
			int i = 0;
			for(String subString : subStrings)
			{
				if(subString.length() > 0)
				{
					if(subString.contains("#"))
					{
						String control = subString.split("#")[0];
						String rString = "";
						if(subString.split("#").length > 1) rString = subString.split("#")[1];
						
						if(control.contains("="))
						{
							String controlName = control.split("=")[0];
							String argument = control.split("=")[1];
							StringControl sControl = stringControls.get(controlName);
							if(sControl == null) continue;
							try {
								color = sControl.format(argument, rString, color, x2, y);
							} catch (Exception e) {
								
							}
							
							if(rString.length() > 0)
							{
								RenderedText i2 = drawString(x, x2, y2, rString, color, x + maxWidth, draw, maxWidth > 0);
								x2 = (int)(x + i2.getDimension().getWidth());
								y2 = (int)(x + i2.getDimension().getHeight());
								finalString += "&" + control + "#" + i2.getText();
							}
							else
							{
								finalString += "&" + control + "#";
							}
						}
						else
						{
							RenderedText i2 = drawString(x, x2, y2, LanguageRegistry.getTranslation(control), color, x + maxWidth, draw, maxWidth > 0);
							x2 = (int)(x + i2.getDimension().getWidth());
							y2 = (int)(x + i2.getDimension().getHeight());
							finalString += "&" + control + "#";
						}
					}
					else
					{
						String control = subString.substring(0, 1);
						String rString = subString.substring((i != 0 ? 1 : 0), subString.length());
						
						boolean valid = true;
						switch(control)
						{
							case "b" : setFont(getFont().deriveFont(Font.BOLD)); break;
							case "i" : setFont(getFont().deriveFont(Font.ITALIC)); break;
							case "p" : setFont(getFont().deriveFont(Font.PLAIN)); break;
							case "0" : color = Color.black; break;
							case "1" : color = Color.white; break;
							case "2" : color = Color.blue; break;
							case "3" : color = Color.green; break;
							case "4" : color = Color.red; break;
							case "5" : color = new Color(218, 165, 032); break;
							case "6" : color = Color.yellow; break;
							case "7" : color = Color.pink; break;
							case "8" : color = Color.gray; break;
							case "9" : color = Color.darkGray; break;
							default: valid = false; break;
						}
						
						if(rString.length() > 0)
						{
							RenderedText i2 = drawString(x, x2, y2, rString, color, x + maxWidth, draw, maxWidth > 0);
							x2 = (int)(x + i2.getDimension().getWidth());
							y2 = (int)(x + i2.getDimension().getHeight());
							if(valid) finalString += "&" + control; 
							finalString += i2.getText();
						}
						else
						{
							if(valid) finalString += "&" + control;
						}
					}
				}
				i++;
			}
		}
		else 
		{
			RenderedText i2 = drawString(x, x2, y2, string, color, x + maxWidth, draw, maxWidth > 0);
			x2 = (int)(x + i2.getDimension().getWidth());
			y2 = (int)(x + i2.getDimension().getHeight());
			finalString += i2.getText();
		}
		
		fontColor = color;
		if(!fontLock) setFont(getFont().deriveFont(Font.PLAIN));
		
		return new RenderedText(x2 - x, y2 - y + FONT.getSize(), finalString);
	}
	
	public static class RenderedText
	{
		private final int width;
		private final int height;
		private final String text;
		
		private RenderedText(int width, int height, String text)
		{
			this.width = width;
			this.height = height;
			this.text = text;
		}
		
		public Dimension getDimension()
		{
			return new Dimension(width, height);
		}
		
		public String getText()
		{
			return text;
		}
		
		public String[] getLineSeperatedText()
		{
			return text.split("\n");
		}
	}
	
	/**
	 * Draws formatted text and automatically wraps the text at {@code maxWidth}.
	 * For a list of all available formats check
	 * {@link #drawString(int, int, String, Color)}.
	 * @param x
	 * @param y
	 * @param string
	 * @param color
	 * @param draw
	 * @param maxWidth
	 * @return Dimension
	 */
	public static void drawString(int x, int y, String string, Color color, int maxWidth)
	{
		drawString(x, y, string, color, true, maxWidth);
	}
	
	/**
	 * Returns a new String with all formatation being wiped.
	 * @param s
	 * @return String
	 */
	public static String removeFormatation(String s)
	{
		if(!s.contains("&")) return s;
		String[] subStrings = s.split("&");
		String out = "";
		
		for(int i = 0; i < subStrings.length; i++)
		{
			String s2 = subStrings[i];
			if(s2.length() > 0)
			{
				if(s2.contains("#"))
				{
					s2 = s2.split("#")[1];
				}
				else if(i != 0)
				{
					s2 = s2.substring(1, s2.length());
				}
				out += s2;
			}
		}
		return out;
	}
	
	public static int getFormattedStringLenght(String string, int maxLenght)
	{
		return drawString(0, 0, string, null, false, maxLenght).width;
	}
	
	public static int getFormattedStringLenght(String string)
	{
		return drawString(0, 0, string, null, false, -1).width;
	}
	
	public static int getFormattedStringHeight(String string, int maxLenght)
	{
		return drawString(0, 0, string, null, false, maxLenght).height;
	}
	
	public static int getFormattedStringHeight(String string)
	{
		return drawString(0, 0, string, null, false, -1).height;
	}
	
	public static RenderedText getFormattedStringMetrics(String string, int maxLenght)
	{
		return drawString(0, 0, string, null, false, maxLenght);
	}
	
	public static RenderedText getFormattedStringMetrics(String string)
	{
		return drawString(0, 0, string, null, false, -1);
	}
	
	/**
	 * See {@link Graphics2D#clipRect(int, int, int, int)}
	 * @see #endClip()
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public static void startClip(int x, int y, int width, int height)
	{
		gl2.glEnable(GL2.GL_SCISSOR_TEST);
		gl2.glScissor((int) (x * ((double)Game.RES_WIDTH / (double)Game.WIDTH)), (int) ((-y + Game.HEIGHT - height) * ((double)Game.RES_HEIGHT / (double)Game.HEIGHT)), (int) (width * ((double)Game.RES_WIDTH / (double)Game.WIDTH)), (int) (height * ((double)Game.RES_HEIGHT / (double)Game.HEIGHT)));	
	}
	
	/**
	 * Does {@link GL2#glColor4f(float, float, float, float)} with a {@link Color}.
	 * @param color
	 */
	public static void glColor(Color color)
	{
		gl2.glColor4f(getR(color), getG(color), getB(color), getA(color));
	}
	
	/**
	 * Stops clipping.
	 */
	public static void endClip()
	{
		gl2.glDisable(GL2.GL_SCISSOR_TEST);
	}
	
	/**
	 * Returns the thickness of the border.
	 * @see #setLineWidth(float)
	 * @return Float
	 */
	public static float getLineWidth()
	{
		return LINE_WIDTH;
	}
	
	/**
	 * Returns the currently active {@link Font}
	 * @return
	 */
	public static Font getFont()
	{
		return FONT;
	}
	
	/**
	 * Returns the currently active {@link TextRenderer}
	 * @return
	 */
	public static TextRenderer getTextRenderer()
	{
		TextRenderer tr = textRenderers.get(FONT);
		if(tr == null) throw new NoSuchElementException("No TextRenderer for the font " + FONT.getName() + "! Create one using setFont(FONT)");
		return tr;
	}
	
	public static float getR(Color color)
	{
		return color.getRed() / 255.0F;
	}
	
	public static float getG(Color color)
	{
		return color.getGreen() / 255.0F;
	}
	
	public static float getB(Color color)
	{
		return color.getBlue() / 255.0F;
	}
	
	public static float getA(Color color)
	{
		return color.getAlpha() / 255.0F;
	}
	
	/**
	 * Calls {@link Animator#animate()} on every {@link Animator} in the given list.
	 * @param animators
	 */
	public static void animate(Animator... animators)
	{
		for(Animator ani : animators)
		{
			ani.animate();
		}
	}

	/**
	 * Creates a new {@link Animator} that caps the framerate on the given FPS and repeats for times.
	 * If times is set to 0, it loops infinitely.
	 * @param fps
	 * @param times
	 * @return FPSAnimator
	 */
	public static FPSAnimator createFPSAnimator(double fps, int times)
	{
		if(times > 0) return new FPSAnimator(fps, times);
		else return new FPSAnimator(fps);
	}
	
	/**
	 * Creates a new {@link Animator} that interpolates between two given {@link Color Colors}. It takes the specified
	 * time to complete.
	 * @param fps
	 * @param times
	 * @return GradientAnimator
	 */
	public static GradientAnimator createGratientAnimator(int timeMillis, Color start, Color end)
	{
		return new GradientAnimator(timeMillis, start, end);
	}
	
	/**
	 * Creates a new {@link Animator} that interpolates between two numbers. It takes the specified
	 * time to complete.
	 * @param timeMillis
	 * @param start
	 * @param end
	 * @return LinearAnimator
	 */
	public static LinearAnimator createLinearAnimator(int timeMillis, long start, long end)
	{
		return new LinearAnimator(timeMillis, start, end);
	}
	
	/** 
	 * Creates a new {@link Animator} that follows the given set of {@link Animator Animators}.
	 * Accepted Animators are: {@link LinearAnimator} and {@link PauseAnimator}.</br></br>
	 * <b>The first and the last Animator in the list has to be of the Type {@link LinearAnimator}!</b>
	 * @param animators
	 * @return CascadeAnimator
	 */
	public static CascadeAnimator createCascadeAnimator(Animator... animators)
	{
		return new CascadeAnimator(animators);
	}
	
	/**
	 * An Animator is providing an easy way to animate stuff.
	 * @author Victorious3
	 *
	 */
	public static interface Animator
	{
		public boolean animate();
		
		/**
		 * Sets the Animator to its original value
		 * @return this
		 */
		public Animator reset();
		
		/**
		 * Sets the Animator to its final value
		 * @return this
		 */
		public Animator forward();
		
		/**
		 * Indicates weather this Animator has reached its final value.
		 * @return
		 */
		public boolean hasFinished();
	}
	
	private static class PauseAnimator implements Animator
	{
		private final double start = 0;
		private final double end = 100;
		private final double step;
		
		private double value;
		
		private static final double FPS = 30D;
		
		private PauseAnimator(int timeMillis)
		{
			double timePerFrame = 0.001 * (double)FPS;	
			step = (end - value) / (timePerFrame * (double)timeMillis);
		}
		
		private FPSAnimator animator = new FPSAnimator(FPS);
		
		@Override
		public boolean animate() 
		{
			if(animator.animate())
			{
				double newVal = value + step;
				if(newVal <= end) value = newVal;
				else value = end;
				return false;
			}
			return true;
		}

		@Override
		public Animator reset() 
		{
			value = start;
			return this;
		}

		@Override
		public Animator forward() 
		{
			value = end;
			return this;
		}

		@Override
		public boolean hasFinished() 
		{
			return value >= end;
		}		
	}
	
	public static class CascadeAnimator implements Animator
	{
		private ArrayList<Animator> list = new ArrayList<Animator>();
		private int pointer = 0;
		private double value = 0;
		
		private CascadeAnimator(Animator... animators)
		{
			value = ((LinearAnimator)animators[0]).value;
			list.addAll(Arrays.asList(animators));
		}
		
		/**
		 * Creates a new {@link PauseAnimator} that does sleep for the given time interval.
		 * @param timeMillis
		 * @return PauseAnimator
		 */
		public static PauseAnimator generateBreak(int timeMillis)
		{
			return new PauseAnimator(timeMillis);
		}
		
		@Override
		public boolean animate() 
		{
			if(!hasFinished())
			{
				list.get(pointer).animate();
				if(list.get(pointer) instanceof LinearAnimator)
				{
					value = ((LinearAnimator)list.get(pointer)).value;
				}			
				if(list.get(pointer).hasFinished())
				{
					pointer++;
				}		
				return true;
			}
			return false;
		}

		@Override
		public Animator reset() 
		{
			for(Animator ani : list)
			{
				ani.reset();
			}
			pointer = 0;
			value = ((LinearAnimator)list.get(0)).value;
			return this;
		}

		@Override
		public Animator forward() 
		{	
			for(Animator ani : list)
			{
				ani.forward();
			}
			pointer = list.size() - 1;
			value = ((LinearAnimator)list.get(list.size() - 1)).value;
			return this;
		}

		@Override
		public boolean hasFinished() 
		{
			return pointer == list.size();
		}
		
		public double getValue()
		{
			return value;
		}
	}
	
	public static class FPSAnimator implements Animator
	{
		private final boolean repeat;
		private final int maxTimes;
		private final double timePerFrame;
		
		private FPSAnimator(double fps)
		{
			this.timePerFrame = 1000D / fps;
			this.repeat = true;
			this.maxTimes = 0;
			lastTime = System.currentTimeMillis();
		}
		
		private FPSAnimator(double fps, int times)
		{
			this.timePerFrame = 1000D / fps;
			this.repeat = false;
			this.maxTimes = times;
			lastTime = System.currentTimeMillis();
		}
		
		private long lastTime;
		private long times = 0;
		
		@Override
		public boolean animate()
		{
			if(!hasFinished()) return false;		
			long currTime = System.currentTimeMillis();
			if(currTime >= lastTime + timePerFrame)
			{
				lastTime = currTime;
				times++;
				return true;
			}
			return false;
		}
		
		/**
		 * Returns how much this Animator already looped.
		 * @return
		 */
		public long getTimes()
		{
			return times;
		}

		@Override
		public Animator reset() 
		{
			times = 0;
			return this;
		}

		@Override
		public Animator forward() 
		{
			times = maxTimes;
			return this;
		}

		@Override
		public boolean hasFinished() 
		{
			return repeat && times >= maxTimes;
		}
	}
	
	public static class GradientAnimator implements Animator
	{
		private final Color start;
		
		private double r;
		private double g;
		private double b;
		private double a;
		private final double rEnd;
		private final double gEnd;
		private final double bEnd;
		private final double aEnd;
		private double rStep;
		private double gStep;
		private double bStep;
		private double aStep;
		
		private static final double FPS = 30D;
		
		private GradientAnimator(int timeMillis, Color start, Color end)
		{
			r = start.getRed();
			g = start.getGreen();
			b = start.getBlue();
			a = start.getAlpha();
			rEnd = end.getRed();
			gEnd = end.getGreen();
			bEnd = end.getBlue();
			aEnd = end.getAlpha();
			
			double timePerFrame = 0.001 * (double)FPS;
			
			rStep = (rEnd - r) / (timePerFrame * (double)timeMillis);
			gStep = (gEnd - g) / (timePerFrame * (double)timeMillis);
			bStep = (bEnd - b) / (timePerFrame * (double)timeMillis);
			aStep = (aEnd - a) / (timePerFrame * (double)timeMillis);
			
			this.start = start;
		}
		
		private FPSAnimator animator = new FPSAnimator(FPS);		
		@Override
		public boolean animate() 
		{	
			if(animator.animate())
			{
				double newR = r + rStep;
				double newG = g + gStep;
				double newB = b + bStep;
				double newA = a + aStep;

				if((rStep < 0 && newR >= rEnd) || (rStep > 0 && newR <= rEnd)) r = newR;
				if((gStep < 0 && newG >= gEnd) || (gStep > 0 && newG <= gEnd)) g = newG;
				if((bStep < 0 && newB >= bEnd) || (bStep > 0 && newB <= bEnd)) b = newB;
				if((aStep < 0 && newA >= aEnd) || (aStep > 0 && newA <= aEnd)) a = newA;
			}
			return true;
		}
		
		/**
		 * Returns the current color.
		 * @return
		 */
		public Color getColor()
		{
			return new Color((int)r, (int)g, (int)b, (int)a);
		}
		
		/**
		 * Sets the color to the initial color.
		 * @return
		 */
		@Override
		public GradientAnimator reset()
		{
			r = start.getRed();
			g = start.getGreen();
			b = start.getBlue();
			a = start.getAlpha();		
			return this;
		}
		
		/**
		 * Sets the color to the final color.
		 * @return
		 */
		@Override
		public GradientAnimator forward()
		{
			r = rEnd;
			g = gEnd;
			b = bEnd;
			a = aEnd;
			return this;
		}

		@Override
		public boolean hasFinished() 
		{
			return r == rEnd && g == gEnd && b == bEnd && a == aEnd;
		}
	}

	public static class LinearAnimator implements Animator
	{	
		private final double end;
		private final double start;		
		private final double step;
		
		private double value;
		
		private static final double FPS = 30D;
		
		private LinearAnimator(int timeMillis, long start, long end)
		{
			this.start = start;
			this.end = end;
			this.value = start;
			
			double timePerFrame = 0.001 * (double)FPS;	
			step = (end - value) / (timePerFrame * (double)timeMillis);
			
		}
		
		private FPSAnimator animator = new FPSAnimator(FPS);
		@Override
		public boolean animate() 
		{
			if(animator.animate())
			{
				double newVal = value + step;
				if(step < 0)
				{
					if(newVal >= end) value = newVal;
					else value = end;
				}
				if(step > 0)
				{
					if(newVal <= end) value = newVal;
					else value = end;
				}
			}
			return true;
		}
		
		/**
		 * Returns the current value.
		 * @return
		 */
		public double getValue()
		{
			return value;
		}

		@Override
		public Animator reset() 
		{
			value = start;
			return this;
		}

		@Override
		public Animator forward() 
		{
			value = end;
			return this;
		}

		@Override
		public boolean hasFinished() 
		{
			return value == end;
		}
	}
}
