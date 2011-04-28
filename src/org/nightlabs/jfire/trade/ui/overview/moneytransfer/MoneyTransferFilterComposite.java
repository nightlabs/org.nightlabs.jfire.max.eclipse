package org.nightlabs.jfire.trade.ui.overview.moneytransfer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.nightlabs.base.ui.composite.DateTimeControl;
import org.nightlabs.base.ui.composite.XComboComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.exceptionhandler.ExceptionHandlerRegistry;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.QueryEvent;
import org.nightlabs.jdo.query.QueryProvider;
import org.nightlabs.jdo.query.AbstractSearchQuery.FieldChangeCarrier;
import org.nightlabs.jfire.accounting.AccountType;
import org.nightlabs.jfire.accounting.dao.AccountTypeDAO;
import org.nightlabs.jfire.accounting.id.AccountTypeID;
import org.nightlabs.jfire.accounting.query.MoneyTransferQuery;
import org.nightlabs.jfire.base.ui.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.transfer.query.AbstractTransferQuery;
import org.nightlabs.l10n.IDateFormatter;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;

public class MoneyTransferFilterComposite
extends AbstractQueryFilterComposite<MoneyTransferQuery>
{
	private DateTimeControl fromTimeEdit;
	private DateTimeControl toTimeEdit;

	private Button fromAccountTypeActiveButton;
	private Button toAccountTypeActiveButton;
	
	private Button fromTimeActiveButton;
	private Button toTimeActiveButton;
	
	private XComboComposite<AccountType> fromAccountTypeCombo;
	private XComboComposite<AccountType> toAccountTypeCombo;
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
	public MoneyTransferFilterComposite(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode,
			QueryProvider<? super MoneyTransferQuery> queryProvider)
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
	public MoneyTransferFilterComposite(Composite parent, int style,
			QueryProvider<? super MoneyTransferQuery> queryProvider)
	{
		super(parent, style, queryProvider);
		createComposite();
	}

	@Override
	public Class<MoneyTransferQuery> getQueryClass() {
		return MoneyTransferQuery.class;
	}

	private LabelProvider accountTypeLabelProvider = new LabelProvider() {
		public String getText(Object element) {
			AccountType accountType = (AccountType)element;
			return accountType.getName().getText();
		};
	};
	
	@Override
	protected void createComposite()
	{
		/********Account Type Group********/
		Group accountTypeGroup = new Group(this, SWT.BORDER);
		accountTypeGroup.setText("Account-Type");
		
		GridLayout gridLayout = new GridLayout(2, true);
		accountTypeGroup.setLayout(gridLayout);
		accountTypeGroup.setLayoutData(new GridData(GridData.FILL_BOTH));

		fromAccountTypeActiveButton = new Button(accountTypeGroup, SWT.CHECK);
		fromAccountTypeActiveButton.setText("From");
		fromAccountTypeActiveButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getQuery().setFieldEnabled(MoneyTransferQuery.FieldName.fromAccountTypeID, fromAccountTypeActiveButton.getSelection());
			}
		});
		
		toAccountTypeActiveButton = new Button(accountTypeGroup, SWT.CHECK);
		toAccountTypeActiveButton.setText("To");
		toAccountTypeActiveButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getQuery().setFieldEnabled(MoneyTransferQuery.FieldName.toAccountTypeID, toAccountTypeActiveButton.getSelection());
			}
		});
		
		fromAccountTypeCombo = new XComboComposite<AccountType>(accountTypeGroup, SWT.NONE);
		fromAccountTypeCombo.setLabelProvider(accountTypeLabelProvider);
		fromAccountTypeCombo.setEnabled(false);
		fromAccountTypeCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent e) {
				getQuery().setFromAccountTypeID((AccountTypeID)JDOHelper.getObjectId(fromAccountTypeCombo.getSelectedElement()));
			}
		});
		
		toAccountTypeCombo = new XComboComposite<AccountType>(accountTypeGroup, SWT.NONE);
		toAccountTypeCombo.setLabelProvider(accountTypeLabelProvider);
		toAccountTypeCombo.setEnabled(false);
		toAccountTypeCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent e) {
				getQuery().setToAccountTypeID((AccountTypeID)JDOHelper.getObjectId(toAccountTypeCombo.getSelectedElement()));
			}
		});
		
		/**************Date Group******************/
		Group transferDateTimeGroup = new Group(this, SWT.BORDER);
		transferDateTimeGroup.setText("Transfer Date");
		
		gridLayout = new GridLayout(2, true);
		transferDateTimeGroup.setLayout(gridLayout);
		transferDateTimeGroup.setLayoutData(new GridData(GridData.FILL_BOTH));

		fromTimeActiveButton = new Button(transferDateTimeGroup, SWT.CHECK);
		fromTimeActiveButton.setText("From");
		fromTimeActiveButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getQuery().setFieldEnabled(AbstractTransferQuery.FieldName.timestampFromIncl, fromTimeActiveButton.getSelection());
			}
		});
		
		toTimeActiveButton = new Button(transferDateTimeGroup, SWT.CHECK);
		toTimeActiveButton.setText("To");
		toTimeActiveButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getQuery().setFieldEnabled(AbstractTransferQuery.FieldName.timestampToIncl, toTimeActiveButton.getSelection());
			}
		});
		
		fromTimeEdit = new DateTimeControl(
				transferDateTimeGroup,
				SWT.NONE,
				IDateFormatter.FLAGS_DATE_SHORT_TIME_HMS_WEEKDAY
				);
		fromTimeEdit.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, cal.getActualMinimum(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, cal.getActualMinimum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getActualMinimum(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, cal.getActualMinimum(Calendar.MILLISECOND));
		fromTimeEdit.setDate(cal.getTime());
		getQuery().setTimestampFromIncl(fromTimeEdit.getDate());
		fromTimeEdit.setEnabled(false);
		fromTimeEdit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getQuery().setTimestampFromIncl(fromTimeEdit.getDate());
			}
		});

		toTimeEdit = new DateTimeControl(
				transferDateTimeGroup,
				SWT.NONE,
				IDateFormatter.FLAGS_DATE_SHORT_TIME_HMS_WEEKDAY
				);
		toTimeEdit.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, cal.getActualMinimum(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, cal.getActualMinimum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getActualMinimum(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, cal.getActualMinimum(Calendar.MILLISECOND));
		toTimeEdit.setDate(cal.getTime());
		getQuery().setTimestampToIncl(toTimeEdit.getDate());
		toTimeEdit.setEnabled(false);
		toTimeEdit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getQuery().setTimestampToIncl(toTimeEdit.getDate());
			}
		});
		
		loadProperties();
	}

	@Override
	protected void updateUI(QueryEvent event, List<FieldChangeCarrier> changedFields)
	{
		for (FieldChangeCarrier changedField : event.getChangedFields())
		{
			if (MoneyTransferQuery.FieldName.fromAccountTypeID.equals(changedField.getPropertyName()))
			{
				final AccountTypeID newAccountTypeID = (AccountTypeID) changedField.getNewValue();
				if (newAccountTypeID == null) {
					fromAccountTypeActiveButton.setSelection(false);
					fromAccountTypeCombo.setEnabled(false);
				}
				else {
					AccountType newAccountType = AccountTypeDAO.sharedInstance().getAccountType(
							newAccountTypeID,
							FETCH_GROUPS_ACCOUNT_TYPE,
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor()
					);
					fromAccountTypeCombo.setSelection(newAccountType);
				}
			}
			else if (getEnableFieldName(MoneyTransferQuery.FieldName.fromAccountTypeID).equals(changedField.getPropertyName()))
			{
				final boolean active = (Boolean) changedField.getNewValue();
				fromAccountTypeCombo.setEnabled(active);
				setSearchSectionActive(active);
			}
			if (MoneyTransferQuery.FieldName.toAccountTypeID.equals(changedField.getPropertyName()))
			{
				final AccountTypeID newAccountTypeID = (AccountTypeID) changedField.getNewValue();
				if (newAccountTypeID == null) {
					toAccountTypeActiveButton.setSelection(false);
					toAccountTypeCombo.setEnabled(false);
				}
				else {
					AccountType newAccountType = AccountTypeDAO.sharedInstance().getAccountType(
							newAccountTypeID,
							FETCH_GROUPS_ACCOUNT_TYPE,
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor()
					);
					toAccountTypeCombo.setSelection(newAccountType);
				}
			}
			else if (getEnableFieldName(MoneyTransferQuery.FieldName.toAccountTypeID).equals(changedField.getPropertyName()))
			{
				final boolean active = (Boolean) changedField.getNewValue();
				toAccountTypeCombo.setEnabled(active);
				setSearchSectionActive(active);
			}
			if (AbstractTransferQuery.FieldName.timestampFromIncl.equals(changedField.getPropertyName()))
			{
				final Date newFromDate = (Date) changedField.getNewValue();
				if (newFromDate == null) {
					fromTimeActiveButton.setSelection(false);
					fromTimeEdit.setEnabled(false);
				}
				else {
					fromTimeEdit.setDate(newFromDate);
				}
			}
			else if (getEnableFieldName(AbstractTransferQuery.FieldName.timestampFromIncl).equals(changedField.getPropertyName()))
			{
				final boolean active = (Boolean) changedField.getNewValue();
				fromTimeEdit.setEnabled(active);
				setSearchSectionActive(active);
			}
			else if (AbstractTransferQuery.FieldName.timestampToIncl.equals(changedField.getPropertyName()))
			{
				final Date newToDate = (Date) changedField.getNewValue();
				if (newToDate == null) {
					toTimeActiveButton.setSelection(false);
					toTimeEdit.setEnabled(false);
				}
				else {
					toTimeEdit.setDate(newToDate);
				}
			}
			else if (getEnableFieldName(AbstractTransferQuery.FieldName.timestampToIncl).equals(changedField.getPropertyName()))
			{
				final boolean active = (Boolean) changedField.getNewValue();
				toTimeEdit.setEnabled(active);
				setSearchSectionActive(active);
			}
		} // for (FieldChangeCarrier changedField : event.getChangedFields())
	}

	private static final Set<String> fieldNames;
	static
	{
		fieldNames = new HashSet<String>(4);
		fieldNames.add(MoneyTransferQuery.FieldName.fromAccountTypeID);
		fieldNames.add(MoneyTransferQuery.FieldName.toAccountTypeID);
		fieldNames.add(AbstractTransferQuery.FieldName.timestampToIncl);
		fieldNames.add(AbstractTransferQuery.FieldName.timestampFromIncl);
		fieldNames.add(AbstractTransferQuery.FieldName.timestampToIncl);
	}

	@Override
	protected Set<String> getFieldNames()
	{
		return fieldNames;
	}

	/**
	 * Group ID for storing active states in the query.
	 */
	public static final String FILTER_GROUP_ID = "MoneyTransferFilterComposite"; //$NON-NLS-1$

	@Override
	protected String getGroupID()
	{
		return FILTER_GROUP_ID;
	}
	
	private static final String[] FETCH_GROUPS_ACCOUNT_TYPE = {
		AccountType.FETCH_GROUP_NAME,
	};
	
	private void loadProperties(){
		Job loadJob = new Job("Loading Data") {
			@Override
			protected IStatus run(final ProgressMonitor monitor) {
				try {
					try {
						final List<AccountType> accountTypeList = new ArrayList<AccountType>(AccountTypeDAO.sharedInstance().getAccountTypes(FETCH_GROUPS_ACCOUNT_TYPE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor));

						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								fromAccountTypeCombo.removeAll();
								fromAccountTypeCombo.addElements(accountTypeList);
								
								toAccountTypeCombo.removeAll();
								toAccountTypeCombo.addElements(accountTypeList);
							}
						});
					}catch (Exception e1) {
						ExceptionHandlerRegistry.asyncHandleException(e1);
						throw new RuntimeException(e1);
					}

					return Status.OK_STATUS;
				} finally {
				}
			}
		};

		loadJob.schedule();
	}
}