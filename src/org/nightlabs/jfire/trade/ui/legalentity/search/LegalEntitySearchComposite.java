/**
 *
 */
package org.nightlabs.jfire.trade.ui.legalentity.search;

import javax.jdo.FetchPlan;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jdo.search.SearchFilter;
import org.nightlabs.jdo.ui.search.SearchFilterProvider;
import org.nightlabs.jdo.ui.search.SearchResultFetcher;
import org.nightlabs.jfire.base.ui.person.search.DynamicPersonSearchFilterProvider;
import org.nightlabs.jfire.base.ui.person.search.StaticPersonSearchFilterProvider;
import org.nightlabs.jfire.base.ui.prop.DefaultPropertySetTableConfig;
import org.nightlabs.jfire.base.ui.prop.IPropertySetTableConfig;
import org.nightlabs.jfire.base.ui.prop.PropertySetSearchComposite;
import org.nightlabs.jfire.base.ui.prop.PropertySetTable;
import org.nightlabs.jfire.base.ui.prop.search.PropertySetSearchFilterItemListMutator;
import org.nightlabs.jfire.organisation.Organisation;
import org.nightlabs.jfire.person.Person;
import org.nightlabs.jfire.person.PersonStruct;
import org.nightlabs.jfire.prop.IStruct;
import org.nightlabs.jfire.prop.PropertySet;
import org.nightlabs.jfire.prop.dao.StructLocalDAO;
import org.nightlabs.jfire.prop.id.StructFieldID;
import org.nightlabs.jfire.prop.id.StructLocalID;
import org.nightlabs.jfire.prop.search.PropSearchFilter;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.LegalEntitySearchFilter;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class LegalEntitySearchComposite extends PropertySetSearchComposite<LegalEntity> {

	/**
	 * @param parent
	 * @param style
	 * @param quickSearchText
	 */
	public LegalEntitySearchComposite(Composite parent, int style,
			String quickSearchText) {
		super(parent, style, quickSearchText, false);
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
		SearchFilterProvider provider = new StaticPersonSearchFilterProvider(resultFetcher, false) {
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
		SearchFilterProvider provider = new DynamicPersonSearchFilterProvider(new PropertySetSearchFilterItemListMutator(), resultFetcher) {
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
	 * Overrides to create Table to list LegalEntity Persons.
	 * </p>
	 */
	@Override
	protected PropertySetTable<LegalEntity> createResultTable(Composite parent) {
		PropertySetTable<LegalEntity> resultTable = new PropertySetTable<LegalEntity>(
				parent, SWT.NONE
		) {
			@Override
			protected PropertySet getPropertySetFromElement(Object element) {
				if (element instanceof LegalEntity)
					return ((LegalEntity) element).getPerson();
				return super.getPropertySetFromElement(element);
			}

			@Override
			protected IPropertySetTableConfig getPropertySetTableConfig() {

				return new LegalEntityTableConfig();
			}

			class LegalEntityTableConfig extends DefaultPropertySetTableConfig {
				@Override
				public IStruct getIStruct() {
					return StructLocalDAO.sharedInstance().getStructLocal(
							StructLocalID.create(
									Organisation.DEV_ORGANISATION_ID,
									Person.class, Person.STRUCT_SCOPE, Person.STRUCT_LOCAL_SCOPE
							), new NullProgressMonitor()
					);
				}

				@Override
				public StructFieldID[] getStructFieldIDs() {
					return new StructFieldID[] {
							PersonStruct.PERSONALDATA_COMPANY, PersonStruct.PERSONALDATA_NAME, PersonStruct.PERSONALDATA_FIRSTNAME,
							PersonStruct.POSTADDRESS_CITY, PersonStruct.POSTADDRESS_ADDRESS
						};
				}
			}
		};
		return resultTable;
	}

	@Override
	protected String[] getFetchGroups() {
		return new String[] {FetchPlan.DEFAULT, LegalEntity.FETCH_GROUP_PERSON, PropertySet.FETCH_GROUP_FULL_DATA};
	}
}
