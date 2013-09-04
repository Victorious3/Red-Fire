package vic.rpg.render;

import java.awt.Color;
import java.awt.Font;

import javax.media.opengl.GL2;

import vic.rpg.Game;

import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.texture.Texture;

public class DrawUtils 
{
	private static GL2 gl2;
	private static float LINE_WIDTH = 1.0F;
	private static Font FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 10); 
	private static TextRenderer TEXT_RENDERER = new TextRenderer(FONT);
	
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
		TEXT_RENDERER = new TextRenderer(f);
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
		TEXT_RENDERER.setColor(color);
		TEXT_RENDERER.beginRendering(Game.WIDTH, Game.HEIGHT);
		TEXT_RENDERER.draw(string, x, -y + Game.HEIGHT);
		TEXT_RENDERER.endRendering();
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
		return TEXT_RENDERER;
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
}
