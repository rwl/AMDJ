/**
 * AMD, Copyright (C) 2009-2011 by Timothy A. Davis, Patrick R. Amestoy,
 * and Iain S. Duff.  All Rights Reserved.
 * Copyright (C) 2011 Richard Lincoln
 *
 * AMD is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * AMD is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with AMD; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301
 */

package edu.ufl.cise.amd.tdouble;

public class Damd_internal extends Damd {

	/**
	 * Enable debugging.
	 */
	public static boolean NDEBUG = true ;

	/**
	 * Enable printing and diagnostics.
	 */
	public static boolean NPRINT = true ;

	protected static final int Int_MAX = Integer.MAX_VALUE;

	/* FLIP is a "negation about -1", and is used to mark an integer i that is
	 * normally non-negative.  FLIP (EMPTY) is EMPTY.  FLIP of a number > EMPTY
	 * is negative, and FLIP of a number < EMTPY is positive.  FLIP (FLIP (i)) = i
	 * for all integers i.  UNFLIP (i) is >= EMPTY. */
	protected static final int EMPTY = (-1) ;

	protected static int FLIP (int i)
	{
		return (-(i)-2) ;
	}

	protected static int UNFLIP (int i)
	{
		return ((i < EMPTY) ? FLIP (i) : (i)) ;
	}

	protected static double sqrt (double a)
	{
		return Math.sqrt (a) ;
	}

	protected static final int MAX(int a, int b)
	{
		return (((a) > (b)) ? (a) : (b)) ;

	}

	protected static final int MIN(int a, int b)
	{
		return (((a) < (b)) ? (a) : (b)) ;
	}

	protected static final double MAX(double a, double b)
	{
		return (((a) > (b)) ? (a) : (b)) ;

	}

	protected static final double MIN(double a, double b)
	{
		return (((a) < (b)) ? (a) : (b)) ;
	}

	protected static final int TRUE = (1) ;
	protected static final int FALSE = (0) ;

	protected static String ID = "%d" ;

	protected static void PRINTF (String format, Object... args)
	{
		if (!NPRINT)
		{
			System.out.printf (format, args) ;
		}
	}

	protected static void ASSERT (boolean a)
	{
		if (!NDEBUG)
		{
			assert a ;
		}
	}

	protected static void ASSERT (int a)
	{
		ASSERT (a != 0) ;
	}

	protected static void AMD_DEBUG0 (String format, Object... args)
	{
		if (!NDEBUG)
		{
			PRINTF (format, args) ;
		}
	}

	protected static void AMD_DEBUG1(String format, Object... args)
	{
		if (!NDEBUG)
		{
			if (AMD_debug >= 1) PRINTF (format, args) ;
		}
	}

	protected static void AMD_DEBUG2(String format, Object... args)
	{
		if (!NDEBUG)
		{
			if (AMD_debug >= 2) PRINTF (format, args) ;
		}
	}

	protected static void AMD_DEBUG3(String format, Object... args)
	{
		if (!NDEBUG)
		{
			if (AMD_debug >= 3) PRINTF (format, args) ;
		}
	}

	protected static void AMD_DEBUG4(String format, Object... args)
	{
		if (!NDEBUG)
		{
			if (AMD_debug >= 4) PRINTF (format, args) ;
		}
	}

}
