// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.runtime.launcher.ui;

import cc.squirreljme.runtime.cldc.SquirrelJME;
import cc.squirreljme.runtime.lcdui.gfx.AdvancedGraphics;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.IOException;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * This is the splash screen for the launcher which always shows.
 *
 * @since 2019/05/19
 */
public final class SplashScreen
	extends Canvas
{
	/** The splash image width. */
	public static final int WIDTH =
		240;
	
	/** The splash image height. */
	public static final int HEIGHT =
		320;
	
	/** The image data to draw. */
	protected final int[] image;
	
	/**
	 * Initializes the splash screen with a precached image.
	 *
	 * @param __sw The screen width.
	 * @param __sh The screen height.
	 * @since 2019/05/19
	 */
	public SplashScreen(int __sw, int __sh)
	{
		// Image is completely operated with using raw data
		int np = WIDTH * HEIGHT;
		int[] image = new int[np];
		
		// Load splash image onto the data
		try (InputStream in = SplashScreen.class.
			getResourceAsStream("splash.raw"))
		{
			// If it exists, use it
			if (in != null)
			{
				// Input raw pixels
				int nr = WIDTH * HEIGHT * 3;
				byte[] raw = new byte[nr];
				
				// Read in raw data
				for (int read = 0; read < nr;)
				{
					int rc = in.read(raw, read, nr - read);
					
					if (rc < 0)
						break;
					
					read += rc;
				}
				
				// Translate RGB byte pixels to RGB int pixels
				for (int o = 0, i = 0; o < np; o++)
					image[o] = ((raw[i++] & 0xFF) << 16) |
						((raw[i++] & 0xFF) << 8) |
						(raw[i++] & 0xFF);
			}
		}
		catch (IOException e)
		{
		}
		
		// Text will be drawn using the advanced graphics since it can
		// operate on integer buffers directly
		Graphics g = new AdvancedGraphics(image, false, null, WIDTH, HEIGHT,
			WIDTH, 0, 0, 0);
		
		// Draw a bunch of text
		g.setColor(0x000000);
		g.setFont(Font.getFont("sansserif", 0, 16));
		g.drawString("SquirrelJME " + SquirrelJME.RUNTIME_VERSION + "\n" +
			"(C) Stephanie Gawroriski\n" +
			"https://squirreljme.cc/\nLicensed w/ the GPLv3!", 0, 0, 0);
		g.drawString("SquirrelJME", 1, 0, 0);
		
		// Use this image
		this.image = image;
		
		// Be full-screen
		this.setFullScreenMode(true);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/05/19
	 */
	@Override
	public final void paint(Graphics __g)
	{
		// Draw the raw image data, is the fastest
		__g.drawRGB(this.image, 0, WIDTH, 0, 0, WIDTH, HEIGHT,
			false);
	}
}
