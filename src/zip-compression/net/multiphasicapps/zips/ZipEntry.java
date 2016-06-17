// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) 2013-2016 Steven Gawroriski <steven@multiphasicapps.net>
//     Copyright (C) 2013-2016 Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU Affero General Public License v3+, or later.
// For more information see license.mkd.
// ---------------------------------------------------------------------------

package net.multiphasicapps.zips;

import java.io.InputStream;
import java.io.IOException;

/**
 * This represents an entry within a standard ZIP file.
 *
 * @since 2016/02/03
 */
public abstract class ZipEntry
{
	/**
	 * Initializes the file entry.
	 *
	 * @throws IOException On read errors.
	 * @since 2016/03/05
	 */
	public ZipEntry()
		throws IOException
	{
	}
	
	/**
	 * Returns the index of this entry.
	 *
	 * @return The ZIP file entry index.
	 * @since 2016/03/06
	 */
	public abstract int index();
	
	/**
	 * Opens an input stream of the ZIP file data.
	 *
	 * @return A stream which reads the deflated or stored data.
	 * @throws IOException On read errors.
	 * @since 2016/03/06
	 */
	public abstract InputStream open()
		throws IOException;
	
	/**
	 * Returns the name of the file.
	 *
	 * @return The file name.
	 * @throws IOException On read errors.
	 * @since 2016/03/06
	 */
	public abstract String name()
		throws IOException;
	
	/**
	 * {@inheritDoc}
	 * @since 2016/03/06
	 */
	@Override
	public final String toString()
	{
		// Possible that the name could not be read
		try
		{
			return name();
		}
		
		// Could not read the name
		catch (IOException ioe)
		{
			return "<IOException>";
		}
	}
}

