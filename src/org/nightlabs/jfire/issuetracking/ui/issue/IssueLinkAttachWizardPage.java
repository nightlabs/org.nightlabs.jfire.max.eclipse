/**
 * 
 */
package org.nightlabs.jfire.issuetracking.ui.issue;

import java.util.Collection;
import java.util.List;

import javax.jdo.FetchPlan;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.XComboComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.wizard.WizardHop;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.base.jdo.JDOObjectID2PCClassMap;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueLinkType;
import org.nightlabs.jfire.issue.dao.IssueLinkTypeDAO;
import org.nightlabs.jfire.issuetracking.ui.overview.IssueEntryListFactory;
import org.nightlabs.jfire.issuetracking.ui.overview.IssueEntryListViewer;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun - chairat at nightlabs dot de
 *
 */
public class IssueLinkAttachWizardPage 
extends WizardHopPage 
{
	private Button createNewCheckBox;
	private Button selectFromCheckBox;
	
	private IssueEntryListViewer issueEntryViewer;
	
	private ObjectID linkedObjectID;
	private WizardHopPage createNewPage;
	
	public IssueLinkAttachWizardPage(ObjectID linkedObjectID) {
		super("Create link to issue", "Create link to issue");
		this.linkedObjectID = linkedObjectID;
		
		new WizardHop(this);
	}

	private String[] FETCH_GROUPS_ISSUE_LINK_TYPE = new String[] { IssueLinkType.FETCH_GROUP_NAME, FetchPlan.DEFAULT };
	
	@Override
	public Control createPageContents(Composite parent) {
		XComposite wrapper = new XComposite(parent, SWT.NONE);
		XComposite mainComposite = new XComposite(wrapper, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA);
		
		XComposite issueLinkTypeChooserComposite = new XComposite(mainComposite, SWT.NONE);
		issueLinkTypeChooserComposite.getGridLayout().numColumns = 2;
		new Label(issueLinkTypeChooserComposite, SWT.NONE).setText("Issue link types: ");
		final XComboComposite<IssueLinkType> issueLinkTypeCombo = new XComboComposite<IssueLinkType>(issueLinkTypeChooserComposite, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessVerticalSpace = true;
		issueLinkTypeCombo.setLayoutData(gridData);
		issueLinkTypeCombo.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof IssueLinkType) {
					IssueLinkType issueLinkType = (IssueLinkType) element;
					return issueLinkType.getName().getText();
				}
				return "";
			}
		});
		issueLinkTypeChooserComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		issueLinkTypeCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		
		Display.getCurrent().asyncExec(new Runnable() {
			@Override
			public void run() {
				Class<?> pcClass = JDOObjectID2PCClassMap.sharedInstance().getPersistenceCapableClass(linkedObjectID);
				List<IssueLinkType> issueLinkTypes = IssueLinkTypeDAO.sharedInstance().getIssueLinkTypesByLinkClass(pcClass, FETCH_GROUPS_ISSUE_LINK_TYPE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
				issueLinkTypeCombo.setInput(issueLinkTypes);
				issueLinkTypeCombo.setSelection(0);
			}
		});

		createNewCheckBox = new Button(mainComposite, SWT.RADIO);		
		createNewCheckBox.setText("Create new issue");
		createNewCheckBox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		selectFromCheckBox = new Button(mainComposite, SWT.RADIO);
		selectFromCheckBox.setText("Select issue");
		selectFromCheckBox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		createNewCheckBox.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setCreateNewIssue(true, true);
			}
		});
		
		selectFromCheckBox.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setCreateNewIssue(false, true);
			}
		});
		
		issueEntryViewer = new IssueEntryListViewer(new IssueEntryListFactory().createEntry()) {
			@Override
			protected void addResultTableListeners(final AbstractTableComposite tableComposite) {
				tableComposite.getTableViewer().addDoubleClickListener(new IDoubleClickListener() {
					@Override
					public void doubleClick(DoubleClickEvent evt) {
						Collection<Issue> issueCollection = tableComposite.getSelectedElements();
						Issue selectedIssue = issueCollection.iterator().next();
						selectedIssue.createIssueLink(issueLinkTypeCombo.getSelectedElement(), linkedObjectID);
					}
				});
				
				tableComposite.addSelectionChangedListener(new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent e) {
						getContainer().updateButtons();
						tableComposite.getSelectedElements();
//						notifyIssueLinkSelectionListeners();
					}
				});
			}
		};
		
		Composite issueEntryViewerComposite = issueEntryViewer.createComposite(mainComposite);
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessVerticalSpace = true;
		
		issueEntryViewerComposite.setLayoutData(gridData);
		
		setCreateNewIssue(false, false);
		return wrapper;
	}
	
	@Override
	public boolean isPageComplete() {
		if (getWizardHop().getNextPage(this) != null) {
			return true;
		}
		return false;//issueEntryViewer.getIssueTable().getSelection() != null;//issueLinkAdder.isComplete(); 
	}
	
	@Override
	public void onNext() {
	}
	
	@Override
	public boolean canBeLastPage() {
		if (createNewCheckBox.getSelection()) {
			return false;
		}
		
		return true;
	}
	
	public void setCreateNewIssue(boolean isCreateNew, boolean isUpdateButton) {
		createNewCheckBox.setSelection(isCreateNew);
		selectFromCheckBox.setSelection(!isCreateNew);
		
		
		issueEntryViewer.getIssueTable().setEnabled(!isCreateNew);
		
		if (isCreateNew) {
			createNewPage = new IssueCreateWizardPage(null);
			getWizardHop().addHopPage(new IssueCreateWizardPage(null));
		} else {
			getWizardHop().removeAllHopPages();
		}
		if (isUpdateButton)
			getContainer().updateButtons();
	}
}