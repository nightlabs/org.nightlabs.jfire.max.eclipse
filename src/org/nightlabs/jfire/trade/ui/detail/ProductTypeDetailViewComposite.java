package org.nightlabs.jfire.trade.ui.detail;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jfire.base.jdo.JDOObjectID2PCClassMap;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.ui.resource.Messages;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class ProductTypeDetailViewComposite extends XComposite {

	private IMemento restoreMemento = null;

	private Map<Class<? extends ProductType>, IProductTypeDetailView> detailViews = new HashMap<Class<? extends ProductType>, IProductTypeDetailView>();
	private Map<IProductTypeDetailView, Composite> detailComposites = new HashMap<IProductTypeDetailView, Composite>();

	public ProductTypeDetailViewComposite(Composite parent, int style) {
		super(parent, style);

		setLayout(new StackLayout());

		FormToolkit toolkit = new FormToolkit(getDisplay());

		Composite composite = toolkit.createComposite(this);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.setLayout(new GridLayout(1, false));

		Label label = toolkit.createLabel(composite, Messages.getString("org.nightlabs.jfire.trade.ui.detail.ProductTypeDetailViewComposite.emptySelectionLabel")); //$NON-NLS-1$
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));

		getStackLayout().topControl = composite;
	}

	protected StackLayout getStackLayout() {
		return (StackLayout) getLayout();
	}

	@SuppressWarnings("unchecked") //$NON-NLS-1$
	public void showProductTypeDetail(ProductTypeID productTypeID) {
		Class<? extends ProductType> pTypeClass = (Class<? extends ProductType>) JDOObjectID2PCClassMap.sharedInstance().getPersistenceCapableClass(productTypeID);
		IProductTypeDetailView detailView = detailViews.get(pTypeClass);
		if (detailView == null) {
			detailView = ProductTypeDetailViewRegistry.sharedInstance().createProductTypeDetailView(pTypeClass);
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
