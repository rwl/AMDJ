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

package edu.ufl.cise.amd.tdouble.test;

import edu.ufl.cise.amd.tdouble.Damd;
import edu.ufl.cise.amd.tdouble.Damd_internal;
import junit.framework.TestCase;

import static edu.ufl.cise.amd.tdouble.Damd.AMD_CONTROL;
import static edu.ufl.cise.amd.tdouble.Damd.AMD_INFO;
import static edu.ufl.cise.amd.tdouble.Damd.AMD_VERSION;
import static edu.ufl.cise.amd.tdouble.Damd.AMD_MAIN_VERSION;
import static edu.ufl.cise.amd.tdouble.Damd.AMD_SUB_VERSION;
import static edu.ufl.cise.amd.tdouble.Damd.AMD_DATE;
import static edu.ufl.cise.amd.tdouble.Damd.AMD_OK;
import static edu.ufl.cise.amd.tdouble.Damd.AMD_VERSION_CODE;

import static edu.ufl.cise.amd.tdouble.Damd_defaults.amd_defaults;
import static edu.ufl.cise.amd.tdouble.Damd_control.amd_control;
import static edu.ufl.cise.amd.tdouble.Damd_order.amd_order;
import static edu.ufl.cise.amd.tdouble.Damd_info.amd_info;

/**
 * A simple C main program that illustrates the use of the interface
 * to AMD.
 */
public class Damd_demo extends TestCase {


	/* The symmetric can_24 Harwell/Boeing matrix, including upper and lower
	 * triangular parts, and the diagonal entries.  Note that this matrix is
	 * 0-based, with row and column indices in the range 0 to n-1. */
	int n = 24, nz;
	int[] Ap = new int [] { 0, 9, 15, 21, 27, 33, 39, 48, 57, 61, 70, 76, 82, 88, 94, 100,
			106, 110, 119, 128, 137, 143, 152, 156, 160 };
	int[] Ai = new int [] {
		/* column  0: */    0, 5, 6, 12, 13, 17, 18, 19, 21,
		/* column  1: */    1, 8, 9, 13, 14, 17,
		/* column  2: */    2, 6, 11, 20, 21, 22,
		/* column  3: */    3, 7, 10, 15, 18, 19,
		/* column  4: */    4, 7, 9, 14, 15, 16,
		/* column  5: */    0, 5, 6, 12, 13, 17,
		/* column  6: */    0, 2, 5, 6, 11, 12, 19, 21, 23,
		/* column  7: */    3, 4, 7, 9, 14, 15, 16, 17, 18,
		/* column  8: */    1, 8, 9, 14,
		/* column  9: */    1, 4, 7, 8, 9, 13, 14, 17, 18,
		/* column 10: */    3, 10, 18, 19, 20, 21,
		/* column 11: */    2, 6, 11, 12, 21, 23,
		/* column 12: */    0, 5, 6, 11, 12, 23,
		/* column 13: */    0, 1, 5, 9, 13, 17,
		/* column 14: */    1, 4, 7, 8, 9, 14,
		/* column 15: */    3, 4, 7, 15, 16, 18,
		/* column 16: */    4, 7, 15, 16,
		/* column 17: */    0, 1, 5, 7, 9, 13, 17, 18, 19,
		/* column 18: */    0, 3, 7, 9, 10, 15, 17, 18, 19,
		/* column 19: */    0, 3, 6, 10, 17, 18, 19, 20, 21,
		/* column 20: */    2, 10, 19, 20, 21, 22,
		/* column 21: */    0, 2, 6, 10, 11, 19, 20, 21, 22,
		/* column 22: */    2, 20, 21, 22,
		/* column 23: */    6, 11, 12, 23 } ;


	public void test_amd_demo() {

		int[] P = new int [24] ;
		int[] Pinv = new int [24] ;
		int i, j, k, jnew, p, inew, result ;
		double[] Control = new double [AMD_CONTROL] ;
		double[] Info = new double [AMD_INFO] ;
		char[][] A = new char[24][24] ;

		Damd_internal.NPRINT = false;
		//Damd_internal.NDEBUG = false;
		//Damd.AMD_debug = 1;

		/* here is an example of how to use AMD_VERSION.  This code will work in
		 * any version of AMD. */
		if (AMD_VERSION != 0 && AMD_VERSION >= AMD_VERSION_CODE(1,2))
		{
			System.out.printf ("AMD version %d.%d, date: %s\n", AMD_MAIN_VERSION,
					AMD_SUB_VERSION, AMD_DATE) ;
		} else {
			System.out.printf ("AMD version: 1.1 or earlier\n") ;
		}

		System.out.printf ("AMD demo, with the 24-by-24 Harwell/Boeing matrix, can_24:\n") ;

		/* get the default parameters, and print them */
		amd_defaults (Control) ;
		amd_control  (Control) ;

		/* print the input matrix */
		nz = Ap [n] ;
		System.out.printf ("\nInput matrix:  %d-by-%d, with %d entries.\n" +
				"   Note that for a symmetric matrix such as this one, only the\n" +
				"   strictly lower or upper triangular parts would need to be\n" +
				"   passed to AMD, since AMD computes the ordering of A+A'.  The\n" +
				"   diagonal entries are also not needed, since AMD ignores them.\n",
				n, n, nz) ;
		for (j = 0 ; j < n ; j++)
		{
			System.out.printf ("\nColumn: %d, number of entries: %d, with row indices in" +
					" Ai [%d ... %d]:\n    row indices:",
					j, Ap [j+1] - Ap [j], Ap [j], Ap [j+1]-1) ;
		for (p = Ap [j] ; p < Ap [j+1] ; p++)
		{
			i = Ai [p] ;
			System.out.printf (" %d", i) ;
		}
		System.out.printf ("\n") ;
		}

		/* print a character plot of the input matrix.  This is only reasonable
		 * because the matrix is small. */
		System.out.printf ("\nPlot of input matrix pattern:\n") ;
		for (j = 0 ; j < n ; j++)
		{
			for (i = 0 ; i < n ; i++) A [i][j] = '.' ;
			for (p = Ap [j] ; p < Ap [j+1] ; p++)
			{
				i = Ai [p] ;
				A [i][j] = 'X' ;
			}
		}
		System.out.printf ("    ") ;
		for (j = 0 ; j < n ; j++) System.out.printf (" %1d", j % 10) ;
		System.out.printf ("\n") ;
		for (i = 0 ; i < n ; i++)
		{
			System.out.printf ("%2d: ", i) ;
			for (j = 0 ; j < n ; j++)
			{
			    System.out.printf (" %c", A [i][j]) ;
			}
			System.out.printf ("\n") ;
		}

		/* order the matrix */
		result = amd_order (n, Ap, Ai, P, Control, Info) ;
		System.out.printf ("return value from amd_order: %d (should be %d)\n",
				result, AMD_OK) ;

		/* print the statistics */
		amd_info (Info) ;

		if (result != AMD_OK)
		{
			System.out.printf ("AMD failed\n") ;
			fail() ;
		}

		/* print the permutation vector, P, and compute the inverse permutation */
		System.out.printf ("Permutation vector:\n") ;
		for (k = 0 ; k < n ; k++)
		{
			/* row/column j is the kth row/column in the permuted matrix */
			j = P [k] ;
			Pinv [j] = k ;
			System.out.printf (" %2d", j) ;
		}
		System.out.printf ("\n\n") ;

		System.out.printf ("Inverse permutation vector:\n") ;
		for (j = 0 ; j < n ; j++)
		{
			k = Pinv [j] ;
			System.out.printf (" %2d", k) ;
		}
		System.out.printf ("\n\n") ;

		/* print a character plot of the permuted matrix. */
		System.out.printf ("\nPlot of permuted matrix pattern:\n") ;
		for (jnew = 0 ; jnew < n ; jnew++)
		{
			j = P [jnew] ;
			for (inew = 0 ; inew < n ; inew++) A [inew][jnew] = '.' ;
			for (p = Ap [j] ; p < Ap [j+1] ; p++)
			{
			    inew = Pinv [Ai [p]] ;
			    A [inew][jnew] = 'X' ;
			}
		}
		System.out.printf ("    ") ;
		for (j = 0 ; j < n ; j++) System.out.printf (" %1d", j % 10) ;
		System.out.printf ("\n") ;
		for (i = 0 ; i < n ; i++)
		{
			System.out.printf ("%2d: ", i) ;
			for (j = 0 ; j < n ; j++)
			{
				System.out.printf (" %c", A [i][j]) ;
			}
			System.out.printf ("\n") ;
		}

		assertEquals(AMD_OK, result) ;
	}

}
