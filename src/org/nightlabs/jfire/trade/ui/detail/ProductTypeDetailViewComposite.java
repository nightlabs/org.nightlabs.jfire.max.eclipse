/**
 * 
 */
package org.nightlabs.jfire.trade.ui.detail;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jfire.base.jdo.JDOObjectID2PCClassMap;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.ui.resource.Messages;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ProductTypeDetailViewComposite extends XComposite {

	private IMemento restoreMemento = null;
	
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
			if (restoreMemento != null) {
				IMemento detailViewMemento = restoreMemento.getChild(getXMLClassName(pTypeClass));
				if (detailViewMemento != null) {
					detailView.init(detailViewMemento);
				}
			}
			Composite composite = detailView.createComposite(this);
			detailComposites.put(detailView, composite);
		}
		Composite composite = detailComposites.get(detailView);
		
		getStackLayout().topControl = composite;
		
		this.layout(true, true);
		
		detailView.setProductTypeID(productTypeID);
	}
	
	public void saveState(IMemento memento) {
		for (Entry<Class<? extends ProductType>, IProductTypeDetailView> entry : detailViews.entrySet()) {
			IMemento detailViewMemento = memento.createChild(getXMLClassName(entry.getKey()));
			entry.getValue().saveState(detailViewMemento);
		}
	}
	
	private String getXMLClassName(Class<?> clazz) {
		return clazz.getName().replaceAll("\\.", "_"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public void init(IMemento memento) {
		this.restoreMemento = memento;
	}
		
}
