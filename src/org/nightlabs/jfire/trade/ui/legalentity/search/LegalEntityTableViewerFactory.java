package org.nightlabs.jfire.trade.ui.legalentity.search;

import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.jfire.base.ui.prop.search.config.PropertySetTableViewerConfigurationComposite;
import org.nightlabs.jfire.base.ui.prop.view.IPropertySetViewer;
import org.nightlabs.jfire.base.ui.prop.view.IPropertySetViewerFactory;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.prop.search.PropSearchFilter;
import org.nightlabs.jfire.prop.view.PropertySetTableViewerConfiguration;
import org.nightlabs.jfire.prop.view.PropertySetViewerConfiguration;
import org.nightlabs.jfire.security.SecurityReflector;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.query.LegalEntityPersonMappingBean;
import org.nightlabs.jfire.trade.query.LegalEntitySearchFilter;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class LegalEntityTableViewerFactory implements IPropertySetViewerFactory<LegalEntityPersonMappingBean, LegalEntity, PropertySetTableViewerConfiguration> {

	private static final Collection<Class<? extends PropSearchFilter>> SUPPORTED_FILTER_CLASSES;
	private PropertySetTableViewerConfigurationComposite personTableViewerConfigurationComposite;
	
	static {
		SUPPORTED_FILTER_CLASSES = new LinkedList<Class<? extends PropSearchFilter>>();
		SUPPORTED_FILTER_CLASSES.add(LegalEntitySearchFilter.class);
	}
	/**
	 * 
	 */
	public LegalEntityTableViewerFactory() {
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Returns {@link LegalEntitySearchFilter}.class.
	 * </p>
	 */
	@Override
	public Collection<Class<? extends PropSearchFilter>> getSupportedFilterClasses() {
		return SUPPORTED_FILTER_CLASSES;
	}

	@Override
	public IPropertySetViewer<LegalEntityPersonMappingBean, LegalEntity, PropertySetTableViewerConfiguration> createViewer() {
		return new LegalEntityTableViewer();
	}

	@Override
	public PropertySetViewerConfiguration createViewerConfiguration() {
		return new PropertySetTableViewerConfiguration(SecurityReflector.getUserDescriptor().getOrganisationID(), IDGenerator
				.nextID(PropertySetViewerConfiguration.class));
	}

	@Override
	public Control createViewerConfigurationControl(Composite parent, PropertySetTableViewerConfiguration configuration) {
		personTableViewerConfigurationComposite = new PropertySetTableViewerConfigurationComposite(parent);
		personTableViewerConfigurationComposite.setViewerConfiguration(configuration);
		return personTableViewerConfigurationComposite;
	}

	@Override
	public String getDescription() {
		return "A table for the person-properties of a legal entity.";
	}

	@Override
	public String getName() {
		return "Legal entity table";
	}

	@Override
	public PropertySetViewerConfiguration getViewerConfiguration() {
		return personTableViewerConfigurationComposite.getViewerConfiguration();
	}

	@Override
	public String getViewerIdentifier() {
		return LegalEntityTableViewer.class.getName();
	}
}
