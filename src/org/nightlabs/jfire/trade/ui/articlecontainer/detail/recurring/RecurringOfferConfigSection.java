package org.nightlabs.jfire.trade.ui.articlecontainer.detail.recurring;

import org.nightlabs.jfire.trade.recurring.RecurringOfferConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;


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
		getSection().setText("Offer configuration");		
		getClient().getGridLayout().numColumns = 3;
		getClient().getGridLayout().makeColumnsEqualWidth = true;
		
		createInvoiceCheck = new Button(getClient(), SWT.CHECK);
		createInvoiceCheck.setText("Create Invoice");
		createInvoiceCheck.setToolTipText("");

		createDeliveryCheck = new Button(getClient(), SWT.CHECK);
		createDeliveryCheck.setText("Create Delivery");
		createDeliveryCheck.setToolTipText("");

		bookInvoiceCheck = new Button(getClient(), SWT.CHECK);
		bookInvoiceCheck.setText("Book Invoice");
		bookInvoiceCheck.setToolTipText("");

		getClient().pack();
        
	}



	@Override
	protected void updateConfigOffer(
			RecurringOfferConfiguration recurringOfferConfiguration) {

		createDeliveryCheck.setSelection(recurringOfferConfiguration.isCreateDelivery());
		createInvoiceCheck.setSelection(recurringOfferConfiguration.isCreateInvoice());
		bookInvoiceCheck.setSelection(recurringOfferConfiguration.isBookInvoice());

		getClient().pack();

	}		









}
