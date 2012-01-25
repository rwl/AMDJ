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

import edu.ufl.cise.amd.tdouble.Damd_internal;
import junit.framework.TestCase;

import static edu.ufl.cise.amd.tdouble.Damd.AMD_CONTROL;
import static edu.ufl.cise.amd.tdouble.Damd.AMD_INFO;
import static edu.ufl.cise.amd.tdouble.Damd.AMD_OK_BUT_JUMBLED;
import static edu.ufl.cise.amd.tdouble.Damd_defaults.amd_defaults;
import static edu.ufl.cise.amd.tdouble.Damd_control.amd_control;
import static edu.ufl.cise.amd.tdouble.Damd_order.amd_order;
import static edu.ufl.cise.amd.tdouble.Damd_info.amd_info;

/**
 * A simple C main program that illustrates the use of the Java interface
 * to AMD.
 *
 * Identical to amd_demo, except that it operates on an input matrix that has
 * unsorted columns and duplicate entries.
 */
public class Damd_demo2 extends TestCase {

	/* The symmetric can_24 Harwell/Boeing matrix (jumbled, and not symmetric).
	 * Since AMD operates on A+A', only A(i,j) or A(j,i) need to be specified,
	 * or both.  The diagonal entries are optional (some are missing).
	 * There are many duplicate entries, which must be removed. */
	int n = 24, nz;
	int[] Ap = new int[] { 0, 9, 14, 20, 28, 33, 37, 44, 53, 58, 63, 63, 66, 69, 72, 75,
			78, 82, 86, 91, 97, 101, 112, 112, 116 };
	int[] Ai = new int[] {
		/* column  0: */    0, 17, 18, 21, 5, 12, 5, 0, 13,
		/* column  1: */    14, 1, 8, 13, 17,
		/* column  2: */    2, 20, 11, 6, 11, 22,
		/* column  3: */    3, 3, 10, 7, 18, 18, 15, 19,
		/* column  4: */    7, 9, 15, 14, 16,
		/* column  5: */    5, 13, 6, 17,
		/* column  6: */    5, 0, 11, 6, 12, 6, 23,
		/* column  7: */    3, 4, 9, 7, 14, 16, 15, 17, 18,
		/* column  8: */    1, 9, 14, 14, 14,
		/* column  9: */    7, 13, 8, 1, 17,
		/* column 10: */
		/* column 11: */    2, 12, 23,
		/* column 12: */    5, 11, 12,
		/* column 13: */    0, 13, 17,
		/* column 14: */    1, 9, 14,
		/* column 15: */    3, 15, 16,
		/* column 16: */    16, 4, 4, 15,
		/* column 17: */    13, 17, 19, 17,
		/* column 18: */    15, 17, 19, 9, 10,
		/* column 19: */    17, 19, 20, 0, 6, 10,
		/* column 20: */    22, 10, 20, 21,
		/* column 21: */    6, 2, 10, 19, 20, 11, 21, 22, 22, 22, 22,
		/* column 22: */
		/* column 23: */    12, 11, 12, 23 } ;

	public void test_amd_demo2() {

		int[] P = new int [24] ;
		int[] Pinv = new int [24] ;
		int i, j, k, jnew, p, inew, result ;
		double[] Control = new double [AMD_CONTROL] ;
		double[] Info = new double [AMD_INFO] ;
		char[][] A = new char[24][24] ;

		Damd_internal.NPRINT = false;

		System.out.printf ("AMD demo, with a jumbled version of the 24-by-24\n") ;
		System.out.printf ("Harwell/Boeing matrix, can_24:\n") ;

		/* get the default parameters, and print them */
		amd_defaults (Control) ;
		amd_control  (Control) ;

		/* print the input matrix */
		nz = Ap [n] ;
		System.out.printf ("\nJumbled input matrix:  %d-by-%d, with %d entries.\n" +
				"   Note that for a symmetric matrix such as this one, only the\n" +
				"   strictly lower or upper triangular parts would need to be\n" +
				"   passed to AMD, since AMD computes the ordering of A+A'.  The\n" +
				"   diagonal entries are also not needed, since AMD ignores them.\n" +
				"   This version of the matrix has jumbled columns and duplicate\n" +
				"   row indices.\n", n, n, nz) ;
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
		System.out.printf ("\nPlot of (jumbled) input matrix pattern:\n") ;
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

		/* print a character plot of the matrix A+A'. */
		System.out.printf ("\nPlot of symmetric matrix to be ordered by amd_order:\n") ;
		for (j = 0 ; j < n ; j++)
		{
			for (i = 0 ; i < n ; i++) A [i][j] = '.' ;
		}
		for (j = 0 ; j < n ; j++)
		{
			A [j][j] = 'X' ;
			for (p = Ap [j] ; p < Ap [j+1] ; p++)
			{
				i = Ai [p] ;
				A [i][j] = 'X' ;
				A [j][i] = 'X' ;
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
				result, AMD_OK_BUT_JUMBLED) ;

		/* print the statistics */
		amd_info (Info) ;

		if (result != AMD_OK_BUT_JUMBLED)
		{
			System.out.printf ("AMD failed\n") ;
			fail () ;
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
		System.out.printf ("\nPlot of (symmetrized) permuted matrix pattern:\n") ;
		for (j = 0 ; j < n ; j++)
		{
			for (i = 0 ; i < n ; i++) A [i][j] = '.' ;
		}
		for (jnew = 0 ; jnew < n ; jnew++)
		{
			j = P [jnew] ;
			A [jnew][jnew] = 'X' ;
			for (p = Ap [j] ; p < Ap [j+1] ; p++)
			{
				inew = Pinv [Ai [p]] ;
				A [inew][jnew] = 'X' ;
				A [jnew][inew] = 'X' ;
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

		assertEquals(AMD_OK_BUT_JUMBLED, result) ;
	}

}
