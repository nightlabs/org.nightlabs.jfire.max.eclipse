/**
 *
 */
package org.nightlabs.jfire.trade.ui.legalentity.search;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jdo.query.ui.search.SearchFilterProvider;
import org.nightlabs.jdo.query.ui.search.SearchResultFetcher;
import org.nightlabs.jdo.search.SearchFilter;
import org.nightlabs.jfire.base.ui.person.search.DynamicPersonSearchFilterProvider;
import org.nightlabs.jfire.base.ui.prop.search.PropertySetSearchComposite;
import org.nightlabs.jfire.base.ui.prop.search.PropertySetSearchFilterItemListMutator;
import org.nightlabs.jfire.base.ui.prop.search.PropertySetSearchFilterProvider;
import org.nightlabs.jfire.organisation.Organisation;
import org.nightlabs.jfire.person.Person;
import org.nightlabs.jfire.prop.Struct;
import org.nightlabs.jfire.prop.StructLocal;
import org.nightlabs.jfire.prop.id.StructLocalID;
import org.nightlabs.jfire.prop.search.PropSearchFilter;
import org.nightlabs.jfire.security.SecurityReflector;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.config.LegalEntitySearchConfigModule;
import org.nightlabs.jfire.trade.query.LegalEntitySearchFilter;
import org.nightlabs.jfire.transfer.id.AnchorID;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class LegalEntitySearchComposite extends PropertySetSearchComposite<AnchorID, LegalEntity> {

	/**
	 * @param parent
	 * @param style
	 * @param quickSearchText
	 */
	public LegalEntitySearchComposite(Composite parent, int style,
			String quickSearchText) {
		super(parent, style, quickSearchText, LegalEntitySearchConfigModule.class,
				PersonSearchUseCaseConstants.USE_CASE_ID_LEGALENTITY_SEARCH);
		createSearchButton(getButtonBar());
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Overrides to create {@link LegalEntitySearchFilter}.
	 * </p>
	 */
	@Override
	protected SearchFilterProvider createStaticSearchFilterProvider(SearchResultFetcher resultFetcher) {
		SearchFilterProvider provider = new PropertySetSearchFilterProvider(resultFetcher, false, LegalEntitySearchConfigModule.class, getPropertySetSearchUseCase(), getSearchText()) {
			@Override
			protected PropSearchFilter createSearchFilter() {
				return new LegalEntitySearchFilter(SearchFilter.CONJUNCTION_DEFAULT);
			}
		};
		return provider;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Overrides to create {@link LegalEntitySearchFilter}.
	 * </p>
	 */
	@Override
	protected SearchFilterProvider createDynamicSearchFilterProvider(
			SearchResultFetcher resultFetcher) {
		SearchFilterProvider provider = new DynamicPersonSearchFilterProvider(new PropertySetSearchFilterItemListMutator(
				createPersonStructLocalID()), resultFetcher) {
			@Override
			protected PropSearchFilter createSearchFilter() {
				return new LegalEntitySearchFilter(SearchFilter.CONJUNCTION_DEFAULT);
			}
		};
		return provider;
	}
	
	/**
	 * @return the {@link StructLocalID} that will be used to get the list of StructFields that will
	 *         be presented for search in the dynamic filter-provider.
	 */
	protected static StructLocalID createPersonStructLocalID() {
		return StructLocalID.create(Organisation.DEV_ORGANISATION_ID, Person.class, Struct.DEFAULT_SCOPE,
				StructLocal.DEFAULT_SCOPE);
	}
	
}
