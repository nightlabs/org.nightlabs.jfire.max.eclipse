/**
 * 
 */
package org.nightlabs.jfire.trade.ui.legalentity.edit;

import org.eclipse.jface.window.Window;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.base.ui.person.search.PersonSearchWizardPage;
import org.nightlabs.jfire.person.Person;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.TradeManager;
import org.nightlabs.jfire.trade.TradeManagerUtil;
import org.nightlabs.jfire.trade.ui.resource.Messages;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class LegalEntitySearchCreateWizard extends DynamicPathWizard {

	private PersonSearchWizardPage personSearchWizardPage;
	private LegalEntity legalEntity;
	
	public LegalEntitySearchCreateWizard(String quickSearchText, 
			boolean allowNewLegalEntityCreation, boolean allowEditLegalEnity) 
	{
		personSearchWizardPage = new PersonSearchWizardPage(quickSearchText, allowNewLegalEntityCreation, allowEditLegalEnity);
		addPage(personSearchWizardPage);
		setWindowTitle(Messages.getString("org.nightlabs.jfire.trade.ui.legalentity.edit.LegalEntitySearchCreateWizard.windowsTitle")); //$NON-NLS-1$
	}

	public LegalEntitySearchCreateWizard(String quickSearchText) 
	{
		this(quickSearchText, true, false);
	}

	public LegalEntitySearchCreateWizard(String quickSearchText, boolean allowNewLegalEntityCreation) 
	{
		this(quickSearchText, allowNewLegalEntityCreation, false);
	}
	
	
	/** {@inheritDoc}
	 */
	@Override
	public boolean performFinish() {
		Person selectedPerson = personSearchWizardPage.getSelectedPerson();
		try {
			TradeManager tradeManager = TradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
			legalEntity = tradeManager.storePersonAsLegalEntity(selectedPerson, true, LegalEntityPersonEditor.FETCH_GROUPS_FULL_LE_DATA,
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	/**
	 * Returns the {@link LegalEntity} this Wizard found.
	 * This will only be set after {@link #performFinish()}
	 * successfully finished.
	 * @return The {@link LegalEntity} this Wizard found.
	 */
	public LegalEntity getLegalEntity() {
		return legalEntity;
	}
	
	/**
	 * Opens the wizard and returns the {@link LegalEntity}
	 * the user found or created.
	 * 
	 * @param quickSearchText The text to search for instantly.
	 * @param allowNewLegalEntityCreation Whether the user should be able to create a new Person/LegalEntity in the wizard.
	 * @return The found or newly created {@link LegalEntity}.
	 */
	public static LegalEntity open(String quickSearchText, boolean allowNewLegalEntityCreation) {
		return open(quickSearchText, allowNewLegalEntityCreation, false);
	}

	/**
	 * Opens the wizard and returns the {@link LegalEntity}
	 * the user found or created.
	 * 
	 * @param quickSearchText The text to search for instantly.
	 * @param allowNewLegalEntityCreation Whether the user should be able to create a new Person/LegalEntity in the wizard.
	 * @param allowEditLegalEntity Whether the user should be able to edit a selected person in the wizard
	 * @return The found or newly created {@link LegalEntity}.
	 */	
	public static LegalEntity open(String quickSearchText, boolean allowNewLegalEntityCreation, boolean allowEditLegalEntity) 
	{
		LegalEntitySearchCreateWizard wiz = new LegalEntitySearchCreateWizard(quickSearchText, allowNewLegalEntityCreation, allowEditLegalEntity);
		DynamicPathWizardDialog dlg = new DynamicPathWizardDialog(wiz);
		if (dlg.open() == Window.OK) {
			return wiz.getLegalEntity();
		}
		return null;
	}
	
}
