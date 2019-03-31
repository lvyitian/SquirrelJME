// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package net.multiphasicapps.classfile.register;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import net.multiphasicapps.classfile.JavaType;

/**
 * This represents the result of operations performed on the Java stack.
 *
 * This class is immutable.
 *
 * @since 2019/03/30
 */
public final class JavaStackResult
{
	/** String representation. */
	private Reference<String> _string;
	
	/**
	 * Represents the new state after the operation was performed.
	 *
	 * @return The state that is the result of the operation.
	 * @since 2019/03/30
	 */
	public final JavaStackState after()
	{
		throw new todo.TODO();
	}
	
	/**
	 * Represents the previous state which this was based off.
	 *
	 * @return The previous state this originated from.
	 * @since 2019/03/30
	 */
	public final JavaStackState before()
	{
		throw new todo.TODO();
	}
	
	/**
	 * Returns the enqueue list which represents everything that is to be
	 * uncounted after the operation completes.
	 *
	 * @return The enqueue list, will be empty if there is nothing to
	 * enqueue.
	 * @since 2019/03/30
	 */
	public final JavaStackEnqueueList enqueue()
	{
		throw new todo.TODO();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/03/30
	 */
	@Override
	public final boolean equals(Object __o)
	{
		throw new todo.TODO();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/03/30
	 */
	@Override
	public final int hashCode()
	{
		throw new todo.TODO();
	}
	
	/**
	 * Returns the information on the input.
	 *
	 * @param __i The input to get.
	 * @return The information on the input.
	 * @since 2019/03/30
	 */
	public final JavaStackResult.Input in(int __i)
	{
		throw new todo.TODO();
	}
	
	/**
	 * Returns the number of generated inputs.
	 *
	 * @return The input count.
	 * @since 2019/03/30
	 */
	public final int inCount()
	{
		throw new todo.TODO();
	}
	
	/**
	 * Returns the information on the output.
	 *
	 * @param __i The output to get.
	 * @return The information on the output.
	 * @since 2019/03/30
	 */
	public final JavaStackResult.Output out(int __i)
	{
		throw new todo.TODO();
	}
	
	/**
	 * Returns the number of generated outputs.
	 *
	 * @return The output count.
	 * @since 2019/03/30
	 */
	public final int outCount()
	{
		throw new todo.TODO();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/03/30
	 */
	@Override
	public final String toString()
	{
		throw new todo.TODO();
	}
	
	/**
	 * Input information.
	 *
	 * @since 2019/03/30
	 */
	public static final class Input
	{
		/** The register used for input. */
		public final int register =
			-1;
		
		/** The type which was read. */
		public final JavaType type =
			null;
	}
	
	/**
	 * Output information.
	 *
	 * @since 2019/03/30
	 */
	public static final class Output
	{
		/** The register used for output. */
		public final int register =
			-1;
	}
}
