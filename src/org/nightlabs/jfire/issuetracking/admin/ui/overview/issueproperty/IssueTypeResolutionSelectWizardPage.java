/**
 * 
 */
package org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.wizard.WizardHop;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.jfire.issue.IssueResolution;
import org.nightlabs.jfire.issue.IssueType;
import org.nightlabs.jfire.issue.dao.IssueResolutionDAO;
import org.nightlabs.jfire.issuetracking.admin.ui.IssueTrackingAdminPlugin;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun 
 *
 */
public class IssueTypeResolutionSelectWizardPage 
extends WizardHopPage {

	private Group choiceGroup;	
	private Button createNewCheckBox;
	private Button selectFromCheckBox;
	
	private IssueType issueType;
	
	private IssueResolutionTable issueTypeResolutionTable;
	
	private IssueTypeResolutionGeneralWizardPage createPage;
	
	public IssueTypeResolutionSelectWizardPage(IssueType issueType) {
		super(	IssueTypeResolutionSelectWizardPage.class.getName(),
	    		"Select Issue Resolution",
	    		SharedImages.getWizardPageImageDescriptor(IssueTrackingAdminPlugin.getDefault(), IssueTypeResolutionSelectWizardPage.class)
	    	);
		new WizardHop(this);
		this.issueType = issueType;
		createPage = new IssueTypeResolutionGeneralWizardPage(null);
	    setDescription("Description");
	}
	
	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.wizard.DynamicPathWizardPage#createPageContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control createPageContents(Composite parent) {
		XComposite wrapper = new XComposite(parent, SWT.NONE);
		XComposite comp = new XComposite(wrapper, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA_HORIZONTAL);
		
		createNewCheckBox = new Button(comp, SWT.RADIO);		
		createNewCheckBox.setText("Create a new resolution");
		createNewCheckBox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		createNewCheckBox.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setCreateNew(true, true);
			}
		});
		
		selectFromCheckBox = new Button(comp, SWT.RADIO);
		selectFromCheckBox.setText("Select from the list");
		selectFromCheckBox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		selectFromCheckBox.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setCreateNew(false, true);
			}
		});
		
		issueTypeResolutionTable = new IssueResolutionTable(wrapper, SWT.NONE);
		Display.getCurrent().asyncExec(new Runnable(){
			public void run() {
				List<IssueResolution> issueResolutions = IssueResolutionDAO.sharedInstance().getIssueResolutions(new NullProgressMonitor());
				issueResolutions.removeAll(issueType.getIssueResolutions());
				
				issueTypeResolutionTable.setInput(issueResolutions);
			}
		});
		issueTypeResolutionTable.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent e) {
				if (issueTypeResolutionTable.getFirstSelectedElement() != null) {
					selectFromCheckBox.setSelection(true);
					setCreateNew(false, true);					
				}
			}
		});
		issueTypeResolutionTable.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent e) {
				if (issueTypeResolutionTable.getFirstSelectedElement() != null) {
					if (getContainer() instanceof WizardDialog) {
						if (getWizard().performFinish()) {
							((WizardDialog) getContainer()).close();
						}
					}
				}
			}
		});
		setCreateNew(false, false);
		return wrapper; 
	}
	
	public void setCreateNew(boolean b, boolean updateButtons) {
		createNewCheckBox.setSelection(b);
		selectFromCheckBox.setSelection(!b);
		if (b) {
			getWizardHop().addHopPage(createPage);
		} else {
			getWizardHop().removeAllHopPages();
		}
		if (updateButtons)
			getContainer().updateButtons();
	}

	@Override
	public boolean isPageComplete() {
		return createNewCheckBox.getSelection() || issueTypeResolutionTable.getFirstSelectedElement() != null;
	}
	
	public Collection<IssueResolution> getSelectedIssueResolutions() {
		if (this.equals(getContainer().getCurrentPage()))
			return new ArrayList<IssueResolution>(issueTypeResolutionTable.getSelectedElements());
		else
			return Collections.singleton(createPage.getResolutionComposite().getIssueResolution());
	}
}
