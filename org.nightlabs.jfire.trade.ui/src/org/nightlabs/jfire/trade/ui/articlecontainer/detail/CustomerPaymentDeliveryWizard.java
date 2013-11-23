package org.nightlabs.jfire.trade.ui.articlecontainer.detail;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.base.login.ui.Login;
import org.nightlabs.jfire.person.Person;
import org.nightlabs.jfire.prop.PropertySet;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.TradeManagerRemote;
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
		addPersonSearchPage();
		super.addPages();
	}

	protected ExtendedPersonSearchWizardPage createPersonSearchWizardPage()
	{
		ExtendedPersonSearchWizardPage personPage = new ExtendedPersonSearchWizardPage(personSearchText, true, true) {
			@Override
			public void onAdditionalDataLoaded() {
				clearCustomerGroupIDs();
				addCustomerGroupID((CustomerGroupID) JDOHelper.getObjectId(getDefaultCustomerGroup()));
				reloadPaymentDeliveryModes();
			}
		};
		return personPage;
	}

	protected void addPersonSearchPage()
	{
		personSearchWizardPage = createPersonSearchWizardPage();
		addPage(personSearchWizardPage);
	}

	@Override
	public boolean performFinish() {
		LegalEntity selectedLegalEntity = personSearchWizardPage.getSelectedLegalEntity();
		if (selectedLegalEntity == null) {
			Person selectedPerson = personSearchWizardPage.getSelectedPerson();
			selectedLegalEntity = createLegalEntityForPerson(selectedPerson);
		}
		// Assign the customer ID
		setCustomerID((AnchorID) JDOHelper.getObjectId(selectedLegalEntity));
		try {
			TradeManagerRemote tradeManager = JFireEjb3Factory.getRemoteBean(TradeManagerRemote.class, Login.getLogin().getInitialContextProperties());
			tradeManager.assignCustomer(orderID, getCustomerID(), false, null, -1);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return super.performFinish();
	}

	protected LegalEntity createLegalEntityForPerson(Person person) {
		try {
			TradeManagerRemote tradeManager = JFireEjb3Factory.getRemoteBean(TradeManagerRemote.class, Login.getLogin().getInitialContextProperties());
			String[] fetchGroups = new String[] { FetchPlan.DEFAULT, LegalEntity.FETCH_GROUP_DEFAULT_CUSTOMER_GROUP,	PropertySet.FETCH_GROUP_FULL_DATA };
			return tradeManager.storePersonAsLegalEntity(person, true, fetchGroups, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ExtendedPersonSearchWizardPage getPersonSearchWizardPage() {
		return personSearchWizardPage;
	}

	protected OrderID getOrderID() {
		return orderID;
	}

	protected String getPersonSearchText() {
		return personSearchText;
	}
}
