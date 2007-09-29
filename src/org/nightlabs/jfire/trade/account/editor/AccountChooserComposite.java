package org.nightlabs.jfire.trade.account.editor;

import java.util.List;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.annotation.Implement;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Account;
import org.nightlabs.jfire.accounting.AccountSearchFilter;
import org.nightlabs.jfire.accounting.dao.AccountDAO;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.overview.account.AccountListComposite;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.progress.ProgressMonitor;

public class AccountChooserComposite extends XComposite{

	private String[] FETCH_GROUPS_ACCOUNT = {FetchPlan.DEFAULT, Account.FETCH_GROUP_THIS_ACCOUNT , LegalEntity.FETCH_GROUP_PERSON};
	private AccountListComposite accountListComposite;

	private Label accountFilterLabel;
	private Text accountFilterName;

	private Combo columnNameCombo;

	private AnchorID selectedAccountAnchorID;

	public AccountChooserComposite(Composite parent, int style) {
		super(parent, style);
		createComposite(this);
	}

	protected void createComposite(Composite parent){
		Composite filterComposite = new Composite(parent, SWT.NONE);
		filterComposite.setLayout(new GridLayout(3, false));
		filterComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		accountFilterLabel = new Label(filterComposite, SWT.NONE);
		accountFilterLabel.setText("Filter: ");

		accountFilterName = new Text(filterComposite, SWT.BORDER | SWT.SINGLE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		accountFilterName.setLayoutData(gridData);
		accountFilterName.addKeyListener(new KeyListener(){
			public void keyPressed(KeyEvent e) {
				//do nothing
			}
			public void keyReleased(KeyEvent e) {
				final String s = accountFilterName.getText();
				ViewerFilter vf = new ViewerFilter(){
					@Override
					public boolean select(Viewer viewer, Object parentElement, Object element) {
						String filterName = columnNameCombo.getText();
						Account account = (Account)element;

						Table accountTable = accountListComposite.getTable();

						int index = -1;
						for(int i = 0; i < accountTable.getColumnCount(); i++){
							if(accountTable.getColumn(i).getText().equals(filterName)) {
								index = i;
								break;
							}
						}//for
						if (index == -1)
							throw new IllegalStateException("The Filtercombo contains Strings that do not match the column titles!");

						ColumnLabelProvider labelProvider = (ColumnLabelProvider) accountListComposite.getTableViewer().getLabelProvider(index);
						if (labelProvider.getText(element).startsWith(s))
							return true;

						return false;
					}
				};

				accountListComposite.getTableViewer().setFilters(new ViewerFilter[]{vf});
			}
		});

		columnNameCombo = new Combo(filterComposite, SWT.DROP_DOWN);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.HORIZONTAL_ALIGN_END;
		columnNameCombo.setLayoutData(gridData);
		columnNameCombo.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent arg0) {
				accountListComposite.refresh();
			}

			public void widgetDefaultSelected(SelectionEvent arg0) {

			}
		});

		Group listGroup = new Group(parent, SWT.NONE);
		listGroup.setText("Account List");
		listGroup.setLayout(new GridLayout());

		accountListComposite = new AccountListComposite(
				listGroup, SWT.NONE);

		TableColumn[] tableColumns = accountListComposite.getTable().getColumns();
		for(int i = 0; i < tableColumns.length; i++){
			TableColumn tc = tableColumns[i];
			columnNameCombo.add(tc.getText());
		}//for
		columnNameCombo.setText(columnNameCombo.getItem(0));

		Job job = new Job("Loading...") {
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
							accountListComposite.setInput(accounts);
						}
					});
				} catch (Exception x) {
					throw new RuntimeException(x);
				}
				return Status.OK_STATUS;
			}
		};
		job.setPriority(Job.SHORT);
		job.schedule();

		accountListComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		accountListComposite.getTableViewer().addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent e) {
				if(accountListComposite.getFirstSelectedElement() != null){
					Account selectedAccount = accountListComposite.getFirstSelectedElement();
					selectedAccountAnchorID = AnchorID.create(selectedAccount.getOrganisationID(), selectedAccount.getAnchorTypeID(), selectedAccount.getAnchorID());
					accountFilterName.setText(selectedAccountAnchorID.anchorID);
				}//if
			}
		});
		
		listGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	@Override
	public void setVisible(boolean visible) {
		if(selectedAccountAnchorID != null){
			accountFilterName.setText(selectedAccountAnchorID.anchorID);
		}
		
		super.setVisible(visible);
	}
	
	public AnchorID getSelectedAccount() {
		return selectedAccountAnchorID;
	}

	public void setSelectedAccount(AnchorID selectedAccountAnchorID) {
		this.selectedAccountAnchorID = selectedAccountAnchorID;
	}

	public AccountListComposite getAccountListComposite(){
		return accountListComposite;
	}
}
