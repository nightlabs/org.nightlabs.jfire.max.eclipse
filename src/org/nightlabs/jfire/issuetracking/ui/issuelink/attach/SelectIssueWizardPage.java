package org.nightlabs.jfire.issuetracking.ui.issuelink.attach;

import javax.jdo.JDOHelper;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.wizard.WizardHop;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueLinkType;
import org.nightlabs.jfire.issuetracking.ui.IssueTrackingPlugin;
import org.nightlabs.jfire.issuetracking.ui.issue.create.CreateIssueDetailWizardPage;
import org.nightlabs.jfire.issuetracking.ui.overview.IssueEntryListFactory;
import org.nightlabs.jfire.issuetracking.ui.overview.IssueEntryListViewer;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 */
public class SelectIssueWizardPage
extends WizardHopPage
{
	//Issue
	private Button createNewIssueRadio;
	private Button selectExistingIssueRadio;

	private Composite issueEntryListViewerComposite;
	private IssueEntryListViewer issueEntryListViewer;

	//Used Objects
	private Issue selectedIssue;
	private Object attachedObject;

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
				break;
			case selectExistingIssue:
				selectExistingIssueRadio.setSelection(true);
				break;
			default:
				throw new IllegalStateException("Unknown actionForIssue: " + actionForIssue); //$NON-NLS-1$
		}

		getContainer().updateButtons();
	}

	public SelectIssueWizardPage(Object attachedObject) {
		super(SelectIssueWizardPage.class.getName(), Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuelink.attach.SelectIssueWizardPage.titleDefault"), SharedImages.getWizardPageImageDescriptor(IssueTrackingPlugin.getDefault(), SelectIssueWizardPage.class)); //$NON-NLS-1$
		this.attachedObject = attachedObject;

		setTitle(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuelink.attach.SelectIssueWizardPage.title")); //$NON-NLS-1$

		String objectNameString = attachedObject.getClass().getSimpleName();
		setDescription(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuelink.attach.SelectIssueWizardPage.description") + objectNameString); //$NON-NLS-1$
	}

	@Override
	public Control createPageContents(Composite parent) {
		XComposite mainComposite = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA);
		mainComposite.getGridLayout().numColumns = 1;

		//Issue
		createNewIssueRadio = new Button(mainComposite, SWT.RADIO);
		createNewIssueRadio.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuelink.attach.SelectIssueWizardPage.radio.createNewIssue.text")); //$NON-NLS-1$
		createNewIssueRadio.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		createNewIssueRadio.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setIssueAction(ActionForIssue.createNewIssue);
			}
		});

		selectExistingIssueRadio = new Button(mainComposite, SWT.RADIO);
		selectExistingIssueRadio.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuelink.attach.SelectIssueWizardPage.radio.selectExistingIssue.text")); //$NON-NLS-1$
		selectExistingIssueRadio.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		selectExistingIssueRadio.addSelectionListener(new SelectionAdapter() {
			@Override
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
						setIssueAction(ActionForIssue.selectExistingIssue);
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
				getShell().layout(true, true);
				issueEntryListViewer.search();
				setIssueAction(ActionForIssue.selectExistingIssue);
			}
		});

		issueEntryListViewer.getIssueTable().setIsTableInWizard(true);

		return mainComposite;
	}

	@Override
	public boolean isPageComplete() {
		if (createNewIssueRadio == null) // check if UI is already created
			return false;

		if (actionForIssue != null)
			switch (actionForIssue) {
				case createNewIssue:
					if (newIssue == null) {
						newIssue = new Issue(IDGenerator.getOrganisationID(), IDGenerator.nextID(Issue.class));
						newIssue.createIssueLink(issueLinkType, (ObjectID)JDOHelper.getObjectId(attachedObject), attachedObject.getClass());
						createIssueGeneralWizardPage = new CreateIssueDetailWizardPage(newIssue);
						new WizardHop(this);
						getWizardHop().addHopPage(createIssueGeneralWizardPage);
					}
					return false;
				case selectExistingIssue:
					return selectedIssue != null;
				default:
					throw new IllegalStateException("Unknown actionForIssueLinkType: " + actionForIssue); //$NON-NLS-1$
			}

		else
			return false;
	}

//	@Override
//	public void onFinish() {
//		selectedIssue.createIssueLink(issueLinkType, linkedObject)
//	}

	private Issue newIssue;
	private CreateIssueDetailWizardPage createIssueGeneralWizardPage;
	@Override
	public boolean canFlipToNextPage() {
		return actionForIssue == ActionForIssue.createNewIssue;
	}

	@Override
	public boolean canBeLastPage() {
		return super.canBeLastPage();
	}

	public Issue getIssue() {
		switch (actionForIssue) {
		case createNewIssue:
			return newIssue;
		case selectExistingIssue:
			return selectedIssue;
		}

		return null;
	}

	private IssueLinkType issueLinkType;
	public void setIssueLinkType(IssueLinkType issueLinkType) {
		this.issueLinkType = issueLinkType;
	}
}