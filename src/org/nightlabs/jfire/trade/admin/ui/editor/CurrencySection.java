package org.nightlabs.jfire.trade.admin.ui.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.entity.editor.EntityEditorUtil;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;

/**
 *
 * @author vince
 *
 */

public class CurrencySection extends ToolBarSectionPart {

	private CurrencyEditorPageController controller;

	private Label currencyIdLabel;
	private Label symbolLabel;
	private Label decimalDigitCountLabel;
	private Text  symbolShowText;
	private Text  currencyIdText;
	private Spinner decimalDigitCountShowSpinner;
	private Currency currency;
	private boolean ignoreModifyEvents = false;




	public CurrencySection(IFormPage page, Composite parent,final CurrencyEditorPageController controller ) {
		super(page, parent, ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE, "Currency");
		this.controller = controller;
		getSection().setExpanded(true);
		createClient(getSection(), page.getEditor().getToolkit());

	}


	@Override
	public boolean setFormInput(Object input) {
		this.currency=(Currency)input;

		return super.setFormInput(input);
	}

	@Override
	public void refresh() {
		ignoreModifyEvents = true;
		try {
			if (currency == null)
				return; // data not yet loaded => silently ignore

			// put data from this.currency into UI
			symbolShowText.setText(currency.getCurrencySymbol());

			decimalDigitCountShowSpinner.setSelection(currency.getDecimalDigitCount());

			super.refresh();
		} finally {
			ignoreModifyEvents = false;
		}
	}

	@Override
	public void commit(boolean onSave) {
		super.commit(onSave);

		currency.setCurrencySymbol(symbolShowText.getText());
		currency.setDecimalDigitCount(decimalDigitCountShowSpinner.getSelection());
		controller.fireModifyEvent(null, currency, false);


	}

	protected void createClient(Section section, FormToolkit toolkit) {

		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Composite container = EntityEditorUtil.createCompositeClient(toolkit, section, 1);
		createCurrency(container,2);
	}

	private void createCurrency(Composite container, int span){

		createCurrencyIdLabel( container,1);
		createcurrencyIdShowText(container);
		createsymbolLabel(container,1);
		createsymbolShowText(container);
		createdecimalDigitCountLabel(container,1);
		createdecimalDigitCountShowText(container);


	}

	private void createdecimalDigitCountShowText(Composite container){

		Composite page = new XComposite(container, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		decimalDigitCountShowSpinner = new Spinner(page, SWT.NONE);
		decimalDigitCountShowSpinner.setMinimum(0);
		decimalDigitCountShowSpinner.setMaximum(Integer.MAX_VALUE);
		decimalDigitCountShowSpinner.setSelection(2); // most currencies in the world have 2 decimal digits, thus using this as default.
		decimalDigitCountShowSpinner.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		decimalDigitCountShowSpinner.addModifyListener(new ModifyListener(){

			@Override
			public void modifyText(ModifyEvent e) {
				if (ignoreModifyEvents)
					return;

 			markDirty();

			}

		});

	}

	private void createcurrencyIdShowText(Composite container){
		currencyIdText = new Text(container, XComposite.getBorderStyle(container));
		currencyIdText.setText(controller.getControllerObject().getCurrencyID());
		currencyIdText.setEditable(false);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		currencyIdText.setLayoutData(gd);
		currencyIdText.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent e) {
				if (ignoreModifyEvents)
					return;

				markDirty();
			}
		});
	}

	private void createsymbolShowText(Composite container){
		symbolShowText = new Text(container, XComposite.getBorderStyle(container));
		symbolShowText.setText(controller.getControllerObject().getCurrencySymbol());
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		symbolShowText.setLayoutData(gd);
		symbolShowText.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent e) {
				if (ignoreModifyEvents)
					return;

				markDirty();
			}
		});
	}

	private void createCurrencyIdLabel(Composite container, int span){
		currencyIdLabel=new Label(container, SWT.LEFT);
		currencyIdLabel.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.CurrencySection.label.currencyid.text"));

		GridData grid=new GridData(GridData.FILL_HORIZONTAL);
		grid.horizontalSpan=span;
		currencyIdLabel.setLayoutData(grid);
	}

	private void createsymbolLabel(Composite container, int span){
		symbolLabel=new Label(container, SWT.LEFT);
		symbolLabel.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.CurrencySection.label.currencysymbol.text"));

		GridData grid=new GridData(GridData.FILL_HORIZONTAL);
		grid.horizontalSpan=span;
		symbolLabel.setLayoutData(grid);
	}

	private void createdecimalDigitCountLabel(Composite container, int span){
		decimalDigitCountLabel=new Label(container, SWT.LEFT);
		decimalDigitCountLabel.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.CurrencySection.label.decimaldigitcount.text"));

		GridData grid=new GridData(GridData.FILL_HORIZONTAL);
		grid.horizontalSpan=span;
		decimalDigitCountLabel.setLayoutData(grid);
	}

}
