package vic.rpg.render;

import java.awt.Color;

import javax.media.opengl.GL2;

import vic.rpg.utils.Utils;

import com.jogamp.opengl.util.texture.Texture;

public class LightSource 
{
	public static Texture baseTexture = TextureLoader.requestTexture(Utils.readImageFromJar("/vic/rpg/resources/light.png"));
	
	public int width;
	private float brightness;
	private Color color;
	
	public LightSource(int width, float brightness, Color color)
	{
		this.width = width;
		this.brightness = brightness;
		this.color = color;
	}
	
	public void draw(GL2 gl2, int x, int y)
	{
		DrawUtils.setGL(gl2);
		gl2.glEnable(GL2.GL_TEXTURE_2D);
		gl2.glPushMatrix();
		gl2.glScalef(width / baseTexture.getWidth(), width / baseTexture.getWidth(), width / baseTexture.getWidth());
		DrawUtils.glColor(color);
		gl2.glBindTexture(GL2.GL_TEXTURE_2D, baseTexture.getTextureObject(gl2));
		gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
		gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
		gl2.glBegin(GL2.GL_QUADS);
		gl2.glNormal3i(0, 0, 1);
        gl2.glTexCoord2i(0, 0);
        gl2.glVertex2i(x, y);
        gl2.glTexCoord2i(1, 0);
        gl2.glVertex2i(x + baseTexture.getWidth(), y);
        gl2.glTexCoord2i(1, 1);
        gl2.glVertex2i(x + baseTexture.getWidth(), y + baseTexture.getHeight());
        gl2.glTexCoord2i(0, 1);
        gl2.glVertex2i(x, y + baseTexture.getHeight());
        gl2.glEnd();
		gl2.glPopMatrix();
		gl2.glDisable(GL2.GL_TEXTURE_2D);
	}
}
