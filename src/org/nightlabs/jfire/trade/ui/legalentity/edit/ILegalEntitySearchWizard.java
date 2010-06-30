package org.nightlabs.jfire.trade.ui.legalentity.edit;

import org.nightlabs.jfire.trade.LegalEntity;

/**
 * Provides access to the {@link LegalEntity} fetched/set by the corresponding implementation
 * of the wizard-delegate.
 *
 * @author khaireel at nightlabs dot de
 */
public interface ILegalEntitySearchWizard {
	/**
	 * @return the {@link LegalEntity} this found through the wizard-delegate implementation.
	 * The return value should be guaranteed only after {@link #performFinish()} has successfully finished.
	 */
	public LegalEntity getLegalEntity();

	/**
	 * Sets the {@link LegalEntity} during the wizard-delegate implementation.
	 */
	public void setLegalEntity(LegalEntity legalEntity);

	/**
	 * @return the quick-search text to be used in this search-wizard.
	 */
	public String getQuickSearchText();

	/**
	 * @return true if this wizard should allow for a new {@link LegalEntity} creation.
	 */
	public boolean getAllowNewLegalEntityCreation();

	/**
	 * @return true if this wizard should allow to edit the current {@link LegalEntity}.
	 */
	public boolean getAllowEditLegalEntity();
}
