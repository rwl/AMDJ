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

public class Damd_defaults extends Damd_internal {

	public static void AMD_defaults(double[] Control)
	{
		int i ;

		if (Control != null)
		{
		for (i = 0 ; i < AMD_CONTROL ; i++)
		{
			Control [i] = 0 ;
		}
		Control [AMD_DENSE] = AMD_DEFAULT_DENSE ;
		Control [AMD_AGGRESSIVE] = AMD_DEFAULT_AGGRESSIVE ;
		}
	}
}
