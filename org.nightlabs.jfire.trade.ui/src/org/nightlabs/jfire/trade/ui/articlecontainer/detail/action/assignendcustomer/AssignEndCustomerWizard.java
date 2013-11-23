package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.assignendcustomer;

import java.util.Set;

import org.nightlabs.jfire.trade.id.ArticleID;
import org.nightlabs.jfire.trade.ui.legalentity.edit.LegalEntitySearchCreateWizard;

public class AssignEndCustomerWizard
extends LegalEntitySearchCreateWizard { // Delegate { // extends LegalEntitySearchCreateWizard
	private Set<ArticleID> articleIDs;

	public AssignEndCustomerWizard(Set<ArticleID> articleIDs) {
		super("", true); //$NON-NLS-1$
		this.articleIDs = articleIDs;
	}

	public Set<ArticleID> getArticleIDs() {
		return articleIDs;
	}

	// RELEGATED (to AssignCustomerWizardDelegate): To conform to the wizard-delegate-framework...
//	@Override
//	public boolean performFinish()
//	{
//		boolean superResult = super.performFinish();
//		if (!superResult)
//			return false;
//		try {
////			LegalEntity legalEntity = getLegalEntity();
//			LegalEntity legalEntity = ((ILegalEntitySearchWizard) getWizard()).getLegalEntity();
//
//			TradeManagerRemote tm = JFireEjb3Factory.getRemoteBean(TradeManagerRemote.class, Login.getLogin().getInitialContextProperties());
//			AnchorID endCustomerID = (AnchorID) JDOHelper.getObjectId(legalEntity);
//
//			tm.assignEndCustomer(endCustomerID, articleIDs);
//		} catch (Exception x) {
//			throw new RuntimeException(x);
//		}
//		return true;
//	}

}
