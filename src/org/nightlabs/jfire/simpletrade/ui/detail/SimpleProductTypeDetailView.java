package org.nightlabs.jfire.simpletrade.ui.detail;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.ui.detail.IProductTypeDetailView;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class SimpleProductTypeDetailView
implements IProductTypeDetailView
{
	private SimpleProductTypeDetailViewComposite simpleProductTypeDetailViewComposite = null;
	private IMemento initMemento;

	public Composite createComposite(Composite parent)
	{
		simpleProductTypeDetailViewComposite = new SimpleProductTypeDetailViewComposite(parent, SWT.NONE);
		if (initMemento != null)
			simpleProductTypeDetailViewComposite.restoreState(initMemento);
		return simpleProductTypeDetailViewComposite;
	}

	public void setProductTypeID(ProductTypeID productTypeID) {
		if (simpleProductTypeDetailViewComposite != null)
			simpleProductTypeDetailViewComposite.setProductTypeID(productTypeID);
	}

	@Override
	public void init(IMemento memento) {
		this.initMemento = memento;
	}

	@Override
	public void saveState(IMemento memento) {
		if (simpleProductTypeDetailViewComposite != null)
			simpleProductTypeDetailViewComposite.saveState(memento);
	}
}
