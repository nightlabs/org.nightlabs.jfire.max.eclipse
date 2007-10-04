package org.nightlabs.jfire.simpletrade.ui.detail;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.ui.detail.IProductTypeDetailView;

/**
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class SimpleProductTypeDetailView 
implements IProductTypeDetailView 
{

	public Composite createComposite(Composite parent) 
	{
		simpleProductTypeDetailViewComposite = new SimpleProductTypeDetailViewComposite(parent, SWT.NONE); 
		return simpleProductTypeDetailViewComposite; 
	}

	private SimpleProductTypeDetailViewComposite simpleProductTypeDetailViewComposite = null;	
	
	private ProductTypeID productTypeID;
	public void setProductTypeID(ProductTypeID productTypeID) {
		this.productTypeID = productTypeID;
		if (simpleProductTypeDetailViewComposite != null)
			simpleProductTypeDetailViewComposite.setProductTypeID(productTypeID);
	}

}
