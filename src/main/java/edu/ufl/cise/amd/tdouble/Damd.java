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
 * AMD finds a symmetric ordering P of a matrix A so that the Cholesky
 * factorization of P*A*P' has fewer nonzeros and takes less work than the
 * Cholesky factorization of A.  If A is not symmetric, then it performs its
 * ordering on the matrix A+A'.  Two sets of user-callable routines are
 * provided, one for int integers and the other for UF_long integers.
 *
 * The method is based on the approximate minimum degree algorithm, discussed
 * in Amestoy, Davis, and Duff, "An approximate degree ordering algorithm",
 * SIAM Journal of Matrix Analysis and Applications, vol. 17, no. 4, pp.
 * 886-905, 1996.  This package can perform both the AMD ordering (with
 * aggressive absorption), and the AMDBAR ordering (without aggressive
 * absorption) discussed in the above paper.  This package differs from the
 * Fortran codes discussed in the paper:
 *
 *	(1) it can ignore "dense" rows and columns, leading to faster run times
 *	(2) it computes the ordering of A+A' if A is not symmetric
 *	(3) it is followed by a depth-first post-ordering of the assembly tree
 *	    (or supernodal elimination tree)
 */
public class Damd {

	/** default is no debug printing */
	public static int AMD_debug = -999 ;

	/**
	 * size of Control array
	 */
	public static final int AMD_CONTROL = 5 ;

	/**
	 * size of Info array
	 */
	public static final int AMD_INFO = 20 ;

	/* contents of Control */

	/**
	 * "dense" if degree > Control [0] * sqrt (n)
	 */
	public static final int AMD_DENSE = 0 ;

	/**
	 * do aggressive absorption if Control [1] != 0
	 */
	public static final int AMD_AGGRESSIVE = 1 ;

	/* default Control settings */

	/**
	 * default "dense" degree 10*sqrt(n)
	 */
	public static final double AMD_DEFAULT_DENSE = 10.0 ;

	/**
	 * do aggressive absorption by default
	 */
	public static final int AMD_DEFAULT_AGGRESSIVE = 1 ;

	/* contents of Info */

	/** return value of amd_order and amd_l_order */
	public static final int AMD_STATUS = 0 ;
	/** A is n-by-n */
	public static final int AMD_N = 1 ;
	/** number of nonzeros in A */
	public static final int AMD_NZ = 2 ;
	/** symmetry of pattern (1 is sym., 0 is unsym.) */
	public static final int AMD_SYMMETRY = 3 ;
	/** # of entries on diagonal */
	public static final int AMD_NZDIAG = 4 ;
	/** nz in A+A' */
	public static final int AMD_NZ_A_PLUS_AT = 5 ;
	/** number of "dense" rows/columns in A */
	public static final int AMD_NDENSE = 6 ;
	/** amount of memory used by AMD */
	public static final int AMD_MEMORY = 7 ;
	/** number of garbage collections in AMD */
	public static final int AMD_NCMPA = 8 ;
	/** approx. nz in L, excluding the diagonal */
	public static final int AMD_LNZ = 9 ;
	/** number of fl. point divides for LU and LDL' */
	public static final int AMD_NDIV = 10 ;
	/** number of fl. point (*,-) pairs for LDL' */
	public static final int AMD_NMULTSUBS_LDL = 11 ;
	/** number of fl. point (*,-) pairs for LU */
	public static final int AMD_NMULTSUBS_LU = 12 ;
	/** max nz. in any column of L, incl. diagonal */
	public static final int AMD_DMAX = 13 ;

	/* ------------------------------------------------------------------------- */
	/* return values of AMD */
	/* ------------------------------------------------------------------------- */

	/* success */
	public static final int AMD_OK = 0 ;
	/* malloc failed, or problem too large */
	public static final int AMD_OUT_OF_MEMORY = -1 ;
	/* input arguments are not valid */
	public static final int AMD_INVALID = -2 ;
	/* input matrix is OK for amd_order, but
	 * columns were not sorted, and/or duplicate entries were present.  AMD had
	 * to do extra work before ordering the matrix.  This is a warning, not an
	 * error.  */
	public static final int AMD_OK_BUT_JUMBLED = 1 ;

	/* ========================================================================== */
	/* === AMD version ========================================================== */
	/* ========================================================================== */

	/* AMD Version 1.2 and later include the following definitions.
	 * As an example, to test if the version you are using is 1.2 or later:
	 *
	 *   if (AMD_VERSION >= AMD_VERSION_CODE (1,2)) ...
	 */

	public static final String AMD_DATE = "Jan 25, 2011" ;
	public static int AMD_VERSION_CODE (int main, int sub)
	{
		return ((main) * 1000 + (sub)) ;
	}
	public static final int AMD_MAIN_VERSION = 2 ;
	public static final int AMD_SUB_VERSION = 2 ;
	public static final int AMD_SUBSUB_VERSION = 2 ;
	public static final int AMD_VERSION = AMD_VERSION_CODE(AMD_MAIN_VERSION,
			AMD_SUB_VERSION) ;

}
