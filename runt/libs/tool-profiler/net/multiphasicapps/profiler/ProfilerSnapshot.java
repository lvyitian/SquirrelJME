// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package net.multiphasicapps.profiler;

/**
 * This represents the main profiler snapshot which contains all of the data
 * within what is to be profiled, it is mutable and accordingly allows for
 * export to NPS formats.
 *
 * @since 2018/11/10
 */
public final class ProfilerSnapshot
{
	/**
	 * Starts profiling the given thread.
	 *
	 * @param __name The name of the thread.
	 * @return A class to handle the profiling of threads via the call stack.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/11/10
	 */
	public final ProfiledThread thread(String __name)
		throws NullPointerException
	{
		if (__name == null)
			throw new NullPointerException("NARG");
		
		throw new todo.TODO();
	}
}

