package org.nightlabs.jfire.trade.ui.store.search;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.nightlabs.jdo.query.QueryEvent;
import org.nightlabs.jdo.query.QueryProvider;
import org.nightlabs.jdo.query.AbstractSearchQuery.FieldChangeCarrier;
import org.nightlabs.jfire.base.ui.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.store.search.AbstractProductTypeQuery;
import org.nightlabs.jfire.trade.ui.resource.Messages;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class ProductTypeSearchCriteriaComposite<Q extends AbstractProductTypeQuery>
	extends AbstractQueryFilterComposite<Q>
{
//	private ActiveTextComposite ownerComp;
//	private ActiveTextComposite productTypeGroupComp;
	private Class<Q> queryClass;
	private ProductTypeRelatedQueryStateTable stateTable;

	/**
	 * @param parent
	 * @param style
	 */
	public ProductTypeSearchCriteriaComposite(Composite parent, int style,
			QueryProvider<? super Q> queryProvider, Class<Q> queryClass)
	{
		super(parent, style, queryProvider);
		this.queryClass = queryClass;
		createComposite();
	}

	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 */
	public ProductTypeSearchCriteriaComposite(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode,
			QueryProvider<? super Q> queryProvider, Class<Q> queryClass)
	{
		super(parent, style, layoutMode, layoutDataMode, queryProvider);
		this.queryClass = queryClass;
		createComposite();
	}

	@Override
	protected void createComposite()
	{
		setLayout(new GridLayout(2, true));
//		ownerComp = new ActiveTextComposite(this, Messages.getString("org.nightlabs.jfire.trade.ui.store.search.ProductTypeSearchCriteriaComposite.ownerGroup.text"), //$NON-NLS-1$
//				ownerActiveListener, ownerBrowseListener);
//		productTypeGroupComp = new ActiveTextComposite(this, Messages.getString("org.nightlabs.jfire.trade.ui.store.search.ProductTypeSearchCriteriaComposite.productTypeGroupGroup.text"), //$NON-NLS-1$
//				productTypeGroupActiveListener, productTypeGroupBrowseListener);
		Group statesGroup = new Group(this, SWT.NONE);
		statesGroup.setText(Messages.getString("org.nightlabs.jfire.trade.ui.store.search.ProductTypeSearchCriteriaComposite.group.productTypeStates.text")); //$NON-NLS-1$
		statesGroup.setLayout(new GridLayout());
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan = 2;
		statesGroup.setLayoutData(gd);
		stateTable = new ProductTypeRelatedQueryStateTable(statesGroup, SWT.NONE);
		stateTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		stateTable.setInput(getQuery());
	}

//	private ButtonSelectionListener ownerActiveListener = new ButtonSelectionListener() {
//		@Override
//		protected void handleSelection(boolean active)
//		{
//			getQuery().setOwnerID(active ? selectedOwnerID : null);
//			ownerComp.setActive(active);
//		}
//	};
//
//	private SelectionListener ownerBrowseListener = new SelectionAdapter() {
//		@Override
//		public void widgetSelected(SelectionEvent e) {
//			LegalEntity legalEntity = LegalEntitySearchCreateWizard.open("", false); //$NON-NLS-1$
//			if (legalEntity != null) {
//				selectedOwnerID = (AnchorID) JDOHelper.getObjectId(legalEntity);
//				ownerComp.setText(legalEntity.getPerson().getDisplayName());
//				getQuery().setOwnerID(selectedOwnerID);
//			}
//		}
//	};
//
//	private ButtonSelectionListener productTypeGroupActiveListener = new ButtonSelectionListener() {
//		@Override
//		protected void handleSelection(boolean active)
//		{
//			getQuery().setProductTypeGroupID(active ? selectedProductTypeGroupID : null);
//			productTypeGroupComp.setActive(active);
//		}
//	};
//
//	private SelectionListener productTypeGroupBrowseListener = new SelectionAdapter() {
//		@Override
//		public void widgetSelected(SelectionEvent e) {
//			// TODO: Implement ProductTypeGroupSearch
//		}
//	};
//
//	private AnchorID selectedOwnerID = null;
//	public AnchorID getSelectedOwnerID() {
//		return selectedOwnerID;
//	}
//
//	private ProductTypeGroupID selectedProductTypeGroupID = null;
//	public ProductTypeGroupID getSelectedProductTypeGroupID() {
//		return selectedProductTypeGroupID;
//	}

	private static final String ProductType_Group_ID = "ProductTypeSearchCriteriaComposite"; //$NON-NLS-1$
	private static final Set<String> fieldNames;
	static
	{
		fieldNames = new HashSet<String>();
		fieldNames.add(AbstractProductTypeQuery.FieldName.ownerID);
		fieldNames.add(AbstractProductTypeQuery.FieldName.productTypeGroupID);
		fieldNames.add(AbstractProductTypeQuery.FieldName.closed);
		fieldNames.add(AbstractProductTypeQuery.FieldName.confirmed);
		fieldNames.add(AbstractProductTypeQuery.FieldName.published);
		fieldNames.add(AbstractProductTypeQuery.FieldName.saleable);
	}

	@Override
	protected Set<String> getFieldNames()
	{
		return fieldNames;
	}

	@Override
	protected String getGroupID()
	{
		return ProductType_Group_ID;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite#updateUI(org.nightlabs.jdo.query.QueryEvent)
	 */
	@Override
	protected void updateUI(QueryEvent event, List<FieldChangeCarrier> changedFields)
	{
//		// there is a new Query -> the changedFieldList is not null!
//		for (FieldChangeCarrier changedField : event.getChangedFields())
//		{
//			final String changedPropName = changedField.getPropertyName();
//			if (AbstractProductTypeQuery.FieldName.ownerID.equals(changedPropName))
//			{
//				AnchorID ownerID = (AnchorID) changedField.getNewValue();
//				if (ownerID == null) {
//					ownerComp.clear();
//				}
//				else {
//					final LegalEntity legalEntity = LegalEntityDAO.sharedInstance().getLegalEntity(
//							ownerID,
//							new String[] {LegalEntity.FETCH_GROUP_PERSON, FetchPlan.DEFAULT},
//							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor()
//						);
//					ownerComp.setText(legalEntity.getPerson().getDisplayName());
//				}
//			}
//			else if (getEnableFieldName(AbstractProductTypeQuery.FieldName.ownerID).equals(
//					changedPropName))
//			{
//				final Boolean active = (Boolean) changedField.getNewValue();
//				ownerComp.setActive(active);
//				setSearchSectionActive(ownerComp.getActiveButton(), active);
//			}
//			else if (AbstractProductTypeQuery.FieldName.productTypeGroupID.equals(changedPropName))
//			{
//				ProductTypeGroupID productTypeGroupID = (ProductTypeGroupID) changedField.getNewValue();
//				if (productTypeGroupID == null) {
//					productTypeGroupComp.clear();
//				}
//				else {
//					ProductTypeGroup productTypeGroup = ProductTypeGroupDAO.sharedInstance().getProductTypeGroup(
//							productTypeGroupID, new String[] {ProductTypeGroup.FETCH_GROUP_NAME},
//							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
//					productTypeGroupComp.setText(productTypeGroup.getName().getText());
//				}
//			}
//			else if (getEnableFieldName(AbstractProductTypeQuery.FieldName.productTypeGroupID).equals(changedPropName))
//			{
//				final Boolean active = (Boolean) changedField.getNewValue();
//				productTypeGroupComp.setActive(active);
//				setSearchSectionActive(productTypeGroupComp.getActiveButton(), active);
//			}
//		}
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite#getQueryClass()
	 */
	@Override
	public Class<Q> getQueryClass() {
		return queryClass;
	}
}
