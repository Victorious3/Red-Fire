package vic.rpg.render;

import java.awt.Color;

import javax.media.opengl.GL2;

import vic.rpg.utils.Utils;

import com.jogamp.opengl.util.texture.Texture;

/**
 * A LightSource provides all values that a proper source of light should have.
 * Right now there are only Spotlights available.
 * @author Victorious3
 */
public class LightSource 
{
	public static Texture baseTexture = TextureLoader.requestTexture(Utils.readImage("/vic/rpg/resources/light.png"));
	
	public int width;
	private float brightness;
	private Color color;
	private boolean isFlickering = true;
	private int randomOffset = 0;
	
	public LightSource(int width, float brightness, Color color, boolean isFlickering)
	{
		this.width = width;
		this.brightness = brightness;
		this.color = color;
		this.isFlickering = isFlickering;
		if(isFlickering) this.randomOffset = Utils.rnd(0, 1000000);
		else this.randomOffset = 0;
	}
	
	/**
	 * Renders the LightSource at the given Isometric Coordinates.
	 * @param gl2
	 * @param x
	 * @param y
	 */
	public void draw(GL2 gl2, int x, int y)
	{
		int lightSize = width;
		if(isFlickering)
		{
			lightSize = (int)(0.80F * width + 0.1F * width * Math.sin(System.currentTimeMillis() / 50 + randomOffset) * Math.random());
		}
		
		x -= lightSize / 2;
		y -= lightSize / 2;
		
		DrawUtils.setGL(gl2);
		gl2.glEnable(GL2.GL_TEXTURE_2D);
		gl2.glPushMatrix();
		Color c2 = new Color((int)(color.getRed() * brightness <= 255 ? color.getRed() * brightness : 255), (int)(color.getGreen() * brightness <= 255 ? color.getGreen() * brightness : 255), (int)(color.getBlue() * brightness <= 255 ? color.getBlue() * brightness : 255), color.getAlpha());
		DrawUtils.glColor(c2);
		gl2.glBindTexture(GL2.GL_TEXTURE_2D, baseTexture.getTextureObject(gl2));
		gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
		gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
		gl2.glBegin(GL2.GL_QUADS);
		gl2.glNormal3i(0, 0, 1);
        gl2.glTexCoord2i(0, 0);
        gl2.glVertex2i(x, y);
        gl2.glTexCoord2i(1, 0);
        gl2.glVertex2i(x + lightSize, y);
        gl2.glTexCoord2i(1, 1);
        gl2.glVertex2i(x + lightSize, y + lightSize);
        gl2.glTexCoord2i(0, 1);
        gl2.glVertex2i(x, y + lightSize);
        gl2.glEnd();
		gl2.glPopMatrix();
		gl2.glDisable(GL2.GL_TEXTURE_2D);
	}
}
