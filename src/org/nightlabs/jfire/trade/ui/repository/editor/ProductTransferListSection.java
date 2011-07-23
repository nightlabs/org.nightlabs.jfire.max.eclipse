package org.nightlabs.jfire.trade.ui.repository.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.editor.RestorableSectionPart;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageControllerModifyEvent;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageControllerModifyListener;
import org.nightlabs.jdo.query.QueryCollection;
import org.nightlabs.jfire.store.ProductTransfer;
import org.nightlabs.jfire.trade.ui.repository.transfer.ProductTransferTable;
import org.nightlabs.jfire.trade.ui.resource.Messages;

class ProductTransferListSection
extends RestorableSectionPart
{
	private ProductTransferPageController controller;
	private ProductTransferTable productTransferTable;

	public ProductTransferListSection(FormPage page, Composite parent, ProductTransferPageController controller)
	{
		super(parent, page.getEditor().getToolkit(), ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		this.controller = controller;
		getSection().setText(Messages.getString("org.nightlabs.jfire.trade.ui.repository.editor.ProductTransferListSection.productTransfersSection.text")); //$NON-NLS-1$
		getSection().setLayoutData(new GridData(GridData.FILL_BOTH));
		getSection().setLayout(new GridLayout());

		this.controller.addPropertyChangeListener(ProductTransferPageController.PROPERTY_PRODUCT_TRANSFER_QUERY, new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt)
			{
				if (ignoreProductTransferQueryChanged)
					return;

				productTransferQueryChanged((QueryCollection<?>) evt.getNewValue());
			}
		});

		this.controller.addModifyListener(new IEntityEditorPageControllerModifyListener() {
			public void controllerObjectModified(final EntityEditorPageControllerModifyEvent modifyEvent)
			{
				Display.getDefault().asyncExec(new Runnable()
				{
					@SuppressWarnings("unchecked") 
					public void run()
					{
						productTransferListChanged((List<ProductTransfer>) modifyEvent.getNewObject());
					}
				});
			}
		});

		XComposite client = new XComposite(getSection(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);

		productTransferTable = new ProductTransferTable(client, SWT.NONE);

		getSection().setClient(client);
		productTransferQueryChanged(this.controller.getQueryWrapper());

		List<ProductTransfer> productTransferList = this.controller.getProductTransferList();
		if (productTransferList != null)
			productTransferListChanged(productTransferList);
	}

	protected boolean ignoreProductTransferQueryChanged = false;
	
	/**
	 * must be called on UI thread!
	 */
	private void fireProductTransferQueryChanged()
	{
		ignoreProductTransferQueryChanged = true;
		try {
			controller.fireProductTransferQueryChange();
		} finally {
			ignoreProductTransferQueryChanged = false;
		}
	}

	/**
	 * This method is called on the UI thread whenever the productTransferQuery has changed.
	 * It is not called, if the change originated from here (i.e. {@link #fireProductTransferQueryChanged()} in
	 * this object).
	 */
	private void productTransferQueryChanged(QueryCollection<?> queryCollection)
	{
		productTransferTable.setLoadingStatus();
	}

	/**
	 * this method is called on the UI thread.
	 */
	private void productTransferListChanged(List<ProductTransfer> productTransferList)
	{
		productTransferTable.setProductTransfers(controller.getCurrentRepositoryID(), productTransferList);
	}
}
