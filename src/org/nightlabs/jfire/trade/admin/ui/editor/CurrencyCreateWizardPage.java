package org.nightlabs.jfire.trade.admin.ui.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.jdo.ObjectIDUtil;
import org.nightlabs.jfire.accounting.Currency;

/**
 *
 * @author vince
 *
 */

public class CurrencyCreateWizardPage
extends WizardHopPage
{
	private Label currencyIdLabel;
	private Label currencySymbolLabel;
	private Label digitalDigitCountLabel;
	private Text currencyIdText;
	private Text currencySymbolText;
	private Spinner decimalDigitCountSpinner;

	public CurrencyCreateWizardPage() {
		super(CurrencyCreateWizardPage.class.getName(), "Create new currency");
		setDescription("Please specify the properties of the new currency to be created.");
	}

	@Override
	public Control createPageContents(final Composite parent) {
		Composite page = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);

		ModifyListener modifyListenerCheckInput = new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent e) {
				checkInput();
			}
		};

		currencyIdLabel = new Label(page, SWT.NONE);
		currencyIdLabel.setText("Currency ID (e.g. \"EUR\")");
		currencyIdText = new Text(page, SWT.SINGLE | SWT.BORDER);
		currencyIdText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		currencyIdText.addModifyListener(modifyListenerCheckInput);

		currencySymbolLabel = new Label(page,SWT.NONE);
		currencySymbolLabel.setText("Currency symbol (e.g. \"€\")");
		currencySymbolText = new Text(page, SWT.SINGLE | SWT.BORDER);
		currencySymbolText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		currencySymbolText.addModifyListener(modifyListenerCheckInput);

		digitalDigitCountLabel = new Label(page, SWT.NONE);
		digitalDigitCountLabel.setText("Number of decimal digits");
		decimalDigitCountSpinner = new Spinner(page, SWT.BORDER);
		decimalDigitCountSpinner.setMinimum(0);
		decimalDigitCountSpinner.setMaximum(Integer.MAX_VALUE);
		decimalDigitCountSpinner.setSelection(2); // most currencies in the world have 2 decimal digits, thus using this as default.
		decimalDigitCountSpinner.addModifyListener(modifyListenerCheckInput);

		new Label(page, SWT.NONE).setText("Note: Once the decimal digit count has been stored. You won't be able to change it again.");

		setPageComplete(false);

		return page;
	}

	@Override
	public void setErrorMessage(final String newMessage) {
		super.setErrorMessage(newMessage);
		setPageComplete(newMessage == null);
	}

	private void checkInput() {
		if (currencyIdText.getText().isEmpty()) {
			setErrorMessage("A currency ID is required!");
		} else if (!ObjectIDUtil.isValidIDString(currencyIdText.getText())) {
			setErrorMessage(
					String.format("The currency ID \"%s\" is not a valid identifier!", currencyIdText.getText())
			);
		} else if (currencySymbolText.getText().isEmpty()) {
			setErrorMessage("A currency symbol is required!");
		} else {
			setErrorMessage(null);

			if (currencyIdText.getText().length() != 3) {
				setMessage(
						String.format(
								"Usually, an ISO-4217 currency ID has exactly 3 characters. The value you entered, contains %2$d characters. Are you sure that your currency ID \"%1$s\" is correct?",
								currencyIdText.getText(),
								currencyIdText.getText().length()
						),
						WARNING
				);
			} else if (!currencyIdText.getText().matches("[A-Z]+")) {
				setMessage(
						String.format(
								"Usually, an ISO-4217 currency ID consists only of capital ASCII letters ('A' to 'Z'). Are you sure that your currency ID \"%s\" is correct?",
								currencyIdText.getText()
						),
						WARNING
				);
			} else if (currencySymbolText.getText().contains(" ") || currencySymbolText.getText().contains("\t")) {
				setMessage(
						String.format(
								"The currency symbol \"%s\" contains white spaces! Are you sure that this is correct?",
								currencySymbolText.getText()
						),
						WARNING
				);
			} else {
				setMessage(null, WARNING);
			}
		}
	}

	public Currency createCurrency() {
		return new Currency(currencyIdText.getText(), currencySymbolText.getText(), decimalDigitCountSpinner.getSelection());
	}
}
