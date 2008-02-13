/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.parameter.guifactory.jfireobjects;

import java.util.ArrayList;
import java.util.Collection;

import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.jfire.base.ui.workstation.WorkstationSearchComposite;
import org.nightlabs.jfire.reporting.ReportingConstants;
import org.nightlabs.jfire.reporting.parameter.config.ValueProviderConfig;
import org.nightlabs.jfire.reporting.parameter.id.ValueProviderID;
import org.nightlabs.jfire.reporting.ui.parameter.AbstractValueProviderGUI;
import org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI;
import org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUIFactory;
import org.nightlabs.jfire.workstation.Workstation;
import org.nightlabs.jfire.workstation.id.WorkstationID;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ValueProviderGUIWorkstations extends AbstractValueProviderGUI<Collection<WorkstationID>> {
	
	public static class Factory implements IValueProviderGUIFactory {

		public IValueProviderGUI<Collection<WorkstationID>> createValueProviderGUI(ValueProviderConfig valueProviderConfig) {
			return new ValueProviderGUIWorkstations(valueProviderConfig);
		}

		public ValueProviderID getValueProviderID() {
			return ReportingConstants.VALUE_PROVIDER_ID_WORKSTATIONS;
		}

		public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
		}
		
	}
	
	
	private WorkstationSearchComposite searchComposite;
	private SelectedWorkstationsTable selectedWorkstationsTable;
	

	/**
	 * 
	 */
	public ValueProviderGUIWorkstations(ValueProviderConfig valueProviderConfig) {
		super(valueProviderConfig);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI#createGUI(org.eclipse.swt.widgets.Composite)
	 */
	public Control createGUI(Composite wrapper) {
		Group group = new Group(wrapper, SWT.NONE);
		GridLayout gl = new GridLayout();
		XComposite.configureLayout(LayoutMode.ORDINARY_WRAPPER, gl);
		group.setLayout(gl);
		group.setLayoutData(new GridData(GridData.FILL_BOTH));
		group.setText(getValueProviderConfig().getMessage().getText());
		
		gl.numColumns = 3;
		gl.makeColumnsEqualWidth = false;
		
		searchComposite = new WorkstationSearchComposite(group, SWT.NONE, WorkstationSearchComposite.FLAG_MULTI_SELECTION | WorkstationSearchComposite.FLAG_SEARCH_BUTTON);
		searchComposite.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				notifyOutputChanged();
			}
		});
		
		XComposite buttonComp = new XComposite(group, SWT.NONE);
		buttonComp.getGridData().grabExcessHorizontalSpace = false;
		
		Button add = new Button(buttonComp, SWT.PUSH);
		add.setText(">>");
		add.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Collection<Workstation> workstations = searchComposite.getSelectedElements();
				for (Workstation workstation : workstations) {
					selectedWorkstationsTable.addWorkstation(workstation);
					notifyOutputChanged();
				}
			}
		});
		
		Button remove = new Button(buttonComp, SWT.PUSH);
		remove.setText("<<");
		remove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedWorkstationsTable.removeSelectedWorkstations();
				notifyOutputChanged();
			}
		});
		
		XComposite selectionWrapper = new XComposite(group, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		Label label = new Label(selectionWrapper, SWT.WRAP);
		label.setText("Selected workstations");
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		selectedWorkstationsTable = new SelectedWorkstationsTable(selectionWrapper, SWT.NONE);
		
		return group;
	}
	
	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI#getOutputValue()
	 */
	public Collection<WorkstationID> getOutputValue() {
		Collection<Workstation> workstations;
		Collection<Workstation> selectedWorkstations = selectedWorkstationsTable.getSelectedElements();
		if (selectedWorkstations.size() == 0) {
			workstations = searchComposite.getSelectedElements();
		} else {
			workstations = selectedWorkstations;
		}
		Collection<WorkstationID> result = new ArrayList<WorkstationID>(workstations.size());
		for (Workstation workstation : workstations) {
			result.add((WorkstationID) JDOHelper.getObjectId(workstation));
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI#isAcquisitionComplete()
	 */
	public boolean isAcquisitionComplete() {
		if (selectedWorkstationsTable.getSelectedElements().size() == 0)
			return searchComposite.getSelectedElements().size() > 0;
		return true;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI#setInputParameterValue(java.lang.String, java.lang.Object)
	 */
	public void setInputParameterValue(String parameterID, Object value) {
	}

}
