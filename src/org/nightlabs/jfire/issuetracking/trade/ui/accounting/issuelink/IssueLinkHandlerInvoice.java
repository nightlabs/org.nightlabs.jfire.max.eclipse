/**
 * 
 */
package org.nightlabs.jfire.issuetracking.trade.ui.accounting.issuelink;

import java.util.Collection;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.eclipse.swt.graphics.Image;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.ObjectIDUtil;
import org.nightlabs.jfire.accounting.Invoice;
import org.nightlabs.jfire.accounting.id.InvoiceID;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issuetracking.trade.ui.IssueTrackingTradePlugin;
import org.nightlabs.jfire.issuetracking.trade.ui.store.issuelink.IssueLinkHandlerDeliveryNote;
import org.nightlabs.jfire.issuetracking.ui.issuelink.AbstractIssueLinkHandler;
import org.nightlabs.jfire.trade.ui.articlecontainer.InvoiceDAO;
import org.nightlabs.jfire.trade.ui.overview.invoice.action.EditInvoiceAction;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author chairatk
 *
 */
public class IssueLinkHandlerInvoice 
extends AbstractIssueLinkHandler<InvoiceID, Invoice>
{
	@Override
	public String getLinkedObjectName(IssueLink issueLink, Invoice linkedObject) {
		InvoiceID invoiceID = (InvoiceID) issueLink.getLinkedObjectID();
		return String.format(
				"Invoice  %s",
				(issueLink.getLinkedObjectID() == null ? "" : invoiceID.invoiceIDPrefix + '/' + ObjectIDUtil.longObjectIDFieldToString(invoiceID.invoiceID)));
	}

	@Override
	public Image getLinkedObjectImage(IssueLink issueLink, Invoice linkedObject) {
		return SharedImages.getSharedImageDescriptor(
				IssueTrackingTradePlugin.getDefault(), 
				IssueLinkHandlerDeliveryNote.class, 
				"LinkObject").createImage();
	}


	@Override
	public void openLinkedObject(IssueLink issueLink, InvoiceID objectID) {
		EditInvoiceAction editAction = new EditInvoiceAction();
		editAction.setArticleContainerID(objectID);
		editAction.run();
	}

	@Override
	protected Collection<Invoice> _getLinkedObjects(
			Set<IssueLink> issueLinks, Set<InvoiceID> linkedObjectIDs,
			ProgressMonitor monitor)
	{
		return InvoiceDAO.sharedInstance().getInvoices(
				linkedObjectIDs,
				new String[] { FetchPlan.DEFAULT }, // TODO do we need more?
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				monitor);
	}
}
