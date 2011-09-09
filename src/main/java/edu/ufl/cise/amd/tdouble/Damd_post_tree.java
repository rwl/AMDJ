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
 * Post-ordering of a supernodal elimination tree.
 */
public class Damd_post_tree extends Damd_internal {

	public static int amd_post_tree (int root, int k, int[] Child,
			final int[] Sibling, int[] Order, int[] Stack)
	{
		// TODO: check default nn value of 0
		return amd_post_tree (root, k, Child, Sibling, Order, Stack, 0) ;
	}

	/**
	 *
	 * @param root root of the tree
	 * @param k start numbering at k
	 * @param Child input argument of size nn, undefined on
	 * output.  Child [i] is the head of a link
	 * list of all nodes that are children of node
	 * i in the tree.
	 * @param Sibling input argument of size nn, not modified.
	 * If f is a node in the link list of the
	 * children of node i, then Sibling [f] is the
	 * next child of node i.
	 * @param Order output order, of size nn.  Order [i] = k
	 * if node i is the kth node of the reordered tree.
	 * @param Stack workspace of size nn
	 * @param nn nodes are in the range 0..nn-1.
	 * @return
	 */
	public static int amd_post_tree (int root, int k, int[] Child,
			final int[] Sibling, int[] Order, int[] Stack, int nn)
	{
		int f, head, h, i ;

		/*if (false)
		{
			 ---------------------------------------------------------------------
			 recursive version (Stack [ ] is not used):
			 ---------------------------------------------------------------------

			 this is simple, but can caouse stack overflow if nn is large
			i = root ;
			for (f = Child [i] ; f != EMPTY ; f = Sibling [f])
			{
			k = AMD_post_tree (f, k, Child, Sibling, Order, Stack, nn) ;
			}
			Order [i] = k++ ;
			return (k) ;
		}*/

		/* --------------------------------------------------------------------- */
		/* non-recursive version, using an explicit stack */
		/* --------------------------------------------------------------------- */

		/* push root on the stack */
		head = 0 ;
		Stack [0] = root ;

		while (head >= 0)
		{
		/* get head of stack */
		ASSERT (head < nn) ;
		i = Stack [head] ;
		AMD_DEBUG1 ("head of stack "+ID+" \n", i) ;
		ASSERT (i >= 0 && i < nn) ;

		if (Child [i] != EMPTY)
		{
			/* the children of i are not yet ordered */
			/* push each child onto the stack in reverse order */
			/* so that small ones at the head of the list get popped first */
			/* and the biggest one at the end of the list gets popped last */
			for (f = Child [i] ; f != EMPTY ; f = Sibling [f])
			{
			head++ ;
			ASSERT (head < nn) ;
			ASSERT (f >= 0 && f < nn) ;
			}
			h = head ;
			ASSERT (head < nn) ;
			for (f = Child [i] ; f != EMPTY ; f = Sibling [f])
			{
			ASSERT (h > 0) ;
			Stack [h--] = f ;
			AMD_DEBUG1 ("push "+ID+" on stack\n", f) ;
			ASSERT (f >= 0 && f < nn) ;
			}
			ASSERT (Stack [h] == i) ;

			/* delete child list so that i gets ordered next time we see it */
			Child [i] = EMPTY ;
		}
		else
		{
			/* the children of i (if there were any) are already ordered */
			/* remove i from the stack and order it.  Front i is kth front */
			head-- ;
			AMD_DEBUG1 ("pop "+ID+" order "+ID+"\n", i, k) ;
			Order [i] = k++ ;
			ASSERT (k <= nn) ;
		}

		if (!NDEBUG)
		{
			AMD_DEBUG1 ("\nStack:") ;
			for (h = head ; h >= 0 ; h--)
			{
				int j = Stack [h] ;
				AMD_DEBUG1 (" "+ID, j) ;
				ASSERT (j >= 0 && j < nn) ;
			}
			AMD_DEBUG1 ("\n\n") ;
			ASSERT (head < nn) ;
		}

		}
		return (k) ;
	}
}
