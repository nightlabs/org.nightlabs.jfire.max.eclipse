package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.assigncustomer;

import javax.jdo.JDOHelper;

import org.eclipse.swt.widgets.Event;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.security.SecurityReflector;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.Order;
import org.nightlabs.jfire.trade.OrganisationLegalEntity;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.id.OrderID;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.IArticleContainerEditor;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.ArticleContainerAction;
import org.nightlabs.jfire.transfer.id.AnchorID;

public class AssignCustomerAction
extends ArticleContainerAction
{
	public boolean calculateVisible()
	{
		IArticleContainerEditor editor = getArticleContainerActionRegistry().getActiveArticleContainerEditorActionBarContributor().getActiveArticleContainerEditor();

		if (editor == null || editor.getArticleContainerEditorComposite().getArticleContainer() == null)
			return false;
		
		
		ArticleContainerID articleContainerID = editor.getArticleContainerEditorComposite().getArticleContainerID();
		if (!(articleContainerID instanceof OrderID))
			return false;
		
		AnchorID localOrgID = AnchorID.create(
				SecurityReflector.getUserDescriptor().getOrganisationID(), 
				OrganisationLegalEntity.ANCHOR_TYPE_ID_LEGAL_ENTITY, 
				OrganisationLegalEntity.class.getName());
		
		if (!localOrgID.equals(editor.getArticleContainerEditorComposite().getArticleContainer().getVendorID())) 
			return false;
		
		
		return true;
	}

	/**
	 * Returns <code>true</code> if all {@link Offer}s of the {@link Order} are not finalized and <code>false</code> otherwise. 
	 * @return <code>true</code> if all {@link Offer}s of the {@link Order} are not finalized and <code>false</code> otherwise.
	 */
	@Override
	public boolean calculateEnabled()
	{		
		ArticleContainer articleContainer = getArticleContainer();
		if (articleContainer instanceof Order) {
			Order order = (Order) articleContainer;

			for (Offer offer : order.getOffers())
				if (offer.isFinalized())
					return false;

			return true;
		}

		return false;
	}

	@Override
	public void runWithEvent(Event event)
	{
		ArticleContainerID articleContainerID = getArticleContainerActionRegistry().getActiveArticleContainerEditorActionBarContributor()
		.getActiveArticleContainerEditor().getArticleContainerEditorComposite().getArticleContainerID();

		OrderID orderID = (OrderID) articleContainerID;

		AssignCustomerWizard wizard = new AssignCustomerWizard(orderID);
		DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(wizard);
		dialog.setPageSize(600, 500);
		dialog.open();
	}
}
