package org.nightlabs.jfire.trade.ui.account.editor;

import java.util.Calendar;
import java.util.List;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.nightlabs.annotation.Implement;
import org.nightlabs.base.ui.composite.DateTimeEdit;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Account;
import org.nightlabs.jfire.accounting.AccountSearchFilter;
import org.nightlabs.jfire.accounting.dao.AccountDAO;
import org.nightlabs.jfire.base.ui.overview.search.SpinnerSearchEntry;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.l10n.DateFormatter;
import org.nightlabs.progress.ProgressMonitor;
/** 
 * @author Chairat Kongarayawetchakun <!-- chairatk [AT] nightlabs [DOT] de -->
 */
public class MoneyTransferFilterComposite extends XComposite
{
	private AccountChooserComposite accountChooserComposite;
	
	public MoneyTransferFilterComposite(Composite parent, int style) {
		super(parent, style);
		createComposite(this);
		setActive(false);
	}

	private SpinnerSearchEntry transferAmountEntry = null;
	private DateTimeEdit createDTMin = null;
	private DateTimeEdit createDTMax = null;
	
	private Button activeAccountButton = null;

	private String[] FETCH_GROUPS_ACCOUNT = {FetchPlan.DEFAULT, Account.FETCH_GROUP_THIS_ACCOUNT , LegalEntity.FETCH_GROUP_PERSON};

	/**
	 * Create the content for this composite.
	 * @param parent The parent composite
	 */
	protected void createComposite(Composite parent) 
	{
		parent.setLayout(new GridLayout(2, false));

		transferAmountEntry = new SpinnerSearchEntry(parent, SWT.NONE, Messages.getString("org.nightlabs.jfire.trade.ui.overview.account.AccountSearchComposite.minBalanceEntry.caption")); //$NON-NLS-1$
		transferAmountEntry.getSpinnerComposite().setMinimum(-Integer.MAX_VALUE);
		transferAmountEntry.setActive(false);
		transferAmountEntry.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Group dateGroup = new Group(parent, SWT.NONE);
		dateGroup.setText(Messages.getString("org.nightlabs.jfire.trade.ui.account.editor.ManualMoneyTransferSearchComposite.dateGroup.text")); //$NON-NLS-1$
		dateGroup.setLayout(new GridLayout(2, false));
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		dateGroup.setLayoutData(gridData);
		
		createDTMin = new DateTimeEdit(
				dateGroup,
				DateFormatter.FLAGS_DATE_SHORT_TIME_HMS_WEEKDAY + DateTimeEdit.FLAGS_SHOW_ACTIVE_CHECK_BOX,
				Messages.getString("org.nightlabs.jfire.trade.ui.overview.StatableFilterComposite.createDateMin.caption")); //$NON-NLS-1$
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, cal.getActualMinimum(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, cal.getActualMinimum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getActualMinimum(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, cal.getActualMinimum(Calendar.MILLISECOND));
		createDTMin.setDate(cal.getTime());
		
		createDTMax = new DateTimeEdit(
				dateGroup,
				DateFormatter.FLAGS_DATE_SHORT_TIME_HMS_WEEKDAY + DateTimeEdit.FLAGS_SHOW_ACTIVE_CHECK_BOX,
				Messages.getString("org.nightlabs.jfire.trade.ui.overview.StatableFilterComposite.createDateMax.caption"));		 //$NON-NLS-1$
		cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, cal.getActualMaximum(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, cal.getActualMaximum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getActualMaximum(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, cal.getActualMaximum(Calendar.MILLISECOND));
		createDTMax.setDate(cal.getTime());		
		
		/**********************************************************/
		Group otherSideAccountGroup = new Group(parent, SWT.NONE);
		otherSideAccountGroup.setText(Messages.getString("org.nightlabs.jfire.trade.ui.account.editor.ManualMoneyTransferSearchComposite.otherSideAccountGroup.text")); //$NON-NLS-1$
		otherSideAccountGroup.setLayout(new GridLayout(1, false));
		
		activeAccountButton = new Button(otherSideAccountGroup, SWT.CHECK);
		activeAccountButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.account.AccountSearchComposite.activeCurrencyButton.text")); //$NON-NLS-1$
		activeAccountButton.addSelectionListener(activeAccountListener);
		
		accountChooserComposite = new AccountChooserComposite(
				otherSideAccountGroup, SWT.NONE);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		gridData.heightHint = 250;
		accountChooserComposite.setLayoutData(gridData);
		
		Job job = new Job(Messages.getString("org.nightlabs.jfire.trade.ui.account.editor.ManualMoneyTransferSearchComposite.loadingAccountsJob.name")) { //$NON-NLS-1$
			@Override
			@Implement
			protected IStatus run(ProgressMonitor monitor) {
				try {
					AccountSearchFilter accountSearchFilter = new AccountSearchFilter();

					final List<Account> accounts = AccountDAO.sharedInstance()
							.getAccounts(accountSearchFilter,
									FETCH_GROUPS_ACCOUNT,
									NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
									monitor);

					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							accountChooserComposite.getAccountListComposite().setInput(accounts);
							accountChooserComposite.getAccountListComposite().update();
						}
					});
				} catch (Exception x) {
					throw new RuntimeException(x);
				}
				return Status.OK_STATUS;
			}
		};
		job.setPriority(org.eclipse.core.runtime.jobs.Job.SHORT);
		job.schedule();
		
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 2;
		otherSideAccountGroup.setLayoutData(gridData);
	}
	
	private SelectionListener activeAccountListener = new SelectionAdapter(){
		@Override
		public void widgetSelected(SelectionEvent e) {
			activeSelected();
		}
	};
	
	public boolean isActive()
	{
		return activeAccountButton == null ? true : activeAccountButton.getSelection();
	}
	
	public void setActive(boolean active)
	{
		if (this.activeAccountButton == null)
			return;

		this.activeAccountButton.setSelection(active);
		activeSelected();
	}
	
	private void activeSelected()
	{
		Control[] controls = accountChooserComposite.getChildren();
		for(Control control : controls)
			control.setEnabled(isActive());
	}
	
	public DateTimeEdit getCreateDTMin() {
		return createDTMin;
	}
	
	public DateTimeEdit getCreateDTMax() {
		return createDTMax;
	}
	
	public AccountChooserComposite getAccountChooserComposite() {
		return accountChooserComposite;
	}
	
	public SpinnerSearchEntry getTransferAmountEntry() {
		return transferAmountEntry;
	}
}
