// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) 2013-2016 Steven Gawroriski <steven@multiphasicapps.net>
//     Copyright (C) 2013-2016 Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// For more information see license.mkd.
// ---------------------------------------------------------------------------

package net.multiphasicapps.tests.zip.streamwriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.SeekableByteChannel;
import java.util.Arrays;
import java.util.Random;
import net.multiphasicapps.tests.IndividualTest;
import net.multiphasicapps.tests.InvalidTestException;
import net.multiphasicapps.tests.TestComparison;
import net.multiphasicapps.tests.TestGroupName;
import net.multiphasicapps.tests.TestFamily;
import net.multiphasicapps.tests.TestInvoker;
import net.multiphasicapps.util.seekablearray.SeekableByteArrayChannel;
import net.multiphasicapps.zip.blockreader.ZipEntry;
import net.multiphasicapps.zip.blockreader.ZipFile;
import net.multiphasicapps.zip.streamwriter.ZipStreamWriter;
import net.multiphasicapps.zip.ZipCompressionType;

/**
 * This tests that ZIP files are correctly streamed and that any output ZIP
 * file can be read by the already existing ZIP code.
 *
 * This test also forms the basis for the stream based ZIP reader support.
 *
 * @since 2016/07/10
 */
public class ZipWriterStreamTest
	implements TestInvoker
{
	/** The compression types to use. */
	private static final ZipCompressionType[] _COMPRESSION_TYPES =
		new ZipCompressionType[]{ZipCompressionType.NO_COMPRESSION};
	
	/** The number of files to write uncompressed and compressed. */
	public static final int NUM_FILES =
		2;
	
	/** The size of the files to write, does not have to be large. */
	public static final int FILE_SIZE =
		384;
	
	/**
	 * {@inheritDoc}
	 * @since 2016/07/10
	 */
	@Override
	public void runTest(IndividualTest __t)
		throws NullPointerException, Throwable
	{
		// Check
		if (__t == null)
			throw new NullPointerException();
		
		// Get random seed to generate some files with
		Random rand = new Random(Long.decode(__t.subName().toString()));
		
		// Create a ZIP with a bunch of random files
		byte[] zipdata = null;
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream())
		{
			// The stream must be closed before the byte array becomes a valid
			// ZIP file
			try (ZipStreamWriter zsw = new ZipStreamWriter(baos))
			{
				// Write a number of files, one compressed and the other not
				// compressed
				int nf = NUM_FILES, fs = FILE_SIZE;
				for (int f = 0; f < nf; f++)
				{
					// Random data used to write
					long frseed = rand.nextLong();
				
					// Setup next ZIP entry
					for (ZipCompressionType ct : _COMPRESSION_TYPES)
						try (OutputStream os = zsw.nextEntry(ct.name() + "/" +
							frseed, ct))
						{
							// Setup random
							Random fr = new Random(frseed);
							
							// Write bytes to the entry
							for (int s = 0; s < fs; s++)
								os.write(fr.nextInt(255));
						}
				}
			}
			
			// Caught an exception, just note the failing stream
			catch (Throwable t)
			{
				// Could fail to note
				try
				{
					__t.result("failpartialstream").note(baos.toByteArray());
				}
			
				// Could not note it either
				catch (Throwable x)
				{
					t.addSuppressed(x);
				}
			
				// Rethrow
				throw t;
			}
			
			// Get ZIP data
			zipdata = baos.toByteArray();
		}
		
		// Read the input ZIP file that was created in memory and try to
		// see if entries were written correctly. If the ZIP cannot be opened
		// here then it is malformed.
		try (ZipFile zip = ZipFile.open(new SeekableByteArrayChannel(zipdata)))
		{
			// Go through all entries and check if they contain valid data
			for (ZipEntry ze : zip)
			{
				// Get the name
				String name = ze.name();
				
				// Split at the directory separator to determine the seed being
				// used.
				int slash = name.indexOf('/');
				if (slash < 0)
					__t.result("illegalname").note(name);
				
				// Decode the seed
				long eseed = Long.decode(name.substring(slash + 1));
				
				// Setup random generator to check against
				Random fr = new Random(eseed);
				
				// Read the entry data into a buffer
				try (InputStream is = ze.open();
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ByteArrayOutputStream barr = new ByteArrayOutputStream())
				{
					// Load file data and random data into buffers
					for (;;)
					{
						// Read value
						int val = is.read();
					
						// EOF?
						if (val < 0)
							break;
						
						// Write
						barr.write(fr.nextInt(255));
						baos.write(val);
					}
					
					// Compare the data
					__t.result("name").compareByteArrays(TestComparison.EQUALS,
						barr.toByteArray(), baos.toByteArray());
				}
			}
		}
			
		// Caught an exception, dump the failing ZIP
		catch (Throwable t)
		{
			// Could fail to note
			try
			{
				__t.result("faildecodestream").note(zipdata);
			}
		
			// Could not note it either
			catch (Throwable x)
			{
				t.addSuppressed(x);
			}
		
			// Rethrow
			throw t;
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/07/10
	 */
	@Override
	public TestFamily testFamily()
	{
		// Generate some random seeds
		Random rand = new Random(0x1989_07_06);
		
		return new TestFamily(
			"net.multiphasicapps.zip.streamwriter.ZipStreamWriter",
			Long.toString(rand.nextLong()),
			Long.toString(rand.nextLong()),
			Long.toString(rand.nextLong()));
	}
}

