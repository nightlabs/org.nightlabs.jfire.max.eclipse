package org.nightlabs.jfire.trade.ui.store.search;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.IExpansionListener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.progress.ProgressMonitorWrapper;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.QueryCollection;
import org.nightlabs.jfire.accounting.book.LocalAccountantDelegate;
import org.nightlabs.jfire.accounting.priceconfig.PriceConfig;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.ProductTypeGroup;
import org.nightlabs.jfire.store.ProductTypeLocal;
import org.nightlabs.jfire.store.dao.ProductTypeDAO;
import org.nightlabs.jfire.store.deliver.DeliveryConfiguration;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.store.search.AbstractProductTypeQuery;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public abstract class AbstractProductTypeSearchComposite
extends XComposite
{
	/**
	 * @param parent
	 * @param style
	 */
	public AbstractProductTypeSearchComposite(Composite parent, int style) {
		super(parent, style);
		createComposite(this);
	}

	private ProductTypeTableComposite productTypeTableComposite;
	private Text searchText;
	private ProductTypeSearchCriteriaComposite searchCriteriaComposite = null;
	
	public ProductTypeTableComposite getProductTypeTableComposite() {
		return productTypeTableComposite;
	}
	
	public ProductTypeSearchCriteriaComposite getSearchCriteriaComposite() {
		return searchCriteriaComposite;
	}
	
	protected void createComposite(Composite parent)
	{
		final Composite criteriaComp = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		criteriaComp.setLayout(new GridLayout(2, false));
		criteriaComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Label searchLabel = new Label(criteriaComp, SWT.NONE);
		searchLabel.setText(Messages.getString("org.nightlabs.jfire.trade.ui.store.search.AbstractProductTypeSearchComposite.searchLabel.text")); //$NON-NLS-1$
		
		searchText = new Text(criteriaComp, SWT.BORDER);
		searchText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		ExpandableComposite expandableComposite = new ExpandableComposite(criteriaComp, ExpandableComposite.TWISTIE);
		expandableComposite.setText(Messages.getString("org.nightlabs.jfire.trade.ui.store.search.AbstractProductTypeSearchComposite.expandableComposite.text")); //$NON-NLS-1$
		expandableComposite.setLayout(new GridLayout());
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 2;
		expandableComposite.setLayoutData(gridData);
		searchCriteriaComposite = new ProductTypeSearchCriteriaComposite(expandableComposite, SWT.NONE);
		searchCriteriaComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		expandableComposite.setClient(searchCriteriaComposite);
		expandableComposite.addExpansionListener(new IExpansionListener(){
			public void expansionStateChanging(ExpansionEvent e) {
				
			}
			public void expansionStateChanged(ExpansionEvent e) {
				layout(true, true);
			}
		});
		
		productTypeTableComposite = new ProductTypeTableComposite(parent, SWT.NONE);
		productTypeTableComposite.addSelectionChangedListener(productTypeSelectionListener);
		
		searchText.addSelectionListener(searchTextListener);
	}
		
	private SelectionListener searchTextListener = new SelectionListener(){
		public void widgetSelected(SelectionEvent e) {
			searchPressed();
		}
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}
	};
	
	private ISelectionChangedListener productTypeSelectionListener = new ISelectionChangedListener(){
		public void selectionChanged(SelectionChangedEvent event)
		{
			StructuredSelection sel = (StructuredSelection) event.getSelection();
			selectedProductType = (ProductType) sel.getFirstElement();
		}
	};
	
	public static final String[] PRODUCT_TYPE_SEARCH_FETCH_GROUPS = new String[] {
		FetchPlan.DEFAULT,
		ProductType.FETCH_GROUP_THIS_PRODUCT_TYPE,
		ProductType.FETCH_GROUP_NAME,
		DeliveryConfiguration.FETCH_GROUP_NAME,
		LocalAccountantDelegate.FETCH_GROUP_NAME,
		ProductTypeGroup.FETCH_GROUP_NAME,
		PriceConfig.FETCH_GROUP_NAME,
		LegalEntity.FETCH_GROUP_PERSON,
		// TODO remove the following fetch-group, because most implementations of ProductType don't support nesting
		ProductTypeLocal.FETCH_GROUP_NESTED_PRODUCT_TYPE_LOCALS
	};
 
	protected String[] getFetchGroups() {
		return PRODUCT_TYPE_SEARCH_FETCH_GROUPS;
	}
		
	public void searchPressed()
	{
		final String searchStr = searchText.getText();
		Job searchJob = new Job(Messages.getString("org.nightlabs.jfire.trade.ui.store.search.AbstractProductTypeSearchComposite.searchJob.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						productTypeTableComposite.setInput(new String[] {Messages.getString("org.nightlabs.jfire.trade.ui.store.search.AbstractProductTypeSearchComposite.productTypeTableComposite.input_searching")}); //$NON-NLS-1$
					}
				});
				QueryCollection<ProductType, AbstractProductTypeQuery<? extends ProductType>> productTypeQueries =
					new QueryCollection<ProductType, AbstractProductTypeQuery<? extends ProductType>>(ProductType.class);
				
				AbstractProductTypeQuery<? extends ProductType> query = createNewQuery();

//				if (!searchStr.trim().equals(""))
					query.setFullTextSearch(".*"+searchStr+".*"); // Need to pass regex here //$NON-NLS-1$ //$NON-NLS-2$

				switch(getSearchCriteriaComposite().getSelectedSaleAccessState()) {
					case PUBLISHED: query.setPublished(true);
					break;
					case CONFIRMED: query.setConfirmed(true);
					break;
					case SALEABLE: query.setSaleable(true);
					break;
					case CLOSED: query.setClosed(true);
					break;
					default:
						throw new IllegalStateException("Unknown SaleAccessState: " + getSearchCriteriaComposite().getSelectedSaleAccessState()); //$NON-NLS-1$
				}

				if (getSearchCriteriaComposite().getSelectedDeliveryConfigurationID() != null)
					query.setDeliveryConfigurationID(getSearchCriteriaComposite().getSelectedDeliveryConfigurationID());

				if (getSearchCriteriaComposite().getSelectedLocalAccountantDelegateID() != null)
					query.setLocalAccountantDelegateID(getSearchCriteriaComposite().getSelectedLocalAccountantDelegateID());

				if (getSearchCriteriaComposite().getSelectedOwnerID() != null)
					query.setOwnerID(getSearchCriteriaComposite().getSelectedOwnerID());

				if (getSearchCriteriaComposite().getSelectedPriceConfigID() != null)
					query.setInnerPriceConfigID(getSearchCriteriaComposite().getSelectedPriceConfigID());

				if (getSearchCriteriaComposite().getSelectedProductTypeGroupID() != null)
					query.setProductTypeGroupID(getSearchCriteriaComposite().getSelectedProductTypeGroupID());
				
				productTypeQueries.add(query);
				
				Set<ProductTypeID> productTypeIDs;
				try {
					productTypeIDs = TradePlugin.getDefault().getStoreManager().getProductTypeIDs(productTypeQueries);
					final Collection<ProductType> productTypes = ProductTypeDAO.sharedInstance().getProductTypes(
							productTypeIDs,
							getFetchGroups(),
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
							new ProgressMonitorWrapper(monitor));
					
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							productTypeTableComposite.setInput(productTypes);
							if (productTypeTableComposite.getItemCount() == 1) {
								productTypeTableComposite.select(0);
								productTypeTableComposite.setFocus();
							}
						}
					});
				} catch (RemoteException e) {
					throw new RuntimeException(e);
				}
				return Status.OK_STATUS;
			}
		};
		searchJob.schedule();
	}
	
	private ProductType selectedProductType = null;
	public ProductType getSelectedProductType() {
		return selectedProductType;
	}
	
	public void setSearchText(String searchTextString) {
		if (searchText != null && !searchText.isDisposed()) {
			searchText.setText(searchTextString);
		}
	}

	protected abstract AbstractProductTypeQuery<? extends ProductType> createNewQuery();
	
	protected Collection<ProductType> retrieveProductTypes(
		QueryCollection<? extends ProductType, ? extends AbstractProductTypeQuery<? extends ProductType>> queries,
			ProgressMonitor monitor)
	{
		Set<ProductTypeID> productTypeIDs;
		try {
			productTypeIDs = TradePlugin.getDefault().getStoreManager().getProductTypeIDs(queries);
			Collection<ProductType> productTypes = ProductTypeDAO.sharedInstance().getProductTypes(
					productTypeIDs,
					getFetchGroups(),
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
					monitor);
			return productTypes;
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}
 
}
