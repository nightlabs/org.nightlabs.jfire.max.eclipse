/**
 * 
 */
package org.nightlabs.jfire.issuetracking.trade.ui.accounting.issuelink;

import org.eclipse.swt.graphics.Image;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jdo.ObjectIDUtil;
import org.nightlabs.jfire.accounting.id.InvoiceID;
import org.nightlabs.jfire.issuetracking.trade.ui.IssueTrackingTradePlugin;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkHandler;
import org.nightlabs.jfire.trade.ui.overview.invoice.action.EditInvoiceAction;

/**
 * @author chairatk
 *
 */
public class IssueInvoiceLinkHandler 
implements IssueLinkHandler 
{

	public String getLinkObjectDescription(ObjectID objectID) {
		InvoiceID invoiceID = (InvoiceID) objectID;
		return String.format(
				"Invoice  %s",
				(invoiceID == null ? "" : invoiceID.invoiceIDPrefix + '/' + ObjectIDUtil.longObjectIDFieldToString(invoiceID.invoiceID)));
	}

	public Image getLinkObjectImage(ObjectID objectID) {
		return SharedImages.getSharedImageDescriptor(
				IssueTrackingTradePlugin.getDefault(), 
				IssueInvoiceLinkHandler.class, 
				"LinkObject").createImage();
	}

	public void openLinkObject(ObjectID objectID) {
		EditInvoiceAction editAction = new EditInvoiceAction();
		editAction.setArticleContainerID(objectID);
		editAction.run();
	}

}
