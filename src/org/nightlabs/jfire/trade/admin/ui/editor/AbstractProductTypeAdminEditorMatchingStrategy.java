/**
 * 
 */
package org.nightlabs.jfire.trade.admin.ui.editor;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorMatchingStrategy;
import org.eclipse.ui.IEditorReference;
import org.nightlabs.jfire.base.jdo.JDOObjectID2PCClassMap;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public abstract class AbstractProductTypeAdminEditorMatchingStrategy 
implements IEditorMatchingStrategy 
{
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorMatchingStrategy#matches(org.eclipse.ui.IEditorReference, org.eclipse.ui.IEditorInput)
	 */
	public boolean matches(IEditorReference editorRef, IEditorInput input) 
	{
		if (input instanceof ProductTypeEditorInput) {
			ProductTypeEditorInput productTypeEditorInput = (ProductTypeEditorInput) input;
			ProductTypeID productTypeID = productTypeEditorInput.getJDOObjectID();
			Class<?> clazz = JDOObjectID2PCClassMap.sharedInstance().getPersistenceCapableClass(productTypeID);
			if (clazz != null && clazz.equals(getProductTypeClass())) {
				if (editorRef == null)
					return true;
			}
		}
		return false;
	}

	/**
	 * Returns the Class of the ProductType the editor of the strategy is used for.
	 * @return the Class of the ProductType the editor of the strategy is used for
	 */
	public abstract Class<? extends ProductType> getProductTypeClass();
}
