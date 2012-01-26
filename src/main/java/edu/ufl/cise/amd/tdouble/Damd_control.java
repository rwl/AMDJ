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

/**
 * User-callable.  Prints the control parameters for AMD.  See amd.h
 * for details.  If the Control array is not present, the defaults are
 * printed instead.
 */
public class Damd_control extends Damd_internal {

	public static void amd_control(double[] Control)
	{
		double alpha ;
		int aggressive ;

		if (Control != null)
		{
		alpha = Control [AMD_DENSE] ;
		aggressive = Control [AMD_AGGRESSIVE] != 0 ? 1 : 0 ;
		}
		else
		{
		alpha = AMD_DEFAULT_DENSE ;
		aggressive = AMD_DEFAULT_AGGRESSIVE ;
		}

		PRINTF ("\nAMD version %d.%d.%d, %s: approximate minimum degree ordering\n" +
		"    dense row parameter: %.2f\n", AMD_MAIN_VERSION, AMD_SUB_VERSION,
		AMD_SUBSUB_VERSION, AMD_DATE, alpha) ;

		if (alpha < 0)
		{
		PRINTF ("    no rows treated as dense\n") ;
		}
		else
		{
		PRINTF (
		"    (rows with more than max (%.2f * sqrt (n), 16) entries are\n" +
		"    considered \"dense\", and placed last in output permutation)\n",
		alpha) ;
		}

		if (aggressive != 0)
		{
		PRINTF ("    aggressive absorption:  yes\n") ;
		}
		else
		{
		PRINTF ("    aggressive absorption:  no\n") ;
		}

		PRINTF ("    size of AMD integer: %d\n\n", 4) ;  // sizeof (int)
	}
}
