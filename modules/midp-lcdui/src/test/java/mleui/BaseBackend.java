// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package mleui;

import cc.squirreljme.runtime.cldc.debug.Debugging;
import cc.squirreljme.runtime.lcdui.mle.UIBackend;
import net.multiphasicapps.tac.TestConsumer;

/**
 * Base for all backend tests.
 *
 * @since 2020/10/09
 */
public abstract class BaseBackend
	extends TestConsumer<String>
{
	/**
	 * Performs the testing of the given backend.
	 * 
	 * @param __backend The backend to test.
	 * @throws Throwable On any exception.
	 * @since 2020/10/09
	 */
	public abstract void backendTest(UIBackend __backend)
		throws Throwable;
	
	/**
	 * {@inheritDoc}
	 * @since 2020/10/10
	 */
	@Override
	public void test(String __s)
		throws Throwable
	{
		Debugging.debugNote("Parameter: %s", __s);
		
		throw Debugging.todo(__s);
	}
}
