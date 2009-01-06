/**
 *
 */
package org.nightlabs.jfire.trade.ui.articlecontainer.detail;

import java.lang.reflect.InvocationTargetException;

import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Button;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.base.ui.wizard.IDynamicPathWizardListener;
import org.nightlabs.jfire.base.JFireEjbFactory;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.TradeManager;
import org.nightlabs.jfire.trade.id.CustomerGroupID;
import org.nightlabs.jfire.trade.id.OrderID;
import org.nightlabs.jfire.trade.ui.legalentity.search.ExtendedPersonSearchWizardPage;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.transfer.id.AnchorID;

/**
 * @author daniel[at]nightlabs[dot]de
 *
 */
public class QuickSaleReserveAndSellWizard
extends CustomerPaymentDeliveryWizard
{
	enum Mode {
		SELL,
		RESERVE
	}

	private Mode mode = Mode.RESERVE;

	/**
	 * @param personSearchText
	 * @param orderID
	 * @param transferMode
	 * @param side
	 */
	public QuickSaleReserveAndSellWizard(String personSearchText,
			OrderID orderID, byte transferMode, Side side) {
		super(personSearchText, orderID, transferMode, side);
	}

	@Override
	public boolean performFinish()
	{
		try {
			getContainer().run(false, false, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					setTransfersSuccessful(false);
					boolean success = false;
					switch (mode) {
						case SELL:
							success = QuickSaleReserveAndSellWizard.super.performFinish();
							break;
						case RESERVE:
							success = performReserveFinish();
							break;
						default:
							success = QuickSaleReserveAndSellWizard.super.performFinish();
							break;
					}
					setTransfersSuccessful(success);
				}
			});
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	protected boolean performReserveFinish()
	{
		LegalEntity selectedLegalEntity = getPersonSearchWizardPage().getSelectedLegalEntity();
		if (selectedLegalEntity != null) {
			AnchorID customerID = (AnchorID) JDOHelper.getObjectId(selectedLegalEntity);
			TradeManager tm;
			try {
				tm = JFireEjbFactory.getBean(TradeManager.class, Login.getLogin().getInitialContextProperties());
				tm.createReservation(getOrderID(), customerID);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return true;
	}

	@Override
	public void addPages()
	{
		super.addPages();

		final DynamicPathWizardDialog dlg = getDynamicWizardDialog();
		if (dlg != null) {
			dlg.addListener(new IDynamicPathWizardListener(){
				@Override
				public void buttonPressed(int buttonId)
				{
//					IWizardPage currentPage = dlg.getCurrentPage();
//					if (currentPage.equals(getPersonSearchWizardPage())) {
//						setMode(Mode.RESERVE);
//					}
//					else {
//						setMode(Mode.SELL);
//					}
				}
				/* (non-Javadoc)
				 * @see org.nightlabs.base.ui.wizard.IDynamicPathWizardListener#pageChanged(org.eclipse.jface.wizard.IWizardPage)
				 */
				@Override
				public void pageChanged(IWizardPage currentPage) {
					if (currentPage.equals(getPersonSearchWizardPage())) {
						setMode(Mode.RESERVE);
					}
					else {
						setMode(Mode.SELL);
					}
				}
			});
		}
	}

	protected void setButtonTexts(Mode mode)
	{
		DynamicPathWizardDialog dlg = getDynamicWizardDialog();
		if (dlg != null) {
			Button finishButton = dlg.getButton(IDialogConstants.FINISH_ID);
			if (finishButton != null) {
				if (mode == Mode.RESERVE) {
					finishButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.QuickSaleReserveAndSellWizard.button.reserve.text")); //$NON-NLS-1$
				}
				if (mode == Mode.SELL) {
					finishButton.setText(IDialogConstants.FINISH_LABEL);
				}
			}
			Button nextButton = dlg.getButton(IDialogConstants.NEXT_ID);
			if (nextButton != null) {
				if (mode == Mode.RESERVE) {
					String arrow = ">"; //$NON-NLS-1$
					nextButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.QuickSaleReserveAndSellWizard.button.sell.text") + " " + arrow); //$NON-NLS-1$ //$NON-NLS-2$
					getShell().setDefaultButton(nextButton);
				}
				if (mode == Mode.SELL) {
					nextButton.setText(IDialogConstants.NEXT_LABEL);
				}
			}
		}
	}

	protected void setMode(Mode mode) {
		this.mode = mode;
		setButtonTexts(mode);

	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.ui.articlecontainer.detail.CustomerPaymentDeliveryWizard#createPersonSearchWizardPage()
	 */
	@Override
	protected ExtendedPersonSearchWizardPage createPersonSearchWizardPage() {
		QuickSalePersonSearchWizardPage personPage = new QuickSalePersonSearchWizardPage(getPersonSearchText(), true, true) {
			@Override
			public void onAdditionalDataLoaded() {
				clearCustomerGroupIDs();
				addCustomerGroupID((CustomerGroupID) JDOHelper.getObjectId(getDefaultCustomerGroup()));
				reloadPaymentDeliveryModes();
			}
		};
		return personPage;
	}

	@Override
	public boolean canFinish()
	{
		if (getDynamicWizardDialog().getCurrentPage().equals(getPersonSearchWizardPage()))
		{
			return getPersonSearchWizardPage().isPageComplete();
		}
		return super.canFinish();
	}
}
