/**
 * 
 */
package org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.jdo.FetchPlan;

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
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.issue.IssuePriority;
import org.nightlabs.jfire.issue.IssueType;
import org.nightlabs.jfire.issue.dao.IssuePriorityDAO;
import org.nightlabs.jfire.issuetracking.admin.ui.IssueTrackingAdminPlugin;
import org.nightlabs.jfire.issuetracking.admin.ui.resource.Messages;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun - chairat [AT] nightlabs [DOT] de
 *
 */
public class IssueTypePrioritySelectWizardPage 
extends WizardHopPage 
{
	private Group choiceGroup;	
	private Button createNewCheckBox;
	private Button selectFromCheckBox;
	
	private IssueType issueType;
	
	private IssuePriorityTable issuePriorityTable;
	
	private IssueTypePriorityGeneralWizardPage createPage;
	
	public IssueTypePrioritySelectWizardPage(IssueType issueType) {
		super(	IssueTypePrioritySelectWizardPage.class.getName(),
	    		Messages.getString("org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty.IssueTypePrioritySelectWizardPage.title"), //$NON-NLS-1$
	    		SharedImages.getWizardPageImageDescriptor(IssueTrackingAdminPlugin.getDefault(), IssueTypePrioritySelectWizardPage.class)
	    	);
		new WizardHop(this);
		this.issueType = issueType;
		createPage = new IssueTypePriorityGeneralWizardPage(null);
	    setDescription(Messages.getString("org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty.IssueTypePrioritySelectWizardPage.description")); //$NON-NLS-1$
	}
	
	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.wizard.DynamicPathWizardPage#createPageContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control createPageContents(Composite parent) {
		XComposite wrapper = new XComposite(parent, SWT.NONE);
		XComposite comp = new XComposite(wrapper, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA_HORIZONTAL);
		
		createNewCheckBox = new Button(comp, SWT.RADIO);		
		createNewCheckBox.setText(Messages.getString("org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty.IssueTypePrioritySelectWizardPage.checkBox.createNew.text")); //$NON-NLS-1$
		createNewCheckBox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		createNewCheckBox.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setCreateNew(true, true);
			}
		});
		
		selectFromCheckBox = new Button(comp, SWT.RADIO);
		selectFromCheckBox.setText(Messages.getString("org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty.IssueTypePrioritySelectWizardPage.checkBox.selectFrom.text")); //$NON-NLS-1$
		selectFromCheckBox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		selectFromCheckBox.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setCreateNew(false, true);
			}
		});
		
		issuePriorityTable = new IssuePriorityTable(wrapper, SWT.NONE);
		Display.getCurrent().asyncExec(new Runnable(){
			public void run() {
				List<IssuePriority> issuePriorities = IssuePriorityDAO.sharedInstance().getIssuePriorities(
						new String[] { FetchPlan.DEFAULT, IssuePriority.FETCH_GROUP_NAME }, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
				issuePriorities.removeAll(issueType.getIssuePriorities());
				
				issuePriorityTable.setInput(issuePriorities);
			}
		});
		issuePriorityTable.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent arg0) {
				if (issuePriorityTable.getFirstSelectedElement() != null) {
					selectFromCheckBox.setSelection(true);
					setCreateNew(false, true);					
				}
			}
		});
		issuePriorityTable.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent arg0) {
				if (issuePriorityTable.getFirstSelectedElement() != null) {
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
//		issuePriorityTable.setEnabled(!b);
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
		return createNewCheckBox.getSelection() || issuePriorityTable.getFirstSelectedElement() != null;
	}
	
	public Collection<IssuePriority> getSelectedIssuePriorities() {
		if (this.equals(getContainer().getCurrentPage()))
			return new ArrayList<IssuePriority>(issuePriorityTable.getSelectedElements());
		else 
			return Collections.singleton(createPage.getPriorityComposite().getIssuePriority());
	}
}
