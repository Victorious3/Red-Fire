package vic.rpg.render;

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.NoSuchElementException;

import javax.media.opengl.GL2;

import vic.rpg.Game;

import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.texture.Texture;

public class DrawUtils 
{
	private static GL2 gl2;
	private static float LINE_WIDTH = 1.0F;
	private static Font FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 10); 	
	private static HashMap<Font, TextRenderer> textRenderers = new HashMap<Font, TextRenderer>();
	
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
	
	public static void drawString(int x, int y, String string, Color color) 
	{
		TextRenderer tr = textRenderers.get(FONT);
		if(tr == null) throw new NoSuchElementException("No TextRenderer for the font " + FONT.getName() + "! Create one using setFont(FONT)");
		tr.setColor(color);
		tr.beginRendering(Game.WIDTH, Game.HEIGHT);
		tr.draw(string, x, -y + Game.HEIGHT);
		tr.endRendering();
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
	
	public static SlopeAnimator createSlopeAnimator(int timeMillis, long start, long end, int fadeInTime, int fadeOutTime)
	{
		return new SlopeAnimator(timeMillis, start, end, fadeInTime, fadeOutTime);
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

	//TODO This is NOT working
	public static class SlopeAnimator implements Animator
	{	
		private final double end;
		private final boolean fadeIN;
		private final boolean fadeOut;
		
		private double acc;
		private double value;
		
		private static final double FPS = 30D;
		
		private SlopeAnimator(int timeMillis, long start, long end, int fadeInTime, int fadeOutTime)
		{
			this.acc = 1;
			this.end = end;
			this.value = start;
			fadeIN = fadeInTime > 0;
			fadeOut = fadeOutTime > 0;
		}
		
		private FPSAnimator animator = new FPSAnimator(FPS);
		@Override
		public boolean animate() 
		{
			if(animator.animate())
			{
				double newVal = value + acc;
				if((acc < 0 && newVal >= end) || (acc > 0 && newVal <= end)) value = newVal;
			}
			return true;
		}
		
		public double getValue()
		{
			return value;
		}
	}
}
