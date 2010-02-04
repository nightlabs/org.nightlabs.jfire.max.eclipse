package org.nightlabs.jfire.trade.admin.ui.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.entity.editor.EntityEditorUtil;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;

/**
 *
 * @author vince
 *
 */
public class CurrencySection 
extends ToolBarSectionPart 
{
	private CurrencyEditorPageController controller;

	private Label currencyIdLabel;
	private Label symbolLabel;
	private Text  symbolShowText;
	private Text  currencyIdText;
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

			super.refresh();
		} finally {
			ignoreModifyEvents = false;
		}
	}

	@Override
	public void commit(boolean onSave) {
		super.commit(onSave);

		currency.setCurrencySymbol(symbolShowText.getText());
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
}