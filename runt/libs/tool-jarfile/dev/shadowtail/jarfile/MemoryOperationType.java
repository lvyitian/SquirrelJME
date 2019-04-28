// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package dev.shadowtail.jarfile;

/**
 * This represents the type of memory operation to perform.
 *
 * @since 2019/04/27
 */
public enum MemoryOperationType
{
	/** Normal non-modified write. */
	NORMAL,
	
	/** Offset by RAM. */
	OFFSET_RAM,
	
	/** Offset by boot JAR. */
	OFFSET_JAR,
	
	/** End. */
	;
}
