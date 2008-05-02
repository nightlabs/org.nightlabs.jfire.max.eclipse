/**
 * 
 */
package org.nightlabs.jfire.trade.ui.producttype.quicklist;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.store.id.ProductTypeGroupID;
import org.nightlabs.jfire.store.id.ProductTypeID;

/**
 * Utility class which checks the containment of {@link ISelection}s for
 * occurrences of {@link ProductTypeID}s and {@link ProductTypeGroupID}s.
 *  
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class SelectionUtil {

	protected SelectionUtil() {}

	/**
	 * Searches in the given {@link ISelection} for {@link ObjectID}s and returns
	 * them.
	 * 
	 * @param selection the ISelection to search for contained {@link ObjectID}s 
	 * @return a {@link Set} of all {@link ObjectID}s which were contained in 
	 * the given {@link ISelection}
	 */
	@SuppressWarnings("unchecked") //$NON-NLS-1$
	public static Set<ObjectID> getObjectIDs(ISelection selection) 
	{
		Set<ObjectID> objectIDs = new HashSet<ObjectID>();
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection sel = (IStructuredSelection) selection;			
			for (Object object : sel.toList()) {
				if (object instanceof Collection) {
					Collection<Object> set = (Collection) object;
					for (Object setEntry : set) {
						if (setEntry instanceof ObjectID) {
							objectIDs.add((ObjectID)setEntry);
						}
					}
				}
				else if (object instanceof ObjectID) {
					objectIDs.add((ProductTypeID)object);
				}
			}
		}
		return objectIDs;
	}
	
	/**
	 * Searches in the given {@link ISelection} for {@link ProductTypeID}s and returns
	 * them.
	 * 
	 * @param selection the ISelection to search for contained {@link ProductTypeID}s 
	 * @return a {@link Set} of all {@link ProductTypeID}s which were contained in 
	 * the given {@link ISelection}
	 */
	@SuppressWarnings("unchecked") //$NON-NLS-1$
	public static Set<ProductTypeID> getProductTypesIDs(ISelection selection) 
	{
		Set<ProductTypeID> typeIDs = new HashSet<ProductTypeID>();
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection sel = (IStructuredSelection) selection;			
			for (Object object : sel.toList()) {
				if (object instanceof Collection) {
					Collection<Object> set = (Collection) object;
					for (Object setEntry : set) {
						if (setEntry instanceof ProductTypeID) {
							typeIDs.add((ProductTypeID)setEntry);
						}
					}
				}
				else if (object instanceof ProductTypeID) {
					typeIDs.add((ProductTypeID)object);
				}
			}
		}
		return typeIDs;
	}
	
	/**
	 * Searches in the given {@link ISelection} for {@link ProductTypeGroupID}s and returns
	 * them.
	 * 
	 * @param selection the ISelection to search for contained {@link ProductTypeGroupID}s 
	 * @return a {@link Set} of all {@link ProductTypeGroupID}s which were contained in 
	 * the given {@link ISelection}
	 */	
	@SuppressWarnings("unchecked") //$NON-NLS-1$
	public static Set<ProductTypeGroupID> getProductTypeGroupsIDs(ISelection selection) 
	{
		Set<ProductTypeGroupID> typeIDs = new HashSet<ProductTypeGroupID>();
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection sel = (IStructuredSelection) selection;			
			for (Object object : sel.toList()) {
				if (object instanceof Collection) {
					Collection<Object> set = (Collection) object;
					for (Object setEntry : set) {
						if (setEntry instanceof ProductTypeGroupID) {
							typeIDs.add((ProductTypeGroupID)setEntry);
						}
					}
				}
				else if (object instanceof ProductTypeGroupID) {
					typeIDs.add((ProductTypeGroupID)object);
				}
			}
		}
		return typeIDs;
	}
	
	/**
	 * Searches in the given {@link ISelection} for {@link ProductTypeGroupID}s and 
	 * {@link ProductTypeID}s and returns them wrapped in a SelectionContainment.
	 * 
	 * @param selection the ISelection to search for contained {@link ProductTypeGroupID}s
	 * and {@link ProductTypeID}s
	 * @return a {@link SelectionContainment} of all {@link ProductTypeGroupID}s 
	 * and {@link ProductTypeID}s which were contained in the given {@link ISelection}
	 */	
	@SuppressWarnings("unchecked") //$NON-NLS-1$
	public static SelectionContainment getSelectionContainment(ISelection selection) 
	{
		Set<ProductTypeGroupID> productTypeGroupIDs = new HashSet<ProductTypeGroupID>();
		Set<ProductTypeID> productTypeIDs = new HashSet<ProductTypeID>();
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection sel = (IStructuredSelection) selection;			
			for (Object object : sel.toList()) {
				if (object instanceof Collection) {
					Collection<Object> set = (Collection) object;
					for (Object setEntry : set) {
						if (setEntry instanceof ProductTypeGroupID) {
							productTypeGroupIDs.add((ProductTypeGroupID)setEntry);
						}
						else if (setEntry instanceof ProductTypeID) {
							productTypeIDs.add((ProductTypeID)setEntry);
						}
					}
				}
				else if (object instanceof ProductTypeGroupID) {
					productTypeGroupIDs.add((ProductTypeGroupID)object);
				}
				else if (object instanceof ProductTypeID) {
					productTypeIDs.add((ProductTypeID)object);
				}				
			}
		}
		return new SelectionContainment(productTypeGroupIDs, productTypeIDs);
	}	
	
	public static class SelectionContainment 
	{
		private Set<ProductTypeGroupID> productTypeGroupIDs;
		private Set<ProductTypeID> productTypeIDs;
		
		public SelectionContainment(Set<ProductTypeGroupID> productTypeGroupIDs,
				Set<ProductTypeID> productTypeIDs)
		{
			this.productTypeGroupIDs = productTypeGroupIDs;
			this.productTypeIDs = productTypeIDs;
		}

		public Set<ProductTypeGroupID> getProductTypeGroupIDs() {
			return productTypeGroupIDs;
		}

		public Set<ProductTypeID> getProductTypeIDs() {
			return productTypeIDs;
		}
		
		public boolean isEmpty() {
			return productTypeGroupIDs.isEmpty() && productTypeIDs.isEmpty();
		}
	}
}
