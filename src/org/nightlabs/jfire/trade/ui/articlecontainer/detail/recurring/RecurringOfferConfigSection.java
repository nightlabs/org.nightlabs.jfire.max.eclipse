package org.nightlabs.jfire.trade.ui.articlecontainer.detail.recurring;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.jfire.trade.recurring.RecurringOfferConfiguration;
import org.nightlabs.jfire.trade.ui.resource.Messages;


/**
 * @author Fitas Amine <!-- fitas [AT] nightlabs [DOT] de -->
 *
 */
public class RecurringOfferConfigSection extends AbstractRecurringConfigGeneralSection {

	private Button createInvoiceCheck;
	private Button createDeliveryCheck;
	private Button bookInvoiceCheck;


	public RecurringOfferConfigSection(FormPage page, Composite parent, final RecurringOfferConfigurationPageController controller)
	{

		super(page, parent, controller);
		getSection().setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.recurring.RecurringOfferConfigSection.section.text")); //$NON-NLS-1$
		XComposite checkboxContainer = new XComposite(getContainer(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		checkboxContainer.getGridLayout().numColumns = 3;
		checkboxContainer.getGridLayout().makeColumnsEqualWidth = false;
		
		createInvoiceCheck = new Button(checkboxContainer, SWT.CHECK);
		createInvoiceCheck.setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.recurring.RecurringOfferConfigSection.button.text.createInvoice")); //$NON-NLS-1$
		createInvoiceCheck.setToolTipText(""); //$NON-NLS-1$
		createInvoiceCheck.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getController().getControllerObject().setCreateInvoice(createInvoiceCheck.getSelection());
				markDirty();
			}
		});

		createDeliveryCheck = new Button(checkboxContainer, SWT.CHECK);
		createDeliveryCheck.setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.recurring.RecurringOfferConfigSection.button.text.createDeliveryNote")); //$NON-NLS-1$
		createDeliveryCheck.setToolTipText(""); //$NON-NLS-1$
		createDeliveryCheck.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getController().getControllerObject().setCreateDelivery(createDeliveryCheck.getSelection());
				markDirty();
			}
		});

		bookInvoiceCheck = new Button(checkboxContainer, SWT.CHECK);
		bookInvoiceCheck.setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.recurring.RecurringOfferConfigSection.button.text.bookInvoice")); //$NON-NLS-1$
		bookInvoiceCheck.setToolTipText(""); //$NON-NLS-1$
		bookInvoiceCheck.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getController().getControllerObject().setBookInvoice(bookInvoiceCheck.getSelection());
				markDirty();
			}
		});

	}


	@Override
	protected void updateConfigOffer(
			RecurringOfferConfiguration recurringOfferConfiguration) {

		createDeliveryCheck.setSelection(recurringOfferConfiguration.isCreateDelivery());
		createInvoiceCheck.setSelection(recurringOfferConfiguration.isCreateInvoice());
		bookInvoiceCheck.setSelection(recurringOfferConfiguration.isBookInvoice());
	}	

}
