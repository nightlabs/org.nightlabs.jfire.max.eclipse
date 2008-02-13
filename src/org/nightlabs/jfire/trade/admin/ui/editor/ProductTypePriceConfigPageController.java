package org.nightlabs.jfire.trade.admin.ui.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageController;
import org.nightlabs.base.ui.progress.ProgressMonitorWrapper;
import org.nightlabs.jfire.accounting.dao.PriceConfigEditDAO;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
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

	public void doLoad(IProgressMonitor monitor) {
		monitor.beginTask(Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.ProductTypePriceConfigPageController.loadPriceConfigMonitor.task.name"), 2); //$NON-NLS-1$
		monitor.worked(1);
		ProductType productType = Util.cloneSerializable(
				PriceConfigEditDAO.sharedInstance().getProductTypeForPriceConfigEditing(
						getProductTypeID(), new ProgressMonitorWrapper(monitor)));
		setProductType(productType);
		monitor.worked(1);
	}

	public void doSave(IProgressMonitor monitor) {
		for (IFormPage page : getPages()) {
			if (page instanceof AbstractGridPriceConfigPage) {
				final AbstractGridPriceConfigPage priceConfigPage = (AbstractGridPriceConfigPage) page;
				Display.getDefault().syncExec(new Runnable(){
					public void run() {
						priceConfigPage.getPriceConfigSection().getPriceConfigComposite().submit();
					}
				});
			}
		}
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
