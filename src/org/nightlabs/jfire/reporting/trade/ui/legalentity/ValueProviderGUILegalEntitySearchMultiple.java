/**
 * 
 */
package org.nightlabs.jfire.reporting.trade.ui.legalentity;

import java.util.ArrayList;
import java.util.Collection;

import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.jfire.reporting.parameter.config.ValueProviderConfig;
import org.nightlabs.jfire.reporting.parameter.id.ValueProviderID;
import org.nightlabs.jfire.reporting.trade.ReportingTradeConstants;
import org.nightlabs.jfire.reporting.ui.parameter.AbstractValueProviderGUI;
import org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI;
import org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUIFactory;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.ui.legalentity.search.LegalEntitySearchComposite;
import org.nightlabs.jfire.transfer.id.AnchorID;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ValueProviderGUILegalEntitySearchMultiple extends AbstractValueProviderGUI<Collection<AnchorID>> {

	public static class Factory implements IValueProviderGUIFactory 
	{
		/* (non-Javadoc)
		 * @see org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUIFactory#createValueProviderGUI()
		 */
		public IValueProviderGUI<Collection<AnchorID>> createValueProviderGUI(ValueProviderConfig valueProviderConfig) {
			return new ValueProviderGUILegalEntitySearchMultiple(valueProviderConfig);
		}

		/* (non-Javadoc)
		 * @see org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUIFactory#getValueProviderID()
		 */
		public ValueProviderID getValueProviderID() {
			return ReportingTradeConstants.VALUE_PROVIDER_ID_LEGAL_ENTITY_SEARCH_MULTIPLE;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement, java.lang.String, java.lang.Object)
		 */
		public void setInitializationData(IConfigurationElement arg0, String arg1, Object arg2) throws CoreException {
		}
	}
	
	private LegalEntitySearchComposite searchComposite;
	
	/**
	 * 
	 */
	public ValueProviderGUILegalEntitySearchMultiple(ValueProviderConfig valueProviderConfig) {
		super(valueProviderConfig);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI#createGUI(org.eclipse.swt.widgets.Composite)
	 */
	public Control createGUI(Composite wrapper) {
		searchComposite = new LegalEntitySearchComposite(wrapper, SWT.NONE, ""); //$NON-NLS-1$
		searchComposite.getResultTable().getTableViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent arg0) {
				notifyOutputChanged();
			}
		});
		return searchComposite;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI#getOutputValue()
	 */
	public Collection<AnchorID> getOutputValue() {
		Collection<LegalEntity> selectedLEs = searchComposite.getResultTable().getSelectedElements();
		if (selectedLEs.size() < 1)
			return null;
		Collection<AnchorID> result = new ArrayList<AnchorID>(selectedLEs.size());
		for (LegalEntity legalEntity : selectedLEs) {
			result.add((AnchorID) JDOHelper.getObjectId(legalEntity));
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI#isAcquisitionComplete()
	 */
	public boolean isAcquisitionComplete() {
		return searchComposite.getResultTable().getFirstSelectedElement() != null || getValueProviderConfig().isAllowNullOutputValue();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI#setInputParameterValue(java.lang.String, java.lang.Object)
	 */
	public void setInputParameterValue(String parameterID, Object value) {
	}

}
