package org.nightlabs.jfire.trade.ui.articlecontainer.detail.invoice;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.trade.ui.resource.Messages;

/**
 * the Editor page which lists all the payments of an Invoice.
 * 
 * @author Fitas Amine - fitas at NightLabs dot de
 */
public class InvoicePaymentsListSection extends ToolBarSectionPart
{

	private InvoicePaymentsListPageController controller;
	private InvoicePaymentsListTable invoicePaymentsListTable;

	/**
	 * Creates a new instance of the InvoicePaymentsListSection.
	 */
	public InvoicePaymentsListSection(IFormPage page, Composite parent, final InvoicePaymentsListPageController controller) {
		super(page, parent, ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR, Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.invoice.InvoicePaymentsListSection.SectionTitle"));  //$NON-NLS-1$
		this.controller = controller;

		getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		getSection().setLayout(new GridLayout());

		XComposite client = new XComposite(getSection(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		client.getGridLayout().numColumns = 1;	

		invoicePaymentsListTable = new InvoicePaymentsListTable(client, SWT.NONE);
		invoicePaymentsListTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		updateToolBarManager();
		getSection().setClient(client);
	}		


	public void setPayableObjectID(final ObjectID payableObjectID)
	{
		invoicePaymentsListTable.setPayableObjectID(controller.getArticleContainerID());
	}
}
