/* ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// --------------------------------------------------------------------------*/

#include <stdio.h>

#include "jni.h"
#include "cc_squirreljme_jvm_Assembly.h"

extern "C"
{

/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/

#define CAST_OBJECT_TO_LONG(v) ((jlong)((uintptr_t)(v)))
#define CAST_LONG_TO_OBJECT(v) ((jobject)(uintptr_t)(v))

static jint javaIsInstance(JNIEnv* env, jobject object, const char* name)
{
	jclass classy;

	// See if the class exists before we check
	classy = env->FindClass(name);
	if (classy == NULL)
		return JNI_FALSE;

	return env->IsInstanceOf(object, classy);
}

/** Called when the library is loaded. */
JNIEXPORT jint JNICALL JNI_OnLoad
	(JavaVM* vm, void* reserved)
{
	// This may be useful
	fprintf(stderr, "SquirrelJME Hook Loaded\n");

	// We could say we support Java 8, however Java ME is at heart a Java 7
	// system so we cannot use the latest features here
	return JNI_VERSION_1_6;
}

JNIEXPORT jint JNICALL Java_cc_squirreljme_jvm_Assembly_arrayLength__J
	(JNIEnv* env, jclass classy, jlong object)
{
	return Java_cc_squirreljme_jvm_Assembly_arrayLength__Ljava_lang_Object_2(env,
		classy, CAST_LONG_TO_OBJECT(object));
}

JNIEXPORT jint JNICALL Java_cc_squirreljme_jvm_Assembly_arrayLength__Ljava_lang_Object_2
	(JNIEnv* env, jclass classy, jobject object)
{
	// Return invalid length if not an array type
	if (object == NULL)
		return -1;

	if (javaIsInstance(env, object, "[Z") ||
		javaIsInstance(env, object, "[B") ||
		javaIsInstance(env, object, "[S") ||
		javaIsInstance(env, object, "[C") ||
		javaIsInstance(env, object, "[I") ||
		javaIsInstance(env, object, "[J") ||
		javaIsInstance(env, object, "[F") ||
		javaIsInstance(env, object, "[D") ||
		javaIsInstance(env, object, "[Ljava/lang/Object;"))
		return env->GetArrayLength((jarray)object);

	return -1;
}

/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/

}
