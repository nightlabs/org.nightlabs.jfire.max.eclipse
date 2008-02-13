package org.nightlabs.jfire.simpletrade.admin.ui.store.search;

import java.util.Collection;

import javax.jdo.JDOHelper;

import org.eclipse.ui.PartInitException;
import org.nightlabs.base.ui.search.AbstractSearchResultActionHandler;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.simpletrade.admin.ui.editor.SimpleProductTypeEditor;
import org.nightlabs.jfire.simpletrade.store.SimpleProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.admin.ui.editor.ProductTypeEditorInput;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class SimpleProductTypeSearchAdminActionHandler
extends AbstractSearchResultActionHandler
{
//	public void run() {
//		Collection<ProductTypeID> selectedObjects = getSearchResultProvider().getSelectedObjects();
//		if (selectedObjects != null) {
//			for (ProductTypeID productTypeID : selectedObjects) {
//				try {
//					RCPUtil.openEditor(new ProductTypeEditorInput(productTypeID), SimpleProductTypeEditor.ID_EDITOR);
//				} catch (PartInitException e) {
//					throw new RuntimeException(e);
//				}
//			}
//		}
//	}

	public void run()
	{
		Collection<SimpleProductType> selectedObjects = getSearchResultProvider().getSelectedObjects();
		if (selectedObjects != null) {
			for (SimpleProductType productType : selectedObjects) {
				try {
					RCPUtil.openEditor(new ProductTypeEditorInput(
							(ProductTypeID) JDOHelper.getObjectId(productType)),
							SimpleProductTypeEditor.ID_EDITOR);
				} catch (PartInitException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
	
}
