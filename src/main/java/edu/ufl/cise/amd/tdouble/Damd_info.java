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
 * User-callable.  Prints the output statistics for AMD.  If the Info array
 * is not present, nothing is printed.
 */
public class Damd_info extends Damd_internal {

	private static void PRI (String format, double x)
	{
		if (x >= 0) { PRINTF (format, x) ; }
	}

	public static void amd_info (double[] Info)
	{
		double n, ndiv, nmultsubs_ldl, nmultsubs_lu, lnz, lnzd ;

		PRINTF ("\nAMD version %d.%d.%d, %s, results:\n",
		AMD_MAIN_VERSION, AMD_SUB_VERSION, AMD_SUBSUB_VERSION, AMD_DATE) ;

		if (Info == null || Info.length == 0)
		{
		return ;
		}

		n = Info [AMD_N] ;
		ndiv = Info [AMD_NDIV] ;
		nmultsubs_ldl = Info [AMD_NMULTSUBS_LDL] ;
		nmultsubs_lu = Info [AMD_NMULTSUBS_LU] ;
		lnz = Info [AMD_LNZ] ;
		lnzd = (n >= 0 && lnz >= 0) ? (n + lnz) : (-1) ;

		/* AMD return status */
		PRINTF ("    status: ") ;
		if (Info [AMD_STATUS] == AMD_OK)
		{
		PRINTF ("OK\n") ;
		}
		else if (Info [AMD_STATUS] == AMD_OUT_OF_MEMORY)
		{
		PRINTF ("out of memory\n") ;
		}
		else if (Info [AMD_STATUS] == AMD_INVALID)
		{
		PRINTF ("invalid matrix\n") ;
		}
		else if (Info [AMD_STATUS] == AMD_OK_BUT_JUMBLED)
		{
		PRINTF ("OK, but jumbled\n") ;
		}
		else
		{
		PRINTF ("unknown\n") ;
		}

		/* statistics about the input matrix */
		PRI ("    n, dimension of A:                                  %6.0f\n",
		n);
		PRI ("    nz, number of nonzeros in A:                        %6.0f\n",
		Info [AMD_NZ]) ;
		PRI ("    symmetry of A:                                      %.4f\n",
		Info [AMD_SYMMETRY]) ;
		PRI ("    number of nonzeros on diagonal:                     %6.0f\n",
		Info [AMD_NZDIAG]) ;
		PRI ("    nonzeros in pattern of A+A' (excl. diagonal):       %6.0f\n",
		Info [AMD_NZ_A_PLUS_AT]) ;
		PRI ("    # dense rows/columns of A+A':                       %6.0f\n",
		Info [AMD_NDENSE]) ;

		/* statistics about AMD's behavior  */
		PRI ("    memory used, in bytes:                              %6.0f\n",
		Info [AMD_MEMORY]) ;
		PRI ("    # of memory compactions:                            %6.0f\n",
		Info [AMD_NCMPA]) ;

		/* statistics about the ordering quality */
		PRINTF ("\n" +
		"    The following approximate statistics are for a subsequent\n" +
		"    factorization of A(P,P) + A(P,P)'.  They are slight upper\n" +
		"    bounds if there are no dense rows/columns in A+A', and become\n" +
		"    looser if dense rows/columns exist.\n\n") ;

		PRI ("    nonzeros in L (excluding diagonal):                 %6.0f\n",
		lnz) ;
		PRI ("    nonzeros in L (including diagonal):                 %6.0f\n",
		lnzd) ;
		PRI ("    # divide operations for LDL' or LU:                 %6.0f\n",
		ndiv) ;
		PRI ("    # multiply-subtract operations for LDL':            %6.0f\n",
		nmultsubs_ldl) ;
		PRI ("    # multiply-subtract operations for LU:              %6.0f\n",
		nmultsubs_lu) ;
		PRI ("    max nz. in any column of L (incl. diagonal):        %6.0f\n",
		Info [AMD_DMAX]) ;

		/* total flop counts for various factorizations */

		if (n >= 0 && ndiv >= 0 && nmultsubs_ldl >= 0 && nmultsubs_lu >= 0)
		{
		PRINTF ("\n" +
		"    chol flop count for real A, sqrt counted as 1 flop: %6.0f\n" +
		"    LDL' flop count for real A:                         %6.0f\n" +
		"    LDL' flop count for complex A:                      %6.0f\n" +
		"    LU flop count for real A (with no pivoting):        %6.0f\n" +
		"    LU flop count for complex A (with no pivoting):     %6.0f\n\n",
		n + ndiv + 2*nmultsubs_ldl,
			ndiv + 2*nmultsubs_ldl,
		9*ndiv + 8*nmultsubs_ldl,
			ndiv + 2*nmultsubs_lu,
		9*ndiv + 8*nmultsubs_lu) ;
		}
	}

}
