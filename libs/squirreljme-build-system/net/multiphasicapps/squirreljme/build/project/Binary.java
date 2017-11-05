// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package net.multiphasicapps.squirreljme.build.project;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import net.multiphasicapps.collections.SortedTreeSet;
import net.multiphasicapps.squirreljme.java.manifest.JavaManifest;
import net.multiphasicapps.squirreljme.java.manifest.JavaManifestKey;
import net.multiphasicapps.zip.streamreader.ZipStreamReader;

/**
 * This class represents a binary which has been loaded by the binary manager.
 *
 * @since 2017/10/31
 */
public final class Binary
{
	/** The name of this binary. */
	protected final SourceName name;
	
	/** The source code for this binary, may be null if there is none. */
	protected final Source source;
	
	/** The path to the binary for this executable. */
	protected final Path path;
	
	/** Reference to the owning binary manager, used for dependencies. */
	private final Reference<BinaryManager> _managerref;
	
	/** The cached manifest for this entry. */
	private volatile Reference<JavaManifest> _manifest;
	
	/**
	 * Initializes the binary.
	 *
	 * @param __ref The reference to the binary manager, to find other binaries
	 * such as for dependencies.
	 * @param __name The name of this binary.
	 * @param __source The source of this binary, may be {@code null} if there
	 * is no source.
	 * @throws InvalidBinaryException If the tiven 
	 * @throws NoSuchBinaryException If the given binary does not exist.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/11/02
	 */
	Binary(Reference<BinaryManager> __ref, SourceName __name, Source __source,
		Path __path)
		throws InvalidBinaryException, NoSuchBinaryException,
			NullPointerException
	{
		if (__ref == null || __name == null || __path == null)
			throw new NullPointerException("NARG");
		
		// Set
		this._managerref = __ref;
		this.name = __name;
		this.source = __source;
		this.path = __path;
	}
	
	/**
	 * Returns all binaries which are dependencies of this project, this
	 * includes any binaries which are recursively depended upon.
	 *
	 * @return The dependencies for this binary.
	 * @since 2017/11/02
	 */
	public final Binary[] allDependencies()
	{
		Set<Binary> rv = new SortedTreeSet<>(); 
		Deque<Binary> queue = new ArrayDeque<>();
		
		// Initially start with the current dependencies
		for (Binary b : dependencies())
			queue.addLast(b);
		
		// Always drain the queue
		while (!queue.isEmpty())
		{
			// Only process once
			Binary b = queue.removeFirst();
			if (!rv.add(b))
				continue;
			
			// Go through those dependencies
			for (Binary d : b.dependencies())
				queue.addLast(d);
		}
		
		// Always remove this from the return value
		rv.remove(this);
		return rv.<Binary>toArray(new Binary[rv.size()]);
	}
	 
	/**
	 * Returns the binaries which are direct dependencies of this binary.
	 *
	 * @return The dependencies for this binary.
	 * @since 2017/11/05
	 */
	public final Binary[] dependencies()
	{
		throw new todo.TODO();
	}
	
	/**
	 * Returns the name of the project.
	 *
	 * @return The project name.
	 * @since 2017/11/02
	 */
	public final SourceName name()
	{
		return this.name;
	}
	
	/**
	 * Opens the binary as a ZIP file for reading the contents.
	 *
	 * @return The stream over the ZIP's contents.
	 * @since 2017/11/02
	 */
	public final ZipStreamReader zipStream()
	{
		throw new todo.TODO();
	}
}

