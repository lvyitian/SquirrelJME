// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.kernel.lib.client;

import cc.squirreljme.kernel.lib.LibrariesPacketTypes;
import cc.squirreljme.kernel.lib.Library;
import cc.squirreljme.runtime.cldc.SystemResourceScope;

/**
 * This class represents a library as seen by the client.
 *
 * @since 2018/01/12
 */
final class __ClientLibrary__
	extends Library
{
	/**
	 * Initializes the library.
	 *
	 * @param __dx The library index.
	 * @since 2018/01/12
	 */
	__ClientLibrary__(int __dx)
	{
		super(__dx);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/15
	 */
	@Override
	protected final byte[] loadResourceBytes(SystemResourceScope __scope,
		String __n)
		throws NullPointerException
	{
		if (__scope == null || __n == null)
			throw new NullPointerException("NARG");
		
		throw new todo.TODO();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/27
	 */
	@Override
	public final int type()
	{
		throw new todo.TODO();
	}
}

