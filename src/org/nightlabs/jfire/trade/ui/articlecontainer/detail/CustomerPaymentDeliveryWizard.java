package org.nightlabs.jfire.trade.ui.articlecontainer.detail;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.base.ui.person.search.PersonSearchWizardPage;
import org.nightlabs.jfire.person.Person;
import org.nightlabs.jfire.prop.PropertySet;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.TradeManager;
import org.nightlabs.jfire.trade.TradeManagerUtil;
import org.nightlabs.jfire.trade.id.CustomerGroupID;
import org.nightlabs.jfire.trade.id.OrderID;
import org.nightlabs.jfire.trade.ui.transfer.wizard.CombiTransferArticleContainerWizard;

public class CustomerPaymentDeliveryWizard extends CombiTransferArticleContainerWizard {

	private PersonSearchWizardPage personSearchWizardPage;
	private String personSearchText;
	private OrderID orderID;

	public CustomerPaymentDeliveryWizard(String personSearchText, OrderID orderID, byte transferMode, Side side) {
		super(orderID, transferMode, side);
		this.personSearchText = personSearchText;
		this.orderID = orderID;
	}
	
	@Override
	public void addPages() {
		personSearchWizardPage = new PersonSearchWizardPage(personSearchText) {
			@Override
			public void onNext() {				
				Person selectedPerson = personSearchWizardPage.getSelectedPerson();
				LegalEntity legalEntity;
				try {
					TradeManager tradeManager = TradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
					String[] fetchGroups = new String[] { FetchPlan.DEFAULT, LegalEntity.FETCH_GROUP_DEFAULT_CUSTOMER_GROUP,	PropertySet.FETCH_GROUP_FULL_DATA };
					
					legalEntity = tradeManager.storePersonAsLegalEntity(selectedPerson, true, fetchGroups, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				
				// Since we are only dealing with a single ArticleContainer here, we can safely remove all existing
				// customer group IDs and then add the one of the selected user.
				
				clearCustomerGroupIDs();				
				addCustomerGroupID((CustomerGroupID) JDOHelper.getObjectId(legalEntity.getDefaultCustomerGroup()));
				
				reloadPaymentDeliveryModes();
			}
		};
		
		addPage(personSearchWizardPage);	
		super.addPages();
	}
}
