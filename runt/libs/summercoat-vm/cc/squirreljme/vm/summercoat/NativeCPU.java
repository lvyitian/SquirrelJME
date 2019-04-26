// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.vm.summercoat;

import cc.squirreljme.runtime.cldc.debug.CallTraceElement;
import dev.shadowtail.classfile.nncc.ArgumentFormat;
import dev.shadowtail.classfile.nncc.NativeCode;
import dev.shadowtail.classfile.nncc.NativeInstruction;
import dev.shadowtail.classfile.nncc.NativeInstructionType;
import dev.shadowtail.classfile.xlate.CompareType;
import dev.shadowtail.classfile.xlate.DataType;
import dev.shadowtail.classfile.xlate.InvokeType;
import dev.shadowtail.classfile.xlate.MathType;
import dev.shadowtail.classfile.xlate.StackJavaType;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import net.multiphasicapps.classfile.InstructionMnemonics;
import net.multiphasicapps.collections.IntegerList;
import net.multiphasicapps.io.HexDumpOutputStream;

/**
 * This represents a native CPU which may run within its own thread to
 * execute code that is running from within the virtual machine.
 *
 * @since 2019/04/21
 */
public final class NativeCPU
	implements Runnable
{
	/** Maximum amount of CPU registers. */
	public static final int MAX_REGISTERS =
		64;
	
	/** The size of the method cache. */
	public static final int METHOD_CACHE =
		2048;
	
	/** Spill over protection for the cache. */
	public static final int METHOD_CACHE_SPILL =
		1024;
	
	/** The memory to read/write from. */
	protected final WritableMemory memory;
	
	/** Stack frames. */
	private final LinkedList<Frame> _frames =
		new LinkedList<>();
	
	/**
	 * Initializes the native CPU.
	 *
	 * @param __mem The memory space.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/21
	 */
	public NativeCPU(WritableMemory __mem)
		throws NullPointerException
	{
		if (__mem == null)
			throw new NullPointerException("NARG");
		
		this.memory = __mem;
	}
	
	/**
	 * Enters the given frame for the given address.
	 *
	 * @param __pc The address of the frame.
	 * @param __args Arguments to the frame
	 * @return The newly created frame.
	 * @since 2019/04/21
	 */
	public final Frame enterFrame(int __pc, int... __args)
	{
		// Debug
		System.err.printf(">>>> %08x >>>>>>>>>>>>>>>>>>>>>>%n", __pc);
		System.err.printf(" > %s%n", new IntegerList(__args));
		
		// Setup new frame
		Frame rv = new Frame();
		rv._pc = __pc;
		rv._entrypc = __pc;
		rv._lastpc = __pc;
		
		// Old frame, to source globals from
		LinkedList<Frame> frames = this._frames;
		Frame lastframe = frames.peekLast();
		
		// Add to frame list
		frames.addLast(rv);
		
		// Seed initial registers, if valid
		int[] dest = rv._registers;
		if (lastframe != null)
		{
			// Copy globals
			int[] src = lastframe._registers;
			for (int i = 0; i < NativeCode.LOCAL_REGISTER_BASE; i++)
				dest[i] = src[i];
			
			// Set the pool register to the next pool register value
			dest[NativeCode.POOL_REGISTER] =
				src[NativeCode.NEXT_POOL_REGISTER];
		}
		
		// Copy the arguments to the argument slots
		for (int i = 0, o = NativeCode.ARGUMENT_REGISTER_BASE,
			n = __args.length; i < n; i++, o++)
			dest[o] = __args[i];
		
		// Clear zero
		dest[0] = 0;
		
		// Use this frame
		return rv;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/21
	 */
	@Override
	public final void run()
	{
		this.run(0);
	}
	
	/**
	 * Runs anything after this frame.
	 *
	 * @param __fl The frame limit.
	 * @since 2019/04/21
	 */
	public final void run(int __fl)
	{
		// Read the CPU stuff
		final WritableMemory memory = this.memory;
		boolean reload = true;
		
		// Frame specific info
		Frame nowframe = null;
		int[] lr = null;
		int pc = -1,
			rp = -1;
		
		// Per operation handling
		final int[] args = new int[6];
		final long[] largs = new long[6];
		
		// Method cache to reduce tons of method reads
		final byte[] icache = new byte[METHOD_CACHE];
		int lasticache = -(METHOD_CACHE_SPILL + 1);
		
		// Execution is effectively an infinite loop
		LinkedList<Frame> frames = this._frames;
		for (int frameat = frames.size(), lastframe = -1; frameat >= __fl;
			frameat = frames.size())
		{
			// Reload parameters?
			if ((reload |= (lastframe != frameat)))
			{
				// Before dumping this frame, store old info
				if (nowframe != null)
				{
					nowframe._pc = pc;
					nowframe._rp = rp;
				}
				
				// Get current frame, stop execution if there is nothing
				// left to execute
				nowframe = frames.peekLast();
				if (nowframe == null)
					return;
				
				// Load stuff needed for execution
				lr = nowframe._registers;
				pc = nowframe._pc;
				rp = nowframe._rp;
				
				// Used to auto-detect frame change
				lastframe = frameat;
				
				// No longer reload information
				reload = false;
			}
			
			// For a bit faster execution of the method, cache a bunch of
			// the code that is being executed in memory. Constantly performing
			// the method calls to read single bytes of memory is a bit so, so
			// this should hopefully improve performance slightly.
			int pcdiff = pc - lasticache;
			if (pcdiff < 0 || pcdiff >= METHOD_CACHE_SPILL)
			{
				memory.memReadBytes(pc, icache, 0, METHOD_CACHE);
				lasticache = pc;
			}
			
			// Calculate last PC base address
			int bpc = pc - lasticache;
			
			// Always set PC address for debugging frames
			nowframe._pc = pc;
			
			// Read operation
			nowframe._lastpc = pc;
			int op = icache[bpc] & 0xFF;
			
			// Reset all input arguments
			for (int i = 0, n = args.length; i < n; i++)
			{
				args[i] = 0;
				largs[i] = 0;
			}
			
			// Register list, just one is used everywhere
			int[] reglist = null;
			
			// Load arguments for this instruction
			ArgumentFormat[] af = NativeInstruction.argumentFormat(op);
			int rargp = bpc + 1;
			for (int i = 0, n = af.length; i < n; i++)
				switch (af[i])
				{
					// Variable sized entries, may be pool values
					case VUINT:
					case VPOOL:
					case VJUMP:
						{
							// Long value?
							int base = (icache[rargp++] & 0xFF);
							if ((base & 0x80) != 0)
							{
								base = ((base & 0x7F) << 8);
								base |= (icache[rargp++] & 0xFF);
							}
							
							// Set
							if (af[i] == ArgumentFormat.VJUMP)
								args[i] = (short)(base |
									((base & 0x4000) << 1));
							else
								args[i] = base;
						}
						break;
					
					// Register list.
					case REGLIST:
						{
							// Wide
							int count = (icache[rargp++] & 0xFF);
							if ((count & 0x80) != 0)
							{
								count = ((count & 0x7F) << 8) |
									(icache[rargp++] & 0xFF);
								
								// Read values
								reglist = new int[count];
								for (int r = 0; r < count; r++)
									reglist[r] =
										((icache[rargp++] & 0xFF) << 8) |
										(icache[rargp++] & 0xFF);
							}
							// Narrow
							else
							{
								reglist = new int[count];
								
								// Read values
								for (int r = 0; r < count; r++)
									reglist[r] = (icache[rargp++] & 0xFF);
							}
						}
						break;
					
					// 32-bit integer/float
					case INT32:
					case FLOAT32:
						args[i] = ((icache[rargp++] & 0xFF) << 24) |
							((icache[rargp++] & 0xFF) << 16) |
							((icache[rargp++] & 0xFF) << 8) |
							((icache[rargp++] & 0xFF));
						break;
					
					// 64-bit long/double
					case INT64:
					case FLOAT64:
						largs[i] = ((icache[rargp++] & 0xFFL) << 56L) |
							((icache[rargp++] & 0xFFL) << 48L) |
							((icache[rargp++] & 0xFFL) << 40L) |
							((icache[rargp++] & 0xFFL) << 32L) |
							((icache[rargp++] & 0xFFL) << 24L) |
							((icache[rargp++] & 0xFFL) << 16L) |
							((icache[rargp++] & 0xFFL) << 8L) |
							((icache[rargp++] & 0xFFL));
						break;
					
					default:
						throw new todo.OOPS(af[i].name());
				}
			
			// Print CPU debug info
			this.__cpuDebugPrint(nowframe, op, af, args, largs, reglist);
			
			// By default the next instruction is the address after all
			// arguments have been read
			int nextpc = lasticache + rargp;
			
			// Handle the operation
			int encoding;
			switch ((encoding = NativeInstruction.encoding(op)))
			{
					// Atomic decrement and get
				case NativeInstructionType.ATOMIC_INT_DECREMENT_AND_GET:
					synchronized (memory)
					{
						// The address to load from/store to
						int addr = lr[args[1]] + args[2];
						
						// Read, increment, and store
						int newval;
						memory.memWriteInt(addr,
							(newval = memory.memReadInt(addr) - 1));
						
						// Store the value after the decrement
						lr[args[0]] = newval;
					}
					break;
					
					// Atomic increment
				case NativeInstructionType.ATOMIC_INT_INCREMENT:
					synchronized (memory)
					{
						// The address to load from/store to
						int addr = lr[args[0]] + args[1];
						
						// Read, increment, and store
						memory.memWriteInt(addr, memory.memReadInt(addr) + 1);
					}
					break;
				
					// Entry marker used for debug
				case NativeInstructionType.ENTRY_MARKER:
					break;
					
					// Conversion (Narrow)
				case NativeInstructionType.CONVERSION:
					{
						StackJavaType a = StackJavaType.of((op >> 2) & 0x3),
							b = StackJavaType.of(op & 0x03);
						
						// The value to convert
						int va = lr[args[0]],
							old = va;
						
						if (a == StackJavaType.INTEGER)
						{
							if (b == StackJavaType.FLOAT)
								va = Float.floatToRawIntBits((float)va);
						}
						else
						{
							if (b == StackJavaType.INTEGER)
								va = (int)Float.intBitsToFloat(va);
						}
						
						// Set destination
						lr[args[1]] = va;
					}
					break;
					
					// Compare integers and possibly jump
				case NativeInstructionType.IF_ICMP:
					{
						// Parts
						int a = lr[args[0]],
							b = lr[args[1]];
						
						// Compare
						boolean branch;
						CompareType ct;
						switch ((ct = CompareType.of(op & 0b111)))
						{
							case EQUALS:
								branch = (a == b); break;
							case NOT_EQUALS:
								branch = (a != b); break;
							case LESS_THAN:
								branch = (a < b); break;
							case LESS_THAN_OR_EQUALS:
								branch = (a <= b); break;
							case GREATER_THAN:
								branch = (a > b); break;
							case GREATER_THAN_OR_EQUALS:
								branch = (a >= b); break;
							case TRUE:
								branch = true; break;
							case FALSE:
								branch = false; break;
							
							default:
								throw new todo.OOPS();
						}
						
						// Branching?
						if (branch)
							nextpc = pc + args[2];
					}
					break;
					
					// If value equal to constant
				case NativeInstructionType.IFEQ_CONST:
					{
						// Branching? Remember that jumps are relative
						if (lr[args[0]] == args[1])
							nextpc = pc + args[2];
					}
					break;
					
					// Invoke a pointer
				case NativeInstructionType.INVOKE:
					{
						// Load values into the register list
						for (int i = 0, n = reglist.length; i < n; i++)
							reglist[i] = lr[reglist[i]];
						
						// Enter the frame
						this.enterFrame(lr[args[0]], reglist);
						
						// Entering some other frame
						reload = true;
					}
					break;
					
					// Integer math
				case NativeInstructionType.MATH_CONST_INT:
				case NativeInstructionType.MATH_REG_INT:
					{
						// Parts
						int a = lr[args[0]],
							b = (((op & 0x80) != 0) ? args[1] : lr[args[1]]),
							c;
						
						// Operation to execute
						MathType mt;
						switch ((mt = MathType.of(op & 0xF)))
						{
							case ADD:		c = a + b; break;
							case SUB:		c = a - b; break;
							case MUL:		c = a * b; break;
							case DIV:		c = a / b; break;
							case REM:		c = a % b; break;
							case NEG:		c = -a; break;
							case SHL:		c = a << b; break;
							case SHR:		c = a >> b; break;
							case USHR:		c = a >>> b; break;
							case AND:		c = a & b; break;
							case OR:		c = a | b; break;
							case XOR:		c = a ^ b; break;
							case SIGN_X8:	c = (byte)a; break;
							case SIGN_HALF:	c = (short)a; break;
							
							case CMPL:
							case CMPG:
								c = (a < b ? -1 : (a == b ? 0 : 1));
								break;
							
							default:
								throw new todo.OOPS();
						}
						
						// Set result
						lr[args[2]] = c;
					}
					break;
				
					// Read off memory
				case NativeInstructionType.MEMORY_OFF_REG:
				case NativeInstructionType.MEMORY_OFF_ICONST:
					{
						// Is this a load operation?
						boolean load = ((op & 0b1000) != 0);
						
						// The address to load from/store to
						int addr = lr[args[1]] +
							(((op & 0x80) != 0) ? args[2] : lr[args[2]]);
						
						// Loads
						DataType dt = DataType.of(op & 0b0111);
						if (load)
						{
							// Load value
							int v;
							switch (dt)
							{
								case BYTE:
									v = (byte)memory.memReadByte(addr);
									break;
									
								case SHORT:
									v = (short)memory.memReadShort(addr);
									break;
									
								case CHARACTER:
									v = memory.memReadShort(addr) & 0xFFFF;
									break;
									
								case INTEGER:
									v = memory.memReadInt(addr);
									break;
									
									// Unknown
								default:
									throw new todo.OOPS(dt.name());
							}
							
							// Set value
							lr[args[0]] = v;
						}
						
						// Stores
						else
						{
							// Value to store
							int v = lr[args[0]];
							
							// Store
							switch (dt)
							{
								case BYTE:
									memory.memWriteByte(addr, v);
									break;
									
								case SHORT:
									memory.memWriteShort(addr, v);
									break;
									
								case CHARACTER:
									memory.memWriteShort(addr, v);
									break;
									
								case INTEGER:
									memory.memWriteInt(addr, v);
									break;
									
									// Unknown
								default:
									throw new todo.OOPS(dt.name());
							}
						}
					}
					break;
					
					// Return from method call
				case NativeInstructionType.RETURN:
					{
						// Go up frame
						Frame was = frames.removeLast(),
							now = frames.peekLast();
						
						// If we are going back onto a frame then copy all
						// the globals which were set since they are meant to
						// be global!
						if (now != null)
						{
							int[] wr = was._registers,
								nr = now._registers;
							
							// Copy globals
							for (int i = 0; i < NativeCode.LOCAL_REGISTER_BASE;
								i++)
							{
								// Ignore the pool register because if it is
								// replaced then it will just explode and
								// cause issues for the parent method
								if (i == NativeCode.POOL_REGISTER)
									continue;
								
								// Reset the next pool register
								else if (i == NativeCode.NEXT_POOL_REGISTER)
								{
									nr[i] = 0;
									break;
								}
								
								// Copy otherwise
								else
									nr[i] = wr[i];
							}
						}
						
						// A reload is done as the frame has changed
						reload = true;
						
						// Debug
						System.err.printf("<<<< %08x <<<<<<<<<<<<<<<<<<<<<<%n",
							(now != null ? now._pc : 0));
					}
					break;
				
				default:
					throw new todo.OOPS(NativeInstruction.mnemonic(op));
			}
			
			// Set next PC address
			pc = nextpc;
		}
	}
	
	/**
	 * Returns the trace of the current execution.
	 *
	 * @return The current trace.
	 * @since 2019/04/22
	 */
	public final CallTraceElement[] trace()
	{
		throw new todo.TODO();
	}
	
	/**
	 * Returns the trace for the given frame.
	 *
	 * @param __f The frame to trace.
	 * @return The trace of this frame.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/22
	 */
	public final CallTraceElement trace(Frame __f)
		throws NullPointerException
	{
		if (__f == null)
			throw new NullPointerException("NARG");
		
		// If the where is this is not set, no idea where this is
		int wit = __f._registers[NativeCode.WHERE_IS_THIS];
		if (wit == 0)
			return new CallTraceElement();
		
		// Need memory to access the where information
		ReadableMemory memory = this.memory;
		
		// The memory address where debug info is located
		int lineoff = (short)memory.memReadShort(wit),
			jopsoff = (short)memory.memReadShort(wit + 2),
			jpcsoff = (short)memory.memReadShort(wit + 4);
		
		// Try to find the line where we should be at?
		int pc = __f._pc,
			relpc = pc - __f._entrypc;
		
		// Try to find where the code is on the debug tables
		int online = (lineoff == 0 ? -1 :
				this.__cpuDebugFindTabIndex(true, wit + lineoff, relpc)),
			onjop = (jopsoff == 0 ? -1 :
				this.__cpuDebugFindTabIndex(false, wit + jopsoff, relpc)),
			onjpc = (jpcsoff == 0 ? -1 :
				this.__cpuDebugFindTabIndex(false, wit + jpcsoff, relpc));
		
		// Read values
		String cname = null,
			mname = null,
			mtype = null;
		
		// Read class, method name, and method type
		try (DataInputStream dis = new DataInputStream(
			new ReadableMemoryInputStream(memory, wit + 6, 1024)))
		{
			cname = dis.readUTF();
			mname = dis.readUTF();
			mtype = dis.readUTF();
		}
		
		// Just ignore
		catch (IOException e)
		{
		}
		
		// Build
		return new CallTraceElement(cname, mname, mtype, pc, null,
			online, onjop, onjpc);
	}
	
	/**
	 * Find index within debug table.
	 *
	 * @param __sh Are shorts being used?
	 * @param __ad The address to the table.
	 * @param __pc The desired PC address.
	 * @return The resulting index or {@code -1} if not found.
	 * @since 2019/04/26
	 */
	private final int __cpuDebugFindTabIndex(boolean __sh, int __ad, int __pc)
	{
		// The last at index, if the index is not found this will be
		// returned at the end.
		int lastat = -1;
		
		// Try to find our line
		try (DataInputStream dis = new DataInputStream(
			new ReadableMemoryInputStream(this.memory, __ad, 4096)))
		{
			// Constant try to read the index
			for (int nowpc = 0;;)
			{
				// Read offset code
				int off = dis.read() & 0xFF;
				
				// End of line marker (or just EOF)
				if (off == 255)
					break;
				
				// Read value
				int nowat = (__sh ? dis.readUnsignedShort() :
					dis.readUnsignedByte());
				
				// If the resulting PC address is after this one then this
				// means the last value is used. Otherwise the values will
				// point to the following indexes and be a bit hard to debug
				int respc = nowpc + off;
				if (respc > __pc)
					return lastat;
				
				// However if this matches the index directly we know it is
				// here
				else if (respc == __pc)
					return nowat;
				
				// Try again at future address
				lastat = nowat;
				nowpc = respc;
			}
		}
		
		// Just ignore
		catch (IOException e)
		{
		}
		
		// Not found, use the last index since maybe the table was
		// truncated or not written enough?
		return lastat;
	}
	
	/**
	 * Performs some nice printing of CPU information.
	 *
	 * @param __nf The current frame.
	 * @param __op The operation.
	 * @param __af The argument format.
	 * @param __args Argument values.
	 * @param __largs Long argument values.
	 * @param __reglist The register list.
	 * @since 2019/04/23
	 */
	private final void __cpuDebugPrint(Frame __nf, int __op,
		ArgumentFormat[] __af, int[] __args, long[] __largs, int[] __reglist)
	{
		PrintStream out = System.err;
		
		// Limit class name
		CallTraceElement trace = this.trace(__nf);
		String cname = "" + trace.className();
		int nl;
		if ((nl = cname.length()) > 20)
			cname = cname.substring(nl - 20, nl);
		
		// Print Header (with location info)
		out.printf("***** @%08x %-19.19s/%10.10s | L%-4d/J%-3d %20.20s::%s %n",
			__nf._pc,
			NativeInstruction.mnemonic(__op),
			InstructionMnemonics.toString(trace.byteCodeInstruction()),
			trace.line(),
			trace.byteCodeAddress(),
			cname,
			trace.methodName() + ":" + trace.methodDescriptor());
		
		// Is this an invoke?
		boolean isinvoke = (__op == NativeInstructionType.INVOKE);
		
		// Arguments to print, invocations get 1 (pc) + register list
		int naf = (isinvoke ? 1 + __reglist.length:
			__af.length);
		
		// Used to modify some calls
		int encoding = NativeInstruction.encoding(__op);
		
		// Print out arguments to the call
		out.printf("  A:[");
		for (int i = 0, n = naf; i < n; i++)
		{
			int iv = (isinvoke ? (i == 0 ? __args[i] : __reglist[i - 1]) :
				__args[i]);
			
			// Comma
			if (i > 0)
				out.print(", ");
			
			// Can be special?
			boolean canspec = true;
			if ((encoding == NativeInstructionType.IF_ICMP &&
					i == 2) ||
				(encoding == NativeInstructionType.MATH_CONST_INT &&
					i == 1) ||
				(encoding == NativeInstructionType.MATH_CONST_FLOAT &&
					i == 1) ||
				(encoding == NativeInstructionType.IFEQ_CONST &&
					i == 1) ||
				(encoding == NativeInstructionType.ATOMIC_INT_INCREMENT &&
					i == 1) ||
				(encoding == NativeInstructionType.
					ATOMIC_INT_DECREMENT_AND_GET && i == 2))
				canspec = false;
			
			// Is this a special register?
			String spec = null;
			if (canspec)
				switch (iv)
				{
					case NativeCode.ZERO_REGISTER:
						spec = "zero";
						break;
					
					case NativeCode.RETURN_REGISTER:
						spec = "return1";
						break;
					
					case NativeCode.RETURN_REGISTER + 1:
						spec = "return2";
						break;
					
					case NativeCode.EXCEPTION_REGISTER:
						spec = "exception";
						break;
					
					case NativeCode.STATIC_FIELD_REGISTER:
						spec = "sfieldptr";
						break;
					
					case NativeCode.CLASS_TABLE_REGISTER:
						spec = "ctableptr";
						break;
					
					case NativeCode.THREAD_REGISTER:
						spec = "thread";
						break;
					
					case NativeCode.POOL_REGISTER:
						spec = "pool";
						break;
					
					case NativeCode.NEXT_POOL_REGISTER:
						spec = "nextpool";
						break;
					
					case NativeCode.WHERE_IS_THIS:
						spec = "whereis";
						break;
					
					case NativeCode.VOLATILE_A_REGISTER:
						spec = "vola";
						break;
					
					case NativeCode.VOLATILE_B_REGISTER:
						spec = "vola";
						break;
					
					case NativeCode.ARGUMENT_REGISTER_BASE:
						spec = "a0/this";
						break;
				}
			
			// Print special register
			if (spec != null)
				out.printf("%10.10s", spec);
			else
				out.printf("%10d", iv);
		}
		out.print("] | ");
		
		// And register value
		out.printf("V:[");
		int[] registers = __nf._registers;
		for (int i = 0, n = naf; i < n; i++)
		{
			int iv = (isinvoke ? (i == 0 ? __args[i] : __reglist[i - 1]) :
				__args[i]);
				
			if (i > 0)
				out.print(", ");
			
			// Load register value
			if (iv < 0 || iv >= registers.length)
				out.print("----------");
			else
				out.printf("%+10d", registers[iv]);
		}
		out.println("]");
	}
	
	/**
	 * This represents a single frame in the execution stack.
	 *
	 * @since 2019/04/21
	 */
	public static final class Frame
	{
		/** Registers for this frame. */
		final int[] _registers =
			new int[MAX_REGISTERS];
		
		/** The entry PC address. */
		int _entrypc;
		
		/** The PC address for this frame. */
		volatile int _pc;
		
		/** The reference queue positoin. */
		volatile int _rp;
		
		/** Last executed address. */
		int _lastpc;
	}
}
