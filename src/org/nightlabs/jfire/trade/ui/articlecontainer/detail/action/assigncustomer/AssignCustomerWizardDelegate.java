package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.assigncustomer;

import javax.jdo.JDOHelper;

import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.base.ui.wizard.IWizardDelegate;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.base.login.ui.Login;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.TradeManagerRemote;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditor;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditorInput;
import org.nightlabs.jfire.trade.ui.legalentity.edit.LegalEntitySearchCreateWizardDelegate;
import org.nightlabs.jfire.trade.ui.legalentity.view.LegalEntityEditorView;
import org.nightlabs.jfire.transfer.id.AnchorID;

/**
 * This is the {@link IWizardDelegate} that is linked tot he {@link AssignCustomerWizard}, for use
 * throught the wizardDelegateFactory.
 *
 * @author khaireel at nightlabs dot de
 */
public class AssignCustomerWizardDelegate extends LegalEntitySearchCreateWizardDelegate {
	@Override
	public boolean performFinish() {
		boolean superResult = super.performFinish();
		if (!superResult)
			return false;
		try {
//			LegalEntity legalEntity = getLegalEntity();
			// Assumption: This delegate MUST correspond to work in tandem with the originally intended AssignCustomerWizard.
			AssignCustomerWizard wiz = (AssignCustomerWizard) getWizard();
			LegalEntity legalEntity = wiz.getLegalEntity();

			TradeManagerRemote tm = JFireEjb3Factory.getRemoteBean(TradeManagerRemote.class, Login.getLogin().getInitialContextProperties());
			AnchorID customerID = (AnchorID) JDOHelper.getObjectId(legalEntity);

			tm.assignCustomer(wiz.getOrderID(), customerID, true, null, 1);

			LegalEntityEditorView view = (LegalEntityEditorView) RCPUtil.showView(LegalEntityEditorView.ID_VIEW);
			ArticleContainerEditorInput input = new ArticleContainerEditorInput(wiz.getOrderID());
			RCPUtil.closeEditor(input, true);
			RCPUtil.openEditor(input, ArticleContainerEditor.ID_EDITOR);
			view.setSelectedLegalEntityID((AnchorID) JDOHelper.getObjectId(legalEntity), true);
		} catch (Exception x) {
			throw new RuntimeException(x);
		}
		return true;
	}
}
