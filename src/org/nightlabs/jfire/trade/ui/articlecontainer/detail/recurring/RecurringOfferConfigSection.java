package org.nightlabs.jfire.trade.ui.articlecontainer.detail.recurring;

import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;

/**
 * @author Fitas Amine <!-- fitas [AT] nightlabs [DOT] de -->
 *
 */
public class RecurringOfferConfigSection extends ToolBarSectionPart {

	Button createInvoiceCheck;
	Button createDeliveryCheck;
	
	
	public RecurringOfferConfigSection(FormPage page, Composite parent, final RecurringOfferConfigurationPageController controller)
	{
		super(
				page, parent, ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR,
				"Offer configuration"
		);
		
		XComposite client = new XComposite(getSection(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		client.getGridLayout().numColumns = 2; // TODO when adding more UI, we might want to switch to 1 and use wrapper-composites
		
		createInvoiceCheck = new Button(client, SWT.CHECK);
		createInvoiceCheck.setText("Create Invoice");
		createInvoiceCheck.setToolTipText("");

		createDeliveryCheck = new Button(client, SWT.CHECK);
		createDeliveryCheck.setText("Create Delivery");
		createDeliveryCheck.setToolTipText("");

		


	}







}
