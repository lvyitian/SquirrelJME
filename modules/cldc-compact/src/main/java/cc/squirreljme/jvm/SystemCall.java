// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm;

import cc.squirreljme.runtime.cldc.debug.Debugging;

/**
 * This class contains helpers for all of the system calls to more reliably
 * have a type-safe and easier to use interface to them.
 *
 * @since 2020/03/27
 */
public final class SystemCall
{
	/**
	 * Not used.
	 *
	 * @since 2020/03/27
	 */
	private SystemCall()
	{
	}
	
	/**
	 * Exits the virtual machine.
	 *
	 * @param __i The exit code.
	 * @since 2020/04/09
	 */
	public static void exit(int __i)
	{
		Assembly.sysCall(SystemCallIndex.EXIT, __i);
	}
	
	/**
	 * Gets the error for the system call.
	 *
	 * @param __si The system call to check the error for.
	 * @return The error for the system call.
	 * @since 2020/03/27
	 */
	public static int getError(short __si)
	{
		throw Debugging.todo();
	}
	
	/**
	 * Does the error for the system call match this specified error?
	 *
	 * @param __si The system call to check the error for.
	 * @param __error The error to match against.
	 * @return If the error matches.
	 * @since 2020/03/27
	 */
	public static boolean isError(short __si, int __error)
	{
		return SystemCall.getError(__si) == __error;
	}
	
	/**
	 * Is this system call supported?
	 *
	 * @param __si The system call to check.
	 * @return If the system call is supported.
	 * @since 2020/03/27
	 */
	public static boolean isSupported(short __si)
	{
		throw Debugging.todo();
	}
	
	/**
	 * Loads the specified class.
	 *
	 * @param __name The name of the class.
	 * @return The class information.
	 * @since 2020/03/27
	 */
	public static ClassInfo loadClass(byte... __name)
		throws ClassNotFoundException
	{
		long nameP = Assembly.objectToPointer(__name);
		
		// The VM itself may be able to perform class loading, so first we
		// try to ask the VM if it is capable of doing so and to initialize
		// the class if so
		if (SystemCall.isSupported(SystemCallIndex.LOAD_CLASS_BYTES))
		{
			long rvP = Assembly.sysCallPVL(SystemCallIndex.LOAD_CLASS_BYTES,
				Assembly.longUnpackHigh(nameP), Assembly.longUnpackLow(nameP));
			
			// Failed to load the class
			if (rvP == 0)
			{
				// {@squirreljme.error ZZ3V No such class exists.}
				if (SystemCall.isError(SystemCallIndex.LOAD_CLASS_BYTES,
					SystemCallError.NO_SUCH_CLASS))
					throw new ClassNotFoundException("ZZ3V");
				
				// {@squirreljme.error ZZ3W Invalid class.}
				throw new NoClassDefFoundError("ZZ3W");
			}
			
			// Use this created info
			return Assembly.pointerToClassInfo(rvP);
		}
		
		// Loading of classes is our responsibility
		throw Debugging.todo();
	}
}