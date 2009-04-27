package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.assigncustomer;

import javax.jdo.JDOHelper;

import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.TradeManagerRemote;
import org.nightlabs.jfire.trade.id.OrderID;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditor;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditorInput;
import org.nightlabs.jfire.trade.ui.legalentity.edit.LegalEntitySearchCreateWizard;
import org.nightlabs.jfire.trade.ui.legalentity.view.LegalEntityEditorView;
import org.nightlabs.jfire.transfer.id.AnchorID;

public class AssignCustomerWizard
		extends LegalEntitySearchCreateWizard
{

	private OrderID orderID;

	public AssignCustomerWizard(OrderID orderID)
	{
		super("", true); //$NON-NLS-1$
		this.orderID = orderID;
	}

	@Override
	public boolean performFinish()
	{
		boolean superResult = super.performFinish();
		if (!superResult)
			return false;
		try {
			LegalEntity legalEntity = getLegalEntity();

			TradeManagerRemote tm = JFireEjb3Factory.getRemoteBean(TradeManagerRemote.class, Login.getLogin().getInitialContextProperties());
			AnchorID customerID = (AnchorID) JDOHelper.getObjectId(legalEntity);

			tm.assignCustomer(orderID, customerID, true, null, 1);

			LegalEntityEditorView view = (LegalEntityEditorView) RCPUtil.showView(LegalEntityEditorView.ID_VIEW);
			ArticleContainerEditorInput input = new ArticleContainerEditorInput(orderID);
			RCPUtil.closeEditor(input, true);
			RCPUtil.openEditor(input, ArticleContainerEditor.ID_EDITOR);
			view.setSelectedLegalEntityID((AnchorID) JDOHelper.getObjectId(legalEntity));
		} catch (Exception x) {
			throw new RuntimeException(x);
		}
		return true;
	}

}
