// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) 2013-2016 Steven Gawroriski <steven@multiphasicapps.net>
//     Copyright (C) 2013-2016 Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU Affero General Public License v3+, or later.
// For more information see license.mkd.
// ---------------------------------------------------------------------------

package net.multiphasicapps.squirreljme.launcher;

import java.io.Closeable;
import java.io.InputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.multiphasicapps.imagereader.ImageData;
import net.multiphasicapps.imagereader.ImageReaderFactory;
import net.multiphasicapps.imagereader.ImageType;
import net.multiphasicapps.imagereader.xpm.XPMImageReader;
import net.multiphasicapps.squirreljme.classpath.ClassPath;
import net.multiphasicapps.squirreljme.classpath.ClassUnit;
import net.multiphasicapps.squirreljme.classpath.ClassUnitProvider;
import net.multiphasicapps.squirreljme.kernel.Kernel;
import net.multiphasicapps.squirreljme.kernel.KernelProcess;
import net.multiphasicapps.squirreljme.kernel.KIOException;
import net.multiphasicapps.squirreljme.kernel.KIOSocket;
import net.multiphasicapps.squirreljme.ui.UIDisplay;
import net.multiphasicapps.squirreljme.ui.UIException;
import net.multiphasicapps.squirreljme.ui.UIImage;
import net.multiphasicapps.squirreljme.ui.UILabel;
import net.multiphasicapps.squirreljme.ui.UIList;
import net.multiphasicapps.squirreljme.ui.UIManager;
import net.multiphasicapps.squirreljme.ui.UIMenu;
import net.multiphasicapps.squirreljme.ui.UIMenuItem;

/**
 * This class is the standard launcher which is used to run list programs,
 * launch programs, and perform other various things.
 *
 * Due to the design of SquirrelJME, only a single launcher is required
 * because the heavy lifting of UI code is done by the implementation
 * specific display manager.
 *
 * @since 2016/05/20
 */
public class LauncherInterface
	implements Runnable
{
	/** The kernel to launch and control for. */
	protected final Kernel kernel;
	
	/** The process of the kernel. */
	protected final KernelProcess kernelprocess;
	
	/** The display manager to use to interact with the user. */
	protected final UIManager displaymanager;
	
	/** Class unit providers. */
	private final ClassUnitProvider[] _cups;
	
	/**
	 * This is the mapping of currently active class units and their interfaces
	 * for providing labels and launching information.
	 */
	private final Map<ClassUnit, ClassUnitInterface> _units =
		new HashMap<>();
	
	/** The primary display. */
	private volatile UIDisplay _maindisp;
	
	/** The list which contains programs to launch. */
	private volatile UIList _programlist;
	
	/** Mapped indices to units. */
	private volatile ClassUnitInterface[] _listmap;
	
	/**
	 * Initializes the launcher interface.
	 *
	 * @param __k The kernel to provide a launcher for.
	 * @param __dm The kernel display manager.
	 * @param __cup The class unit providers which are available.
	 * @throws IllegalStateException If the server could not be found or did
	 * not permit a connection.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/05/20
	 */
	public LauncherInterface(Kernel __k, UIManager __dm,
		ClassUnitProvider... __cups)
		throws IllegalStateException, NullPointerException
	{
		// Check
		if (__k == null || __dm == null || __cups == null)
			throw new NullPointerException("NARG");
		
		// Set
		this.kernel = __k;
		KernelProcess kernelprocess = __k.kernelProcess();
		this.kernelprocess = kernelprocess;
		this.displaymanager = __dm;
		this._cups = __cups.clone();
		
		// Setup new launcher thread which runs under the kernel
		kernelprocess.createThread(this);
	}
	
	/**
	 * Returns the used display manager.
	 *
	 * @return The used display manager.
	 * @since 2016/05/25
	 */
	public UIManager manager()
	{
		return this.displaymanager;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/05/20
	 */
	@Override
	public void run()
	{
		// Setup the local user interface
		setup();
		
		// Infinite loop
		for (;;)
		{
			// Sleep for a bit
			try
			{
				Thread.sleep(100L);
			}
			
			// Yield to let other threads besides the launcher run/
			catch (InterruptedException e)
			{
				Thread.yield();
			}
		}
	}
	
	/**
	 * Sets up the local launcher user interface and initailizes basic
	 * widgets to provide an interface to the user.
	 *
	 * @since 2016/05/21
	 */
	protected void setup()
	{
		// Create new display to be shown to the user
		UIManager displaymanager = this.displaymanager;
		UIDisplay maindisp = displaymanager.createDisplay();
		this._maindisp = maindisp;
		
		// Set title
		maindisp.setText("SquirrelJME");
		
		// Setup image which acts as the icon
		UIImage icon = displaymanager.createImage();
		
		// Get image factory
		ImageReaderFactory irf = ImageReaderFactory.instance();
		
		// Load all preferred icon sizes
		int[] pis = displaymanager.preferredIconSizes();
		int pisn = pis.length;
		for (int i = 0; i < pisn; i += 2)
		{
			// Get dimensions
			int w = pis[i];
			int h = pis[i + 1];
			
			// Try reading the XPM image data
			try (InputStream is = getClass().getResourceAsStream(
				"/net/multiphasicapps/squirreljme/mascot/xpm/low/head_" + w +
				"x" + h + ".xpm"))
			{
				// If no image exists, ignore
				if (is == null)
					continue;
				
				// Read the image and register it with the image
				icon.addImageData(irf.readImage("xpm", is));
			}
			
			// Ignore read errors
			catch (OutOfMemoryError|UIException|IOException e)
			{
			}
		}
		
		// Set icon
		maindisp.setIcon(icon);
		
		// Setup menu
		UIMenu mainmenu = displaymanager.createMenu();
		mainmenu.setText("Menu");
		
		// Quit item
		UIMenuItem quititem = displaymanager.createMenuItem();
		quititem.setText("Quit");
		mainmenu.add(quititem);
		
		// Use the menu
		maindisp.setMenu(mainmenu);
		
		// The list which contains the programs which are available to run
		UIList programlist = displaymanager.createList();
		this._programlist = programlist;
		maindisp.add(maindisp.size(), programlist);
		
		programlist.add(0, displaymanager.createLabel());
		programlist.add(0, displaymanager.createLabel());
		programlist.add(0, displaymanager.createLabel());
		programlist.add(0, displaymanager.createLabel());
		
		// Refresh the program list
		//refresh();
		
		// Done, make it visible
		maindisp.setVisible(true);
	}
	
	/**
	 * Refreshes the program list to display all the class units that are
	 * potentially available for execution.
	 *
	 * @since 2016/05/25
	 */
	public void refresh()
	{
		// Lock
		UIList list = this._programlist;
		Map<ClassUnit, ClassUnitInterface> units = this._units;
		synchronized (units)
		{
			// Clear the list
			while (list.size() > 0)
				list.remove(0);
			
			// Class unit interfaces which have "stuck"
			Set<ClassUnit> stuck = new HashSet<>();
			
			// Go through class units
			ClassUnitProvider[] cups = this._cups;
			for (ClassUnitProvider cup : cups)
				for (ClassUnit cu : cup)
				{
					// Use cache always
					ClassUnitInterface cui = units.get(cu);
					
					// Does not exist?
					if (cui == null)
						units.put(cu, new ClassUnitInterface(this, cu));
					
					// Make it stick
					stuck.add(cu);
				}
			
			// Setup array to sort interfaces with
			int max = units.size();
			ClassUnitInterface[] sort = new ClassUnitInterface[max];
			
			// Go through the unit set and remove any interfaces for items
			Iterator<Map.Entry<ClassUnit, ClassUnitInterface>> it =
				units.entrySet().iterator();
			int act = 0;
			while (it.hasNext())
			{
				Map.Entry<ClassUnit, ClassUnitInterface> e = it.next();
				if (!stuck.contains(e.getKey()))
					it.remove();
				else
					sort[act++] = e.getValue();
			}
			
			// Setup new list mappings
			ClassUnitInterface[] lm = new ClassUnitInterface[act];
			this._listmap = lm;
			
			// Add labels to the list
			for (int i = 0; i < act; i++)
				list.add(i, (lm[i] = sort[i]).label());
		}
	}
}

