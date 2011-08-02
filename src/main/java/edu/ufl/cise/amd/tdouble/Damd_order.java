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
 * User-callable AMD minimum degree ordering routine.
 */
public class Damd_order extends Damd_internal {

	public static int AMD_order (int n,
			final int[] Ap,
			final int[] Ai,
			int[] P,
			double[] Control,
			double[] Info)
	{
		int Len, S, Pinv, Rp, Ri, Cp, Ci ;
		int nz, i, info, status, ok ;
		int nzaat, slen ;
		double mem = 0 ;

		if (!NDEBUG)
		{
			Damd_dump.AMD_debug_init ("amd") ;
		}

		/* clear the Info array, if it exists */
		info = Info != null ? 1 : 0 ;
		if (info != 0)
		{
		for (i = 0 ; i < AMD_INFO ; i++)
		{
			Info [i] = EMPTY ;
		}
		Info [AMD_N] = n ;
		Info [AMD_STATUS] = AMD_OK ;
		}

		/* make sure inputs exist and n is >= 0 */
		if (Ai == null || Ap == null || P == null || n < 0)
		{
		if (info != 0) Info [AMD_STATUS] = AMD_INVALID ;
		return (AMD_INVALID) ;	    /* arguments are invalid */
		}

		if (n == 0)
		{
		return (AMD_OK) ;	    /* n is 0 so there's nothing to do */
		}

		nz = Ap [n] ;
		if (info != 0)
		{
		Info [AMD_NZ] = nz ;
		}
		if (nz < 0)
		{
		if (info != 0) Info [AMD_STATUS] = AMD_INVALID ;
		return (AMD_INVALID) ;
		}

		/* check if n or nz will cause size_t overflow */
		if (((size_t) n) >= SIZE_T_MAX / sizeof (int)
		 || ((size_t) nz) >= SIZE_T_MAX / sizeof (int))
		{
		if (info) Info [AMD_STATUS] = AMD_OUT_OF_MEMORY ;
		return (AMD_OUT_OF_MEMORY) ;	    /* problem too large */
		}

		/* check the input matrix:	AMD_OK, AMD_INVALID, or AMD_OK_BUT_JUMBLED */
		status = AMD_valid (n, n, Ap, Ai) ;

		if (status == AMD_INVALID)
		{
		if (info) Info [AMD_STATUS] = AMD_INVALID ;
		return (AMD_INVALID) ;	    /* matrix is invalid */
		}

		/* allocate two size-n integer workspaces */
		Len = amd_malloc (n * sizeof (int)) ;
		Pinv = amd_malloc (n * sizeof (int)) ;
		mem += n ;
		mem += n ;
		if (!Len || !Pinv)
		{
		/* :: out of memory :: */
		amd_free (Len) ;
		amd_free (Pinv) ;
		if (info) Info [AMD_STATUS] = AMD_OUT_OF_MEMORY ;
		return (AMD_OUT_OF_MEMORY) ;
		}

		if (status == AMD_OK_BUT_JUMBLED)
		{
		/* sort the input matrix and remove duplicate entries */
		AMD_DEBUG1 (("Matrix is jumbled\n")) ;
		Rp = amd_malloc ((n+1) * sizeof (int)) ;
		Ri = amd_malloc (MAX (nz,1) * sizeof (int)) ;
		mem += (n+1) ;
		mem += MAX (nz,1) ;
		if (!Rp || !Ri)
		{
			/* :: out of memory :: */
			amd_free (Rp) ;
			amd_free (Ri) ;
			amd_free (Len) ;
			amd_free (Pinv) ;
			if (info) Info [AMD_STATUS] = AMD_OUT_OF_MEMORY ;
			return (AMD_OUT_OF_MEMORY) ;
		}
		/* use Len and Pinv as workspace to create R = A' */
		AMD_preprocess (n, Ap, Ai, Rp, Ri, Len, Pinv) ;
		Cp = Rp ;
		Ci = Ri ;
		}
		else
		{
		/* order the input matrix as-is.  No need to compute R = A' first */
		Rp = null ;
		Ri = null ;
		Cp = (int[]) Ap ;
		Ci = (int[]) Ai ;
		}

		/* --------------------------------------------------------------------- */
		/* determine the symmetry and count off-diagonal nonzeros in A+A' */
		/* --------------------------------------------------------------------- */

		nzaat = AMD_aat (n, Cp, Ci, Len, P, Info) ;
		AMD_DEBUG1 ("nzaat: %g\n", (double) nzaat) ;
		ASSERT ((MAX (nz-n, 0) <= nzaat) && (nzaat <= 2 * (size_t) nz)) ;

		/* --------------------------------------------------------------------- */
		/* allocate workspace for matrix, elbow room, and 6 size-n vectors */
		/* --------------------------------------------------------------------- */

		S = null ;
		slen = nzaat ;			/* space for matrix */
		ok = ((slen + nzaat/5) >= slen) ;	/* check for size_t overflow */
		slen += nzaat/5 ;			/* add elbow room */
		for (i = 0 ; ok && i < 7 ; i++)
		{
		ok = ((slen + n) > slen) ;	/* check for size_t overflow */
		slen += n ;			/* size-n elbow room, 6 size-n work */
		}
		mem += slen ;
		ok = ok && (slen < SIZE_T_MAX / sizeof (int)) ? 1 : 0 ; /* check for overflow */
		ok = ok && (slen < Int_MAX) ;	/* S[i] for int i must be OK */
		if (ok != 0)
		{
		S = amd_malloc (slen * sizeof (int)) ;
		}
		AMD_DEBUG1 ("slen %g\n", (double) slen) ;
		if (!S)
		{
		/* :: out of memory :: (or problem too large) */
		amd_free (Rp) ;
		amd_free (Ri) ;
		amd_free (Len) ;
		amd_free (Pinv) ;
		if (info) Info [AMD_STATUS] = AMD_OUT_OF_MEMORY ;
		return (AMD_OUT_OF_MEMORY) ;
		}
		if (info)
		{
		/* memory usage, in bytes. */
		Info [AMD_MEMORY] = mem * sizeof (int) ;
		}

		/* --------------------------------------------------------------------- */
		/* order the matrix */
		/* --------------------------------------------------------------------- */

		Damd_1.AMD_1 (n, Cp, Ci, P, Pinv, Len, slen, S, Control, Info) ;

		/* --------------------------------------------------------------------- */
		/* free the workspace */
		/* --------------------------------------------------------------------- */

		amd_free (Rp) ;
		amd_free (Ri) ;
		amd_free (Len) ;
		amd_free (Pinv) ;
		amd_free (S) ;
		if (info != 0) Info [AMD_STATUS] = status ;
		return (status) ;	    /* successful ordering */
	}

}
