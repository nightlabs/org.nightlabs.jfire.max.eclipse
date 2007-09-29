/**
 * 
 */
package org.nightlabs.jfire.trade.ui.legalentity.search.report;

import javax.jdo.JDOHelper;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.jfire.reporting.parameter.AbstractValueProviderGUI;
import org.nightlabs.jfire.reporting.parameter.config.ValueProviderConfig;
import org.nightlabs.jfire.trade.ui.legalentity.search.LegalEntitySearchComposite;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ValueProviderGUILegalEntitySearch extends AbstractValueProviderGUI {

	private LegalEntitySearchComposite searchComposite;
	
	/**
	 * 
	 */
	public ValueProviderGUILegalEntitySearch(ValueProviderConfig valueProviderConfig) {
		super(valueProviderConfig);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.parameter.IValueProviderGUI#createGUI(org.eclipse.swt.widgets.Composite)
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
	 * @see org.nightlabs.jfire.reporting.parameter.IValueProviderGUI#getOutputValue()
	 */
	public Object getOutputValue() {
		return JDOHelper.getObjectId(searchComposite.getResultTable().getFirstSelectedElement());
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.parameter.IValueProviderGUI#isAcquisitionComplete()
	 */
	public boolean isAcquisitionComplete() {
		return searchComposite.getResultTable().getFirstSelectedElement() != null;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.parameter.IValueProviderGUI#setInputParameterValue(java.lang.String, java.lang.Object)
	 */
	public void setInputParameterValue(String parameterID, Object value) {
	}

}
