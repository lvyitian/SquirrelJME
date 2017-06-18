// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Steven Gawroriski <steven@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package net.multiphasicapps.squirreljme.jit.bin;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * This represents and stores the binary data and code which makes up a section.
 * Sections may have some regions of their output be determined in a later stage
 * of the linker on output such as the address where a method is located.
 *
 * @since 2017/06/15
 */
public class Section
	extends __SubState__
{
	/**
	 * Initializes the section.
	 *
	 * @param __ls The reference to the owning linker state.
	 * @since 2017/06/15
	 */
	Section(Reference<LinkerState> __ls)
	{
		super(__ls);
		
		throw new todo.TODO();
	}
}

