// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.vm.summercoat;

import cc.squirreljme.vm.VMClassLibrary;
import cc.squirreljme.vm.VMException;
import cc.squirreljme.vm.VMSuiteManager;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class contains the memory information for every single suite which
 * exists within the VM.
 *
 * @since 2019/04/21
 */
public final class SuitesMemory
	implements ReadableMemory
{
	/** Configuration and table space size. */
	public static final int CONFIG_TABLE_SIZE =
		1048576;
	
	/** The suite chunk size. */
	public static final int SUITE_CHUNK_SIZE =
		4194304;
	
	/** The suite manage to base from. */
	protected final VMSuiteManager suites;
	
	/** Offset. */
	protected final int offset;
	
	/** The size of this memory region. */
	protected final int size;
	
	/** The suite configuration table. */
	protected final RawMemory configtable;
	
	/** The individual regions of suite memory. */
	private final SuiteMemory[] _suitemem;
	
	/** This is the mapping of suite names to memory. */
	private final Map<String, SuiteMemory> _suitemap;
	
	/** Was the config table initialized? */
	private volatile boolean _didconfiginit;
	
	/**
	 * Initializes the suites memory.
	 *
	 * @param __off The offset of suite memory.
	 * @param __sm The suite manager.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/21
	 */
	public SuitesMemory(int __off, VMSuiteManager __sm)
		throws NullPointerException
	{
		if (__sm == null)
			throw new NullPointerException("NARG");
		
		// Set suites
		this.suites = __sm;
		
		// All the libraries which are available for usage
		String[] libnames = __sm.listLibraryNames();
		
		// Setup configuration space
		this.configtable = new RawMemory(__off, CONFIG_TABLE_SIZE);
		
		// Setup suite memory area
		int n = libnames.length;
		SuiteMemory[] suitemem = new SuiteMemory[n];
		Map<String, SuiteMemory> suitemap = new LinkedHashMap<>();
		
		// Setup memory regions for the various suites
		int off = CONFIG_TABLE_SIZE;
		for (int i = 0; i < n; i++, off += SUITE_CHUNK_SIZE)
		{
			// Set
			String ln = libnames[i];
			SuiteMemory sm;
			suitemem[i] = (sm = new SuiteMemory(off, __sm, ln));
			
			// Also use map for quick access
			suitemap.put(ln, sm);
			
			// Debug
			todo.DEBUG.note("MMap Suite %s -> %08x", ln, __off + off);
		}
		
		// Store all the various suite memories
		this._suitemem = suitemem;
		this._suitemap = suitemap;
		
		// Store final memory parameters
		this.offset = __off;
		this.size = off;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/21
	 */
	@Override
	public int memReadInt(int __addr)
	{
		// Reading from the config table?
		if (__addr < CONFIG_TABLE_SIZE)
		{
			// Needs to be initialized
			if (!this._didconfiginit)
			{
				// Initialize config space memory
				this.__initConfigSpace();
				
				// Set to initialized
				this._didconfiginit = true;
			}
			
			// Read from memory
			return this.configtable.memReadInt(__addr);
		}
		
		throw new todo.TODO();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/21
	 */
	@Override
	public int memRegionOffset()
	{
		return this.offset;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/04/21
	 */
	@Override
	public final int memRegionSize()
	{
		return 0x7FFFFFFF;
	}
	
	/**
	 * Initializes the configuration space.
	 *
	 * @since 2019/04/21
	 */
	private final void __initConfigSpace()
	{
		// Do not initialize twice!
		if (this._didconfiginit)
			return;
		
		// The bootstrap is in CLDC compact
		SuiteMemory cldc = this._suitemap.get("cldc-compact.jar");
		try
		{
			cldc.__init();
		}
		
		// {@squirreljme.error AE0t Could not initialize CLDC library.}
		catch (IOException e)
		{
			throw new RuntimeException("AE0t", e);
		}
		
		if (true)
			throw new todo.TODO();
		
		// Did initialize
		this._didconfiginit = true;
	}
}
