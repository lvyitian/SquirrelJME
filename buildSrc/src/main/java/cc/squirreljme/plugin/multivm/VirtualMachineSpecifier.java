// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.plugin.multivm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Map;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.SourceSet;

/**
 * Provider interface for the various virtual machines that are available.
 * 
 * Note that source sets refer to {@link SourceSet#MAIN_SOURCE_SET_NAME}
 * and {@link SourceSet#TEST_SOURCE_SET_NAME}.
 *
 * @since 2020/08/06
 */
@SuppressWarnings("InterfaceWithOnlyOneDirectInheritor")
public interface VirtualMachineSpecifier
{
	/**
	 * Determines the name of the library that the provider uses for what is
	 * executed by the virtual machine and output by the virtual machine.
	 * 
	 * @param __project The project to get from.
	 * @param __sourceSet The source set to determine the path for.
	 * @return The path to the source set's library.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/08/06
	 */
	String outputLibraryName(Project __project, String __sourceSet)
		throws NullPointerException;
	
	/**
	 * Processes the library.
	 * 
	 * @param __in The input data, this may be a JAR or otherwise.
	 * @param __out The destination output file.
	 * @throws IOException On read/write errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/08/15
	 */
	void processLibrary(InputStream __in, OutputStream __out)
		throws IOException, NullPointerException;
	
	/**
	 * Spawns a virtual machine.
	 * 
	 * @param __task The task being executed, may be used as context.
	 * @param __stdOut Standard output.
	 * @param __stdErr Standard error.
	 * @param __mainClass The main class to execute.
	 * @param __sysProps The system properties to define.
	 * @param __classPath The class path of the execution target.
	 * @param __args Arguments to the started program.
	 * @return The exit status of the program
	 * @throws NullPointerException On null arguments.
	 * @since 2020/08/15
	 */
	int spawnJvm(Task __task, OutputStream __stdOut,
		OutputStream __stdErr, String __mainClass,
		Map<String, String> __sysProps, Path[] __classPath,
		String... __args)
		throws NullPointerException;  
	
	/**
	 * Returns the virtual machine name using the given name format.
	 * 
	 * @param __format The format of the name to use.
	 * @return The formatted name.
	 * @throws NullPointerException On null arguments.
	 * @since 2020/08/06
	 */
	String vmName(VMNameFormat __format)
		throws NullPointerException;
	
	/**
	 * Returns the name of the project that is used to run this using the
	 * emulator.
	 * 
	 * @return The project used for running the emulator.
	 * @since 2020/08/16
	 */
	String emulatorProject();
}