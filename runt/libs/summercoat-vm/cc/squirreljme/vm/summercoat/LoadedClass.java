// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.vm.summercoat;

import dev.shadowtail.classfile.mini.MinimizedClassFile;
import dev.shadowtail.classfile.mini.MinimizedMethod;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.Map;
import net.multiphasicapps.classfile.MethodDescriptor;
import net.multiphasicapps.classfile.MethodName;
import net.multiphasicapps.classfile.MethodNameAndType;

/**
 * This represents a class which has been loaded.
 *
 * @since 2019/01/06
 */
public final class LoadedClass
{
	/** The minimized class. */
	protected final MinimizedClassFile miniclass;
	
	/** The super class. */
	protected final LoadedClass superclass;
	
	/** Runtime constant pool, which is initialized when it is needed. */
	protected final RuntimeConstantPool runpool;
	
	/** Interface classes. */
	final LoadedClass[] _interfaces;
	
	/** Static methods. */
	private final Map<MethodNameAndType, MethodHandle> _smethods;
	
	/** Instance methods, note that these are initialized as static! */
	private final Map<MethodNameAndType, MethodHandle> _imethods;
	
	/** String form. */
	private Reference<String> _string;
	
	/** Has this class been initialized? */
	volatile boolean _beeninit;
	
	/**
	 * Initializes the loaded class.
	 *
	 * @param __cf The minimized class file.
	 * @param __sn The super class.
	 * @param __in The interfaces.
	 * @throws NullPointerException On null arguments, except for {@code __sn}.
	 * @since 2019/04/17
	 */
	public LoadedClass(MinimizedClassFile __cf, LoadedClass __sn,
		LoadedClass[] __in)
		throws NullPointerException
	{
		if (__cf == null || __in == null)
			throw new NullPointerException("NARG");
		
		for (LoadedClass o : (__in = __in.clone()))
			if (o == null)
				throw new NullPointerException("NARG");
		
		this.miniclass = __cf;
		this.superclass = __sn;
		this._interfaces = __in;
		
		// Run-time constant pool
		RuntimeConstantPool runpool;
		this.runpool = (runpool = new RuntimeConstantPool(__cf.pool()));
		
		// Initialize static methods
		Map<MethodNameAndType, MethodHandle> smethods = new LinkedHashMap<>();
		for (MinimizedMethod mm : __cf.methods(true))
			smethods.put(new MethodNameAndType(mm.name, mm.type),
				new StaticMethodHandle(runpool, mm));
		this._smethods = smethods;
		
		// Initialize instance methods
		// Note that these are initialized as static handles to refer to them
		// directly, just instance based lookup will use a different handle
		// type...
		Map<MethodNameAndType, MethodHandle> imethods = new LinkedHashMap<>();
		for (MinimizedMethod mm : __cf.methods(false))
			smethods.put(new MethodNameAndType(mm.name, mm.type),
				new StaticMethodHandle(runpool, mm));
		this._imethods = imethods;
	}
	
	/**
	 * Looks up the given method.
	 *
	 * @param __lut The type of lookup to perform.
	 * @param __static Is the specified method static?
	 * @param __name The name of the method.
	 * @param __desc The method descriptor.
	 * @return The handle to the method.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/01/10
	 */
	public final MethodHandle lookupMethod(MethodLookupType __lut,
		boolean __static, String __name, String __desc)
		throws NullPointerException
	{
		return this.lookupMethod(__lut, __static, new MethodName(__name),
			new MethodDescriptor(__desc));
	}
	
	/**
	 * Looks up the given method.
	 *
	 * @param __lut The type of lookup to perform.
	 * @param __static Is the specified method static?
	 * @param __name The name of the method.
	 * @param __desc The method descriptor.
	 * @return The handle to the method.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/01/10
	 */
	public final MethodHandle lookupMethod(MethodLookupType __lut, 
		boolean __static, MethodName __name, MethodDescriptor __desc)
		throws NullPointerException
	{
		if (__lut == MethodLookupType.STATIC)
			return _smethods.get(new MethodNameAndType(__name, __desc));
		throw new todo.TODO();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/17
	 */
	@Override
	public final String toString()
	{
		Reference<String> ref = this._string;
		String rv;
		
		if (ref == null || null == (rv = ref.get()))
			this._string = new WeakReference<>((rv = "Class " +
				this.miniclass.thisName()));
		
		return rv;
	}
}

