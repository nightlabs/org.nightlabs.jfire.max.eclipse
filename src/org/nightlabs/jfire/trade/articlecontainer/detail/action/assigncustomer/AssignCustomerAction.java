package org.nightlabs.jfire.trade.articlecontainer.detail.action.assigncustomer;

import org.eclipse.swt.widgets.Event;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.trade.articlecontainer.detail.action.ArticleContainerAction;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.id.OrderID;

public class AssignCustomerAction
		extends ArticleContainerAction
{
	public boolean calculateVisible()
	{
		ArticleContainerID articleContainerID = getArticleContainerActionRegistry().getActiveGeneralEditorActionBarContributor()
		.getActiveGeneralEditor().getGeneralEditorComposite().getArticleContainerID();

//		if (!(articleContainerID instanceof OrderID || articleContainerID instanceof OfferID))
		if (!(articleContainerID instanceof OrderID))
			return false;

		return true;
	}

	@Override
	public boolean calculateEnabled()
	{
		// TODO check whether the Order has a finalized Offer

		return true;
	}

	@Override
	public void runWithEvent(Event event)
	{
		ArticleContainerID articleContainerID = getArticleContainerActionRegistry().getActiveGeneralEditorActionBarContributor()
		.getActiveGeneralEditor().getGeneralEditorComposite().getArticleContainerID();

		OrderID orderID = (OrderID) articleContainerID;

		AssignCustomerWizard wizard = new AssignCustomerWizard(orderID);
		DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(wizard);
		dialog.setPageSize(600, 500);
		dialog.open();		
	}
}
