package org.nightlabs.jfire.trade.ui.overview.account;

import java.util.Iterator;
import java.util.List;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Account;
import org.nightlabs.jfire.accounting.AccountType;
import org.nightlabs.jfire.accounting.dao.AccountDAO;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleManager;
import org.nightlabs.jfire.jdo.notification.DirtyObjectID;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.ui.account.editor.AccountEditor;
import org.nightlabs.jfire.trade.ui.account.editor.AccountEditorInput;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.l10n.NumberFormatter;
import org.nightlabs.notification.NotificationAdapterCallerThread;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 */
public class AccountListComposite
extends AbstractTableComposite<Account>
{
	
	private boolean openAcountEditor;
	private TableViewerColumn tableViewerColumn;
	public static String[] FETCH_GROUPS_ACCOUNT = new String[] {
		FetchPlan.DEFAULT,
		Account.FETCH_GROUP_OWNER,
		Account.FETCH_GROUP_CURRENCY,
		Account.FETCH_GROUP_NAME,
		Account.FETCH_GROUP_ACCOUNT_TYPE,
		AccountType.FETCH_GROUP_NAME,
		LegalEntity.FETCH_GROUP_PERSON
	};
	
	public AccountListComposite(Composite parent, int style)
	{
		this(parent, style, false);
	}
	
	public AccountListComposite(Composite parent, int style, boolean openAcountEditor) {
		super(parent, style);
		setOpenAcountEditor(openAcountEditor);
		getTableViewer().addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent e) {
				if (!isOpenAcountEditor() || isDisposed()) return;
				
				StructuredSelection s = (StructuredSelection)e.getSelection();
				if (s.isEmpty())
					return;
				
				Account account = (Account)s.getFirstElement();
				try {
					RCPUtil.openEditor(
							new AccountEditorInput((AnchorID) JDOHelper.getObjectId(account)),
							AccountEditor.EDITOR_ID);
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}
			}

		});

		JDOLifecycleManager.sharedInstance().addNotificationListener(Account.class, accountChangedListener);
		addDisposeListener(new DisposeListener()
		{
			@Override
			public void widgetDisposed(DisposeEvent e)
			{
				JDOLifecycleManager.sharedInstance().removeNotificationListener(Account.class, accountChangedListener);
			}
		});
	}
	
	
	public Boolean isOpenAcountEditor() {
		return openAcountEditor;
	}

	public void setOpenAcountEditor(Boolean openAcountEditor) {
		this.openAcountEditor = openAcountEditor;
	}

	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table)
	{
		tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		tableViewerColumn.getColumn().setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.account.AccountListComposite.id"));  //$NON-NLS-1$

		tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		tableViewerColumn.getColumn().setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.account.AccountListComposite.accountName"));	//$NON-NLS-1$

		tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		tableViewerColumn.getColumn().setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.account.AccountListComposite.owner"));	//$NON-NLS-1$

		tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		tableViewerColumn.getColumn().setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.account.AccountListComposite.accountTypeName"));	//$NON-NLS-1$

		tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		tableViewerColumn.getColumn().setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.account.AccountListComposite.balance"));	//$NON-NLS-1$
//		tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
//			@Override
//			public String getText(Object element) {
//				if (!(element instanceof Account))
//					return null;
//				return NumberFormatter.formatCurrency(((Account) element).getBalance(), ((Account) element).getCurrency(), true);
//			}
//
//			@Override
//			public Color getForeground(Object element) {
//				if (element instanceof Account) {
//					Account account = (Account) element;
//					if(account.getBalance() < 0){
//						return new Color(getDisplay(), 255, 0, 0);
//					}
//				}
//				return null;
//			}
//
////			@Override
////			public Color getBackground(Object element) {
////				if (element instanceof Account) {
////					Account account = (Account) element;
////					if(account.getBalance() < 0){
////						return new Color(getDisplay(), 0, 0, 255);
////					}
////				}
////				return null;
////			}
//		});

		table.setLayout(new WeightedTableLayout(new int[] {30, 30, 20, 10, 10}));
	}

	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setContentProvider(new ArrayContentProvider());
//		tableViewer.setLabelProvider(new AccountTableLabelProvider());
		tableViewer.setLabelProvider(new AcountListLabelProvider());
	}

	class AcountListLabelProvider
	extends TableLabelProvider
	{
		public String getColumnText(Object element, int columnIndex)
		{
			if (element instanceof Account) {
				Account account = (Account) element;
				switch (columnIndex)
				{
					case(0):
						return account.getAnchorID();
					case(1):
						if (account.getName() != null)
							return account.getName().getText();
					case(2):
						if (account.getOwner() != null && account.getOwner().getPerson() != null)
							return account.getOwner().getPerson().getDisplayName();
						break;
					case(3):
						return account.getAccountType().getName().getText();
					case(4):
						if (account.getCurrency() != null)
							return NumberFormatter.formatCurrency(account.getBalance(), account.getCurrency());
						break;
					default:
						return ""; //$NON-NLS-1$
				}
			}
			return null;
		}

		public Color getBackground(Object element, int columnIndex) {
			if (element instanceof Account) {
				Account account = (Account) element;
				switch (columnIndex)
				{
					case(4):
						if(account.getBalance() < 0){
							return new Color(getDisplay(), 255, 0, 0);
						}
					break;
					default:
						return new Color(getDisplay(), 0, 255, 0);
				}
			}
			return new Color(getDisplay(), 0, 255, 0);
		}

		public Color getForeground(Object element, int columnIndex) {
			if (element instanceof Account) {
				Account account = (Account) element;
				switch (columnIndex)
				{
					case(4):
						if(account.getBalance() < 0){
							return new Color(getDisplay(), 255, 0, 0);
						}
					break;
					default:
						return new Color(getDisplay(), 0, 255, 0);
				}
			}
			return new Color(getDisplay(), 0, 255, 0);
		}
	}

//	public static String getAnchorTypeIDName(String anchorTypeID )
//	{
////		String anchorTypeID = account.getAnchorTypeID();
//		if (anchorTypeID.equals(Account.ANCHOR_TYPE_ID_LOCAL_EXPENSE)) {
//			return "Expense Account"; //$NON-NLS-1$
//		}
////		else if (anchorTypeID.equals(Account.ANCHOR_TYPE_ID_LOCAL_REVENUE_IN)) {
////			return "Revenue-In Account"; //$NON-NLS-1$
////		}
////		else if (anchorTypeID.equals(Account.ANCHOR_TYPE_ID_LOCAL_REVENUE_OUT)) {
////			return "Revenue-Out Account"; //$NON-NLS-1$
////		}
//		else if (anchorTypeID.equals(Account.ANCHOR_TYPE_ID_LOCAL_REVENUE)) {
//			return "Revenue Account"; //$NON-NLS-1$
//		}
//		else if (anchorTypeID.equals(Account.ANCHOR_TYPE_ID_OUTSIDE)) {
//			return "External Income/Outcome Account"; //$NON-NLS-1$
//		}
//		else if (anchorTypeID.equals(Account.ANCHOR_TYPE_ID_PARTNER_CUSTOMER)) {
//			return "Partner Account"; //$NON-NLS-1$
//		}
//		else if (anchorTypeID.equals(Account.ANCHOR_TYPE_ID_PARTNER_NEUTRAL)) {
//			return "Neutral Account"; //$NON-NLS-1$
//		}
//		else if (anchorTypeID.equals(Account.ANCHOR_TYPE_ID_PARTNER_VENDOR)) {
//			return "Vendor Account"; //$NON-NLS-1$
//		}
//		else if (anchorTypeID.equals(SummaryAccount.ANCHOR_TYPE_ID_SUMMARY)) {
//			return "Summary Account"; //$NON-NLS-1$
//		}
//		// WORKAROUND: Cannot use VoucherLocalAccountantDelegate.ACCOUNT_ANCHOR_TYPE_ID_VOUCHER
//		// as no dependency exists
//		// TODO This info should come from an extension point
//		else if (anchorTypeID.equals("Account.Voucher")) { //$NON-NLS-1$
//			return "Voucher Account"; //$NON-NLS-1$
//		}
//
//		return anchorTypeID;
//	}

	private NotificationListener accountChangedListener = new NotificationAdapterCallerThread()
	{
		@SuppressWarnings("unchecked")
		public void notify(NotificationEvent notificationEvent) {
			List<Account> accountList = (List<Account>)getTableViewer().getInput();
			for (Iterator<?> it = notificationEvent.getSubjects().iterator(); it.hasNext(); ) {
				DirtyObjectID dirtyObjectID = (DirtyObjectID) it.next();
				for(Account a : accountList){
					final Account account = a;
					final AnchorID anchorID = (AnchorID)JDOHelper.getObjectId(account);
					if (anchorID.equals(dirtyObjectID.getObjectID())) {
						Job job = new Job(Messages.getString("org.nightlabs.jfire.trade.ui.account.editor.AccountEditor.loadingAccountJob.name")) //$NON-NLS-1$
						{
							@Override
							protected IStatus run(ProgressMonitor monitor)
							throws Exception
							{
								final Account newAccount = AccountDAO.sharedInstance().getAccount(
										AnchorID.create(account.getOrganisationID(), account.getAnchorTypeID(), account.getAnchorID()),
										FETCH_GROUPS_ACCOUNT,
										NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
								final Job thisJob = this;
								Display.getDefault().asyncExec(new Runnable()
								{
									public void run()
									{
										if (loadAccountJob != thisJob)
											return;

										if(!getTable().isDisposed())
											getTableViewer().update(newAccount, null);
									}
								});
								return Status.OK_STATUS;
							}
						};

						loadAccountJob = job;
						job.setPriority(org.eclipse.core.runtime.jobs.Job.SHORT);
						job.schedule();
					}//if
				}//for
			}//for
		}
	};

	private Job loadAccountJob = null;


	@Override
	public void dispose()
	{
		JDOLifecycleManager.sharedInstance().removeNotificationListener(Account.class, accountChangedListener);
		super.dispose();
	}

	public void removeAccount(final AnchorID anchorID){
		Job job = new Job(Messages.getString("org.nightlabs.jfire.trade.ui.account.editor.AccountEditor.loadingAccountJob.name")) //$NON-NLS-1$
		{
			@Override
			protected IStatus run(ProgressMonitor monitor)
			throws Exception
			{
				final Account account = AccountDAO.sharedInstance().getAccount(anchorID, FETCH_GROUPS_ACCOUNT, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, null);
				Display.getDefault().asyncExec(new Runnable()
				{
					public void run()
					{
						getTableViewer().remove(account);
						getTableViewer().refresh();
					}
				});
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}
}
