package org.nightlabs.jfire.dynamictrade.admin.ui.store.search;

import java.util.Collection;

import javax.jdo.JDOHelper;

import org.eclipse.ui.PartInitException;
import org.nightlabs.base.ui.search.AbstractSearchResultActionHandler;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.dynamictrade.admin.ui.editor.DynamicProductTypeEditor;
import org.nightlabs.jfire.dynamictrade.admin.ui.editor.DynamicProductTypeEditorInput;
import org.nightlabs.jfire.dynamictrade.store.DynamicProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class DynamicProductTypeSearchAdminActionHandler 
extends AbstractSearchResultActionHandler 
{
//	public void run() {		
//		Collection<ProductTypeID> selectedObjects = getSearchResultProvider().getSelectedObjects();
//		if (selectedObjects != null) {
//			for (ProductTypeID productTypeID : selectedObjects) {				
//				try {
//					RCPUtil.openEditor(new DynamicProductTypeEditorInput(productTypeID), 
//							DynamicProductTypeEditor.EDITOR_ID);
//				} catch (PartInitException e) {
//					throw new RuntimeException(e);
//				}
//			}
//		}
//	}
	
	public void run() {		
		Collection<DynamicProductType> selectedObjects = getSearchResultProvider().getSelectedObjects();
		if (selectedObjects != null) {
			for (DynamicProductType productType : selectedObjects) {				
				try {
					RCPUtil.openEditor(new DynamicProductTypeEditorInput(
							(ProductTypeID) JDOHelper.getObjectId(productType)), 
							DynamicProductTypeEditor.EDITOR_ID);
				} catch (PartInitException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

}
