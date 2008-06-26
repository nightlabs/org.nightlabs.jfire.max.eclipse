package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.assigncustomer;

import javax.security.auth.login.LoginException;

import org.eclipse.swt.widgets.Event;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.Order;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.id.OrderID;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.IArticleContainerEditor;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.ArticleContainerAction;

public class AssignCustomerAction
extends ArticleContainerAction
{
	public boolean calculateVisible()
	{
		String owner =null;		
		IArticleContainerEditor editor = getArticleContainerActionRegistry().getActiveArticleContainerEditorActionBarContributor().getActiveArticleContainerEditor();

		if (editor == null || editor.getArticleContainerEditorComposite().getArticleContainer() == null)
			return false;
		ArticleContainerID articleContainerID = editor.getArticleContainerEditorComposite().getArticleContainerID();
		try {
			owner = Login.getLogin().getOrganisationID();
		} catch (LoginException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // TODO make this nicer - e.g. by using a DAO and maybe restructuring this whole wizard		 

		String vendor = editor.getArticleContainerEditorComposite().getArticleContainer().getVendor().getAnchorID();
		if (!(articleContainerID instanceof OrderID) ||!vendor.equals(owner))
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
