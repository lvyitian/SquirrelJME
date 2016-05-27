// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) 2013-2016 Steven Gawroriski <steven@multiphasicapps.net>
//     Copyright (C) 2013-2016 Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU Affero General Public License v3+, or later.
// For more information see license.mkd.
// ---------------------------------------------------------------------------

package net.multiphasicapps.squirreljme.bytecode;

/**
 * This contains a large number of static field constants which identify the
 * instruction IDs.
 *
 * @since 2016/05/12
 */
public interface BCInstructionID
{
	/** NOP. */
	public static final int NOP =
		0;

	/** ACONST_NULL. */
	public static final int ACONST_NULL =
		1;

	/** ICONST_M1. */
	public static final int ICONST_M1 =
		2;

	/** ICONST_0. */
	public static final int ICONST_0 =
		3;

	/** ICONST_1. */
	public static final int ICONST_1 =
		4;

	/** ICONST_2. */
	public static final int ICONST_2 =
		5;

	/** ICONST_3. */
	public static final int ICONST_3 =
		6;

	/** ICONST_4. */
	public static final int ICONST_4 =
		7;

	/** ICONST_5. */
	public static final int ICONST_5 =
		8;

	/** LCONST_0. */
	public static final int LCONST_0 =
		9;

	/** LCONST_1. */
	public static final int LCONST_1 =
		10;

	/** FCONST_0. */
	public static final int FCONST_0 =
		11;

	/** FCONST_1. */
	public static final int FCONST_1 =
		12;

	/** FCONST_2. */
	public static final int FCONST_2 =
		13;

	/** DCONST_0. */
	public static final int DCONST_0 =
		14;

	/** DCONST_1. */
	public static final int DCONST_1 =
		15;

	/** BIPUSH. */
	public static final int BIPUSH =
		16;

	/** SIPUSH. */
	public static final int SIPUSH =
		17;

	/** LDC. */
	public static final int LDC =
		18;

	/** LDC_W. */
	public static final int LDC_W =
		19;

	/** LDC2_W. */
	public static final int LDC2_W =
		20;

	/** ILOAD. */
	public static final int ILOAD =
		21;

	/** LLOAD. */
	public static final int LLOAD =
		22;

	/** FLOAD. */
	public static final int FLOAD =
		23;

	/** DLOAD. */
	public static final int DLOAD =
		24;

	/** ALOAD. */
	public static final int ALOAD =
		25;

	/** ILOAD_0. */
	public static final int ILOAD_0 =
		26;

	/** ILOAD_1. */
	public static final int ILOAD_1 =
		27;

	/** ILOAD_2. */
	public static final int ILOAD_2 =
		28;

	/** ILOAD_3. */
	public static final int ILOAD_3 =
		29;

	/** LLOAD_0. */
	public static final int LLOAD_0 =
		30;

	/** LLOAD_1. */
	public static final int LLOAD_1 =
		31;

	/** LLOAD_2. */
	public static final int LLOAD_2 =
		32;

	/** LLOAD_3. */
	public static final int LLOAD_3 =
		33;

	/** FLOAD_0. */
	public static final int FLOAD_0 =
		34;

	/** FLOAD_1. */
	public static final int FLOAD_1 =
		35;

	/** FLOAD_2. */
	public static final int FLOAD_2 =
		36;

	/** FLOAD_3. */
	public static final int FLOAD_3 =
		37;

	/** DLOAD_0. */
	public static final int DLOAD_0 =
		38;

	/** DLOAD_1. */
	public static final int DLOAD_1 =
		39;

	/** DLOAD_2. */
	public static final int DLOAD_2 =
		40;

	/** DLOAD_3. */
	public static final int DLOAD_3 =
		41;

	/** ALOAD_0. */
	public static final int ALOAD_0 =
		42;

	/** ALOAD_1. */
	public static final int ALOAD_1 =
		43;

	/** ALOAD_2. */
	public static final int ALOAD_2 =
		44;

	/** ALOAD_3. */
	public static final int ALOAD_3 =
		45;

	/** IALOAD. */
	public static final int IALOAD =
		46;

	/** LALOAD. */
	public static final int LALOAD =
		47;

	/** FALOAD. */
	public static final int FALOAD =
		48;

	/** DALOAD. */
	public static final int DALOAD =
		49;

	/** AALOAD. */
	public static final int AALOAD =
		50;

	/** BALOAD. */
	public static final int BALOAD =
		51;

	/** CALOAD. */
	public static final int CALOAD =
		52;

	/** SALOAD. */
	public static final int SALOAD =
		53;

	/** ISTORE. */
	public static final int ISTORE =
		54;

	/** LSTORE. */
	public static final int LSTORE =
		55;

	/** FSTORE. */
	public static final int FSTORE =
		56;

	/** DSTORE. */
	public static final int DSTORE =
		57;

	/** ASTORE. */
	public static final int ASTORE =
		58;

	/** ISTORE_0. */
	public static final int ISTORE_0 =
		59;

	/** ISTORE_1. */
	public static final int ISTORE_1 =
		60;

	/** ISTORE_2. */
	public static final int ISTORE_2 =
		61;

	/** ISTORE_3. */
	public static final int ISTORE_3 =
		62;

	/** LSTORE_0. */
	public static final int LSTORE_0 =
		63;

	/** LSTORE_1. */
	public static final int LSTORE_1 =
		64;

	/** LSTORE_2. */
	public static final int LSTORE_2 =
		65;

	/** LSTORE_3. */
	public static final int LSTORE_3 =
		66;

	/** FSTORE_0. */
	public static final int FSTORE_0 =
		67;

	/** FSTORE_1. */
	public static final int FSTORE_1 =
		68;

	/** FSTORE_2. */
	public static final int FSTORE_2 =
		69;

	/** FSTORE_3. */
	public static final int FSTORE_3 =
		70;

	/** DSTORE_0. */
	public static final int DSTORE_0 =
		71;

	/** DSTORE_1. */
	public static final int DSTORE_1 =
		72;

	/** DSTORE_2. */
	public static final int DSTORE_2 =
		73;

	/** DSTORE_3. */
	public static final int DSTORE_3 =
		74;

	/** ASTORE_0. */
	public static final int ASTORE_0 =
		75;

	/** ASTORE_1. */
	public static final int ASTORE_1 =
		76;

	/** ASTORE_2. */
	public static final int ASTORE_2 =
		77;

	/** ASTORE_3. */
	public static final int ASTORE_3 =
		78;

	/** IASTORE. */
	public static final int IASTORE =
		79;

	/** LASTORE. */
	public static final int LASTORE =
		80;

	/** FASTORE. */
	public static final int FASTORE =
		81;

	/** DASTORE. */
	public static final int DASTORE =
		82;

	/** AASTORE. */
	public static final int AASTORE =
		83;

	/** BASTORE. */
	public static final int BASTORE =
		84;

	/** CASTORE. */
	public static final int CASTORE =
		85;

	/** SASTORE. */
	public static final int SASTORE =
		86;

	/** POP. */
	public static final int POP =
		87;

	/** POP2. */
	public static final int POP2 =
		88;

	/** DUP. */
	public static final int DUP =
		89;

	/** DUP_X1. */
	public static final int DUP_X1 =
		90;

	/** DUP_X2. */
	public static final int DUP_X2 =
		91;

	/** DUP2. */
	public static final int DUP2 =
		92;

	/** DUP2_X1. */
	public static final int DUP2_X1 =
		93;

	/** DUP2_X2. */
	public static final int DUP2_X2 =
		94;

	/** SWAP. */
	public static final int SWAP =
		95;

	/** IADD. */
	public static final int IADD =
		96;

	/** LADD. */
	public static final int LADD =
		97;

	/** FADD. */
	public static final int FADD =
		98;

	/** DADD. */
	public static final int DADD =
		99;

	/** ISUB. */
	public static final int ISUB =
		100;

	/** LSUB. */
	public static final int LSUB =
		101;

	/** FSUB. */
	public static final int FSUB =
		102;

	/** DSUB. */
	public static final int DSUB =
		103;

	/** IMUL. */
	public static final int IMUL =
		104;

	/** LMUL. */
	public static final int LMUL =
		105;

	/** FMUL. */
	public static final int FMUL =
		106;

	/** DMUL. */
	public static final int DMUL =
		107;

	/** IDIV. */
	public static final int IDIV =
		108;

	/** LDIV. */
	public static final int LDIV =
		109;

	/** FDIV. */
	public static final int FDIV =
		110;

	/** DDIV. */
	public static final int DDIV =
		111;

	/** IREM. */
	public static final int IREM =
		112;

	/** LREM. */
	public static final int LREM =
		113;

	/** FREM. */
	public static final int FREM =
		114;

	/** DREM. */
	public static final int DREM =
		115;

	/** INEG. */
	public static final int INEG =
		116;

	/** LNEG. */
	public static final int LNEG =
		117;

	/** FNEG. */
	public static final int FNEG =
		118;

	/** DNEG. */
	public static final int DNEG =
		119;

	/** ISHL. */
	public static final int ISHL =
		120;

	/** LSHL. */
	public static final int LSHL =
		121;

	/** ISHR. */
	public static final int ISHR =
		122;

	/** LSHR. */
	public static final int LSHR =
		123;

	/** IUSHR. */
	public static final int IUSHR =
		124;

	/** LUSHR. */
	public static final int LUSHR =
		125;

	/** IAND. */
	public static final int IAND =
		126;

	/** LAND. */
	public static final int LAND =
		127;

	/** IOR. */
	public static final int IOR =
		128;

	/** LOR. */
	public static final int LOR =
		129;

	/** IXOR. */
	public static final int IXOR =
		130;

	/** LXOR. */
	public static final int LXOR =
		131;

	/** IINC. */
	public static final int IINC =
		132;

	/** I2L. */
	public static final int I2L =
		133;

	/** I2F. */
	public static final int I2F =
		134;

	/** I2D. */
	public static final int I2D =
		135;

	/** L2I. */
	public static final int L2I =
		136;

	/** L2F. */
	public static final int L2F =
		137;

	/** L2D. */
	public static final int L2D =
		138;

	/** F2I. */
	public static final int F2I =
		139;

	/** F2L. */
	public static final int F2L =
		140;

	/** F2D. */
	public static final int F2D =
		141;

	/** D2I. */
	public static final int D2I =
		142;

	/** D2L. */
	public static final int D2L =
		143;

	/** D2F. */
	public static final int D2F =
		144;

	/** I2B. */
	public static final int I2B =
		145;

	/** I2C. */
	public static final int I2C =
		146;

	/** I2S. */
	public static final int I2S =
		147;

	/** LCMP. */
	public static final int LCMP =
		148;

	/** FCMPL. */
	public static final int FCMPL =
		149;

	/** FCMPG. */
	public static final int FCMPG =
		150;

	/** DCMPL. */
	public static final int DCMPL =
		151;

	/** DCMPG. */
	public static final int DCMPG =
		152;

	/** IFEQ. */
	public static final int IFEQ =
		153;

	/** IFNE. */
	public static final int IFNE =
		154;

	/** IFLT. */
	public static final int IFLT =
		155;

	/** IFGE. */
	public static final int IFGE =
		156;

	/** IFGT. */
	public static final int IFGT =
		157;

	/** IFLE. */
	public static final int IFLE =
		158;

	/** IF_ICMPEQ. */
	public static final int IF_ICMPEQ =
		159;

	/** IF_ICMPNE. */
	public static final int IF_ICMPNE =
		160;

	/** IF_ICMPLT. */
	public static final int IF_ICMPLT =
		161;

	/** IF_ICMPGE. */
	public static final int IF_ICMPGE =
		162;

	/** IF_ICMPGT. */
	public static final int IF_ICMPGT =
		163;

	/** IF_ICMPLE. */
	public static final int IF_ICMPLE =
		164;

	/** IF_ACMPEQ. */
	public static final int IF_ACMPEQ =
		165;

	/** IF_ACMPNE. */
	public static final int IF_ACMPNE =
		166;

	/** GOTO. */
	public static final int GOTO =
		167;

	/** JSR. */
	public static final int JSR =
		168;

	/** RET. */
	public static final int RET =
		169;

	/** TABLESWITCH. */
	public static final int TABLESWITCH =
		170;

	/** LOOKUPSWITCH. */
	public static final int LOOKUPSWITCH =
		171;

	/** IRETURN. */
	public static final int IRETURN =
		172;

	/** LRETURN. */
	public static final int LRETURN =
		173;

	/** FRETURN. */
	public static final int FRETURN =
		174;

	/** DRETURN. */
	public static final int DRETURN =
		175;

	/** ARETURN. */
	public static final int ARETURN =
		176;

	/** RETURN. */
	public static final int RETURN =
		177;

	/** GETSTATIC. */
	public static final int GETSTATIC =
		178;

	/** PUTSTATIC. */
	public static final int PUTSTATIC =
		179;

	/** GETFIELD. */
	public static final int GETFIELD =
		180;

	/** PUTFIELD. */
	public static final int PUTFIELD =
		181;

	/** INVOKEVIRTUAL. */
	public static final int INVOKEVIRTUAL =
		182;

	/** INVOKESPECIAL. */
	public static final int INVOKESPECIAL =
		183;

	/** INVOKESTATIC. */
	public static final int INVOKESTATIC =
		184;

	/** INVOKEINTERFACE. */
	public static final int INVOKEINTERFACE =
		185;

	/** INVOKEDYNAMIC. */
	public static final int INVOKEDYNAMIC =
		186;

	/** NEW. */
	public static final int NEW =
		187;

	/** NEWARRAY. */
	public static final int NEWARRAY =
		188;

	/** ANEWARRAY. */
	public static final int ANEWARRAY =
		189;

	/** ARRAYLENGTH. */
	public static final int ARRAYLENGTH =
		190;

	/** ATHROW. */
	public static final int ATHROW =
		191;

	/** CHECKCAST. */
	public static final int CHECKCAST =
		192;

	/** INSTANCEOF. */
	public static final int INSTANCEOF =
		193;

	/** MONITORENTER. */
	public static final int MONITORENTER =
		194;

	/** MONITOREXIT. */
	public static final int MONITOREXIT =
		195;

	/** WIDE. */
	public static final int WIDE =
		196;

	/** MULTIANEWARRAY. */
	public static final int MULTIANEWARRAY =
		197;

	/** IFNULL. */
	public static final int IFNULL =
		198;

	/** IFNONNULL. */
	public static final int IFNONNULL =
		199;

	/** GOTO_W. */
	public static final int GOTO_W =
		200;

	/** JSR_W. */
	public static final int JSR_W =
		201;

	/** BREAKPOINT. */
	public static final int BREAKPOINT =
		202;

	/** IMPDEP1. */
	public static final int IMPDEP1 =
		254;

	/** IMPDEP2. */
	public static final int IMPDEP2 =
		255;
	
	/**
	 * Synthetic: Push value to stack.
	 *
	 * The first argument contains the value to push.
	 */
	public static final int SYNTHETIC_PUSH_VALUE =
		65536;
	
	/**
	 * Synthetic: Check null on stack, push value to stack.
	 *
	 * An object is popped from the stack and checked against null.
	 *
	 * The first argument contains the value to push.
	 */
	public static final int SYNTHETIC_CHECK_NULL_PUSH_VALUE =
		65537;
	
	/**
	 * Synthetic: Shuffle items on the stack.
	 *
	 * Values are popped from the stack in a specific order and their values
	 * are temporarily copied. Pushing of values uses stack value reference
	 * variety of {@link BCVariablePush} ({@link BCVariablePush#popIndex()})
	 * which refers to the specified order that instruction were popped from
	 * the lowest pop index to the highest (a pop index of zero is the zeroth
	 * element of the pop value types, not the last).
	 */
	public static final int SYNTHETIC_STACK_SHUFFLE =
		65538;
	
	/**
	 * Synthetic: Specific instance invocation, using the exact method that
	 * was given.
	 *
	 * The first argument is a reference to the method (CIMethod) to invoke.
	 *
	 * An object along with the arguments to the call are popped to the stack.
	 *
	 * The instance method should be checked for {@code null}.
	 */
	public static final int SYNTHETIC_INSTANCE_INVOKE_EXACT =
		65539;
	
	/**
	 * Synthetic: Virtual instance invocation.
	 *
	 * The first argument is a reference to the method (CIMethod) to invoke.
	 * If the class has a sub-class then the topmost virtual method should be
	 * used.
	 *
	 * An object along with the arguments to the call are popped to the stack.
	 *
	 * The instance method should be checked for {@code null}.
	 */
	public static final int SYNTHETIC_INSTANCE_INVOKE_VIRTUAL =
		65540;
}

