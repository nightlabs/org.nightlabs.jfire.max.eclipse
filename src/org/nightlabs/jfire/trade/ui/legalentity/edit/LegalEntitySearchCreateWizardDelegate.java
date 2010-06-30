/**
 * 
 */
package org.nightlabs.jfire.trade.ui.legalentity.edit;

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.nightlabs.base.ui.wizard.AbstractWizardPageProviderDelegate;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.base.ui.person.search.PersonSearchWizardPage;
import org.nightlabs.jfire.person.Person;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.TradeManagerRemote;

/**
 * @author Daniel Mazurek
 *
 */
public class LegalEntitySearchCreateWizardDelegate extends AbstractWizardPageProviderDelegate {

	private PersonSearchWizardPage personSearchWizardPage;
	/**
	 * 
	 */
	public LegalEntitySearchCreateWizardDelegate() {
		personSearchWizardPage = new PersonSearchWizardPage(null);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.wizard.IPageProvider#getPages()
	 */
	@Override
	public List<? extends IWizardPage> getPages() {
		// Prepare the PersonSearchWizardPage, if there are preparatorial information available.
		IWizard iWizard = getWizard();
		if (iWizard instanceof ILegalEntitySearchWizard) {
			ILegalEntitySearchWizard legalEntityWizard = (ILegalEntitySearchWizard) iWizard;
			personSearchWizardPage = new PersonSearchWizardPage(
					legalEntityWizard.getQuickSearchText(),
					legalEntityWizard.getAllowNewLegalEntityCreation(),
					legalEntityWizard.getAllowEditLegalEntity()
				);
		}

		return Collections.singletonList(personSearchWizardPage); // Since there is only ONE page...
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.wizard.IWizardDelegate#performFinish()
	 */
	@Override
	public boolean performFinish() {
		Person selectedPerson = personSearchWizardPage.getSelectedPerson();
		try {
			TradeManagerRemote tradeManager = JFireEjb3Factory.getRemoteBean(TradeManagerRemote.class, Login.getLogin().getInitialContextProperties());
			LegalEntity legalEntity = tradeManager.storePersonAsLegalEntity(selectedPerson, true, LegalEntityPersonEditor.FETCH_GROUPS_FULL_LE_DATA,
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);

			((ILegalEntitySearchWizard) getWizard()).setLegalEntity(legalEntity);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return true;
	}

}
