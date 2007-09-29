package org.nightlabs.jfire.trade.overview.account;

import java.util.ArrayList;
import java.util.List;

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
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.JDOQuery;
import org.nightlabs.jdo.ui.JDOQueryComposite;
import org.nightlabs.jfire.accounting.Account;
import org.nightlabs.jfire.accounting.SummaryAccount;
import org.nightlabs.jfire.accounting.id.CurrencyID;
import org.nightlabs.jfire.accounting.query.AccountQuery;
import org.nightlabs.jfire.base.ui.overview.search.SpinnerSearchEntry;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.accounting.CurrencyCombo;
import org.nightlabs.jfire.trade.dao.LegalEntityDAO;
import org.nightlabs.jfire.trade.legalentity.edit.LegalEntitySearchCreateWizard;
import org.nightlabs.jfire.trade.resource.Messages;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class AccountSearchComposite 
extends JDOQueryComposite
{
	public AccountSearchComposite(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode) 
	{
		super(parent, style, layoutMode, layoutDataMode);
		createComposite(this);
	}

	public AccountSearchComposite(Composite parent, int style) {
		super(parent, style);
		createComposite(this);
	}

	private SpinnerSearchEntry minBalanceEntry = null;
	private SpinnerSearchEntry maxBalanceEntry = null;
	private Button activeCurrencyButton = null;
	private CurrencyCombo currencyCombo = null;
	private Button ownerActiveButton = null;
	private Text ownerText = null;
	private Button ownerBrowseButton = null;	
	private Button anchorTypeIDActiveButton = null;
	private XComboComposite<String> anchorTypeIDs = null;
	
	@Override
	protected void createComposite(Composite parent) 
	{
		parent.setLayout(new GridLayout(3, false));
		
		minBalanceEntry = new SpinnerSearchEntry(parent, SWT.NONE, Messages.getString("org.nightlabs.jfire.trade.overview.account.AccountSearchComposite.minBalanceEntry.caption")); //$NON-NLS-1$
		minBalanceEntry.getSpinnerComposite().setMinimum(-Integer.MAX_VALUE);
		minBalanceEntry.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		maxBalanceEntry = new SpinnerSearchEntry(parent, SWT.NONE, Messages.getString("org.nightlabs.jfire.trade.overview.account.AccountSearchComposite.maxBalanceEntry.caption")); //$NON-NLS-1$
		maxBalanceEntry.getSpinnerComposite().setMinimum(-Integer.MAX_VALUE);
		maxBalanceEntry.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));		
		
		Group currencyGroup = new Group(parent, SWT.NONE);
		currencyGroup.setText(Messages.getString("org.nightlabs.jfire.trade.overview.account.AccountSearchComposite.currencyGroup.text")); //$NON-NLS-1$
		currencyGroup.setLayout(new GridLayout());
		currencyGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		activeCurrencyButton = new Button(currencyGroup, SWT.CHECK);
		activeCurrencyButton.setText(Messages.getString("org.nightlabs.jfire.trade.overview.account.AccountSearchComposite.activeCurrencyButton.text")); //$NON-NLS-1$
		activeCurrencyButton.addSelectionListener(activeCurrencyListener);
		currencyCombo = new CurrencyCombo(currencyGroup, SWT.NONE);
		currencyGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		activeCurrencyButton.setSelection(false);
		currencyCombo.setEnabled(false);
		
		final Group ownerGroup = new Group(parent, SWT.NONE);
		ownerGroup.setText(Messages.getString("org.nightlabs.jfire.trade.overview.account.AccountListComposite.AccountSearchComposite.ownerGroup.text")); //$NON-NLS-1$
		ownerGroup.setLayout(new GridLayout(2, false));	
		ownerGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		ownerActiveButton = new Button(ownerGroup, SWT.CHECK);
		ownerActiveButton.setText(Messages.getString("org.nightlabs.jfire.trade.overview.account.AccountListComposite.AccountSearchComposite.ownerActiveButton.text")); //$NON-NLS-1$
		GridData vendorLabelData = new GridData(GridData.FILL_HORIZONTAL);
		vendorLabelData.horizontalSpan = 2;
		ownerActiveButton.setLayoutData(vendorLabelData);		
		ownerText = new Text(ownerGroup, SWT.BORDER);
		ownerText.setEnabled(false);
		ownerText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));		
		ownerText.addSelectionListener(ownerSelectionListener);
		ownerBrowseButton = new Button(ownerGroup, SWT.NONE);
		ownerBrowseButton.setText(Messages.getString("org.nightlabs.jfire.trade.overview.account.AccountListComposite.AccountSearchComposite.ownerBrowseButton.text")); //$NON-NLS-1$
		ownerBrowseButton.addSelectionListener(ownerSelectionListener);
		ownerBrowseButton.setEnabled(false);
		ownerActiveButton.addSelectionListener(new SelectionListener(){		
			public void widgetSelected(SelectionEvent e) {
				ownerText.setEnabled(((Button)e.getSource()).getSelection());
				ownerBrowseButton.setEnabled(((Button)e.getSource()).getSelection());
			}		
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}		
		});
		
		final Group anchorTypeIDGroup = new Group(parent, SWT.NONE);
		anchorTypeIDGroup.setText(Messages.getString("org.nightlabs.jfire.trade.overview.account.AccountListComposite.AccountSearchComposite.anchorTypeIdGroup.text")); //$NON-NLS-1$
		anchorTypeIDGroup.setLayout(new GridLayout());	
		anchorTypeIDGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		anchorTypeIDActiveButton = new Button(anchorTypeIDGroup, SWT.CHECK);
		anchorTypeIDActiveButton.setText(Messages.getString("org.nightlabs.jfire.trade.overview.account.AccountListComposite.AccountSearchComposite.anchorTypeIdActiveButton.text")); //$NON-NLS-1$
		anchorTypeIDActiveButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		anchorTypeIDs = new XComboComposite<String>(
				anchorTypeIDGroup, SWT.BORDER, 
				new LabelProvider() {
					@Override
					public String getText(Object element) {
						return AccountListComposite.getAnchorTypeIDName((String)element);
					}					
				}
		);
		anchorTypeIDs.setInput(getAvailableAccountAnchorTypeIDs());
		anchorTypeIDActiveButton.addSelectionListener(new SelectionListener(){		
			public void widgetSelected(SelectionEvent e) {
				anchorTypeIDs.setEnabled(((Button)e.getSource()).getSelection());
			}		
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}		
		});		
		
//		Group nameGroup = new Group(parent, SWT.NONE);
//		nameGroup.setText("Account Name");
//		nameGroup.setLayout(new GridLayout());
//		nameGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));		
//		activeNameButton = new Button(nameGroup, SWT.CHECK);
//		activeNameButton.setText("Active");
//		activeNameButton.addSelectionListener(activeNameListener);
//		accountNameText = new Text(nameGroup, SWT.BORDER);
//		accountNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));		
//		activeNameButton.setSelection(false);
//		accountNameText.setEnabled(false);
//		
//		Group anchorIDGroup = new Group(parent, SWT.NONE);
//		anchorIDGroup.setText("Account ID");
//		anchorIDGroup.setLayout(new GridLayout());
//		anchorIDGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));	
//		activeAnchorIDButton = new Button(anchorIDGroup, SWT.CHECK);
//		activeAnchorIDButton.setText("Active");
//		activeAnchorIDButton.addSelectionListener(activeNameListener);
//		anchorIDText = new Text(anchorIDGroup, SWT.BORDER);
//		anchorIDText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		activeAnchorIDButton.setSelection(false);
//		anchorIDText.setEnabled(false);		
	}
	
	private SelectionListener activeCurrencyListener = new SelectionListener(){	
		public void widgetSelected(SelectionEvent e) {
			currencyCombo.setEnabled(activeCurrencyButton.getSelection());
		}	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
	};

	private AnchorID selectedOwnerID = null;
	private SelectionListener ownerSelectionListener = new SelectionListener(){
		public void widgetSelected(SelectionEvent e) {
			LegalEntity _legalEntity = LegalEntitySearchCreateWizard.open(ownerText.getText(), false);
			if (_legalEntity != null) {
				selectedOwnerID = (AnchorID) JDOHelper.getObjectId(_legalEntity);
				// TODO perform this expensive code asynchronously
				LegalEntity legalEntity = LegalEntityDAO.sharedInstance().getLegalEntity(selectedOwnerID, 
						new String[] {LegalEntity.FETCH_GROUP_PERSON, FetchPlan.DEFAULT}, 
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
				ownerText.setText(legalEntity.getPerson().getDisplayName());
			}
		}
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}
	};
	
//	private SelectionListener activeNameListener = new SelectionListener(){	
//		public void widgetSelected(SelectionEvent e) {
//			accountNameText.setEnabled(activeNameButton.getSelection());
//		}	
//		public void widgetDefaultSelected(SelectionEvent e) {
//			widgetSelected(e);
//		}	
//	};
//		
//	private SelectionListener activeAnchorIDListener = new SelectionListener(){	
//		public void widgetSelected(SelectionEvent e) {
//			anchorIDText.setEnabled(activeAnchorIDButton.getSelection());
//		}	
//		public void widgetDefaultSelected(SelectionEvent e) {
//			widgetSelected(e);
//		}	
//	};
	
	@Override
	public JDOQuery getJDOQuery() 
	{
		AccountQuery accountQuery = new AccountQuery();
		
		if (minBalanceEntry.isActive()) {
			int numDigits = minBalanceEntry.getSpinnerComposite().getNumDigits();
			double multiplier = Math.pow(10, numDigits);
			Number number = minBalanceEntry.getSpinnerComposite().getValue();
			double val = number.doubleValue() * multiplier;
			long value = new Double(val).longValue();
			accountQuery.setMinBalance(value);			
		}

		if (maxBalanceEntry.isActive()) {
			int numDigits = maxBalanceEntry.getSpinnerComposite().getNumDigits();
			double multiplier = Math.pow(10, numDigits);
			Number number = maxBalanceEntry.getSpinnerComposite().getValue();
			double val = number.doubleValue() * multiplier;
			long value = new Double(val).longValue();
			accountQuery.setMaxBalance(value);
		}
		
		if (activeCurrencyButton.getSelection())
			accountQuery.setCurrencyID((CurrencyID)JDOHelper.getObjectId(currencyCombo.getSelectedCurrency()));
		
		if (ownerActiveButton.getSelection())
			accountQuery.setOwnerID(selectedOwnerID);
		
		if (anchorTypeIDActiveButton.getSelection() && anchorTypeIDs.getSelectedElement() != null)
			accountQuery.setAnchorTypeID(anchorTypeIDs.getSelectedElement());
		
//		if (activeNameButton.getSelection() && accountNameText.getText() != null && !accountNameText.getText().trim().equals(""))
//			accountQuery.setName(accountNameText.getText());
//
//		if (activeAnchorIDButton.getSelection() && anchorIDText.getText() != null && !anchorIDText.getText().trim().equals(""))
//			accountQuery.setAnchorID(anchorIDText.getText());
		
		return accountQuery;
	}
	
	private List<String> availableAccountAnchorTypeIDs = null;
	protected List<String> getAvailableAccountAnchorTypeIDs() 
	{
		if (availableAccountAnchorTypeIDs == null) {
			List<String> list = new ArrayList<String>();
			list.add(Account.ANCHOR_TYPE_ID_LOCAL_EXPENSE);
			list.add(Account.ANCHOR_TYPE_ID_LOCAL_REVENUE);
//			list.add(Account.ANCHOR_TYPE_ID_LOCAL_REVENUE_IN);			
//			list.add(Account.ANCHOR_TYPE_ID_LOCAL_REVENUE_OUT);
			list.add(Account.ANCHOR_TYPE_ID_OUTSIDE);			
			list.add(Account.ANCHOR_TYPE_ID_PARTNER_CUSTOMER);			
			list.add(Account.ANCHOR_TYPE_ID_PARTNER_NEUTRAL);			
			list.add(Account.ANCHOR_TYPE_ID_PARTNER_VENDOR);
			list.add(SummaryAccount.ANCHOR_TYPE_ID_SUMMARY);			
			// WORKAROUND: Cannot use VoucherLocalAccountantDelegate.ACCOUNT_ANCHOR_TYPE_ID_VOUCHER
			// as no dependency exists			
			// TODO Information should come from an extension point
			list.add("Account.Voucher"); //$NON-NLS-1$
			availableAccountAnchorTypeIDs = list;
		}
		return availableAccountAnchorTypeIDs;
	}
}
