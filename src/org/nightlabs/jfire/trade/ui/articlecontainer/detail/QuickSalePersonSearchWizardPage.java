/**
 *
 */
package org.nightlabs.jfire.trade.ui.articlecontainer.detail;

import org.eclipse.jface.wizard.IWizard;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.QuickSaleReserveAndSellWizard.Mode;
import org.nightlabs.jfire.trade.ui.legalentity.search.ExtendedPersonSearchWizardPage;

/**
 * @author daniel[at]nightlabs[dot]de
 *
 */
public class QuickSalePersonSearchWizardPage
extends ExtendedPersonSearchWizardPage
{
	/**
	 * @param quickSearchText
	 */
	public QuickSalePersonSearchWizardPage(String quickSearchText) {
		super(quickSearchText);
	}

	/**
	 * @param quickSearchText
	 * @param allowNewLegalEntityCreation
	 * @param allowEditLegalEntity
	 */
	public QuickSalePersonSearchWizardPage(String quickSearchText,
			boolean allowNewLegalEntityCreation, boolean allowEditLegalEntity)
	{
		super(quickSearchText, allowNewLegalEntityCreation,
				allowEditLegalEntity);
	}

	private boolean firstShow = false;

	@Override
	public void onShow()
	{
		super.onShow();
		if (!firstShow) {
			IWizard wizard = getWizard();
			if (wizard instanceof QuickSaleReserveAndSellWizard) {
				QuickSaleReserveAndSellWizard w = (QuickSaleReserveAndSellWizard) wizard;
				w.setMode(Mode.RESERVE);
			}
			firstShow = true;
		}
	}

}
