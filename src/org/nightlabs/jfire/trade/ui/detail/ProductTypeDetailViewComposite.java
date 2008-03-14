/**
 * 
 */
package org.nightlabs.jfire.trade.ui.detail;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jfire.base.jdo.JDOObjectID2PCClassMap;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ProductTypeDetailViewComposite extends XComposite {

	private Map<Class<? extends ProductType>, IProductTypeDetailView> detailViews = new HashMap<Class<? extends ProductType>, IProductTypeDetailView>();
	private Map<IProductTypeDetailView, Composite> detailComposites = new HashMap<IProductTypeDetailView, Composite>();
	
	/**
	 * @param parent
	 * @param style
	 */
	public ProductTypeDetailViewComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new StackLayout());
	}

	protected void createComposite(XComposite parent) {
		parent.setLayout(new StackLayout());
		
	}
	
	protected StackLayout getStackLayout() {
		return (StackLayout) getLayout();
	}
	
	public void showProductTypeDetail(ProductTypeID productTypeID) {
		Class<? extends ProductType> pTypeClass = (Class<? extends ProductType>) JDOObjectID2PCClassMap.sharedInstance().getPersistenceCapableClass(productTypeID);
		IProductTypeDetailView detailView = detailViews.get(pTypeClass);
		if (detailView == null) {
			detailView = ProductTypeDetailViewRegistry.sharedInstance().getProductTypeDetailView(pTypeClass);
			detailViews.put(pTypeClass, detailView);
			Composite composite = detailView.createComposite(this);
			detailComposites.put(detailView, composite);
		}
		Composite composite = detailComposites.get(detailView);
		
		getStackLayout().topControl = composite;
		
		this.layout(true, true);
		
		detailView.setProductTypeID(productTypeID);
	}
}
