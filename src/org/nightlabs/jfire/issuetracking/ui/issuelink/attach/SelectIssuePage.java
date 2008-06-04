package org.nightlabs.jfire.issuetracking.ui.issuelink.attach;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.wizard.WizardHop;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issuetracking.ui.issue.create.CreateIssueWizardPage;
import org.nightlabs.jfire.issuetracking.ui.overview.IssueEntryListFactory;
import org.nightlabs.jfire.issuetracking.ui.overview.IssueEntryListViewer;

public class SelectIssuePage 
extends WizardHopPage
{
	//Issue
	private Button createNewIssueRadio;
	private Button selectExistingIssueRadio;
	
	private Composite issueEntryListViewerComposite;
	private IssueEntryListViewer issueEntryListViewer;

	//Used Objects
	private Issue selectedIssue;

	public static enum ActionForIssue {
		createNewIssue,
		selectExistingIssue
	}

	private ActionForIssue actionForIssue;

	public ActionForIssue getIssueAction() {
		return actionForIssue;
	}

	public void setIssueAction(ActionForIssue actionForIssue) {
		this.actionForIssue = actionForIssue;
		createNewIssueRadio.setSelection(false);
		selectExistingIssueRadio.setSelection(false);

		switch (actionForIssue) {
			case createNewIssue:
				createNewIssueRadio.setSelection(true);
				issueEntryListViewerComposite.setEnabled(false);
				break;
			case selectExistingIssue:
				selectExistingIssueRadio.setSelection(true);
				issueEntryListViewerComposite.setEnabled(true);
				break;
			default:
				throw new IllegalStateException("Unknown actionForIssue: " + actionForIssue);
		}

		getContainer().updateButtons();
	}

	public SelectIssuePage(Object attachedObject) {
		super(SelectIssueLinkTypePage.class.getName());
		setTitle("Create/Attach issue");
		
		String objectNameString = attachedObject.getClass().getSimpleName();
		setDescription("Create/Attach issue to " + objectNameString);
		
		new WizardHop(this);
	}

	@Override
	public Control createPageContents(Composite parent) {
		XComposite mainComposite = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA);
		mainComposite.getGridLayout().numColumns = 1;

		//Issue
		createNewIssueRadio = new Button(mainComposite, SWT.RADIO);		
		createNewIssueRadio.setText("Create new issue");
		createNewIssueRadio.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		createNewIssueRadio.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setIssueAction(ActionForIssue.createNewIssue);
			}
		});
		
		selectExistingIssueRadio = new Button(mainComposite, SWT.RADIO);
		selectExistingIssueRadio.setText("Select issue");
		selectExistingIssueRadio.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		selectExistingIssueRadio.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setIssueAction(ActionForIssue.selectExistingIssue);
			}
		});
		
		issueEntryListViewer = new IssueEntryListViewer(new IssueEntryListFactory().createEntry()) {
			@Override
			protected void addResultTableListeners(AbstractTableComposite tableComposite) {
				tableComposite.addDoubleClickListener(new IDoubleClickListener() {
					@Override
					public void doubleClick(DoubleClickEvent evt) {
						//do nothing!!!
					}
				});

				tableComposite.addSelectionChangedListener(new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent e) {
						selectedIssue = issueEntryListViewer.getIssueTable().getFirstSelectedElement();
						getContainer().updateButtons();
					}
				});
			}
		};	

		issueEntryListViewerComposite = issueEntryListViewer.createComposite(mainComposite);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		issueEntryListViewerComposite.setLayoutData(gridData);

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				issueEntryListViewer.search();
				setIssueAction(ActionForIssue.selectExistingIssue);
			}
		});
		
		return mainComposite;
	}

	@Override
	public boolean isPageComplete() {
		if (createNewIssueRadio == null) // check if UI is already created
			return false;

		if (actionForIssue != null)
			switch (actionForIssue) {
				case createNewIssue:
					return false;
				case selectExistingIssue:
					return selectedIssue != null;
				default:
					throw new IllegalStateException("Unknown actionForIssueLinkType: " + actionForIssue);
			}
		
		else 
			return false;
	}

//	@Override
//	public IWizardPage getNextPage() {
//		Issue newIssue = new Issue(IDGenerator.getOrganisationID(), IDGenerator.nextID(Issue.class));
//		return new CreateIssueWizardPage(newIssue);
//	}
	
	private Issue newIssue;
//	@Override
//	public boolean canFlipToNextPage() {
//		if (actionForIssue == ActionForIssue.createNewIssue) {
//			if (newIssue == null)
//				newIssue = new Issue(IDGenerator.getOrganisationID(), IDGenerator.nextID(Issue.class));
////			return new CreateIssueWizardPage(newIssue);
////			getWizardHop().addHopPage(createPage);
//		} else {
////			getWizardHop().removeAllHopPages();
//		}
////		if (updateButtons)
////			getContainer().updateButtons();
////		return 
//	}
	
	public Issue getSelectedIssue() {
		return selectedIssue;
	}
}