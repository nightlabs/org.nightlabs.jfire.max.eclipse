package org.nightlabs.jfire.trade.ui.account.transfer.manual;

import javax.jdo.FetchPlan;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.CurrencyEdit;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.language.I18nTextEditorMultiLine;
import org.nightlabs.base.ui.language.I18nTextEditor.EditMode;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Account;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.accounting.dao.AccountDAO;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.l10n.NumberFormatter;

/**
 * @author Chairat Kongarayawetchakun - chairatk[at]nightlabs[dot]de
 */
public class ManualMoneyTransferComposite extends XComposite {

	private AnchorID fromAccountID;
	private AnchorID toAccountID;

	/*
	 * From-Account Information Components
	 */
	private Label fAccountIDLabel;
	private Label fAccountIDDataLabel;
	private Label fAccountNameLabel;
	private Label fAccountNameDataLabel;
	private Label fBalanceLabel;
	private Label fBalanceDataLabel;
	private Label fOrganizationLabel;
	private Label fOrganizationDataLabel;

	/*
	 * To-Account Information Components
	 */
	private Label tAccountIDLabel;
	private Label tAccountIDDataLabel;
	private Label tAccountNameLabel;
	private Label tAccountNameDataLabel;
	private Label tBalanceLabel;
	private Label tBalanceDataLabel;
	private Label tOrganizationLabel;
	private Label tOrganizationDataLabel;

	private Label reasonLabel;
	private I18nTextEditorMultiLine reasonText;

	private Label amountLabel;
	private CurrencyEdit amountText;

	private Button transferButton;
	
	private boolean isFromTo = true;
	
	public ManualMoneyTransferComposite(Composite parent, int style) {
		super(parent, style);

		getGridLayout().verticalSpacing = 10;
		GridData gridData = new GridData();

		amountLabel = new Label(this, SWT.NONE);
		amountLabel
		.setText(Messages
				.getString("org.nightlabs.jfire.trade.ui.account.transfer.manual.ManualMoneyTransferComposite.amountLabel.text")); //$NON-NLS-1$

		amountText = new CurrencyEdit(this, new Currency("TH", "TH", 2)); //$NON-NLS-1$ //$NON-NLS-2$
		amountText.setErrorDialogEnabled(false);
		amountText.addVerifyListener(new VerifyListener() {
			public void verifyText(VerifyEvent event) {
				event.doit = event.text.length() == 0
				|| Character.isDigit(event.text.charAt(0)) || event.text.charAt(0) == '-';
			}
		});

		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalIndent = 4;
		amountText.setLayoutData(gridData);

		reasonLabel = new Label(this, SWT.NONE);
		reasonLabel
		.setText(Messages
				.getString("org.nightlabs.jfire.trade.ui.account.transfer.manual.ManualMoneyTransferComposite.reasonLabel.text")); //$NON-NLS-1$

		reasonText = new I18nTextEditorMultiLine(this);
		reasonText.setI18nText(null, EditMode.BUFFERED);

		gridData = new GridData(GridData.FILL_BOTH);
		reasonText.setLayoutData(gridData);
		// reasonText.addModifyListener(new ModifyListener(){
		// public void modifyText(ModifyEvent arg0) {
		//
		// }
		// });

		/** ***************************************************** */

		XComposite bottomComposite = new XComposite(this, SWT.NONE);
		bottomComposite.getGridLayout().numColumns = 3;
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 3;
		gridData.grabExcessVerticalSpace = true;
		bottomComposite.setLayoutData(gridData);

		Group lg = new Group(bottomComposite, SWT.NONE);
		lg
		.setText(Messages
				.getString("org.nightlabs.jfire.trade.ui.account.transfer.manual.ManualMoneyTransferComposite.sourceAccountGroup.text")); //$NON-NLS-1$
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessVerticalSpace = true;
		lg.setLayoutData(gridData);

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		lg.setLayout(gridLayout);

		gridData = new GridData();

		fAccountIDLabel = new Label(lg, SWT.LEFT);
		fAccountIDLabel
		.setText(Messages
				.getString("org.nightlabs.jfire.trade.ui.account.transfer.manual.ManualMoneyTransferComposite.sourceAccountIDLabel.text")); //$NON-NLS-1$

		fAccountIDDataLabel = new Label(lg, SWT.LEFT);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		fAccountIDDataLabel.setLayoutData(gridData);

		fAccountNameLabel = new Label(lg, SWT.LEFT);
		fAccountNameLabel
		.setText(Messages
				.getString("org.nightlabs.jfire.trade.ui.account.transfer.manual.ManualMoneyTransferComposite.sourceAccountNameLabel.text")); //$NON-NLS-1$

		fAccountNameDataLabel = new Label(lg, SWT.LEFT);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		fAccountNameDataLabel.setLayoutData(gridData);

		fBalanceLabel = new Label(lg, SWT.LEFT);
		fBalanceLabel
		.setText(Messages
				.getString("org.nightlabs.jfire.trade.ui.account.transfer.manual.ManualMoneyTransferComposite.sourceAccountBalanceLabel.text")); //$NON-NLS-1$

		fBalanceDataLabel = new Label(lg, SWT.LEFT);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		fBalanceDataLabel.setLayoutData(gridData);

		fOrganizationLabel = new Label(lg, SWT.LEFT);
		fOrganizationLabel
		.setText(Messages
				.getString("org.nightlabs.jfire.trade.ui.account.transfer.manual.ManualMoneyTransferComposite.sourceAccountOrganisationLabel.text")); //$NON-NLS-1$

		fOrganizationDataLabel = new Label(lg, SWT.LEFT);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		fOrganizationDataLabel.setLayoutData(gridData);

		transferButton = new Button(bottomComposite, SWT.PUSH);
		transferButton.setText("<----->"); //$NON-NLS-1$
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER,
				GridData.VERTICAL_ALIGN_CENTER);
		gridData.heightHint = 20;
		transferButton.setLayoutData(gridData);
		transferButton.addSelectionListener(transferButtonListener);

		// *************************************
		Group rg = new Group(bottomComposite, SWT.NONE);
		rg
		.setText(Messages
				.getString("org.nightlabs.jfire.trade.ui.account.transfer.manual.ManualMoneyTransferComposite.targetAccountGroup.text")); //$NON-NLS-1$
		rg.setLayoutData(new GridData(GridData.FILL_BOTH));
		// *************************************
		gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		rg.setLayout(gridLayout);

		gridData = new GridData();

		tAccountIDLabel = new Label(rg, SWT.LEFT);
		tAccountIDLabel
		.setText(Messages
				.getString("org.nightlabs.jfire.trade.ui.account.transfer.manual.ManualMoneyTransferComposite.targetAccountIDLabel.text")); //$NON-NLS-1$

		tAccountIDDataLabel = new Label(rg, SWT.LEFT);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		tAccountIDDataLabel.setLayoutData(gridData);

		tAccountNameLabel = new Label(rg, SWT.LEFT);
		tAccountNameLabel
		.setText(Messages
				.getString("org.nightlabs.jfire.trade.ui.account.transfer.manual.ManualMoneyTransferComposite.targetAccountNameLabel.text")); //$NON-NLS-1$

		tAccountNameDataLabel = new Label(rg, SWT.LEFT);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		tAccountNameDataLabel.setLayoutData(gridData);

		tBalanceLabel = new Label(rg, SWT.LEFT);
		tBalanceLabel
		.setText(Messages
				.getString("org.nightlabs.jfire.trade.ui.account.transfer.manual.ManualMoneyTransferComposite.targetAccountBalanceLabel.text")); //$NON-NLS-1$

		tBalanceDataLabel = new Label(rg, SWT.LEFT);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		tBalanceDataLabel.setLayoutData(gridData);

		tOrganizationLabel = new Label(rg, SWT.LEFT);
		tOrganizationLabel
		.setText(Messages
				.getString("org.nightlabs.jfire.trade.ui.account.transfer.manual.ManualMoneyTransferComposite.targetAccountOrganisationLabel.text")); //$NON-NLS-1$

		tOrganizationDataLabel = new Label(rg, SWT.LEFT);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		tOrganizationDataLabel.setLayoutData(gridData);
	}

	public ManualMoneyTransferComposite(Composite parent, int style,
			AnchorID fromAccountID) {
		this(parent, style);
		this.fromAccountID = fromAccountID;
	}

	public void setFromAccount(AnchorID fromAccountID) {
		this.fromAccountID = fromAccountID;
		updateData();
	}

	public AnchorID getFromAccount() {
		return fromAccountID;
	}

	public void setToAccount(AnchorID toAccount) {
		this.toAccountID = toAccount;
		updateData();
	}

	public AnchorID getToAccount() {
		return toAccountID;
	}

	private String[] FETCH_GROUPS_ACCOUNT = { FetchPlan.DEFAULT,
			Account.FETCH_GROUP_THIS_ACCOUNT, LegalEntity.FETCH_GROUP_PERSON };

	private void updateData() {
		if (fromAccountID != null) {
			Account fromAccount = AccountDAO.sharedInstance().getAccount(
					fromAccountID, FETCH_GROUPS_ACCOUNT,
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, null);
			fAccountIDDataLabel.setText(fromAccount.getAnchorID());
			fAccountNameDataLabel.setText(fromAccount.getName().getText());
			fBalanceDataLabel.setText(NumberFormatter.formatCurrency(fromAccount.getBalance(), fromAccount.getCurrency()));
			fOrganizationDataLabel.setText(fromAccount.getOrganisationID());

			amountText.setCurrency(fromAccount.getCurrency());
		}// if

		if (toAccountID != null) {
			Account toAccount = AccountDAO.sharedInstance().getAccount(
					toAccountID, FETCH_GROUPS_ACCOUNT,
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, null);
			tAccountIDDataLabel.setText(toAccount.getAnchorID());
			tAccountNameDataLabel.setText(toAccount.getName().getText());
			tBalanceDataLabel.setText(NumberFormatter.formatCurrency(toAccount.getBalance(), toAccount.getCurrency()));
			tOrganizationDataLabel.setText(toAccount.getOrganisationID());
		}// if
	}

	SelectionListener transferButtonListener = new SelectionListener() {
		public void widgetSelected(SelectionEvent e) {
			AnchorID tempID = fromAccountID;
			fromAccountID = toAccountID;
			toAccountID = tempID;

			isFromTo = !isFromTo;
			updateData();
		}

		public void widgetDefaultSelected(SelectionEvent e) {
			//do nothing
		}
	};

	public CurrencyEdit getCurrencyEdit() {
		return amountText;
	}

	public I18nTextEditorMultiLine getReasonText() {
		return reasonText;
	}
}
