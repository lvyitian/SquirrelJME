// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) 2013-2016 Steven Gawroriski <steven@multiphasicapps.net>
//     Copyright (C) 2013-2016 Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// For more information see license.mkd.
// ---------------------------------------------------------------------------

package net.multiphasicapps.markdownwriter;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Writer;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Formatter;
import java.util.Objects;

/**
 * This is a class which writes markdown formatted text to the specified
 * {@link Appendable} which may be any implentation of one. This handle all
 * of the standard formatting details that markdown supports.
 *
 * This writer supports closing and flushing, however those operations will
 * only be performed on the wrapped {@link Appendable} if those also implement
 * such things.
 *
 * This class is not thread safe.
 *
 * @since 2016/09/13
 */
public class MarkdownWriter
	implements Appendable, Closeable, Flushable
{
	/** Markdown right column limit. */
	public static final int RIGHT_COLUMN =
		72;
	
	/** Where text may be written to. */
	protected final Appendable append;
	
	/** Formatter to write output text. */
	protected final Formatter formatter;
	
	/** The current section being written. */
	volatile __Section__ _section;
	
	/**
	 * Initializes the markdown writer.
	 *
	 * @param __a The appendable to send characters to.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/09/13
	 */
	public MarkdownWriter(Appendable __a)
		throws NullPointerException
	{
		// Check
		if (__a == null)
			throw new NullPointerException("NARG");
		
		// Set
		this.append = __a;
		
		// Setup formatter
		this.formatter = new Formatter(this);
	}
	
	/**
	 * {@inheritDoc}
	 * @sicne 2016/09/13
	 */
	@Override
	public MarkdownWriter append(char __c)
		throws IOException
	{
		__sectionedPut(__c, false);
		return this;
	}
	
	/**
	 * {@inheritDoc}
	 * @sicne 2016/09/13
	 */
	@Override
	public MarkdownWriter append(CharSequence __cs)
		throws IOException
	{
		return this.append(__cs, 0, __cs.length());
	}
	
	/**
	 * {@inheritDoc}
	 * @sicne 2016/09/13
	 */
	@Override
	public MarkdownWriter append(CharSequence __cs, int __s, int __e)
		throws IOException
	{
		for (int i = __s; i < __e; i++)
			__sectionedPut(__cs.charAt(i), false);
		return this;
	}
	
	/**
	 * {@inheritDoc}
	 * @sicne 2016/09/13
	 */
	@Override
	public void close()
		throws IOException
	{
		// Only close if it is closeable
		Appendable append = this.append;
		if (append instanceof Closeable)
			((Closeable)append).close();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/09/13
	 */
	@Override
	public void flush()
		throws IOException
	{
		// Only flush if the target is appendable also
		Appendable append = this.append;
		if (append instanceof Flushable)
			((Flushable)append).flush();
	}
	
	/**
	 * Prints the specified header into the output document.
	 *
	 * @param __abs If {@code true} then the header is at the specified level,
	 * otherwise if {@code false} it will be relative to the existing header
	 * level.
	 * @param __level If absolute then this level is set where the level is
	 * based on an index of one, otherwise this will be the relative header
	 * level adjustment from the current header level.
	 * @param __s The text to print.
	 * @throws IOException On write errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/09/13
	 */
	public void header(boolean __abs, int __level, String __s)
		throws IOException, NullPointerException
	{
		// Check
		if (__s == null)
			throw new NullPointerException("NARG");
		
		// Setup section
		__SectionHeader__ header = new __SectionHeader__(this, __abs, __level);
		
		// Print header text
		append(__s);
		
		// Enter paragraph mode
		paragraph();
	}
	
	/**
	 * End a list.
	 *
	 * @throws IOException On write errors.
	 * @since 2016/10/01
	 */
	public void listEnd()
		throws IOException
	{
		throw new Error("TODO");
	}
	
	/**
	 * Go the next item in the list.
	 *
	 * @throws IOException On write errros.
	 * @since 2016/10/01
	 */
	public void listNext()
		throws IOException
	{
		throw new Error("TODO");
	}
	
	/**
	 * Start a list.
	 *
	 * @throws IOException On write errors.
	 * @since 2016/10/01
	 */
	public void listStart()
		throws IOException
	{
		throw new Error("TODO");
	}
	
	/**
	 * Enters paragraph mode which may be used .
	 *
	 * @throws IOException On write errors.
	 * @since 2016/10/02
	 */
	public void paragraph()
		throws IOException
	{
		new __SectionParagraph__(this);
	}
	
	/**
	 * Prints a single character.
	 *
	 * @param __c The character to print.
	 * @throws IOException On write errors.
	 * @since 2016/10/02
	 */
	public void print(char __c)
		throws IOException
	{
		append(__c);
	}
	
	/**
	 * Prints the specified object.
	 *
	 * @param __o The object to print.
	 * @throws IOException On write errors.
	 * @since 2016/10/01
	 */
	public void print(Object __o)
		throws IOException
	{
		append(Objects.toString(__o));
	}
	
	/**
	 * Prints formatted text to the output.
	 *
	 * @param __f The format specifier.
	 * @param __args The format arguments.
	 * @throws IOException On write errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/09/13
	 */
	public void printf(String __f, Object... __args)
		throws IOException, NullPointerException
	{
		// Check
		if (__f == null)
			throw new NullPointerException("NARG");
		
		// Format
		this.formatter.format(__f, __args);
	}
	
	/**
	 * Prints the end of the line.
	 *
	 * @throws IOException On write errors.
	 * @since 2016/10/01
	 */
	public void println()
		throws IOException
	{
		append('\n');
	}
	
	/**
	 * Prints the specified object followed by a new line.
	 *
	 * @param __o The object to print.
	 * @throws IOException On write errors.
	 * @since 2016/10/01
	 */
	public void println(Object __o)
		throws IOException
	{
		print(__o);
		println();
	}
	
	/**
	 * Prints a URI to the output document.
	 *
	 * @param __uri The URI to print.
	 * @throws IOException On write errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/10/01
	 */
	public void uri(String __uri)
		throws IOException, NullPointerException
	{
		// Check
		if (__uri == null)
			throw new NullPointerException("NARG");
		
		// Print it out
		__sectionedPut('<', true);
		append(__uri);
		__sectionedPut('>', true);
	}
	
	/**
	 * Prints a URI to the output document with the given display text.
	 *
	 * @param __uri The URI to point to.
	 * @param __text The display text for the URI.
	 * @throws IOException On write errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/10/01
	 */
	public void uri(String __uri, String __text)
		throws IOException, NullPointerException
	{
		// Check
		if (__uri == null || __text == null)
			throw new NullPointerException("NARG");
		
		// Print it out
		__sectionedPut('[', true);
		append(__text);
		__sectionedPut(']', true);
		__sectionedPut('(', true);
		append(__uri);
		__sectionedPut(')', true);
	}
	
	/**
	 * Prints a URI to the output document with the given display text.
	 *
	 * @param __uri The URI to point to.
	 * @param __text The display text for the URI.
	 * @param __title The text text for the URI.
	 * @throws IOException On write errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/10/01
	 */
	public void uri(String __uri, String __text, String __title)
		throws IOException, NullPointerException
	{
		// Check
		if (__uri == null || __text == null || __title == null)
			throw new NullPointerException("NARG");
		
		// Print it out
		__sectionedPut('[', true);
		append(__text);
		__sectionedPut(']', true);
		__sectionedPut('(', true);
		append(__uri);
		__sectionedPut(' ', true);
		__sectionedPut('"', true);
		append(__title);
		__sectionedPut('"', true);
		__sectionedPut(')', true);
	}
	
	/**
	 * Places a single character into the output sending the character to
	 * be printed to the currently being written to section.
	 *
	 * @param __c The character to put.
	 * @param __nospec If {@code true} then the character is not given
	 * special handling.
	 * @throws IOException On write errors.
	 * @since 2016/09/13
	 */
	private void __sectionedPut(char __c, boolean __nospec)
		throws IOException
	{
		// Ignore CR
		if (__c == '\r')
			return;
		
		throw new Error("TODO");
	}
}

