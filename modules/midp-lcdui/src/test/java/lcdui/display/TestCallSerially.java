// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package lcdui.display;

import cc.squirreljme.runtime.cldc.debug.Debugging;
import javax.microedition.lcdui.Display;
import lcdui.BaseDisplay;

/**
 * Tests that serial calls happen properly.
 *
 * @since 2020/10/03
 */
public class TestCallSerially
	extends BaseDisplay
{
	/**
	 * {@inheritDoc}
	 * @since 2020/10/03
	 */
	@SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
	@Override
	public void test(Display __display)
	{
		__SerialRun__ run = new __SerialRun__();
		
		// This call should run the code then wait for it to be completed
		__display.callSerially(run);
		
		// Then get the result of that, should be true
		synchronized (run)
		{
			this.secondary("flagged", run._flag);
		}
	}
}
