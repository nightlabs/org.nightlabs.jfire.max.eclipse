package org.nightlabs.jfire.trade.ui.store.search;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.QueryEvent;
import org.nightlabs.jdo.query.QueryProvider;
import org.nightlabs.jdo.query.AbstractSearchQuery.FieldChangeCarrier;
import org.nightlabs.jfire.base.ui.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.base.ui.search.ActiveTextComposite;
import org.nightlabs.jfire.store.ProductTypeGroup;
import org.nightlabs.jfire.store.dao.ProductTypeGroupDAO;
import org.nightlabs.jfire.store.id.ProductTypeGroupID;
import org.nightlabs.jfire.store.search.AbstractProductTypeQuery;
import org.nightlabs.jfire.store.search.ISaleAccessQuery;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.dao.LegalEntityDAO;
import org.nightlabs.jfire.trade.ui.legalentity.edit.LegalEntitySearchCreateWizard;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ProductTypeSearchCriteriaComposite<Q extends AbstractProductTypeQuery>
extends AbstractQueryFilterComposite<Q>
{		
	private ActiveTextComposite ownerComp;
	private ActiveTextComposite productTypeGroupComp;
	private Class<Q> queryClass;
	private SaleAccessStateCombo saleAccessStateCombo;
	
	/**
	 * @param parent
	 * @param style
	 */
	public ProductTypeSearchCriteriaComposite(Composite parent, int style, 
			QueryProvider<? super Q> queryProvider,
			Class<Q> queryClass) 
	{
		super(parent, style, queryProvider);
		this.queryClass = queryClass;
		createComposite(this);
	}

	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 */
	public ProductTypeSearchCriteriaComposite(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode, 
			QueryProvider<? super Q> queryProvider,
			Class<Q> queryClass) 
	{
		super(parent, style, layoutMode, layoutDataMode, queryProvider);
		this.queryClass = queryClass;
		createComposite(this);
	}
	
	protected void createComposite(Composite parent)
	{
		parent.setLayout(new GridLayout(3, true));
		saleAccessStateCombo = new SaleAccessStateCombo(parent, SWT.NONE);
		saleAccessStateCombo.getStateCombo().addSelectionListener(stateComboListener);
		saleAccessStateCombo.getActiveButton().addSelectionListener(activeSaleAccessButtonListener);
		if (selectedSaleAccessState != null) {
			saleAccessStateCombo.getStateCombo().selectElement(selectedSaleAccessState);
			saleAccessStateCombo.setActive(true);
		}
		ownerComp = new ActiveTextComposite(parent, Messages.getString("org.nightlabs.jfire.trade.ui.store.search.ProductTypeSearchCriteriaComposite.ownerGroup.text"), //$NON-NLS-1$ 
				ownerActiveListener, ownerBrowseListener);
		productTypeGroupComp = new ActiveTextComposite(parent, Messages.getString("org.nightlabs.jfire.trade.ui.store.search.ProductTypeSearchCriteriaComposite.productTypeGroupGroup.text"), //$NON-NLS-1$ 
				productTypeGroupActiveListener, productTypeGroupBrowseListener);		
	}

	private ButtonSelectionListener ownerActiveListener = new ButtonSelectionListener() {
		@Override
		protected void handleSelection(boolean active) 
		{
			getQuery().setOwnerID(active ? selectedOwnerID : null);
			ownerComp.setActive(active);			
		}	
	};
	
	private SelectionListener ownerBrowseListener = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			LegalEntity legalEntity = LegalEntitySearchCreateWizard.open("", false);
			if (legalEntity != null) {
				selectedOwnerID = (AnchorID) JDOHelper.getObjectId(legalEntity);
				ownerComp.setText(legalEntity.getPerson().getDisplayName());
				getQuery().setOwnerID(selectedOwnerID);
			}
		}
	};
	
	private ButtonSelectionListener productTypeGroupActiveListener = new ButtonSelectionListener() {
		@Override
		protected void handleSelection(boolean active) 
		{
			getQuery().setProductTypeGroupID(active ? selectedProductTypeGroupID : null);
			productTypeGroupComp.setActive(active);						
		}	
	};
	
	private SelectionListener productTypeGroupBrowseListener = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			// TODO: Implement ProductTypeGroupSearch
		}
	};
		
	private SelectionListener stateComboListener = new SelectionListener() {
		public void widgetSelected(SelectionEvent e) {
			selectedSaleAccessState = saleAccessStateCombo.getStateCombo().getSelectedElement();
			SaleAccessStateUtil.applySaleAccessState(selectedSaleAccessState, getQuery());
		}
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}
	};
	
	private SelectionListener activeSaleAccessButtonListener = new SelectionListener(){
		public void widgetSelected(SelectionEvent e) {
			boolean selection = ((Button)e.getSource()).getSelection();
			saleAccessStateCombo.getStateCombo().setEnabled(selection);
			if (selection) {
				selectedSaleAccessState = saleAccessStateCombo.getStateCombo().getSelectedElement();
			} else {
				selectedSaleAccessState = null;
			}
			SaleAccessStateUtil.applySaleAccessState(selectedSaleAccessState, getQuery());
		}
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}
	};
	
	private SaleAccessState selectedSaleAccessState = SaleAccessState.SALEABLE;
	public SaleAccessState getSelectedSaleAccessState() {
		return selectedSaleAccessState;
	}
	
	private AnchorID selectedOwnerID = null;
	public AnchorID getSelectedOwnerID() {
		return selectedOwnerID;
	}
	
	private ProductTypeGroupID selectedProductTypeGroupID = null;
	public ProductTypeGroupID getSelectedProductTypeGroupID() {
		return selectedProductTypeGroupID;
	}
	
	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite#resetSearchQueryValues(org.nightlabs.jdo.query.AbstractSearchQuery)
	 */
	@Override
	protected void resetSearchQueryValues(Q query) {
		SaleAccessStateUtil.applySaleAccessState(selectedSaleAccessState, query);
		query.setOwnerID(selectedOwnerID);
		query.setProductTypeGroupID(selectedProductTypeGroupID);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite#unsetSearchQueryValues(org.nightlabs.jdo.query.AbstractSearchQuery)
	 */
	@Override
	protected void unsetSearchQueryValues(Q query) 
	{
		if (!productTypeGroupComp.isActive()) {
			selectedProductTypeGroupID = null;			
		}
		if (!ownerComp.isActive()) {
			selectedOwnerID = null;
		}
		if (!saleAccessStateCombo.getActiveButton().getSelection()) {
			selectedSaleAccessState = null;
		}
		
		query.setOwnerID(null);
		query.setProductTypeGroupID(null);
		SaleAccessStateUtil.applySaleAccessState(null, query);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite#updateUI(org.nightlabs.jdo.query.QueryEvent)
	 */
	@Override
	protected void updateUI(QueryEvent event) 
	{
		if (event.getChangedQuery() == null)
		{
			selectedOwnerID = null;
			ownerComp.clear();
			setSearchSectionActive(ownerComp.getActiveButton(), false);
			
			selectedProductTypeGroupID = null;
			productTypeGroupComp.clear();
			setSearchSectionActive(productTypeGroupComp.getActiveButton(), false);
			
			selectedSaleAccessState = SaleAccessState.SALEABLE;
			saleAccessStateCombo.getStateCombo().selectElement(selectedSaleAccessState);
			setSearchSectionActive(saleAccessStateCombo.getActiveButton(), false);
		}
		else
		{ 
			boolean saleAccessStateComboSetToActive = false;
			// there is a new Query -> the changedFieldList is not null!
			for (FieldChangeCarrier changedField : event.getChangedFields())
			{				
//				boolean active = isValueIntentionallySet();

				if (AbstractProductTypeQuery.PROPERTY_OWNER_ID.equals(
						changedField.getPropertyName())) 
				{
					AnchorID ownerID = (AnchorID) changedField.getNewValue();
					if (ownerID == null) {
						ownerComp.clear();
					}
					else {
						final LegalEntity legalEntity = LegalEntityDAO.sharedInstance().getLegalEntity(
								ownerID,
								new String[] {LegalEntity.FETCH_GROUP_PERSON, FetchPlan.DEFAULT},
								NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor()
							);					
						ownerComp.setText(legalEntity.getPerson().getDisplayName());
					}
//					active |= ownerID != null;
					selectedOwnerID = ownerID;
					boolean active = (ownerID != null);
					ownerComp.setActive(active);
					setSearchSectionActive(ownerComp.getActiveButton(), active);					
				}
				
				if (AbstractProductTypeQuery.PROPERTY_PRODUCTTYPE_GROUP_ID.equals(
						changedField.getPropertyName())) 
				{
					ProductTypeGroupID productTypeGroupID = (ProductTypeGroupID) changedField.getNewValue();
					if (productTypeGroupID == null) {
						productTypeGroupComp.clear();
					}
					else {
						ProductTypeGroup productTypeGroup = ProductTypeGroupDAO.sharedInstance().getProductTypeGroup(
								productTypeGroupID, new String[] {ProductTypeGroup.FETCH_GROUP_NAME}, 
								NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
						productTypeGroupComp.setText(productTypeGroup.getName().getText());
					}
//					active |= productTypeGroupID != null;
					selectedProductTypeGroupID = productTypeGroupID;
					boolean active = (productTypeGroupID != null);
					productTypeGroupComp.setActive(active);
					setSearchSectionActive(productTypeGroupComp.getActiveButton(), active);					
				}
				
				if (AbstractProductTypeQuery.PROPERTY_CLOSED.equals(
						changedField.getPropertyName())) 
				{
					Boolean closed = (Boolean) changedField.getNewValue();
					boolean active = (closed != null && closed);
					if (active) {
						saleAccessStateCombo.getStateCombo().selectElement(SaleAccessState.CLOSED);
						saleAccessStateComboSetToActive = true;
						selectedSaleAccessState = SaleAccessState.CLOSED;
					}
				}
				
				if (AbstractProductTypeQuery.PROPERTY_CONFIRMED.equals(
						changedField.getPropertyName())) 
				{
					Boolean confirmed = (Boolean) changedField.getNewValue();
					boolean active = (confirmed != null && confirmed);
					if (active) {
						saleAccessStateCombo.getStateCombo().selectElement(SaleAccessState.CONFIRMED);
						saleAccessStateComboSetToActive = true;
						selectedSaleAccessState = SaleAccessState.CONFIRMED;
					}
				}				

				if (AbstractProductTypeQuery.PROPERTY_PUBLISHED.equals(
						changedField.getPropertyName())) 
				{
					Boolean published = (Boolean) changedField.getNewValue();
					boolean active = (published != null && published);
					if (active) {
						saleAccessStateCombo.getStateCombo().selectElement(SaleAccessState.PUBLISHED);
						saleAccessStateComboSetToActive = true;
						selectedSaleAccessState = SaleAccessState.PUBLISHED;
					}
				}				

				if (AbstractProductTypeQuery.PROPERTY_SALEABLE.equals(
						changedField.getPropertyName())) 
				{
					Boolean saleable = (Boolean) changedField.getNewValue();
					boolean active = (saleable != null && saleable);
					if (active) {
						saleAccessStateCombo.getStateCombo().selectElement(SaleAccessState.SALEABLE);
						saleAccessStateComboSetToActive = true;
						selectedSaleAccessState = SaleAccessState.SALEABLE;
					}
				}
				
			} // for (FieldChangeCarrier changedField : event.getChangedFields())
			if (saleAccessStateComboSetToActive) {
				boolean active = (selectedSaleAccessState != null);
				saleAccessStateCombo.setActive(active);
				setSearchSectionActive(saleAccessStateCombo.getActiveButton(), active);					
			}
		} // changedQuery != null
	}
		
	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite#getQueryClass()
	 */
	@Override
	public Class<Q> getQueryClass() {
		return queryClass;
	}
	
}
