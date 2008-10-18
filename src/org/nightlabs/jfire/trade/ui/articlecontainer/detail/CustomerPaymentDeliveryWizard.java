package org.nightlabs.jfire.trade.ui.articlecontainer.detail;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.person.Person;
import org.nightlabs.jfire.prop.PropertySet;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.TradeManager;
import org.nightlabs.jfire.trade.TradeManagerUtil;
import org.nightlabs.jfire.trade.id.CustomerGroupID;
import org.nightlabs.jfire.trade.id.OrderID;
import org.nightlabs.jfire.trade.ui.legalentity.search.ExtendedPersonSearchWizardPage;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.trade.ui.transfer.wizard.CombiTransferArticleContainerWizard;
import org.nightlabs.jfire.transfer.id.AnchorID;

public class CustomerPaymentDeliveryWizard extends CombiTransferArticleContainerWizard {

	private ExtendedPersonSearchWizardPage personSearchWizardPage;
	private String personSearchText;
	private OrderID orderID;

	public CustomerPaymentDeliveryWizard(String personSearchText, OrderID orderID, byte transferMode, Side side) {
		super(orderID, transferMode);
		this.personSearchText = personSearchText;
		this.orderID = orderID;
		setWindowTitle(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.CustomerPaymentDeliveryWizard.window.title")); //$NON-NLS-1$
	}

	@Override
	public void addPages() {
		personSearchWizardPage = new ExtendedPersonSearchWizardPage(personSearchText, true, true) {
			@Override
			public void onAdditionalDataLoaded() {
				clearCustomerGroupIDs();
				addCustomerGroupID((CustomerGroupID) JDOHelper.getObjectId(getDefaultCustomerGroup()));
//			if (getSelectedLegalEntity() != null)
//				setCustomerID((AnchorID) JDOHelper.getObjectId(getSelectedLegalEntity()));

				reloadPaymentDeliveryModes();
			}
		};

		addPage(personSearchWizardPage);
		super.addPages();

//		getPaymentEntryPages().get(0);
	}

	@Override
	public boolean performFinish() {
		LegalEntity selectedLegalEntity = personSearchWizardPage.getSelectedLegalEntity();
		if (selectedLegalEntity == null) {
			Person selectedPerson = personSearchWizardPage.getSelectedPerson();
			try {
				TradeManager tradeManager = TradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
				String[] fetchGroups = new String[] { FetchPlan.DEFAULT, LegalEntity.FETCH_GROUP_DEFAULT_CUSTOMER_GROUP,	PropertySet.FETCH_GROUP_FULL_DATA };

				selectedLegalEntity = tradeManager.storePersonAsLegalEntity(selectedPerson, true, fetchGroups, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		// Assign the customer ID
		setCustomerID((AnchorID) JDOHelper.getObjectId(selectedLegalEntity));
		try {
			TradeManager tradeManager = TradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
			tradeManager.assignCustomer(orderID, getCustomerID(), false, null, -1);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return super.performFinish();
	}
}
