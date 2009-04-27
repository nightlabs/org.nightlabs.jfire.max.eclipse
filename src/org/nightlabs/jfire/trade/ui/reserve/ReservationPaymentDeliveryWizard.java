/**
 *
 */
package org.nightlabs.jfire.trade.ui.reserve;

import java.lang.reflect.InvocationTargetException;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardPage;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.base.ui.wizard.IDynamicPathWizardListener;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.person.Person;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.Order;
import org.nightlabs.jfire.trade.TradeManagerRemote;
import org.nightlabs.jfire.trade.dao.LegalEntityDAO;
import org.nightlabs.jfire.trade.dao.OrderDAO;
import org.nightlabs.jfire.trade.id.OrderID;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.CustomerPaymentDeliveryWizard;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author daniel[at]nightlabs[dot]de
 *
 */
public class ReservationPaymentDeliveryWizard
extends CustomerPaymentDeliveryWizard
{
	/**
	 * @param personSearchText
	 * @param orderID
	 * @param transferMode
	 * @param side
	 */
	public ReservationPaymentDeliveryWizard(String personSearchText,
			OrderID orderID, byte transferMode, Side side)
	{
		super(personSearchText, orderID, transferMode, side);
	}

	protected boolean canReserve()
	{
		// check for selected person
		Person selectedPerson = getPersonSearchWizardPage().getSelectedPerson();
		if (selectedPerson == null) {
			return false;
		}
		// is the load additional data job still running
		if (getPersonSearchWizardPage().isLoadAdditionalDataJobRunning()) {
			return false;
		}

		// check can order be reserved => are all contained offers not finalized
		Order order = OrderDAO.sharedInstance().getOrder(getOrderID(), new String[] {FetchPlan.DEFAULT, Order.FETCH_GROUP_OFFERS},
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
		for (Offer offer : order.getOffers()) {
			boolean finalized = offer.isFinalized();
			if (finalized)
				return false;
		}

		// check for anonymous legal entity
		LegalEntity selectedLegalEntity = getPersonSearchWizardPage().getSelectedLegalEntity();
		if (selectedLegalEntity != null) {
			LegalEntity anonymousLegalEntity = LegalEntityDAO.sharedInstance().getAnonymousLegalEntity(new NullProgressMonitor());
			AnchorID selectedID = (AnchorID) JDOHelper.getObjectId(selectedLegalEntity);
			AnchorID anonymousID = (AnchorID) JDOHelper.getObjectId(anonymousLegalEntity);
			if (selectedID.equals(anonymousID)) {
				return false;
			}
		}

		return true;
	}

	protected boolean performReservation()
	{
		try {
			getContainer().run(false, false, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					setTransfersSuccessful(false);
					LegalEntity selectedLegalEntity = getPersonSearchWizardPage().getSelectedLegalEntity();
					if (selectedLegalEntity == null) {
						Person selectedPerson = getPersonSearchWizardPage().getSelectedPerson();
						selectedLegalEntity = createLegalEntityForPerson(selectedPerson);
					}

					if (selectedLegalEntity != null) {
						AnchorID customerID = (AnchorID) JDOHelper.getObjectId(selectedLegalEntity);
						TradeManagerRemote tm;
						try {
							tm = JFireEjb3Factory.getRemoteBean(TradeManagerRemote.class, Login.getLogin().getInitialContextProperties());
							tm.createReservation(getOrderID(), customerID);
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					}
					setTransfersSuccessful(true);
				}
			});
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public void setContainer(IWizardContainer wizardContainer) {
		super.setContainer(wizardContainer);
		if (wizardContainer instanceof DynamicPathWizardDialog) {
			DynamicPathWizardDialog dlg = (DynamicPathWizardDialog) wizardContainer;
			dlg.addListener(new IDynamicPathWizardListener(){
				@Override
				public void pageChanged(IWizardPage currentPage) {

				}
				@Override
				public void buttonPressed(int buttonId) {
					if (buttonId == ReservationWizardDialog.RESERVATION_ID) {
						performReservation();
					}
				}
			});
		}
	}

	protected ReservationWizardDialog getReservationWizardDialog()
	{
		IWizardContainer container = getContainer();
		if (container instanceof ReservationWizardDialog) {
			ReservationWizardDialog dlg = (ReservationWizardDialog) container;
			return dlg;
		}
		return null;
	}
}
