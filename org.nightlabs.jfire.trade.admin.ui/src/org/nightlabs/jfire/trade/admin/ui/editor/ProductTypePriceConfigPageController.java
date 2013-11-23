package org.nightlabs.jfire.trade.admin.ui.editor;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageController;
import org.nightlabs.jfire.accounting.dao.PriceConfigEditDAO;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.util.Util;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ProductTypePriceConfigPageController
extends EntityEditorPageController
{
	private ProductTypeID productTypeID;
	private ProductType productType;
	
	/**
	 * @param editor
	 */
	public ProductTypePriceConfigPageController(EntityEditor editor) {
		this(editor, false);
	}

	/**
	 * @param editor
	 * @param startBackgroundLoading
	 */
	public ProductTypePriceConfigPageController(EntityEditor editor,
			boolean startBackgroundLoading) {
		super(editor, startBackgroundLoading);
		productTypeID = ((ProductTypeEditorInput)editor.getEditorInput()).getJDOObjectID();
	}

	public void doLoad(ProgressMonitor monitor) {
		monitor.beginTask(Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.ProductTypePriceConfigPageController.loadPriceConfigMonitor.task.name"), 2); //$NON-NLS-1$
		monitor.worked(1);
		ProductType productType = Util.cloneSerializable(
				PriceConfigEditDAO.sharedInstance().getProductTypeForPriceConfigEditing(
						getProductTypeID(), monitor));
		setProductType(productType);
		fireModifyEvent(null, productType);
		monitor.worked(1);
	}

	public boolean doSave(ProgressMonitor monitor) {
		for (IFormPage page : getPages()) {
			if (page instanceof AbstractGridPriceConfigPage) {
				final AbstractGridPriceConfigPage priceConfigPage = (AbstractGridPriceConfigPage) page;
				final boolean[] result = new boolean[1];
				Display.getDefault().syncExec(new Runnable(){
					public void run() {
						result[0] = priceConfigPage.getPriceConfigSection().getPriceConfigComposite().submit();
					}
				});
				return result[0];
			}
		}
		return false;
	}
	
	
	public ProductTypeID getProductTypeID() {
		return productTypeID;
	}
	
	protected void setProductType(ProductType productType) {
		this.productType = productType;
	}
	
	public ProductType getProductType() {
		return productType;
	}

}
