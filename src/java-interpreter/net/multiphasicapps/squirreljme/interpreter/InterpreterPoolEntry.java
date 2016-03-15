// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) 2013-2016 Steven Gawroriski <steven@multiphasicapps.net>
//     Copyright (C) 2013-2016 Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU Affero General Public License v3+, or later.
// For more information see license.mkd.
// ---------------------------------------------------------------------------

package net.multiphasicapps.squirreljme.interpreter;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.UTFDataFormatException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Objects;
import net.multiphasicapps.descriptors.BinaryNameSymbol;
import net.multiphasicapps.descriptors.ClassNameSymbol;
import net.multiphasicapps.descriptors.FieldSymbol;
import net.multiphasicapps.descriptors.IllegalSymbolException;
import net.multiphasicapps.descriptors.MemberTypeSymbol;
import net.multiphasicapps.descriptors.MethodSymbol;

/**
 * This represents the base class for all constant pool entries.
 *
 * @since 2016/03/15
 */
public abstract class InterpreterPoolEntry
{
	/** The owning pool. */
	protected final InterpreterClassPool pool;
	
	/**
	 * Initializes the base of an entry.
	 *
	 * @param __icp The owning constant pool.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/03/15
	 */
	private InterpreterPoolEntry(InterpreterClassPool __icp)
		throws NullPointerException
	{
		// Check
		if (__icp == null)
			throw new NullPointerException();
		
		// Set
		pool = __icp;
	}
	
	/**
	 * Checks the range of a reference to make sure it is within bounds of
	 * an existing entry.
	 *
	 * @param __v The index to check the range for.
	 * @return {@code __v} if the range is valid.
	 * @throws InterpreterClassFormatError If the range is not valid.
	 * @since 2016/03/15
	 */
	int __rangeCheck(int __v)
		throws InterpreterClassFormatError
	{
		if (__v > 0 && __v < pool.size())
			return __v;
		throw new InterpreterClassFormatError("Reference index " + __v +
			"is not within with the constant pool bounds.");
	}
	
	/**
	 * This represents the base of the constant value pool.
	 *
	 * @param <C> The type of constant value to return.
	 * @since 2016/03/15
	 */
	public static abstract class ConstantValue<C>
		extends InterpreterPoolEntry
	{
		/** The type of value to store. */
		protected final Class<C> castas;
		
		/**
		 * Initializes the base constant information.
		 *
		 * @param __icp The owning constant pool.
		 * @param __cl The class to cast to.
		 * @throws NullPointerException On null arguments.
		 * @since 2016/03/15
		 */
		ConstantValue(InterpreterClassPool __icp, Class<C> __cl)
		{
			super(__icp);
			
			// Check
			if (__cl == null)
				throw new NullPointerException();
			
			// Set
			castas = __cl;
		}
		
		/**
		 * Returns the value of the constant.
		 *
		 * @return The constant value.
		 * @since 2016/03/15
		 */
		public abstract C getValue();
		
		/**
		 * {@inheritDoc}
		 * @since 2016/03/15
		 */
		@Override
		public final String toString()
		{
			return Objects.toString(getValue());
		}
	}
	
	/**
	 * This represents a reference type.
	 *
	 * @param V The symbol
	 * @since 2016/03/15
	 */
	public static abstract class MemberReference<V extends MemberTypeSymbol>
		extends InterpreterPoolEntry
	{
		/** The type to cast the type as. */
		protected final Class<V> castas;
		
		/** The class index. */
		protected final int classdx;
		
		/** The name and type index. */
		protected final int natdx;
		
		/**
		 * This initializes
		 *
		 * @param __icp The owning constant pool.
		 * @param __dis The constant data to load in.
		 * @param __cl The class to cast the type to.
		 * @throws IOException On read errors.
		 * @throws NullPointerException On null arguments.
		 * @since 2016/03/15
		 */
		MemberReference(InterpreterClassPool __icp,
			DataInputStream __dis, Class<V> __cl)
			throws IOException, NullPointerException
		{
			super(__icp);
			
			// Check
			if (__dis == null || __cl == null)
				throw new NullPointerException();
			
			// Set
			castas = __cl;
			
			// Read in
			classdx = __rangeCheck(__dis.readUnsignedShort());
			natdx = __rangeCheck(__dis.readUnsignedShort());
		}
		
		/**
		 * Returns the utilized class name.
		 *
		 * @return The class name for the member reference.
		 * @since 2016/03/15
		 */
		public final ClassName className()
		{
			return pool.<ClassName>getAs(classdx,
				ClassName.class);
		}
	}
	
	/**
	 * This represents the name of a class.
	 *
	 * @since 2016/03/15
	 */
	public static final class ClassName
		extends InterpreterPoolEntry
	{
		/** The class name index. */
		protected final int index;
		
		/** The actual class symbol. */
		private volatile Reference<ClassNameSymbol> _cname;
		
		/**
		 * Initializes the class name.
		 *
		 * @param __icp The owning constant pool.
		 * @param __dis Input stream to read data from.
		 * @throws InterpreterClassFormatError If the class name is not
		 * valid.
		 * @throws IOException On read errors.
		 * @throws NullPointerException On null arguments.
		 * @since 2016/03/15
		 */
		ClassName(InterpreterClassPool __icp, DataInputStream __dis)
			throws InterpreterClassFormatError, IOException,
				NullPointerException
		{
			super(__icp);
			
			// Check
			if (__dis == null)
				throw new NullPointerException();
			
			// Get id
			index = __rangeCheck(__dis.readUnsignedShort());
		}
		
		/**
		 * Returns the symbol associated with this class.
		 *
		 * @return The class name symbol.
		 * @throws InterpreterClassFormatError If the class name symbol is
		 * invalid.
		 * @since 2016/03/15
		 */
		public ClassNameSymbol symbol()
			throws InterpreterClassFormatError
		{
			// Get reference
			Reference<ClassNameSymbol> ref = _cname;
			ClassNameSymbol rv = null;
			
			// In reference?
			if (ref != null)
				rv = ref.get();
			
			// Needs initialization
			if (rv == null)
				try
				{
					_cname = new WeakReference<>((rv = new ClassNameSymbol(
						pool.<UTF8>getAs(index, UTF8.class).toString())));
				}
				
				// Bad symbol
				catch (IllegalSymbolException ise)
				{
					throw new InterpreterClassFormatError(ise);
				}
			
			// Return it
			return rv;
		}
	}
	
	/**
	 * This represents a constant string value.
	 *
	 * @since 2016/03/15
	 */
	public static final class ConstantString
		extends ConstantValue<String>
		implements CharSequence
	{
		/** The indexed UTF-8 constant. */
		protected final int index;
		
		/**
		 * Initializes the string constant.
		 *
		 * @param __icp The owning constant pool.
		 * @param __dis Data source.
		 * @throws IOException On read errors.
		 * @throws NullPointerException On null arguments.
		 * @since 2016/03/15
		 */
		ConstantString(InterpreterClassPool __icp,
			DataInputStream __dis)
			throws IOException, NullPointerException
		{
			super(__icp, String.class);
			
			// Check
			if (__dis == null)
				throw new NullPointerException();
			
			// Read the string index
			index = __dis.readUnsignedShort();
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2016/03/15
		 */
		@Override
		public char charAt(int __i)
		{
			return toString().charAt(__i);
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2016/03/15
		 */
		@Override
		public String getValue()
		{
			return pool.<UTF8>getAs(index, UTF8.class).toString();
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2016/03/15
		 */
		@Override
		public int length()
		{
			return toString().length();
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2016/03/15
		 */
		@Override
		public CharSequence subSequence(int __s, int __e)
		{
			return toString().subSequence(__s, __e);
		}
	}
	
	/**
	 * This represents a field reference.
	 *
	 * @since 2016/03/15
	 */
	public static final class FieldReference
		extends MemberReference<FieldSymbol>
	{
		/**
		 * Initializes the field reference.
		 *
		 * @param __icp The owning constant pool.
		 * @param __dis Data source.
		 * @throws IOException On read errors.
		 * @since 2016/03/15
		 */
		FieldReference(InterpreterClassPool __icp,
			DataInputStream __dis)
			throws IOException
		{
			super(__icp, __dis, FieldSymbol.class);
		}
	}
	
	/**
	 * This implements a interface method reference.
	 *
	 * @since 2016/03/15
	 */
	public static final class InterfaceMethodReference
		extends MemberReference<MethodSymbol>
	{
		/**
		 * Initializes the interface method reference.
		 *
		 * @param __icp The owning constant pool.
		 * @param __dis Data source.
		 * @throws IOException On read errors.
		 * @since 2016/03/15
		 */
		InterfaceMethodReference(InterpreterClassPool __icp,
			DataInputStream __dis)
			throws IOException
		{
			super(__icp, __dis, MethodSymbol.class);
		}
	}
	
	/**
	 * This implements a method reference.
	 *
	 * @since 2016/03/15
	 */
	public static final class MethodReference
		extends MemberReference<MethodSymbol>
	{
		/**
		 * Initializes the method reference.
		 *
		 * @param __icp The owning constant pool.
		 * @param __dis Data source.
		 * @throws IOException On read errors.
		 * @since 2016/03/15
		 */
		MethodReference(InterpreterClassPool __icp,
			DataInputStream __dis)
			throws IOException
		{
			super(__icp, __dis, MethodSymbol.class);
		}
	}
	
	/**
	 * This represents name and type information.
	 *
	 * @since 2016/03/15
	 */
	public static final class NameAndType
		extends InterpreterPoolEntry
	{
		/** Name index. */
		protected final int namedx;
		
		/** Type index. */
		protected final int typedx;
		
		/**
		 * Initializes name and type information.
		 *
		 * @param __icp The owning constant pool.
		 * @param __dis Data source.
		 * @throws IOException On read errors.
		 * @throws NullPointerException On null arguments.
		 * @since 2016/03/15
		 */
		NameAndType(InterpreterClassPool __icp, DataInputStream __dis)
			throws IOException, NullPointerException
		{
			super(__icp);
			
			// Check
			if (__dis == null)
				throw new NullPointerException();
			
			// Read values
			namedx = __rangeCheck(__dis.readUnsignedShort());
			typedx = __rangeCheck(__dis.readUnsignedShort());
		}
	}
	
	/**
	 * This is a UTF-8 string constant.
	 *
	 * @since 2016/03/13
	 */
	public static final class UTF8
		extends InterpreterPoolEntry
		implements CharSequence
	{
		/** Internally read string. */
		protected final String string;
		
		/**
		 * Initializes the constant value.
		 *
		 * @param __icp The owning constant pool.
		 * @param __is Data input source.
		 * @throws InterpreterClassFormatError If the modfied UTF string is
		 * malformed.
		 * @throws IOException On read errors.
		 * @throws NullPointerException On null arguments.
		 * @since 2016/03/13
		 */
		UTF8(InterpreterClassPool __icp, DataInputStream __dis)
			throws InterpreterClassFormatError, IOException,
				NullPointerException
		{
			super(__icp);
			
			// Check
			if (__dis == null)
				throw new NullPointerException();
			
			// Read
			try
			{
				string = __dis.readUTF();
			}
			
			// Malformed sequence
			catch (UTFDataFormatException utfdfe)
			{
				throw new InterpreterClassFormatError(utfdfe);
			}
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2016/03/13
		 */
		@Override
		public char charAt(int __i)
		{
			return string.charAt(__i);
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2016/03/13
		 */
		@Override
		public int length()
		{
			return string.length();
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2016/03/13
		 */
		@Override
		public CharSequence subSequence(int __s, int __e)
		{
			return string.subSequence(__s, __e);
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2016/03/13
		 */
		@Override
		public String toString()
		{
			return string;
		}
	}
}

