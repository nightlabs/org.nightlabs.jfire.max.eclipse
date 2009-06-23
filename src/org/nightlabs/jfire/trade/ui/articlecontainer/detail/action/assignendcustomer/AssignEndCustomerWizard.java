package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.assignendcustomer;

import java.util.Set;

import javax.jdo.JDOHelper;

import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.TradeManagerRemote;
import org.nightlabs.jfire.trade.id.ArticleID;
import org.nightlabs.jfire.trade.ui.legalentity.edit.LegalEntitySearchCreateWizard;
import org.nightlabs.jfire.transfer.id.AnchorID;

public class AssignEndCustomerWizard
		extends LegalEntitySearchCreateWizard
{

	private Set<ArticleID> articleIDs;

	public AssignEndCustomerWizard(Set<ArticleID> articleIDs)
	{
		super("", true); //$NON-NLS-1$
		this.articleIDs = articleIDs;
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
			AnchorID endCustomerID = (AnchorID) JDOHelper.getObjectId(legalEntity);

			tm.assignEndCustomer(endCustomerID, articleIDs);
		} catch (Exception x) {
			throw new RuntimeException(x);
		}
		return true;
	}

}
