package org.nightlabs.jfire.trade.ui.overview;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.DateTimeEdit;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.AbstractSearchQuery;
import org.nightlabs.jdo.query.QueryEvent;
import org.nightlabs.jdo.query.QueryProvider;
import org.nightlabs.jdo.query.AbstractSearchQuery.FieldChangeCarrier;
import org.nightlabs.jfire.base.ui.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.base.ui.security.UserSearchDialog;
import org.nightlabs.jfire.security.User;
import org.nightlabs.jfire.security.dao.UserDAO;
import org.nightlabs.jfire.security.id.UserID;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.dao.LegalEntityDAO;
import org.nightlabs.jfire.trade.query.AbstractArticleContainerQuery;
import org.nightlabs.jfire.trade.ui.legalentity.edit.LegalEntitySearchCreateWizard;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.l10n.DateFormatter;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public abstract class AbstractArticleContainerFilterComposite<Q extends AbstractArticleContainerQuery>
	extends AbstractQueryFilterComposite<Q>
{
	private static final String ARTICLE_CONTAINER_GROUP_ID = "ArticleContainerFilterComposite"; //$NON-NLS-1$

	private DateTimeEdit createDTMin;
	private DateTimeEdit createDTMax;

	private Button userActiveButton;
	private Text userText;
	private Button userBrowseButton;

	private Button vendorActiveButton;
	private Text vendorText;
	private Button vendorBrowseButton;

	private Button customerActiveButton;
	private Text customerText;
	private Button customerBrowseButton;

	/**
	 * @param parent
	 *          The parent to instantiate this filter into.
	 * @param style
	 *          The style to apply.
	 * @param layoutMode
	 *          The layout mode to use. See {@link XComposite.LayoutMode}.
	 * @param layoutDataMode
	 *          The layout data mode to use. See {@link XComposite.LayoutDataMode}.
	 * @param queryProvider
	 *          The queryProvider to use. It may be <code>null</code>, but the caller has to
	 *          ensure, that it is set before {@link #getQuery()} is called!
	 */
	public AbstractArticleContainerFilterComposite(Composite parent, int style,
		LayoutMode layoutMode, LayoutDataMode layoutDataMode,
		QueryProvider<? super Q> queryProvider)
	{
		super(parent, style, layoutMode, layoutDataMode, queryProvider);
		createComposite();
	}

	/**
	 * @param parent
	 *          The parent to instantiate this filter into.
	 * @param style
	 *          The style to apply.
	 * @param queryProvider
	 *          The queryProvider to use. It may be <code>null</code>, but the caller has to
	 *          ensure, that it is set before {@link #getQuery()} is called!
	 */
	public AbstractArticleContainerFilterComposite(Composite parent, int style,
		QueryProvider<? super Q> queryProvider)
	{
		super(parent, style, queryProvider);
		createComposite();
	}

	@Override
	protected void createComposite()
	{
		Group createDTGroup = new Group(this, SWT.NONE);
		createDTGroup.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.ArticleContainerFilterComposite.createDateGroup.text")); //$NON-NLS-1$
		createDTGroup.setLayout(new GridLayout(2, true));
		long dateTimeEditStyle = DateFormatter.FLAGS_DATE_SHORT_TIME_HMS_WEEKDAY + DateTimeEdit.FLAGS_SHOW_ACTIVE_CHECK_BOX;
		createDTMin = new DateTimeEdit(createDTGroup, dateTimeEditStyle, Messages.getString("org.nightlabs.jfire.trade.ui.overview.ArticleContainerFilterComposite.createDateMin.caption")); //$NON-NLS-1$
		createDTMin.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		createDTMin.setActive(false);
		createDTMin.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(ModifyEvent e)
			{
				getQuery().setCreateDTMin(createDTMin.getDate());
			}
		});
		createDTMin.addActiveChangeListener(new ButtonSelectionListener()
		{
			@Override
			protected void handleSelection(boolean active)
			{
				getQuery().setFieldEnabled(AbstractArticleContainerQuery.FieldName.createDTMin, active);
			}
		});
		createDTMax = new DateTimeEdit(createDTGroup, dateTimeEditStyle, Messages.getString("org.nightlabs.jfire.trade.ui.overview.ArticleContainerFilterComposite.createDateMax.caption")); //$NON-NLS-1$
		createDTMax.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		createDTMax.setActive(false);
		createDTMax.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(ModifyEvent e)
			{
				getQuery().setCreateDTMax(createDTMax.getDate());
			}
		});
		createDTMax.addActiveChangeListener(new ButtonSelectionListener()
		{
			@Override
			protected void handleSelection(boolean active)
			{
				getQuery().setFieldEnabled(AbstractArticleContainerQuery.FieldName.createDTMax, active);
			}
		});
		createDTGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Composite wrapper = new XComposite(this, SWT.NONE, LayoutMode.TIGHT_WRAPPER,
			LayoutDataMode.GRID_DATA_HORIZONTAL, 3);

		final Group userGroup = new Group(wrapper, SWT.NONE);
		userGroup.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.ArticleContainerFilterComposite.userGroup.text")); //$NON-NLS-1$
		userGroup.setLayout(new GridLayout(2, false));
		userGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		userActiveButton = new Button(userGroup, SWT.CHECK);
		userActiveButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.ArticleContainerFilterComposite.userActiveButton.text")); //$NON-NLS-1$
		GridData userLabelData = new GridData(GridData.FILL_HORIZONTAL);
		userLabelData.horizontalSpan = 2;
		userActiveButton.setLayoutData(userLabelData);
		userActiveButton.addSelectionListener(new ButtonSelectionListener()
		{
			@Override
			protected void handleSelection(boolean active)
			{
				getQuery().setFieldEnabled(AbstractArticleContainerQuery.FieldName.createUserID, active);
			}
		});
		userText = new Text(userGroup, getBorderStyle());
		userText.setEnabled(false);
		userText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		userText.addSelectionListener(userSelectionListener);
		userBrowseButton = new Button(userGroup, SWT.NONE);
		userBrowseButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.ArticleContainerFilterComposite.userBrowseButton.text")); //$NON-NLS-1$
		userBrowseButton.addSelectionListener(userSelectionListener);
		userBrowseButton.setEnabled(false);

		final Group vendorGroup = new Group(wrapper, SWT.NONE);
		vendorGroup.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.ArticleContainerFilterComposite.vendorGroup.text")); //$NON-NLS-1$
		vendorGroup.setLayout(new GridLayout(2, false));
		vendorGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		vendorActiveButton = new Button(vendorGroup, SWT.CHECK);
		vendorActiveButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.ArticleContainerFilterComposite.vendorActiveButton.text")); //$NON-NLS-1$
		GridData vendorLabelData = new GridData(GridData.FILL_HORIZONTAL);
		vendorLabelData.horizontalSpan = 2;
		vendorActiveButton.setLayoutData(vendorLabelData);
		vendorActiveButton.addSelectionListener(new ButtonSelectionListener()
		{
			@Override
			protected void handleSelection(boolean active)
			{
				getQuery().setFieldEnabled(AbstractArticleContainerQuery.FieldName.vendorID, active);
			}
		});
		vendorText = new Text(vendorGroup, getBorderStyle());
		vendorText.setEnabled(false);
		vendorText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		vendorText.addSelectionListener(vendorSelectionListener);
		vendorBrowseButton = new Button(vendorGroup, SWT.NONE);
		vendorBrowseButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.ArticleContainerFilterComposite.vendorBrowseButton.text")); //$NON-NLS-1$
		vendorBrowseButton.addSelectionListener(vendorSelectionListener);
		vendorBrowseButton.setEnabled(false);

		final Group customerGroup = new Group(wrapper, SWT.NONE);
		customerGroup.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.ArticleContainerFilterComposite.customerGroup.text")); //$NON-NLS-1$
		customerGroup.setLayout(new GridLayout(2, false));
		customerGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		customerActiveButton = new Button(customerGroup, SWT.CHECK);
		customerActiveButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.ArticleContainerFilterComposite.customerActiveButton.text")); //$NON-NLS-1$
		GridData customerLabelData = new GridData(GridData.FILL_HORIZONTAL);
		customerLabelData.horizontalSpan = 2;
		customerActiveButton.setLayoutData(customerLabelData);
		customerText = new Text(customerGroup, getBorderStyle());
		customerText.setEnabled(false);
		customerText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		customerText.addSelectionListener(customerSelectionListener);
		customerBrowseButton = new Button(customerGroup, SWT.NONE);
		customerBrowseButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.ArticleContainerFilterComposite.customerBrowseButton.text")); //$NON-NLS-1$
		customerBrowseButton.addSelectionListener(customerSelectionListener);
		customerBrowseButton.setEnabled(false);
		customerActiveButton.addSelectionListener(new ButtonSelectionListener()
		{
			@Override
			protected void handleSelection(boolean active)
			{
				getQuery().setFieldEnabled(AbstractArticleContainerQuery.FieldName.customerID, active);
			}
		});
	}

	private SelectionListener userSelectionListener = new SelectionAdapter()
	{
		@Override
		public void widgetSelected(SelectionEvent e)
		{
			UserSearchDialog dialog = new UserSearchDialog(getShell(), userText.getText());
			int returnCode = dialog.open();
			if (returnCode == Window.OK) {
				User selectedUser = dialog.getSelectedUser();
				final UserID selectedUserID = (UserID) JDOHelper.getObjectId(selectedUser);
				getQuery().setCreateUserID(selectedUserID);
				if (selectedUser != null)
					userText.setText(selectedUser.getName());
			}
		}
	};

	private SelectionListener vendorSelectionListener = new SelectionAdapter()
	{
		@Override
		public void widgetSelected(SelectionEvent e)
		{
			LegalEntity _legalEntity = LegalEntitySearchCreateWizard.open(vendorText.getText(), false);
			if (_legalEntity != null) {
				final AnchorID selectedVendorID = (AnchorID) JDOHelper.getObjectId(_legalEntity);
				getQuery().setVendorID(selectedVendorID);
				LegalEntity legalEntity = LegalEntityDAO.sharedInstance().getLegalEntity(selectedVendorID,
						new String[] {LegalEntity.FETCH_GROUP_PERSON, FetchPlan.DEFAULT},
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						new NullProgressMonitor()
				);
				vendorText.setText(legalEntity.getPerson().getDisplayName());
			}
		}
	};

	private SelectionListener customerSelectionListener = new SelectionAdapter()
	{
		@Override
		public void widgetSelected(SelectionEvent e)
		{
			LegalEntity _legalEntity = LegalEntitySearchCreateWizard.open(customerText.getText(), false);
			if (_legalEntity != null) {
				final AnchorID selectedCustomerID = (AnchorID) JDOHelper.getObjectId(_legalEntity);
				getQuery().setCustomerID(selectedCustomerID);
				LegalEntity legalEntity = LegalEntityDAO.sharedInstance().getLegalEntity(selectedCustomerID,
						new String[] {LegalEntity.FETCH_GROUP_PERSON, FetchPlan.DEFAULT},
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						new NullProgressMonitor()
				);
				if (legalEntity.getPerson() != null && legalEntity.getPerson().getDisplayName() != null)
					customerText.setText(legalEntity.getPerson().getDisplayName());
			}
		}
	};

	/**
	 * The names of the fields used in this filter.
	 */
	private static Set<String> fieldNames;
	static
	{
		fieldNames = new HashSet<String>(5);
		fieldNames.add(AbstractArticleContainerQuery.FieldName.createDTMax);
		fieldNames.add(AbstractArticleContainerQuery.FieldName.createDTMin);
		fieldNames.add(AbstractArticleContainerQuery.FieldName.createUserID);
		fieldNames.add(AbstractArticleContainerQuery.FieldName.customerID);
		fieldNames.add(AbstractArticleContainerQuery.FieldName.vendorID);
	}

	@Override
	protected Set<String> getFieldNames()
	{
		return fieldNames;
	}

	@Override
	protected String getGroupID()
	{
		return ARTICLE_CONTAINER_GROUP_ID;
	}

	@Override
	protected void updateUI(QueryEvent event, List<FieldChangeCarrier> changedFields)
	{
		for (FieldChangeCarrier changedField : changedFields)
		{
			final String propertyName = changedField.getPropertyName();
			if (AbstractArticleContainerQuery.FieldName.createDTMax.equals(propertyName))
			{
				Date maxDate = (Date) changedField.getNewValue();
				createDTMax.setDate(maxDate);
			}
			else if (getEnableFieldName(AbstractArticleContainerQuery.FieldName.createDTMax).equals(propertyName))
			{
				final boolean newActiveState = (Boolean) changedField.getNewValue();
				if (createDTMax.isActive() != newActiveState)
				{
					createDTMax.setActive(newActiveState);
					setSearchSectionActive(newActiveState);
				}
			}
			else if (AbstractArticleContainerQuery.FieldName.createDTMin.equals(propertyName))
			{
				Date minDate = (Date) changedField.getNewValue();
				createDTMin.setDate(minDate);
			}
			else if (AbstractSearchQuery.getEnabledFieldName(
					AbstractArticleContainerQuery.FieldName.createDTMin).equals(propertyName))
			{
				final boolean active = (Boolean) changedField.getNewValue();
				if (createDTMin.isActive() != active)
				{
					createDTMin.setActive(active);
					setSearchSectionActive(active);
				}
			}
			else if (AbstractArticleContainerQuery.FieldName.createUserID.equals(propertyName))
			{
				UserID userID = (UserID) changedField.getNewValue();
				if (userID == null)
				{
					userText.setText(""); //$NON-NLS-1$
				}
				else
				{
					final User selectedUser = UserDAO.sharedInstance().getUser(
						userID, new String[] { FetchPlan.DEFAULT },
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor()
					);

					if (selectedUser != null) {
						userText.setText(selectedUser.getName());
					}
				}
			}
			else if (AbstractSearchQuery.getEnabledFieldName(
					AbstractArticleContainerQuery.FieldName.createUserID).equals(propertyName))
			{
				final boolean active = (Boolean) changedField.getNewValue();
				userText.setEnabled(active);
				userBrowseButton.setEnabled(active);
				setSearchSectionActive(userActiveButton, active);
			}
			else if (AbstractArticleContainerQuery.FieldName.customerID.equals(propertyName))
			{
				AnchorID customerID = (AnchorID) changedField.getNewValue();
				if (customerID == null)
				{
					customerText.setText(""); //$NON-NLS-1$
				}
				else
				{
					final LegalEntity customer = LegalEntityDAO.sharedInstance().getLegalEntity(
						customerID, new String[] {LegalEntity.FETCH_GROUP_PERSON, FetchPlan.DEFAULT},
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor()
					);

					customerText.setText(customer.getPerson().getDisplayName());
				}
			}
			else if (AbstractSearchQuery.getEnabledFieldName(
					AbstractArticleContainerQuery.FieldName.customerID).equals(propertyName))
			{
				final boolean active = (Boolean) changedField.getNewValue();
				customerText.setEnabled(active);
				customerBrowseButton.setEnabled(active);
				setSearchSectionActive(customerActiveButton, active);
			}
			else if (AbstractArticleContainerQuery.FieldName.vendorID.equals(propertyName))
			{
				AnchorID vendorID = (AnchorID) changedField.getNewValue();
				if (vendorID == null)
				{
					vendorText.setText(""); //$NON-NLS-1$
				}
				else
				{
					final LegalEntity vendor = LegalEntityDAO.sharedInstance().getLegalEntity(
						vendorID, new String[] {LegalEntity.FETCH_GROUP_PERSON, FetchPlan.DEFAULT},
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor()
					);

					vendorText.setText(vendor.getPerson().getDisplayName());
				}
			}
			else if (AbstractSearchQuery.getEnabledFieldName(
					AbstractArticleContainerQuery.FieldName.vendorID).equals(propertyName))
			{
				final boolean active = (Boolean) changedField.getNewValue();
				vendorText.setEnabled(active);
				vendorBrowseButton.setEnabled(active);
				setSearchSectionActive(vendorActiveButton, active);
			}
		} // for (FieldChangeCarrier changedField : event.getChangedFields())
	}

}
