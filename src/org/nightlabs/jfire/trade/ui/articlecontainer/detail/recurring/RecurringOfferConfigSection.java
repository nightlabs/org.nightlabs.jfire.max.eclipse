package org.nightlabs.jfire.trade.ui.articlecontainer.detail.recurring;

import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;

/**
 * @author Fitas Amine <!-- fitas [AT] nightlabs [DOT] de -->
 *
 */
public class RecurringOfferConfigSection extends ToolBarSectionPart {

	private Button createInvoiceCheck;
	private Button createDeliveryCheck;
	private Button bookInvoiceCheck;
	private RecurringOfferConfigurationPageController controller;



	public RecurringOfferConfigSection(FormPage page, Composite parent, final RecurringOfferConfigurationPageController controller)
	{
		super(page, parent, ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR,
		"Offer configuration");

		this.controller = controller;
		getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		getSection().setLayout(new GridLayout());

		XComposite client = new XComposite(getSection(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		client.getGridLayout().numColumns = 3; 
		createInvoiceCheck = new Button(client, SWT.CHECK);
		createInvoiceCheck.setText("Create Invoice");
		createInvoiceCheck.setToolTipText("");
		createInvoiceCheck.setSelection(controller.getControllerObject().isCreateInvoice());

		createDeliveryCheck = new Button(client, SWT.CHECK);
		createDeliveryCheck.setText("Create Delivery");
		createDeliveryCheck.setToolTipText("");
		createDeliveryCheck.setSelection(controller.getControllerObject().isCreateDelivery());

		bookInvoiceCheck = new Button(client, SWT.CHECK);
		bookInvoiceCheck.setText("Book Invoice");
		bookInvoiceCheck.setToolTipText("");
		bookInvoiceCheck.setSelection(controller.getControllerObject().isBookInvoice());


		getSection().setClient(client);

	}







}
