package vic.rpg.render;

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.NoSuchElementException;

import javax.media.opengl.GL2;

import vic.rpg.Game;
import vic.rpg.Init;
import vic.rpg.registry.LanguageRegistry;
import vic.rpg.utils.Utils.Side;

import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.texture.Texture;

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
	
	public static void setGL(GL2 gl2)
	{
		DrawUtils.gl2 = gl2;
	}
	
	public static void setLineWidth(float width)
	{
		LINE_WIDTH = width;
	}
	
	public static void setFont(Font f)
	{
		FONT = f;
		if(!textRenderers.containsKey(FONT))
		{
			textRenderers.put(FONT, new TextRenderer(FONT));
		}
	}
	
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
	
	public static void drawPoint(int x, int y, Color color)
	{
		gl2.glPushMatrix();
		gl2.glColor4f(getR(color), getG(color), getB(color), getA(color));
		gl2.glBegin(GL2.GL_POINTS);
		gl2.glVertex2i(x, y);
		gl2.glEnd();
		gl2.glPopMatrix();
	}
	
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
	
	public static void drawString(int x, int y, String string, Color color)
	{
		drawString(x, y, string, color, true);
	}
	
	private static int drawString(int x, int y, String string, Color color, boolean draw) 
	{		
		int x2 = x;
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
						}
						else
						{
							if(draw) drawUnformattedString(x2, y, LanguageRegistry.getTranslation(control), color);
							x2 += FONT.getStringBounds(LanguageRegistry.getTranslation(control), getTextRenderer().getFontRenderContext()).getWidth();
						}
					
						if(rString.length() > 0)
						{
							if(draw) drawUnformattedString(x2, y, rString, color);
							x2 += FONT.getStringBounds(rString, getTextRenderer().getFontRenderContext()).getWidth();				
						}
					}
					else
					{
						String control = subString.substring(0, 1);
						String rString = subString.substring((i != 0 ? 1 : 0), subString.length());
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
						}
						if(rString.length() > 0)
						{
							if(draw) drawUnformattedString(x2, y, rString, color);
							x2 += FONT.getStringBounds(rString, getTextRenderer().getFontRenderContext()).getWidth();
						}
					}
				}
				i++;
			}
			setFont(getFont().deriveFont(Font.PLAIN));
		}
		else 
		{
			if(draw) drawUnformattedString(x2, y, string, color);
			x2 += FONT.getStringBounds(string, getTextRenderer().getFontRenderContext()).getWidth();
		}
		return x2 - x;
	}
	
	private static String addString(String a, String b, Color color, Integer style)
	{
		for(int i = 0; i < b.length(); i++)
		{
			String sub = b.substring(i, i + 1);
			a += "#" + (color == null ? -1 : color.getRGB()) + "#" + (style == null ? -1 : style) + "#" + sub + ";";
		}
		return a;
	}
	
	//TODO Not sure how to use this yet.
	public static String processFormattedString(String string)
	{
		String newString = "";
		Color color = null;
		Integer style = null;
		
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
								color = sControl.format(argument, rString, color, 0, 0);
							} catch (Exception e) {
								
							}
						}
						else
						{
							newString = addString(newString, LanguageRegistry.getTranslation(control), color, style);
						}
					
						if(rString.length() > 0)
						{
							newString = addString(newString, rString, color, style);		
						}
					}
					else
					{
						String control = subString.substring(0, 1);
						String rString = subString.substring((i != 0 ? 1 : 0), subString.length());
						switch(control)
						{
						case "b" : style = Font.BOLD; break;
						case "i" : style = Font.ITALIC; break;
						case "p" : style = Font.PLAIN; break;
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
						}
						if(rString.length() > 0)
						{
							newString = addString(newString, rString, color, style);
						}
					}
				}
				i++;
			}
		}
		else 
		{
			newString = addString(newString, string, color, style);
		}
		return newString;
	}
	
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
	
	public static int getFormattedStringLenght(String string)
	{
		return drawString(0, 0, string, null, false);
	}
	
	public static void startClip(int x, int y, int width, int height)
	{
		gl2.glEnable(GL2.GL_SCISSOR_TEST);
		gl2.glScissor((int) (x * ((double)Game.RES_WIDTH / (double)Game.WIDTH)), (int) ((-y + Game.HEIGHT - height) * ((double)Game.RES_HEIGHT / (double)Game.HEIGHT)), (int) (width * ((double)Game.RES_WIDTH / (double)Game.WIDTH)), (int) (height * ((double)Game.RES_HEIGHT / (double)Game.HEIGHT)));	
	}
	
	public static void glColor(Color color)
	{
		gl2.glColor4f(getR(color), getG(color), getB(color), getA(color));
	}
	
	public static void endClip()
	{
		gl2.glDisable(GL2.GL_SCISSOR_TEST);
	}
	
	public static float getLineWidth()
	{
		return LINE_WIDTH;
	}
	
	public static Font getFont()
	{
		return FONT;
	}
	
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

	public static FPSAnimator createFPSAnimator(double fps, int times)
	{
		if(times > 0) return new FPSAnimator(fps, times);
		else return new FPSAnimator(fps);
	}
	
	public static GradientAnimator createGratientAnimator(int timeMillis, Color start, Color end)
	{
		return new GradientAnimator(timeMillis, start, end);
	}
	
	public static LinearAnimator createLinearAnimator(int timeMillis, long start, long end)
	{
		return new LinearAnimator(timeMillis, start, end);
	}
	
	public static interface Animator
	{
		public boolean animate();
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
			if(!repeat && times >= maxTimes) return false;		
			long currTime = System.currentTimeMillis();
			if(currTime >= lastTime + timePerFrame)
			{
				lastTime = currTime;
				times++;
				return true;
			}
			return false;
		}
		
		public long getTimes()
		{
			return times;
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
		
		public Color getColor()
		{
			return new Color((int)r, (int)g, (int)b, (int)a);
		}
		
		public GradientAnimator reset()
		{
			r = start.getRed();
			g = start.getGreen();
			b = start.getBlue();
			a = start.getAlpha();		
			return this;
		}
		
		public GradientAnimator forward()
		{
			r = rEnd;
			g = gEnd;
			b = bEnd;
			a = aEnd;
			return this;
		}
	}

	public static class LinearAnimator implements Animator
	{	
		private final double end;
			
		private double step;
		private double value;
		
		private static final double FPS = 30D;
		
		private LinearAnimator(int timeMillis, long start, long end)
		{
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
		
		public double getValue()
		{
			return value;
		}
	}
}
