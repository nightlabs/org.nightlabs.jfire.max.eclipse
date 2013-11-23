package org.nightlabs.jfire.simpletrade.admin.ui.gridpriceconfig;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.simpletrade.admin.ui.producttype.ProductTypeTree;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.AbstractCellReferenceProductTypeSelector;

public class SimpleProductTypeSelector extends AbstractCellReferenceProductTypeSelector
{
	private ProductTypeTree productTypeTree = null;

	@Override
	protected Composite _createComposite(Composite parent) {
		productTypeTree = new ProductTypeTree(parent, SWT.NONE); // , RCPUtil.getActiveWorkbenchPage().getActivePart().getSite(), SimpleProductTypeSelector.class.getName());
		productTypeTree.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event)
			{
				fireSelectionChangedEvent();
			}
		});
		return productTypeTree;
	}

	@Override
	public ProductTypeID getSelectedProductTypeID() {
		return productTypeTree.getSelectedElements().isEmpty() ? null : productTypeTree.getSelectedElements().iterator().next().getObjectId();
//		return productTypeTree.getSelectedProductTypeTreeNode().getProductType().getObjectId();
	}
}
