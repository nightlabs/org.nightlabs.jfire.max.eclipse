/**
 * 
 */
package org.nightlabs.jfire.reporting.trade.ui.store.repository;

import java.util.ArrayList;
import java.util.Collection;

import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.nightlabs.jfire.reporting.parameter.config.ValueProviderConfig;
import org.nightlabs.jfire.reporting.parameter.id.ValueProviderID;
import org.nightlabs.jfire.reporting.trade.ReportingTradeConstants;
import org.nightlabs.jfire.reporting.ui.parameter.AbstractValueProviderGUI;
import org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI;
import org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUIFactory;
import org.nightlabs.jfire.trade.ui.overview.repository.RepositoryEntryFactory;
import org.nightlabs.jfire.trade.ui.overview.repository.RepositoryEntryViewer;
import org.nightlabs.jfire.transfer.Anchor;
import org.nightlabs.jfire.transfer.id.AnchorID;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ValueProviderGUIRepositories
extends AbstractValueProviderGUI<Collection<AnchorID>>
{
	public static class Factory implements IValueProviderGUIFactory {
		
		/* (non-Javadoc)
		 * @see org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUIFactory#createValueProviderGUI()
		 */
		public IValueProviderGUI<Collection<AnchorID>> createValueProviderGUI(ValueProviderConfig valueProviderConfig) {
			return new ValueProviderGUIRepositories(valueProviderConfig);
		}

		/* (non-Javadoc)
		 * @see org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUIFactory#getValueProviderID()
		 */
		public ValueProviderID getValueProviderID() {
			return ReportingTradeConstants.VALUE_PROVIDER_ID_STORE_REPOSITORY_IDS;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement, java.lang.String, java.lang.Object)
		 */
		public void setInitializationData(IConfigurationElement arg0, String arg1,
				Object arg2) throws CoreException {
		}
	}
	
	private RepositoryEntryViewer repositoryEntryViewer;
	
	public ValueProviderGUIRepositories(ValueProviderConfig valueProviderConfig) {
		super(valueProviderConfig);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI#createGUI(org.eclipse.swt.widgets.Composite)
	 */
	public Control createGUI(Composite wrapper) {
		Group group = new Group(wrapper, SWT.NONE);
		group.setLayoutData(new GridData(GridData.FILL_BOTH));
		group.setLayout(new GridLayout());
		group.setText(getValueProviderConfig().getMessage().getText());
		
		repositoryEntryViewer = new RepositoryEntryViewer(new RepositoryEntryFactory().createEntry());
		repositoryEntryViewer.createComposite(group);
		repositoryEntryViewer.getListComposite().addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				notifyOutputChanged();
			}
		});		
		
		return group;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI#getOutputValue()
	 */
	@SuppressWarnings("unchecked")
	public Collection<AnchorID> getOutputValue() {
		Collection<Anchor> anchors = (Collection<Anchor>) repositoryEntryViewer.getListComposite().getSelectedElements();		
		if (anchors != null && anchors.size() > 0) {
			Collection<AnchorID> result = new ArrayList<AnchorID>(anchors.size());
			for (Anchor anchor : anchors) {
				result.add((AnchorID) JDOHelper.getObjectId(anchor));
			}
			return result;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI#isAcquisitionComplete()
	 */
	public boolean isAcquisitionComplete() {
		return getOutputValue() != null || getValueProviderConfig().isAllowNullOutputValue();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI#setInputParameterValue(java.lang.String, java.lang.Object)
	 */
	public void setInputParameterValue(String parameterID, final Object value) {
	}

}
