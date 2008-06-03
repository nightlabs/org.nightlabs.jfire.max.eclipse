package org.nightlabs.jfire.trade.ui.store.search;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComboComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.QueryEvent;
import org.nightlabs.jdo.query.QueryProvider;
import org.nightlabs.jdo.query.AbstractSearchQuery.FieldChangeCarrier;
import org.nightlabs.jfire.accounting.book.id.LocalAccountantDelegateID;
import org.nightlabs.jfire.accounting.priceconfig.id.PriceConfigID;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.store.ProductTypeGroup;
import org.nightlabs.jfire.store.dao.ProductTypeGroupDAO;
import org.nightlabs.jfire.store.deliver.id.DeliveryConfigurationID;
import org.nightlabs.jfire.store.id.ProductTypeGroupID;
import org.nightlabs.jfire.store.search.AbstractProductTypeQuery;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.dao.LegalEntityDAO;
import org.nightlabs.jfire.trade.ui.legalentity.edit.LegalEntitySearchCreateWizard;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.util.CollectionUtil;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ProductTypeSearchCriteriaComposite<Q extends AbstractProductTypeQuery>
extends AbstractQueryFilterComposite<Q>
{	
	public enum SaleAccessState {
		PUBLISHED, CONFIRMED, SALEABLE, CLOSED
	}

	class ActiveTextComposite extends XComposite 
	{
		private Group group;
		private Button activeButton;
		private Text text;
		private Button browseButton;
		
		public ActiveTextComposite(Composite parent, String groupTitle,
				SelectionListener selectionListener) 
		{
			super(parent, SWT.NONE);
			createComposite(this, groupTitle, selectionListener);
		}	
		
		protected void createComposite(Composite parent, String groupTitle,
				SelectionListener selectionListener) 
		{
			setLayout(new GridLayout());
			setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			group = new Group(parent, SWT.NONE);
			group.setText(groupTitle);
			group.setLayout(new GridLayout(2, false));
			group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			activeButton = new Button(group, SWT.CHECK);
			GridData buttonData = new GridData(GridData.FILL_HORIZONTAL);
			buttonData.horizontalSpan = 2;
			activeButton.setLayoutData(buttonData);
			activeButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.store.search.ProductTypeSearchCriteriaComposite.activeButton.text"));		  //$NON-NLS-1$
			text = new Text(group, SWT.BORDER);
			text.setEnabled(false);
			text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			text.addSelectionListener(selectionListener);
			browseButton = new Button(group, SWT.NONE);
			browseButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.store.search.ProductTypeSearchCriteriaComposite.browseButton.text")); //$NON-NLS-1$
			browseButton.addSelectionListener(selectionListener);
			browseButton.setEnabled(false);
			
			activeButton.addSelectionListener(new SelectionListener(){
				public void widgetSelected(SelectionEvent e) {
					text.setEnabled(((Button)e.getSource()).getSelection());
					browseButton.setEnabled(((Button)e.getSource()).getSelection());
				}
				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			});			
		}
		
		public boolean isActive() {
			return activeButton.getSelection();
		}
		
		public void setActive(boolean active) {
			text.setEnabled(active);
			browseButton.setEnabled(active);
			activeButton.setSelection(active);
		}
		
		public void clear() {
			text.setText("");
		}
		
		public Button getActiveButton() {
			return activeButton;
		}
		
		private void setText(String text) {
			this.text.setText(text);
		}
	}
	
	private ActiveTextComposite deliveryConfigurationComp;
	private ActiveTextComposite localAccountDelegateComp;
	private ActiveTextComposite ownerComp;
	private ActiveTextComposite priceConfigComp;
	private ActiveTextComposite productTypeGroupComp;
	
	private Class<Q> queryClass;
	
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
		
		createSaleAccessComp(parent);
		deliveryConfigurationComp = new ActiveTextComposite(parent, Messages.getString("org.nightlabs.jfire.trade.ui.store.search.ProductTypeSearchCriteriaComposite.deliveryConfigurationGroup.text"), deliveryConfigurationListener); //$NON-NLS-1$
		priceConfigComp = new ActiveTextComposite(parent, Messages.getString("org.nightlabs.jfire.trade.ui.store.search.ProductTypeSearchCriteriaComposite.priceConfigurationGroup.text"), innerPriceConfigListener); //$NON-NLS-1$
		localAccountDelegateComp = new ActiveTextComposite(parent, Messages.getString("org.nightlabs.jfire.trade.ui.store.search.ProductTypeSearchCriteriaComposite.accountConfigurationGroup.text"), localAccountDelegateListener); //$NON-NLS-1$
		ownerComp = new ActiveTextComposite(parent, Messages.getString("org.nightlabs.jfire.trade.ui.store.search.ProductTypeSearchCriteriaComposite.ownerGroup.text"), ownerListener); //$NON-NLS-1$
		productTypeGroupComp = new ActiveTextComposite(parent, Messages.getString("org.nightlabs.jfire.trade.ui.store.search.ProductTypeSearchCriteriaComposite.productTypeGroupGroup.text"), productTypeGroupListener);		 //$NON-NLS-1$
	}

	private SelectionListener deliveryConfigurationListener = new SelectionListener() {
		public void widgetSelected(SelectionEvent e) {
			// TODO: select delivery configuration and set selectedDeliveryConfigurationID
		}
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}
	};

	private SelectionListener innerPriceConfigListener = new SelectionListener() {
		public void widgetSelected(SelectionEvent e) {
			// TODO: select price configuration and set selectedPriceConfigID
		}
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}
	};
	
	private SelectionListener localAccountDelegateListener = new SelectionListener() {
		public void widgetSelected(SelectionEvent e) {
			// TODO: select account configuration and set selectedLocalAccountDelegateID
		}
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}
	};

	private SelectionListener ownerListener = new SelectionListener() {
		public void widgetSelected(SelectionEvent e) {
			LegalEntity legalEntity = LegalEntitySearchCreateWizard.open("", false);
			if (legalEntity != null) {
					selectedOwnerID = (AnchorID) JDOHelper.getObjectId(legalEntity);
					ownerComp.setText(legalEntity.getPerson().getDisplayName());
			}
		}
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}
	};

	private SelectionListener productTypeGroupListener = new SelectionListener() {
		public void widgetSelected(SelectionEvent e) {
			// TODO: select productType and set selectedLocalAccountDelegateID
		}
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}
	};
	
	private Button saleAccessStateButton = null;
	private XComboComposite<SaleAccessState> stateCombo = null;
	protected void createSaleAccessComp(Composite parent)
	{
		Group group = new Group(parent, SWT.NONE);
		group.setText(Messages.getString("org.nightlabs.jfire.trade.ui.store.search.ProductTypeSearchCriteriaComposite.saleAccessGroup.text")); //$NON-NLS-1$
		group.setLayout(new GridLayout());
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		saleAccessStateButton = new Button(group, SWT.CHECK);
		saleAccessStateButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.store.search.ProductTypeSearchCriteriaComposite.activeStateButton.text")); //$NON-NLS-1$
		saleAccessStateButton.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
				stateCombo.setEnabled(((Button)e.getSource()).getSelection());
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		stateCombo = new XComboComposite<SaleAccessState>(group, SWT.READ_ONLY, new LabelProvider() {
			@Override
			public String getText(Object element)
			{
				SaleAccessState state = (SaleAccessState) element;
				String prefix = "org.nightlabs.jfire.trade.ui.store.search.ProductTypeSearchCriteriaComposite.saleAccessState_"; //$NON-NLS-1$
				return Messages.getString(prefix + state.name());
			}
		});
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		stateCombo.setLayoutData(data);
		stateCombo.addElements(CollectionUtil.array2ArrayList(SaleAccessState.values()));
		stateCombo.selectElement(selectedSaleAccessState);
		stateCombo.setEnabled(false);
		stateCombo.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
				selectedSaleAccessState = stateCombo.getSelectedElement();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
	}

	private DeliveryConfigurationID selectedDeliveryConfigurationID = null;
	public DeliveryConfigurationID getSelectedDeliveryConfigurationID() {
		return selectedDeliveryConfigurationID;
	}
	
	private PriceConfigID selectedPriceConfigID = null;
	public PriceConfigID getSelectedPriceConfigID() {
		return selectedPriceConfigID;
	}
	
	private AnchorID selectedOwnerID = null;
	public AnchorID getSelectedOwnerID() {
		return selectedOwnerID;
	}

	private ProductTypeGroupID selectedProductTypeGroupID = null;
	public ProductTypeGroupID getSelectedProductTypeGroupID() {
		return selectedProductTypeGroupID;
	}
	
	private LocalAccountantDelegateID selectedLocalAccountantDelegateID = null;
	public LocalAccountantDelegateID getSelectedLocalAccountantDelegateID() {
		return selectedLocalAccountantDelegateID;
	}

	private SaleAccessState selectedSaleAccessState = SaleAccessState.SALEABLE;
	public SaleAccessState getSelectedSaleAccessState() {
		return selectedSaleAccessState;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite#resetSearchQueryValues(org.nightlabs.jdo.query.AbstractSearchQuery)
	 */
	@Override
	protected void resetSearchQueryValues(Q query) {
		query.setDeliveryConfigurationID(selectedDeliveryConfigurationID);
		query.setInnerPriceConfigID(selectedPriceConfigID);
		query.setLocalAccountantDelegateID(selectedLocalAccountantDelegateID);
		query.setOwnerID(selectedOwnerID);
		query.setProductTypeGroupID(selectedProductTypeGroupID);
		applySaleAccessState(selectedSaleAccessState, query);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite#unsetSearchQueryValues(org.nightlabs.jdo.query.AbstractSearchQuery)
	 */
	@Override
	protected void unsetSearchQueryValues(Q query) 
	{
		if (!deliveryConfigurationComp.isActive()) {
			selectedDeliveryConfigurationID = null;
		}
		if (!localAccountDelegateComp.isActive()) {
			selectedLocalAccountantDelegateID = null;
		}
		if (!priceConfigComp.isActive()) {
			selectedPriceConfigID = null;			
		}
		if (!productTypeGroupComp.isActive()) {
			selectedProductTypeGroupID = null;			
		}
		if (!ownerComp.isActive()) {
			selectedOwnerID = null;
		}
		if (!saleAccessStateButton.getSelection()) {
			selectedSaleAccessState = SaleAccessState.SALEABLE;
		}		
		
		query.setDeliveryConfigurationID(null);
		query.setInnerPriceConfigID(null);
		query.setLocalAccountantDelegateID(null);
		query.setOwnerID(null);
		query.setProductTypeGroupID(null);
		applySaleAccessState(SaleAccessState.SALEABLE, query);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite#updateUI(org.nightlabs.jdo.query.QueryEvent)
	 */
	@Override
	protected void updateUI(QueryEvent event) 
	{
		if (event.getChangedQuery() == null)
		{
			selectedDeliveryConfigurationID = null;
			deliveryConfigurationComp.clear();
			setSearchSectionActive(deliveryConfigurationComp.getActiveButton(), false);
			
			selectedLocalAccountantDelegateID = null;
			localAccountDelegateComp.clear();
			setSearchSectionActive(localAccountDelegateComp.getActiveButton(), false);

			selectedOwnerID = null;
			ownerComp.clear();
			setSearchSectionActive(ownerComp.getActiveButton(), false);
			
			selectedPriceConfigID = null;
			priceConfigComp.clear();
			setSearchSectionActive(priceConfigComp.getActiveButton(), false);

			selectedProductTypeGroupID = null;
			productTypeGroupComp.clear();
			setSearchSectionActive(productTypeGroupComp.getActiveButton(), false);

			selectedSaleAccessState = SaleAccessState.SALEABLE;
			stateCombo.selectElement(selectedSaleAccessState);
			setSearchSectionActive(saleAccessStateButton, false);
		}
		else
		{ // there is a new Query -> the changedFieldList is not null!
			for (FieldChangeCarrier changedField : event.getChangedFields())
			{
				boolean active = isValueIntentionallySet();
				if (AbstractProductTypeQuery.PROPERTY_DELIVERY_CONFIGURATION_ID.equals(
						changedField.getPropertyName())) 
				{
					DeliveryConfigurationID deliveryConfigurationID = (DeliveryConfigurationID) changedField.getNewValue();
					if (deliveryConfigurationID == null) {
						deliveryConfigurationComp.clear();
					}
					else {
						// TODO get name from DAO and set it as text
						deliveryConfigurationComp.setText(deliveryConfigurationID.toString());
					}
					active |= deliveryConfigurationID != null;
					deliveryConfigurationComp.setActive(active);
					setSearchSectionActive(deliveryConfigurationComp.getActiveButton(), active);					
				}
				
				if (AbstractProductTypeQuery.PROPERTY_INNER_PRICE_CONFIG_ID.equals(
						changedField.getPropertyName())) 
				{
					PriceConfigID innerPriceConfigID = (PriceConfigID) changedField.getNewValue();
					if (innerPriceConfigID == null) {
						priceConfigComp.clear();
					}
					else {
						// TODO get name from DAO and set it as text						
						priceConfigComp.setText(innerPriceConfigID.toString());
					}
					active |= innerPriceConfigID != null;
					priceConfigComp.setActive(active);
					setSearchSectionActive(priceConfigComp.getActiveButton(), active);					
				}

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
					active |= ownerID != null;
					ownerComp.setActive(active);
					setSearchSectionActive(ownerComp.getActiveButton(), active);					
				}
				
				if (AbstractProductTypeQuery.PROPERTY_LOCAL_ACCOUNTANT_DELEGATE_ID.equals(
						changedField.getPropertyName())) 
				{
					LocalAccountantDelegateID localAccountantDelegateID = (LocalAccountantDelegateID) changedField.getNewValue();
					if (localAccountantDelegateID == null) {
						localAccountDelegateComp.clear();
					}
					else {
						// TODO get name from DAO and set it as text						
						localAccountDelegateComp.setText(localAccountantDelegateID.toString());
					}
					active |= localAccountantDelegateID != null;
					localAccountDelegateComp.setActive(active);
					setSearchSectionActive(localAccountDelegateComp.getActiveButton(), active);					
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
					active |= productTypeGroupID != null;
					productTypeGroupComp.setActive(active);
					setSearchSectionActive(productTypeGroupComp.getActiveButton(), active);					
				}
				
				if (AbstractProductTypeQuery.PROPERTY_CLOSED.equals(
						changedField.getPropertyName())) 
				{
					Boolean closed = (Boolean) changedField.getNewValue();
					if (closed) {
						stateCombo.selectElement(SaleAccessState.CLOSED);
					}
				}
				
				if (AbstractProductTypeQuery.PROPERTY_CONFIRMED.equals(
						changedField.getPropertyName())) 
				{
					Boolean confirmed = (Boolean) changedField.getNewValue();
					if (confirmed) {
						stateCombo.selectElement(SaleAccessState.CONFIRMED);
					}
				}				

				if (AbstractProductTypeQuery.PROPERTY_PUBLISHED.equals(
						changedField.getPropertyName())) 
				{
					Boolean published = (Boolean) changedField.getNewValue();
					if (published) {
						stateCombo.selectElement(SaleAccessState.PUBLISHED);
					}
				}				

				if (AbstractProductTypeQuery.PROPERTY_SALEABLE.equals(
						changedField.getPropertyName())) 
				{
					Boolean saleable = (Boolean) changedField.getNewValue();
					if (saleable) {
						stateCombo.selectElement(SaleAccessState.SALEABLE);
					}
				}				
				
			} // for (FieldChangeCarrier changedField : event.getChangedFields())
		} // changedQuery != null
	}

	protected void applySaleAccessState(SaleAccessState state, AbstractProductTypeQuery query) 
	{
		switch (state) {			
			case PUBLISHED:
				query.setPublished(true);
				query.setConfirmed(false);
				query.setSaleable(false);
				query.setClosed(false);				
				break;
			case CONFIRMED:
				query.setPublished(false);
				query.setConfirmed(true);
				query.setSaleable(false);
				query.setClosed(false);				
				break;
			case SALEABLE:
				query.setPublished(false);
				query.setConfirmed(false);
				query.setSaleable(true);
				query.setClosed(false);				
				break;
			case CLOSED:
				query.setPublished(false);
				query.setConfirmed(false);
				query.setSaleable(false);
				query.setClosed(true);				
				break;
		}
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite#getQueryClass()
	 */
	@Override
	public Class<Q> getQueryClass() {
		return queryClass;
	}
	
}
