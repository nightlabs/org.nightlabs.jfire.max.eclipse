package org.nightlabs.jfire.trade.ui.overview;

import java.util.Calendar;
import java.util.Date;

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
import org.nightlabs.jdo.query.QueryEvent;
import org.nightlabs.jdo.query.QueryProvider;
import org.nightlabs.jdo.query.AbstractSearchQuery.FieldChangeCarrier;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.base.ui.security.UserSearchDialog;
import org.nightlabs.jfire.security.User;
import org.nightlabs.jfire.security.dao.UserDAO;
import org.nightlabs.jfire.security.id.UserID;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.dao.LegalEntityDAO;
import org.nightlabs.jfire.trade.query.AbstractArticleContainerQuery;
import org.nightlabs.jfire.trade.ui.legalentity.edit.LegalEntitySearchCreateWizard;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.l10n.DateFormatter;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.util.Util;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * 
 */
public abstract class AbstractArticleContainerFilterComposite<R extends ArticleContainer, Q extends AbstractArticleContainerQuery<R>>
	extends AbstractQueryFilterComposite<R, Q>
{
	private DateTimeEdit createDTMin = null;
	private DateTimeEdit createDTMax = null;
	private Text userText = null;
	private Button userBrowseButton = null;
	private Text vendorText = null;
	private Button vendorBrowseButton = null;
	private Text customerText = null;
	private Button customerBrowseButton = null;
	
	private Button userActiveButton = null;
	private Button customerActiveButton = null;
	private Button vendorActiveButton = null;
	
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
		QueryProvider<R, ? super Q> queryProvider)
	{
		super(parent, style, layoutMode, layoutDataMode, queryProvider);
		createComposite(this);
	}

	public AbstractArticleContainerFilterComposite(Composite parent, int style,
		QueryProvider<R, ? super Q> queryProvider)
	{
		super(parent, style, queryProvider);
		createComposite(this);
	}

	@Override
	protected void createComposite(Composite parent)
	{
		GridLayout layout = new GridLayout();
		XComposite.getLayout(LayoutMode.TIGHT_WRAPPER, layout);
		parent.setLayout(layout);
		
		Group createDTGroup = new Group(parent, SWT.NONE);
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
				if (isUpdatingUI())
					return;
				
				setUIChangedQuery(true);
				getQuery().setCreateDTMin(createDTMin.getDate());
				setUIChangedQuery(false);
			}
		});
		createDTMin.addActiveChangeListener(new SelectionAdapter() 
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				final boolean active = ((Button)e.getSource()).getSelection();
				setSearchSectionActive(active);
				
				if (isUpdatingUI())
					return;
				
				setUIChangedQuery(true);
				if (active)
				{
					getQuery().setCreateDTMin(createDTMin.getDate());					
				}
				else
				{
					getQuery().setCreateDTMin(null);
				}
				setUIChangedQuery(false);
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
				if (isUpdatingUI())
					return;
				
				setUIChangedQuery(true);
				getQuery().setCreateDTMax(createDTMax.getDate());
				setUIChangedQuery(false);
			}
		});
		createDTMax.addActiveChangeListener(new SelectionAdapter() 
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				final boolean active = ((Button)e.getSource()).getSelection();
				setSearchSectionActive(active);
				
				if (isUpdatingUI())
					return;
				
				setUIChangedQuery(true);
				if (active)
				{
					getQuery().setCreateDTMax(createDTMax.getDate());					
				}
				else
				{
					getQuery().setCreateDTMax(null);
				}
				setUIChangedQuery(false);
			}
		});
		createDTGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Composite wrapper = new XComposite(parent, SWT.NONE);
		GridLayout wrapperLayout = new GridLayout(3, true);
		wrapperLayout = XComposite.getLayout(LayoutMode.TOP_BOTTOM_WRAPPER, wrapperLayout);
		wrapper.setLayout(wrapperLayout);
		wrapper.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		final Group userGroup = new Group(wrapper, SWT.NONE);
		userGroup.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.ArticleContainerFilterComposite.userGroup.text")); //$NON-NLS-1$
		userGroup.setLayout(new GridLayout(2, false));
		userGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		userActiveButton = new Button(userGroup, SWT.CHECK);
		userActiveButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.ArticleContainerFilterComposite.userActiveButton.text")); //$NON-NLS-1$
		GridData userLabelData = new GridData(GridData.FILL_HORIZONTAL);
		userLabelData.horizontalSpan = 2;
		userActiveButton.setLayoutData(userLabelData);
		userActiveButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				final boolean active = ((Button)e.getSource()).getSelection();
				userBrowseButton.setEnabled(active);
				userText.setEnabled(active);
				setSearchSectionActive(active);
				
				if (isUpdatingUI())
					return;

				setUIChangedQuery(true);
				if (active)
				{
					getQuery().setCreateUserID(selectedUserID);
				}
				else
				{
					getQuery().setCreateUserID(null);
				}
				setUIChangedQuery(false);
			}
		});
		userText = new Text(userGroup, SWT.BORDER);
		userText.setEnabled(false);
		userText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		userText.addSelectionListener(userSelectionListener);
		userBrowseButton = new Button(userGroup, SWT.NONE);
		userBrowseButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.ArticleContainerFilterComposite.userBrowseButton.text")); //$NON-NLS-1$
		userBrowseButton.addSelectionListener(userSelectionListener);
		userBrowseButton.setEnabled(false);
		userActiveButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				final boolean active = ((Button)e.getSource()).getSelection(); 
				userText.setEnabled(active);
				userBrowseButton.setEnabled(active);
				setSearchSectionActive(active);
				
				if (isUpdatingUI())
					return;
				
				setUIChangedQuery(true);
				getQuery().setCreateUserID(selectedUserID);
				setUIChangedQuery(false);
			}
		});
		
		final Group vendorGroup = new Group(wrapper, SWT.NONE);
		vendorGroup.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.ArticleContainerFilterComposite.vendorGroup.text")); //$NON-NLS-1$
		vendorGroup.setLayout(new GridLayout(2, false));
		vendorGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		vendorActiveButton = new Button(vendorGroup, SWT.CHECK);
		vendorActiveButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.ArticleContainerFilterComposite.vendorActiveButton.text")); //$NON-NLS-1$
		GridData vendorLabelData = new GridData(GridData.FILL_HORIZONTAL);
		vendorLabelData.horizontalSpan = 2;
		vendorActiveButton.setLayoutData(vendorLabelData);
		vendorActiveButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				final boolean active = ((Button)e.getSource()).getSelection();
				vendorText.setEnabled(active);
				vendorBrowseButton.setEnabled(active);
				setSearchSectionActive(active);
				
				if (isUpdatingUI())
					return;
				
				setUIChangedQuery(true);
				if (active)
				{
					getQuery().setVendorID(selectedVendorID);
				}
				else
				{
					getQuery().setVendorID(null);
				}
				setUIChangedQuery(false);
			}
		});
		vendorText = new Text(vendorGroup, SWT.BORDER);
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
		customerText = new Text(customerGroup, SWT.BORDER);
		customerText.setEnabled(false);
		customerText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		customerText.addSelectionListener(customerSelectionListener);
		customerBrowseButton = new Button(customerGroup, SWT.NONE);
		customerBrowseButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.ArticleContainerFilterComposite.customerBrowseButton.text")); //$NON-NLS-1$
		customerBrowseButton.addSelectionListener(customerSelectionListener);
		customerBrowseButton.setEnabled(false);
		customerActiveButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				final boolean active = ((Button)e.getSource()).getSelection();
				customerText.setEnabled(active);
				customerBrowseButton.setEnabled(active);
				setSearchSectionActive(active);
				
				if (isUpdatingUI())
					return;
				
				setUIChangedQuery(true);
				if (active)
				{
					getQuery().setCustomerID(selectedCustomerID);
				}
				else
				{
					getQuery().setCustomerID(null);
				}
				setUIChangedQuery(false);
			}
		});
		
		pack(true);
	}

	private UserID selectedUserID = null;
	private SelectionListener userSelectionListener = new SelectionAdapter()
	{
		@Override
		public void widgetSelected(SelectionEvent e)
		{
			UserSearchDialog dialog = new UserSearchDialog(getShell(), userText.getText());
			int returnCode = dialog.open();
			if (returnCode == Window.OK) {
				User selectedUser = dialog.getSelectedUser();
				selectedUserID = (UserID) JDOHelper.getObjectId(selectedUser);
				setUIChangedQuery(true);
				getQuery().setCreateUserID(selectedUserID);
				setUIChangedQuery(false);
				if (selectedUser != null)
					userText.setText(selectedUser.getName());
			}
		}
	};
	
	private AnchorID selectedVendorID = null;
	private SelectionListener vendorSelectionListener = new SelectionAdapter()
	{
		@Override
		public void widgetSelected(SelectionEvent e)
		{
			LegalEntity _legalEntity = LegalEntitySearchCreateWizard.open(vendorText.getText(), false);
			if (_legalEntity != null) {
				selectedVendorID = (AnchorID) JDOHelper.getObjectId(_legalEntity);
				setUIChangedQuery(true);
				getQuery().setVendorID(selectedVendorID);
				setUIChangedQuery(false);
				LegalEntity legalEntity = LegalEntityDAO.sharedInstance().getLegalEntity(selectedVendorID,
						new String[] {LegalEntity.FETCH_GROUP_PERSON, FetchPlan.DEFAULT},
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						new NullProgressMonitor()
				);
				vendorText.setText(legalEntity.getPerson().getDisplayName());
			}
		}
	};
	
	private AnchorID selectedCustomerID = null;
	private SelectionListener customerSelectionListener = new SelectionAdapter()
	{
		@Override
		public void widgetSelected(SelectionEvent e)
		{
			LegalEntity _legalEntity = LegalEntitySearchCreateWizard.open(customerText.getText(), false);
			if (_legalEntity != null) {
				selectedCustomerID = (AnchorID) JDOHelper.getObjectId(_legalEntity);
				setUIChangedQuery(true);
				getQuery().setCustomerID(selectedCustomerID);
				setUIChangedQuery(false);
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
	
	@Override
	protected void resetSearchQueryValues(Q query)
	{
		query.setCreateDTMin(createDTMin.getDate());
		query.setCreateDTMax(createDTMax.getDate());
		query.setCreateUserID(selectedUserID);
		query.setVendorID(selectedVendorID);
		query.setCustomerID(selectedCustomerID);
	}

	@Override
	protected void unsetSearchQueryValues(Q query)
	{
		query.setCreateDTMin(null);
		query.setCreateDTMax(null);
		query.setCreateUserID(null);
		query.setVendorID(null);
		query.setCustomerID(null);
	}

	@Override
	protected void doUpdateUI(QueryEvent event)
	{
		if (event.getChangedQuery() == null)
		{
			createDTMin.setTimestamp(Calendar.getInstance().getTimeInMillis());
			createDTMin.setActive(false);
			createDTMax.setTimestamp(Calendar.getInstance().getTimeInMillis());
			createDTMax.setActive(false);
			selectedUserID = null;
			userText.setText("");
			selectedVendorID = null;
			vendorText.setText("");
			selectedCustomerID = null;
			customerText.setText("");
		}
		else
		{ // there is a new Query -> the changedFieldList is not null!
			for (FieldChangeCarrier changedField : event.getChangedFields())
			{
				if (AbstractArticleContainerQuery.PROPERTY_CREATE_DATE_MAX.equals(changedField.getPropertyName()))
				{
					if (changedField.getNewValue() == null)
					{
						createDTMax.setTimestamp(Calendar.getInstance().getTimeInMillis());
						createDTMax.setActive(false);
					}
					else
					{
						createDTMax.setDate((Date) changedField.getNewValue());
						createDTMax.setActive(true);
					}
				}
				
				if (AbstractArticleContainerQuery.PROPERTY_CREATE_DATE_MIN.equals(changedField.getPropertyName()))
				{
					if (changedField.getNewValue() == null)
					{
						createDTMin.setTimestamp(Calendar.getInstance().getTimeInMillis());
						createDTMin.setActive(false);
					}
					else
					{
						createDTMin.setDate((Date) changedField.getNewValue());
						createDTMin.setActive(true);
					}
				}
				
				if (AbstractArticleContainerQuery.PROPERTY_CREATE_USER_ID.equals(changedField.getPropertyName()))
				{
					UserID tmpUserID = (UserID) changedField.getNewValue();
					if (! Util.equals(selectedUserID, tmpUserID))
					{
						selectedUserID = tmpUserID;
						if (selectedUserID == null)
						{
							userText.setText("");
						}
						else
						{
							final User selectedUser = UserDAO.sharedInstance().getUser(
								selectedUserID, new String[] { FetchPlan.DEFAULT }, 
								NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor()
								);
							
							userText.setText(selectedUser.getName());							
						}
						userActiveButton.setSelection(selectedUserID != null);
					}
				}
				
				if (AbstractArticleContainerQuery.PROPERTY_CUSTOMER_ID.equals(changedField.getPropertyName()))
				{
					AnchorID tmpCustomerID = (AnchorID) changedField.getNewValue();
					if (! Util.equals(selectedCustomerID, tmpCustomerID))
					{
						selectedCustomerID = tmpCustomerID;
						if (tmpCustomerID == null)
						{
							customerText.setText("");
						}
						else
						{
							final LegalEntity customer = LegalEntityDAO.sharedInstance().getLegalEntity(
								selectedVendorID, new String[] {LegalEntity.FETCH_GROUP_PERSON, FetchPlan.DEFAULT}, 
								NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor()
								);

							customerText.setText(customer.getPerson().getDisplayName());
						}
						customerActiveButton.setSelection(selectedCustomerID != null);
					}
				}
				
				if (AbstractArticleContainerQuery.PROPERTY_VENDOR_ID.equals(changedField.getPropertyName()))
				{
					AnchorID tmpVendorID = (AnchorID) changedField.getNewValue();
					if (! Util.equals(selectedVendorID, tmpVendorID))
					{
						selectedVendorID = tmpVendorID;
						if (tmpVendorID == null)
						{
							vendorText.setText("");
						}
						else
						{
							final LegalEntity vendor = LegalEntityDAO.sharedInstance().getLegalEntity(
								selectedVendorID, new String[] {LegalEntity.FETCH_GROUP_PERSON, FetchPlan.DEFAULT}, 
								NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor()
								);
							
							vendorText.setText(vendor.getPerson().getDisplayName());							
						}
						vendorActiveButton.setSelection(tmpVendorID != null);
					}
				}
				
			} // for (FieldChangeCarrier changedField : event.getChangedFields())
		} // (event.getChangedQuery() != null)
	}
	
}
