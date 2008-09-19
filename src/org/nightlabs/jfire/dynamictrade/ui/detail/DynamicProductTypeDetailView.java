package org.nightlabs.jfire.dynamictrade.ui.detail;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.ui.detail.IProductTypeDetailView;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 * @author marco schulze - marco at nightlabs dot de
 */
public class DynamicProductTypeDetailView
implements IProductTypeDetailView
{
	private DynamicProductTypeDetailViewComposite dynamicProductTypeDetailViewComposite = null;
	@SuppressWarnings("unused") //$NON-NLS-1$
	private ProductTypeID productTypeID;

	public Composite createComposite(Composite parent)
	{
		dynamicProductTypeDetailViewComposite = new DynamicProductTypeDetailViewComposite(parent, SWT.NONE);
//		if (initMemento != null)
//			dynamicProductTypeDetailViewComposite.restoreState(initMemento);
		return dynamicProductTypeDetailViewComposite;
	}

	public void setProductTypeID(ProductTypeID productTypeID) {
		this.productTypeID = productTypeID;
		if (dynamicProductTypeDetailViewComposite != null)
			dynamicProductTypeDetailViewComposite.setProductTypeID(productTypeID);
	}

//	private IMemento initMemento;
	@Override
	public void init(IMemento memento) {
//		this.initMemento = memento;
	}

	@Override
	public void saveState(IMemento memento) {
//		if (dynamicProductTypeDetailViewComposite != null)
//			dynamicProductTypeDetailViewComposite.saveState(memento);
	}

}
