// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) 2013-2016 Steven Gawroriski <steven@multiphasicapps.net>
//     Copyright (C) 2013-2016 Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// For more information see license.mkd.
// ---------------------------------------------------------------------------

package net.multiphasicapps.squirreljme.jit.generic;

import java.util.Deque;
import java.util.List;
import net.multiphasicapps.squirreljme.jit.base.JITException;
import net.multiphasicapps.squirreljme.jit.JITOutputConfig;
import net.multiphasicapps.squirreljme.jit.JITVariableType;
import net.multiphasicapps.util.msd.MultiSetDeque;

/**
 * This is a class which is used to allocate and manage native registers
 * which are available for a given CPU.
 *
 * @since 2016/08/30
 */
public class GenericAllocator
{
	/** The configuration used. */
	protected final JITOutputConfig.Immutable config;
	
	/** The ABI used. */
	protected final GenericABI abi;
	
	/** The multi-set deque, used for removal. */
	final MultiSetDeque<GenericRegister> _msd;
	
	/** Saved int register queue. */
	final Deque<GenericRegister> _savedintq;
	
	/** Saved float register queue. */
	final Deque<GenericRegister> _savedfloatq;
	
	/** Temporary int register queue. */
	final Deque<GenericRegister> _tempintq;
	
	/** Temporary float register queue. */
	final Deque<GenericRegister> _tempfloatq;
	
	/** Registers bound to local variables. */
	volatile __VarStates__ _jlocals;
	
	/** Registers bound to stack variables. */
	volatile __VarStates__ _jstack;
	
	/**
	 * Initializes the register allocator using the specified configuration
	 * and the given ABI.
	 *
	 * @param __conf The configuration being used.
	 * @param __abi The ABI used on the target system.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/09/01
	 */
	GenericAllocator(JITOutputConfig.Immutable __conf, GenericABI __abi)
		throws NullPointerException
	{
		// Check
		if (__conf == null || __abi == null)
			throw new NullPointerException("NARG");
		
		// Set
		this.config = __conf;
		this.abi = __abi;
		
		// Setup queues
		MultiSetDeque<GenericRegister> msd = new MultiSetDeque<>();
		Deque<GenericRegister> savedintq = msd.subDeque();
		Deque<GenericRegister> savedfloatq = msd.subDeque();
		Deque<GenericRegister> tempintq = msd.subDeque();
		Deque<GenericRegister> tempfloatq = msd.subDeque();
		this._msd = msd;
		this._savedintq = savedintq;
		this._savedfloatq = savedfloatq;
		this._tempintq = tempintq;
		this._tempfloatq = tempfloatq;
		
		// Add all registers to the queues
		savedintq.addAll(__abi.saved(GenericRegisterKind.INTEGER));
		savedfloatq.addAll(__abi.saved(GenericRegisterKind.FLOAT));
		tempintq.addAll(__abi.temporary(GenericRegisterKind.INTEGER));
		tempfloatq.addAll(__abi.temporary(GenericRegisterKind.FLOAT));
	}
	
	/**
	 * Returns the ABI that is used for this.
	 *
	 * @return The used ABI.
	 * @since 2016/09/03
	 */
	public final GenericABI abi()
	{
		return this.abi;
	}
	
	/**
	 * Primes the method arguments and sets the initial state that is used
	 * on entry of a method.
	 *
	 * @param __t The arguments to the method.
	 * @throws JITException If they could not be primed.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/09/03
	 */
	public void primeArguments(JITVariableType[] __t)
		throws JITException, NullPointerException
	{
		// Check
		if (__t == null)
			throw new NullPointerException("NARG");
		
		// Get locals
		__VarStates__ jlocals = this._jlocals;
		
		// Get integral and float arguments
		GenericABI abi = this.abi;
		List<GenericRegister> ai = abi.arguments(GenericRegisterKind.INTEGER);
		List<GenericRegister> af = abi.arguments(GenericRegisterKind.FLOAT);
		
		// If there are any used variables they will be consumed from the
		// argument list.
		MultiSetDeque<GenericRegister> msd = this._msd;
		
		// Int/float argument at and limits
		int iat = 0, ilim = ai.size(),
			fat = 0, flim = af.size();
		
		// Go through variable types
		int n = __t.length;
		JITVariableType last = null;
		for (int i = 0; i < n; i++)
		{
			// Get
			JITVariableType type = __t[i];
			
			// The tops of variables will always skip a slot, but use similar
			// storage for the old value.
			if (type == JITVariableType.TOP)
			{
				// {@squirreljme.error BA1i Expected long/double to precede
				// top variable type. (The last type; The current type)}
				if (last != JITVariableType.LONG &&
					last != JITVariableType.DOUBLE)
					throw new JITException(String.format("BA1i %s %s", last,
						type));
				
				throw new Error("TODO");
			}
			
			// Need to get the used register, if one is used at all
			GenericRegister usereg;
			
			// Floating point
			if (type.isFloat())
			{
				// No more float regs?
				if (fat >= flim)
					usereg = null;
				
				// Grab one
				else
					usereg = af.get(fat++);
			}
			
			// Integer
			else
			{
				// No more int regs?
				if (iat >= ilim)
					usereg = null;
				
				// Use one
				else
					usereg = ai.get(iat++);
			}
			
			// Debug
			System.err.printf("DEBUG -- Selected register: %s%n", usereg);
			
			// Assign register to local
			if (usereg != null)
			{
				// Assign
				jlocals._regs[i] = usereg;
				
				// Also need to remove the register from the available queues
				// since argument registers are initially used
				msd.remove(usereg);
			}
			
			// Allocate some stack space
			else
				throw new Error("TODO");
		}
	}
	
	/**
	 * Records the current state of the allocator.
	 *
	 * @return The allocator state.
	 * @since 2016/09/03
	 */
	public final GenericAllocatorState recordState()
	{
		throw new Error("TODO");
	}
	
	/**
	 * This registers temporary and local variable slots which are assigned
	 * to registers in a method.
	 *
	 * @param __stack The number of stack entries.
	 * @param __locals The number of local variables.
	 * @throws JITException If they could not be counted.
	 * @since 2016/09/03
	 */
	public final void variableCounts(int __stack, int __locals)
		throws JITException
	{
		// {@squirreljme.error BA1h The number of stack and/or local variables
		// has a negative count. (The stack variable count; The local variable
		// count)}
		if (__stack < 0 || __locals < 0)
			throw new JITException(String.format("BA1h %d %d", __stack,
				__locals));
		
		// Initialize state
		this._jlocals = new __VarStates__(__locals);
		this._jstack = new __VarStates__(__stack);
	}
}

