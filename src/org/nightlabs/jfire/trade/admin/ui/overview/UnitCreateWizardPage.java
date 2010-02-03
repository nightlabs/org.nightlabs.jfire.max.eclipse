package org.nightlabs.jfire.trade.admin.ui.overview;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.base.ui.wizard.WizardHopPage;

public class UnitCreateWizardPage
extends WizardHopPage
{
	//GUI
	private I18nTextEditor unitSymbolTextEditor;
	private I18nTextEditor unitNameTextEditor;
	
	private Spinner decimalDigitSpinner;

	public UnitCreateWizardPage() {
		super(UnitCreateWizardPage.class.getName(), "Create new unit");
		setDescription("Please enter the symbol & name of the new unit.");
	}

	@Override
	public Control createPageContents(Composite parent) {
		XComposite mainComposite = new XComposite(parent, SWT.NONE, LayoutMode.TOP_BOTTOM_WRAPPER, LayoutDataMode.GRID_DATA);

		new Label(mainComposite, SWT.NONE).setText("Unit Symbol: ");
		unitSymbolTextEditor = new I18nTextEditor(mainComposite);
		unitSymbolTextEditor.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				getContainer().updateButtons();
			}
		});

		new Label(mainComposite, SWT.NONE).setText("Unit Name: ");
		unitNameTextEditor = new I18nTextEditor(mainComposite, unitSymbolTextEditor.getLanguageChooser());
		unitNameTextEditor.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				getContainer().updateButtons();
			}
		});
		
		new Label(mainComposite, SWT.NONE).setText("Decimal Digit Count: ");
		decimalDigitSpinner = new Spinner(mainComposite, SWT.BORDER);
		decimalDigitSpinner.setDigits(0);
		decimalDigitSpinner.setMaximum(100);
		
		return mainComposite;
	}

	
	@Override
	public void onShow() {
		unitSymbolTextEditor.setFocus();
	}

	@Override
	public boolean canFlipToNextPage() {
		return false;
	}

	@Override
	public boolean isPageComplete() {
		if (unitSymbolTextEditor == null || unitNameTextEditor == null)
			return false; // If we didn't even create our UI yet, we're definitely not complete.

		boolean result = true;
		setErrorMessage(null);

		if (unitSymbolTextEditor.getEditText().equals("") || 
				unitSymbolTextEditor.getI18nText().getText() == null) { //$NON-NLS-1$
			result = false;
		}

		if (unitNameTextEditor.getEditText().equals("") || 
				unitNameTextEditor.getI18nText().getText() == null) { //$NON-NLS-1$
			result = false;
		}

		return result;
	}
	
	public I18nTextEditor getUnitNameTextEditor() {
		return unitNameTextEditor;
	}
	
	public I18nTextEditor getUnitSymbolTextEditor() {
		return unitSymbolTextEditor;
	}
	
	public int getDecimalDigits() {
		return decimalDigitSpinner.getDigits();
	}
}