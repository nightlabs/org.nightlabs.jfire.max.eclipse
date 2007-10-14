package org.nightlabs.jfire.voucher.transfer.pay.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.exceptionhandler.ExceptionHandlerRegistry;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.jfire.voucher.VoucherPlugin;
import org.nightlabs.jfire.voucher.accounting.pay.PaymentDataVoucher;
import org.nightlabs.jfire.voucher.detail.VoucherKeyDetailComposite;
import org.nightlabs.jfire.voucher.resource.Messages;
import org.nightlabs.jfire.voucher.store.VoucherKey;
import org.nightlabs.jfire.voucher.transfer.pay.ClientPaymentProcessorVoucher;
import org.nightlabs.keyreader.KeyReadEvent;
import org.nightlabs.keyreader.KeyReadListener;
import org.nightlabs.keyreader.KeyReader;
import org.nightlabs.keyreader.KeyReaderErrorEvent;
import org.nightlabs.keyreader.KeyReaderErrorListener;
import org.nightlabs.keyreader.KeyReaderMan;
import org.nightlabs.keyreader.ui.KeyReaderImplementationRegistry;
import org.nightlabs.l10n.NumberFormatter;

public class VoucherKeyWizardPage
		extends WizardHopPage
{
	private static final Logger logger = Logger.getLogger(VoucherKeyWizardPage.class);

	private ClientPaymentProcessorVoucher clientPaymentProcessor;

	private Text voucherKeyText;
	private Button voucherKeyOK;

	private VoucherKeyDetailComposite voucherKeyDetailComposite;

	public VoucherKeyWizardPage(ClientPaymentProcessorVoucher clientPaymentProcessor)
	{
		super(VoucherKeyWizardPage.class.getName(), Messages.getString("org.nightlabs.jfire.voucher.transfer.pay.wizard.VoucherKeyWizardPage.title")); //$NON-NLS-1$
		setDescription(Messages.getString("org.nightlabs.jfire.voucher.transfer.pay.wizard.VoucherKeyWizardPage.description")); //$NON-NLS-1$
		this.clientPaymentProcessor = clientPaymentProcessor;
	}

	private void clearVoucherKey()
	{
		setErrorMessage(null);
		setMessage(null, WARNING);
		setMessage(null);

		ignorePropertyChange = true;
		try {
			voucherKeyDetailComposite.setVoucherKeyString(null);
		} finally {
			ignorePropertyChange = false;
		}

		if (voucherKey == null)
			return;

		voucherKey = null;
		getContainer().updateButtons();
	}

	private KeyReaderMan keyReaderMan;

	private KeyReaderErrorListener keyReaderErrorListener = new KeyReaderErrorListener() {
		public void errorOccured(KeyReaderErrorEvent e)
		{
			ExceptionHandlerRegistry.asyncHandleException(e.getError());
		}
	};

	private KeyReadListener keyReadListener = new KeyReadListener()
	{
		public void keyRead(final KeyReadEvent e)
		{
			voucherKey = null;
			Display.getDefault().asyncExec(new Runnable()
			{
				public void run()
				{
					voucherKeyText.setText(e.getKey());
					voucherKeyEntered();
				}
			});
		}
	};

	private static final String KEY_READER_ID = VoucherPlugin.getDefault().getBundle().getSymbolicName();

	private KeyReader keyReader;
	private boolean keyReaderListenersRegistered = false;

	private void startKeyReader()
	{
		try {
			KeyReaderImplementationRegistry.sharedInstance(); // this initializes the extension point in order to allow class-loading without buddy-registration

			if (keyReaderMan == null)
				keyReaderMan = KeyReaderMan.sharedInstance();

			keyReader = keyReaderMan.createKeyReader(KEY_READER_ID);
			if (!keyReaderListenersRegistered) {
				keyReader.addKeyReaderErrorListener(keyReaderErrorListener);
				keyReader.addKeyReadListener(keyReadListener);
				keyReaderListenersRegistered = true;
			}
			keyReader.openPort();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void stopKeyReader(boolean onlyUnregisterListeners)
	{
		if (keyReader == null)
			return;

		keyReader.removeKeyReaderErrorListener(keyReaderErrorListener);
		keyReader.removeKeyReadListener(keyReadListener);
		keyReaderListenersRegistered = false;

		if (onlyUnregisterListeners)
			return;

		keyReader.close(true);
		keyReader = null;
	}

	@Override
	public Control createPageContents(Composite parent)
	{
		XComposite page = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);

		XComposite voucherKeyComp = new XComposite(page, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		voucherKeyComp.getGridLayout().numColumns = 2;
		voucherKeyComp.getGridData().grabExcessVerticalSpace = false;
		voucherKeyText = new Text(voucherKeyComp, SWT.BORDER);
		voucherKeyText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		voucherKeyText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e)
			{
				if (logger.isDebugEnabled())
					logger.debug("voucherKeyText: modifyText"); //$NON-NLS-1$

				clearVoucherKey();
			}
		});
		voucherKeyText.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e)
			{
				if (logger.isDebugEnabled())
					logger.debug("voucherKeyText: focusGained - defaultButton=" + getContainer().getShell().getDefaultButton()); //$NON-NLS-1$
//				getContainer().getShell().setDefaultButton(null);
			}
			@Override
			public void focusLost(FocusEvent e)
			{
				logger.debug("voucherKeyText: focusLost"); //$NON-NLS-1$
			}
		});
		voucherKeyText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e)
			{
				logger.debug("voucherKeyText: keyPressed: " + (int)e.character); //$NON-NLS-1$
			}
			@Override
			public void keyReleased(KeyEvent e)
			{
				logger.debug("voucherKeyText: keyReleased: " + (int)e.character); //$NON-NLS-1$
				if (e.character == '\r') {
					voucherKeyEntered();
				}
			}
		});
		voucherKeyText.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				logger.debug("voucherKeyText: widgetDefaultSelected"); //$NON-NLS-1$
			}
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				logger.debug("voucherKeyText: widgetSelected"); //$NON-NLS-1$
			}
		});

		voucherKeyOK = new Button(voucherKeyComp, SWT.PUSH);
		voucherKeyOK.setText(Messages.getString("org.nightlabs.jfire.voucher.transfer.pay.wizard.VoucherKeyWizardPage.voucherKeyOKButton.text")); //$NON-NLS-1$
		voucherKeyOK.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				voucherKeyEntered();
			}
		});

		voucherKeyDetailComposite = new VoucherKeyDetailComposite(page, SWT.BORDER);
		voucherKeyDetailComposite.addPropertyChangeListener(VoucherKeyDetailComposite.PROPERTY_NAME_VOUCHER_KEY, new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt)
			{
				if (ignorePropertyChange)
					return;

				voucherKey = (VoucherKey) evt.getNewValue();
				if (voucherKey == null) {
					errorMessage(
							Messages.getString("org.nightlabs.jfire.voucher.transfer.pay.wizard.VoucherKeyWizardPage.errorMessageVoucherNotFound.title"), //$NON-NLS-1$
							Messages.getString("org.nightlabs.jfire.voucher.transfer.pay.wizard.VoucherKeyWizardPage.errorMessageVoucherNotFound.message") //$NON-NLS-1$
					);
				}
				onShow();
			}
		});

		// register the listeners after creating the gui in order to ensure it exists, when they're triggered
		startKeyReader();
		page.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e)
			{
				stopKeyReader(false);
			}
		});

		return page;
	}

	private void errorMessage(String title, String message)
	{
		setErrorMessage(message);
		MessageDialog.openError(
				getShell(), title, message
		);
	}

	private void warnMessage(String title, String message)
	{
		setMessage(message, WARNING);
		MessageDialog.openWarning(
				getShell(), title, message
		);
	}

	private boolean ignorePropertyChange = false;

	@Override
	public void onHide()
	{
		stopKeyReader(true);
	}

	@Override
	public void onShow()
	{
		startKeyReader();

		if (voucherKey != null) {
			if (VoucherKey.VALIDITY_VALID != voucherKey.getValidity()) {
				errorMessage(
						Messages.getString("org.nightlabs.jfire.voucher.transfer.pay.wizard.VoucherKeyWizardPage.errorMessageVoucherNotValid.title"), //$NON-NLS-1$
						Messages.getString("org.nightlabs.jfire.voucher.transfer.pay.wizard.VoucherKeyWizardPage.errorMessageVoucherNotValid.message") //$NON-NLS-1$
				);
			} // if voucherKey not valid
			else {

				if (!clientPaymentProcessor.getCurrency().equals(voucherKey.getRestValue().getCurrency())) {
					errorMessage(
							Messages.getString("org.nightlabs.jfire.voucher.transfer.pay.wizard.VoucherKeyWizardPage.errorMessageCurrencyMismatch.title"), //$NON-NLS-1$
							String.format(
									Messages.getString("org.nightlabs.jfire.voucher.transfer.pay.wizard.VoucherKeyWizardPage.errorMessageCurrencyMismatch.message"), //$NON-NLS-1$
									voucherKey.getRestValue().getCurrency().getCurrencySymbol(),
									clientPaymentProcessor.getCurrency().getCurrencySymbol()
							)
					);
					voucherKey = null;
				}
				else {
					IWizardPage page = getWizard().getStartingPage();
					iterateWizardPages: while (page != null && page != this) {
						if (page instanceof VoucherKeyWizardPage) {
							VoucherKeyWizardPage vkwp = (VoucherKeyWizardPage) page;
							if (voucherKey.equals(vkwp.voucherKey)) {
								errorMessage(
										Messages.getString("org.nightlabs.jfire.voucher.transfer.pay.wizard.VoucherKeyWizardPage.errorMessageVoucherAlreadySelected.title"), //$NON-NLS-1$
										Messages.getString("org.nightlabs.jfire.voucher.transfer.pay.wizard.VoucherKeyWizardPage.errorMessageVoucherAlreadySelected.message") //$NON-NLS-1$
								);

								voucherKey = null;
								break iterateWizardPages;
							}
						}

						page = page.getNextPage();
					}

					if (voucherKey != null) {

						if (clientPaymentProcessor.getAmount() > voucherKey.getRestValue().getAmount()) {
							warnMessage(
									Messages.getString("org.nightlabs.jfire.voucher.transfer.pay.wizard.VoucherKeyWizardPage.errorMessageVoucherInsufficient.title"), //$NON-NLS-1$
									String.format(
											Messages.getString("org.nightlabs.jfire.voucher.transfer.pay.wizard.VoucherKeyWizardPage.errorMessageVoucherInsufficient.message"), //$NON-NLS-1$
											NumberFormatter.formatCurrency(clientPaymentProcessor.getAmount(), clientPaymentProcessor.getCurrency()),
											NumberFormatter.formatCurrency(voucherKey.getRestValue().getAmount(), voucherKey.getRestValue().getCurrency())
									)
							);
							clientPaymentProcessor.getPaymentEntryPage().setAmount(voucherKey.getRestValue().getAmount());
						}

						if (voucherKey.getRestValue().getAmount() > clientPaymentProcessor.getAmount() &&
								clientPaymentProcessor.getAmount() < clientPaymentProcessor.getPaymentEntryPage().getMaxAmount())
						{
							if (MessageDialog.openQuestion(
									getShell(),
									Messages.getString("org.nightlabs.jfire.voucher.transfer.pay.wizard.VoucherKeyWizardPage.dialogIncreaseAmount.title"), //$NON-NLS-1$
									String.format(
											Messages.getString("org.nightlabs.jfire.voucher.transfer.pay.wizard.VoucherKeyWizardPage.dialogIncreaseAmount.message"), //$NON-NLS-1$
											NumberFormatter.formatCurrency(clientPaymentProcessor.getAmount(), clientPaymentProcessor.getCurrency()),
											NumberFormatter.formatCurrency(clientPaymentProcessor.getPaymentEntryPage().getMaxAmount(), clientPaymentProcessor.getCurrency()),
											NumberFormatter.formatCurrency(voucherKey.getRestValue().getAmount(), voucherKey.getRestValue().getCurrency())
									)
							))
							{
								clientPaymentProcessor.getPaymentEntryPage().setAmount(Math.min(
										clientPaymentProcessor.getPaymentEntryPage().getMaxAmount(),
										voucherKey.getRestValue().getAmount()
								));
							}
						}

					} // if (voucherKey != null) {

				}
			} // if voucherKey valid
		} // if (voucherKey != null) {

		PaymentDataVoucher paymentData = (PaymentDataVoucher) clientPaymentProcessor.getPaymentData();
		paymentData.setVoucherKey(voucherKey == null ? null : voucherKey.getVoucherKey());

		getContainer().updateButtons();
	}

	private void voucherKeyEntered()
	{
		voucherKeyDetailComposite.setVoucherKeyString(voucherKeyText.getText());
	}

	private VoucherKey voucherKey = null;

	@Override
	public boolean isPageComplete()
	{
		return voucherKey != null && VoucherKey.VALIDITY_VALID == voucherKey.getValidity();
	}
}
