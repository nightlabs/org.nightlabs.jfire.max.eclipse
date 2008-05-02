package org.nightlabs.jfire.trade.ui.detail;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;

/**
 * The Generic implementation of {@link IProductTypeDetailView}
 * which displays only those information which all {@link ProductType}s have
 * 
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class GenericProductTypeDetailView
implements IProductTypeDetailView
{

	public Composite createComposite(Composite parent)
	{
		genericProductTypeDetailViewComposite = new GenericProductTypeDetailViewComposite(parent, SWT.NONE);
		return genericProductTypeDetailViewComposite;
	}

	private GenericProductTypeDetailViewComposite genericProductTypeDetailViewComposite = null;
	
	private ProductTypeID productTypeID;
	public void setProductTypeID(ProductTypeID productTypeID) {
		this.productTypeID = productTypeID;
		if (genericProductTypeDetailViewComposite != null)
			genericProductTypeDetailViewComposite.setProductTypeID(productTypeID);
	}
	@Override
	public void init(IMemento memento) {
	}
	
	@Override
	public void saveState(IMemento memento) {
	}

}
