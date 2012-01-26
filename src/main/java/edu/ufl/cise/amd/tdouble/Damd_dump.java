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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Debugging routines for AMD.  Not used if NDEBUG is not defined at compile-
 * time (the default).  See comments in amd_internal.h on how to enable
 * debugging.  Not user-callable.
 */
public class Damd_dump extends Damd_internal {

	/**
	 * Sets the debug print level, by reading the file debug.amd (if it exists)
	 *
	 * @param s
	 */
	public static void amd_debug_init (String s)
	{
		if (!NDEBUG)
		{
			File f ;
			f = new File("debug.amd") ;
			if (!f.exists())
			{
			Damd.AMD_debug = -999 ;
			}
			else
			{
			try {
				FileReader fr ;
				fr = new FileReader(f) ;
				BufferedReader br ;
				br = new BufferedReader(fr) ;
				AMD_debug = Integer.valueOf( br.readLine() ) ;
				br.close() ;
				fr.close() ;
			} catch (IOException e) {
				System.out.printf ("%s: AMD_debug_init, " +
						"error reading debug.amd file", s) ;
			}
			}
			if (AMD_debug >= 0)
			{
			System.out.printf ("%s: AMD_debug_init, D= "+ID+"\n", s, AMD_debug) ;
			}
		}
	}

	/**
	 * Dump AMD's data structure, except for the hash buckets.  This routine
	 * cannot be called when the hash buckets are non-empty.
	 *
	 * @param n A is n-by-n
	 * @param Pe pe [0..n-1]: index in iw of start of row i
	 * @param Iw workspace of size iwlen, iwlen [0..pfree-1]
	 * holds the matrix on input
	 * @param Len len [0..n-1]: length for row i
	 * @param iwlen length of iw
	 * @param pfree iw [pfree ... iwlen-1] is empty on input
	 * @param Nv nv [0..n-1]
	 * @param Next next [0..n-1]
	 * @param Last last [0..n-1]
	 * @param Head head [0..n-1]
	 * @param Elen size n
	 * @param Degree size n
	 * @param W size n
	 * @param nel
	 */
	public static void amd_dump (int n, int[] Pe, int[] Iw, int[] Len,
			int iwlen, int pfree, int[] Nv, int[] Next, int[] Last, int[] Head,
			int[] Elen, int[] Degree, int[] W, int nel)
	{
		if (!NDEBUG)
		{
			int i, pe, elen, nv, len, e, p, k, j, deg, w, cnt, ilast ;

			if (AMD_debug < 0) return ;
			ASSERT (pfree <= iwlen) ;
			AMD_DEBUG3 ("\nAMD dump, pfree: "+ID+"\n", pfree) ;
			for (i = 0 ; i < n ; i++)
			{
			pe = Pe [i] ;
			elen = Elen [i] ;
			nv = Nv [i] ;
			len = Len [i] ;
			w = W [i] ;

			if (elen >= EMPTY)
			{
				if (nv == 0)
				{
				AMD_DEBUG3 ("\nI "+ID+": nonprincipal:    ", i) ;
				ASSERT (elen == EMPTY) ;
				if (pe == EMPTY)
				{
					AMD_DEBUG3 (" dense node\n") ;
					ASSERT (w == 1) ;
				}
				else
				{
					ASSERT (pe < EMPTY) ;
					AMD_DEBUG3 (" i "+ID+" -> parent "+ID+"\n", i, FLIP (Pe[i]));
				}
				}
				else
				{
				AMD_DEBUG3 ("\nI "+ID+": active principal supervariable:\n",i);
				AMD_DEBUG3 ("   nv(i): "+ID+"  Flag: %d\n", nv, (nv < 0) ? 1 : 0) ;
				ASSERT (elen >= 0) ;
				ASSERT (nv > 0 && pe >= 0) ;
				p = pe ;
				AMD_DEBUG3 (("   e/s: ")) ;
				if (elen == 0) AMD_DEBUG3 (" : ") ;
				ASSERT (pe + len <= pfree) ;
				for (k = 0 ; k < len ; k++)
				{
					j = Iw [p] ;
					AMD_DEBUG3 ("  "+ID+"", j) ;
					ASSERT (j >= 0 && j < n) ;
					if (k == elen-1) AMD_DEBUG3 ((" : ")) ;
					p++ ;
				}
				AMD_DEBUG3 (("\n")) ;
				}
			}
			else
			{
				e = i ;
				if (w == 0)
				{
				AMD_DEBUG3 ("\nE "+ID+": absorbed element: w "+ID+"\n", e, w) ;
				ASSERT (nv > 0 && pe < 0) ;
				AMD_DEBUG3 (" e "+ID+" -> parent "+ID+"\n", e, FLIP (Pe [e])) ;
				}
				else
				{
				AMD_DEBUG3 ("\nE "+ID+": unabsorbed element: w "+ID+"\n", e, w) ;
				ASSERT (nv > 0 && pe >= 0) ;
				p = pe ;
				AMD_DEBUG3 ((" : ")) ;
				ASSERT (pe + len <= pfree) ;
				for (k = 0 ; k < len ; k++)
				{
					j = Iw [p] ;
					AMD_DEBUG3 ("  "+ID+"", j) ;
					ASSERT (j >= 0 && j < n) ;
					p++ ;
				}
				AMD_DEBUG3 ("\n") ;
				}
			}
			}

			/* this routine cannot be called when the hash buckets are non-empty */
			AMD_DEBUG3 ("\nDegree lists:\n") ;
			if (nel >= 0)
			{
			cnt = 0 ;
			for (deg = 0 ; deg < n ; deg++)
			{
				if (Head [deg] == EMPTY) continue ;
				ilast = EMPTY ;
				AMD_DEBUG3 (ID+": \n", deg) ;
				for (i = Head [deg] ; i != EMPTY ; i = Next [i])
				{
				AMD_DEBUG3 ("   "+ID+" : next "+ID+" last "+ID+" deg "+ID+"\n",
					i, Next [i], Last [i], Degree [i]) ;
				ASSERT (i >= 0 && i < n && ilast == Last [i] &&
					deg == Degree [i]) ;
				cnt += Nv [i] ;
				ilast = i ;
				}
				AMD_DEBUG3 ("\n") ;
			}
			ASSERT (cnt == n - nel) ;
			}
		}
	}

}
