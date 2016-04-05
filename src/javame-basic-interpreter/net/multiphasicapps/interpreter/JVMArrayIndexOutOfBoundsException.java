// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) 2013-2016 Steven Gawroriski <steven@multiphasicapps.net>
//     Copyright (C) 2013-2016 Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU Affero General Public License v3+, or later.
// For more information see license.mkd.
// ---------------------------------------------------------------------------

package net.multiphasicapps.interpreter;

/**
 * This is thrown when an illegal array index is referenced.
 *
 * @since 2016/04/05
 */
public class JVMArrayIndexOutOfBoundsException
	extends JVMEngineException
{
	/**
	 * Initializes the exception with no message.
	 *
	 * @since 2016/04/05
	 */
	public JVMArrayIndexOutOfBoundsException()
	{
		super();
	}
	
	/**
	 * Initializes exception with the given message.
	 *
	 * @param __msg The exception message.
	 * @since 2016/04/05
	 */
	public JVMArrayIndexOutOfBoundsException(String __msg)
	{
		super(__msg);
	}
	
	/**
	 * Initializes exception with the given message and cause.
	 *
	 * @param __msg The exception message.
	 * @param __c The cause.
	 * @since 2016/04/05
	 */
	public JVMArrayIndexOutOfBoundsException(String __msg, Throwable __c)
	{
		super(__msg, __c);
	}
	
	/**
	 * Initializes the exception with the given cause and no message.
	 *
	 * @param __c The cause of the exception.
	 * @since 2016/04/05
	 */
	public JVMArrayIndexOutOfBoundsException(Throwable __c)
	{
		super(__c);
	}
}

