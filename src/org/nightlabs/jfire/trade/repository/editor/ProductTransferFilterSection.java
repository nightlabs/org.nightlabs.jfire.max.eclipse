package org.nightlabs.jfire.trade.repository.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.editor.RestorableSectionPart;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jfire.store.query.AbstractProductTransferQuery;
import org.nightlabs.jfire.trade.TradePlugin;
import org.nightlabs.jfire.trade.resource.Messages;

class ProductTransferFilterSection
extends RestorableSectionPart
{
	private ProductTransferPageController controller;
	private Spinner limit;
	private Button refreshButton;

	public ProductTransferFilterSection(FormPage page, Composite parent, ProductTransferPageController _controller)
	{
		super(parent, page.getEditor().getToolkit(), ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		this.controller = _controller;
		getSection().setText(Messages.getString("org.nightlabs.jfire.trade.repository.editor.ProductTransferFilterSection.filterCriteriaSection.text")); //$NON-NLS-1$
		getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		getSection().setLayout(new GridLayout());

		this.controller.addPropertyChangeListener(ProductTransferPageController.PROPERTY_PRODUCT_TRANSFER_QUERY, new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt)
			{
				if (ignoreProductTransferQueryChanged)
					return;

				productTransferQueryChanged((AbstractProductTransferQuery<?>) evt.getNewValue());
			}
		});

		XComposite client = new XComposite(getSection(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		client.getGridLayout().numColumns = 3; // TODO when adding more UI, we might want to switch to 1 and use wrapper-composites

		new Label(client, SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.trade.repository.editor.ProductTransferFilterSection.resultLimitLabel.text")); //$NON-NLS-1$
		limit = new Spinner(client, SWT.BORDER);
		limit.setMinimum(0);
		limit.setMaximum(Integer.MAX_VALUE);
		limit.setSelection(1000000000);

		refreshButton = new Button(client, SWT.NONE);
		refreshButton.setImage(SharedImages.getSharedImage(TradePlugin.getDefault(), ProductTransferFilterSection.class, "refreshButton")); //$NON-NLS-1$
		refreshButton.setText(Messages.getString("org.nightlabs.jfire.trade.repository.editor.ProductTransferFilterSection.refreshButton.text")); //$NON-NLS-1$
		refreshButton.setToolTipText(Messages.getString("org.nightlabs.jfire.trade.repository.editor.ProductTransferFilterSection.refreshButton.toolTipText")); //$NON-NLS-1$
		refreshButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				controller.getProductTransferQuery().setFromInclude(0);
				controller.getProductTransferQuery().setToExclude(limit.getSelection() == 0 ? Long.MAX_VALUE : limit.getSelection());
				fireProductTransferQueryChanged();
			}
		});

		getSection().setClient(client);
		productTransferQueryChanged(this.controller.getProductTransferQuery());
	}

	private boolean ignoreProductTransferQueryChanged = false;
	
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
	private void productTransferQueryChanged(AbstractProductTransferQuery<?> productTransferQuery)
	{
		if (productTransferQuery.getToExclude() > Integer.MAX_VALUE)
			limit.setSelection(0);
		else
			limit.setSelection((int)productTransferQuery.getToExclude());
	}
}
