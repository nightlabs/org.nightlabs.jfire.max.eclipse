package org.nightlabs.jfire.voucher.admin.ui.store.search;

import java.util.Collection;

import javax.jdo.JDOHelper;

import org.eclipse.ui.PartInitException;
import org.nightlabs.base.ui.search.AbstractSearchResultActionHandler;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.voucher.admin.ui.editor.VoucherTypeEditor;
import org.nightlabs.jfire.voucher.admin.ui.editor.VoucherTypeEditorInput;
import org.nightlabs.jfire.voucher.store.VoucherType;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class VoucherTypeSearchAdminActionHandler
extends AbstractSearchResultActionHandler
{
//	public void run() {
//		Collection<ProductTypeID> selectedObjects = getSearchResultProvider().getSelectedObjects();
//		if (selectedObjects != null) {
//			for (ProductTypeID productTypeID : selectedObjects) {
//				try {
//					RCPUtil.openEditor(new VoucherTypeEditorInput(productTypeID), VoucherTypeEditor.EDITOR_ID);
//				} catch (PartInitException e) {
//					throw new RuntimeException(e);
//				}
//			}
//		}
//	}

	public void run() {
		Collection<VoucherType> selectedObjects = getSearchResultProvider().getSelectedObjects();
		if (selectedObjects != null) {
			for (VoucherType productType : selectedObjects) {
				try {
					RCPUtil.openEditor(new VoucherTypeEditorInput(
							(ProductTypeID) JDOHelper.getObjectId(productType)),
							VoucherTypeEditor.EDITOR_ID);
				} catch (PartInitException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
	
}
