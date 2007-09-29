package org.nightlabs.jfire.trade.ui.overview;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
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
import org.nightlabs.jdo.query.JDOQuery;
import org.nightlabs.jdo.ui.JDOQueryComposite;
import org.nightlabs.jfire.base.ui.security.UserSearchDialog;
import org.nightlabs.jfire.security.User;
import org.nightlabs.jfire.security.id.UserID;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.dao.LegalEntityDAO;
import org.nightlabs.jfire.trade.query.ArticleContainerQuery;
import org.nightlabs.jfire.trade.ui.legalentity.edit.LegalEntitySearchCreateWizard;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.l10n.DateFormatter;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ArticleContainerFilterComposite 
extends JDOQueryComposite 
{
	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 */
	public ArticleContainerFilterComposite(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode) {
		super(parent, style, layoutMode, layoutDataMode);
		createComposite(this);
	}

	/**
	 * @param parent
	 * @param style
	 */
	public ArticleContainerFilterComposite(Composite parent, int style) {
		super(parent, style);
		createComposite(this);
	}
	 
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
	
	protected void createComposite(Composite parent) 
	{
//		parent.setLayout(new RowLayout());
//		GridLayout layout = new GridLayout(4, false);
		GridLayout layout = new GridLayout();
		XComposite.getLayout(LayoutMode.TIGHT_WRAPPER, layout);
		parent.setLayout(layout);
		
		Group createDTGroup = new Group(parent, SWT.NONE);
		createDTGroup.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.ArticleContainerFilterComposite.createDateGroup.text")); //$NON-NLS-1$
		createDTGroup.setLayout(new GridLayout(2, true));
		long dateTimeEditStyle = DateFormatter.FLAGS_DATE_SHORT_TIME_HMS_WEEKDAY + DateTimeEdit.FLAGS_SHOW_ACTIVE_CHECK_BOX;
		createDTMin = new DateTimeEdit(createDTGroup, dateTimeEditStyle, Messages.getString("org.nightlabs.jfire.trade.ui.overview.ArticleContainerFilterComposite.createDateMin.caption")); //$NON-NLS-1$
		createDTMin.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		createDTMax = new DateTimeEdit(createDTGroup, dateTimeEditStyle, Messages.getString("org.nightlabs.jfire.trade.ui.overview.ArticleContainerFilterComposite.createDateMax.caption")); //$NON-NLS-1$
		createDTMax.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
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
		userText = new Text(userGroup, SWT.BORDER);
		userText.setEnabled(false);
		userText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		userText.addSelectionListener(userSelectionListener);
		userBrowseButton = new Button(userGroup, SWT.NONE);
		userBrowseButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.ArticleContainerFilterComposite.userBrowseButton.text")); //$NON-NLS-1$
		userBrowseButton.addSelectionListener(userSelectionListener);
		userBrowseButton.setEnabled(false);
		userActiveButton.addSelectionListener(new SelectionListener(){		
			public void widgetSelected(SelectionEvent e) {
				userText.setEnabled(((Button)e.getSource()).getSelection());
				userBrowseButton.setEnabled(((Button)e.getSource()).getSelection());
			}		
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
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
		vendorText = new Text(vendorGroup, SWT.BORDER);
		vendorText.setEnabled(false);
		vendorText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));		
		vendorText.addSelectionListener(vendorSelectionListener);
		vendorBrowseButton = new Button(vendorGroup, SWT.NONE);
		vendorBrowseButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.ArticleContainerFilterComposite.vendorBrowseButton.text")); //$NON-NLS-1$
		vendorBrowseButton.addSelectionListener(vendorSelectionListener);
		vendorBrowseButton.setEnabled(false);
		vendorActiveButton.addSelectionListener(new SelectionListener(){		
			public void widgetSelected(SelectionEvent e) {
				vendorText.setEnabled(((Button)e.getSource()).getSelection());
				vendorBrowseButton.setEnabled(((Button)e.getSource()).getSelection());
			}		
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}		
		});

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
		customerActiveButton.addSelectionListener(new SelectionListener(){		
			public void widgetSelected(SelectionEvent e) {
				customerText.setEnabled(((Button)e.getSource()).getSelection());				
				customerBrowseButton.setEnabled(((Button)e.getSource()).getSelection());
			}		
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}		
		});		
		
		pack(true);
	}
	
	private UserID selectedUserID = null;
	private SelectionListener userSelectionListener = new SelectionListener(){	
		public void widgetSelected(SelectionEvent e) {
			UserSearchDialog dialog = new UserSearchDialog(getShell(), userText.getText());
			int returnCode = dialog.open();
			if (returnCode == Dialog.OK) {
				User selectedUser = dialog.getSelectedUser();
				selectedUserID = (UserID) JDOHelper.getObjectId(selectedUser);
				if (selectedUser != null)
					userText.setText(selectedUser.getName());
			}
		}	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
	};
	
	private AnchorID selectedVendorID = null;
	private SelectionListener vendorSelectionListener = new SelectionListener(){	
		public void widgetSelected(SelectionEvent e) {
			LegalEntity _legalEntity = LegalEntitySearchCreateWizard.open(vendorText.getText(), false);
			if (_legalEntity != null) {
				selectedVendorID = (AnchorID) JDOHelper.getObjectId(_legalEntity);
				LegalEntity legalEntity = LegalEntityDAO.sharedInstance().getLegalEntity(selectedVendorID, 
						new String[] {LegalEntity.FETCH_GROUP_PERSON, FetchPlan.DEFAULT}, 
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						new NullProgressMonitor()
				);
				vendorText.setText(legalEntity.getPerson().getDisplayName());
			}
		}	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
	};	
	
	private AnchorID selectedCustomerID = null;
	private SelectionListener customerSelectionListener = new SelectionListener(){	
		public void widgetSelected(SelectionEvent e) {
			LegalEntity _legalEntity = LegalEntitySearchCreateWizard.open(customerText.getText(), false);
			if (_legalEntity != null) {
				selectedVendorID = (AnchorID) JDOHelper.getObjectId(_legalEntity);
				LegalEntity legalEntity = LegalEntityDAO.sharedInstance().getLegalEntity(selectedCustomerID, 
						new String[] {LegalEntity.FETCH_GROUP_PERSON, FetchPlan.DEFAULT}, 
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						new NullProgressMonitor()
				);
				if (legalEntity.getPerson() != null && legalEntity.getPerson().getDisplayName() != null)
					customerText.setText(legalEntity.getPerson().getDisplayName());				
			}
		}	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
	};	
	
	@Override
	public JDOQuery getJDOQuery() 
	{
		articleContainerQuery = new ArticleContainerQuery(getArticleContainerClass());
		if (articleContainerQuery != null)
			prepareQuery(articleContainerQuery);
		return articleContainerQuery;
	}
	
	protected void prepareQuery(ArticleContainerQuery query) 
	{
		if (createDTMax.isActive())
			query.setCreateDTMax(createDTMax.getDate());
		
		if (createDTMin.isActive())
			query.setCreateDTMin(createDTMin.getDate());
	
		if (userActiveButton.getSelection() && selectedUserID != null)
			query.setCreateUserID(selectedUserID);
		
		if (vendorActiveButton.getSelection() && selectedVendorID != null)
			query.setVendorID(selectedVendorID);
		
		if (customerActiveButton.getSelection() && selectedCustomerID != null)
			query.setCustomerID(selectedCustomerID);		
	}
	
	private ArticleContainerQuery articleContainerQuery = null;
//	protected ArticleContainerQuery initArticleContainerQuery() {
//		return new ArticleContainerQuery(getArticleContainerClass());
//	}
		
	private Class articleContainerClass;
	public Class getArticleContainerClass() {
		return articleContainerClass;
	}
	public void setArticleContainerClass(Class articleContainerClass) {
		this.articleContainerClass = articleContainerClass;
		if (articleContainerQuery == null)
			articleContainerQuery = new ArticleContainerQuery(articleContainerClass);
	}
	
}
