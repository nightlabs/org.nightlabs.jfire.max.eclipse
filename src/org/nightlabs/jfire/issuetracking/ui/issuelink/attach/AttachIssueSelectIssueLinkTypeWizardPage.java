package org.nightlabs.jfire.issuetracking.ui.issuelink.attach;

import java.util.Collection;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.nightlabs.base.ui.composite.ListComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.issue.IssueLinkType;
import org.nightlabs.jfire.issue.dao.IssueLinkTypeDAO;
import org.nightlabs.jfire.issuetracking.ui.IssueTrackingPlugin;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class AttachIssueSelectIssueLinkTypeWizardPage 
extends WizardHopPage
{
	private ListComposite<IssueLinkType> issueLinkTypeList;

	//Used Objects
	private IssueLinkType selectedIssueLinkType;
	private Object attachedObject;

	public AttachIssueSelectIssueLinkTypeWizardPage(Object attachedObject) {
		super(AttachIssueSelectIssueLinkTypeWizardPage.class.getName(), Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuelink.attach.AttachIssueSelectIssueLinkTypeWizardPage.titleDefault"), SharedImages.getWizardPageImageDescriptor(IssueTrackingPlugin.getDefault(), AttachIssueSelectIssueLinkTypeWizardPage.class)); //$NON-NLS-1$
		this.attachedObject = attachedObject;

		setTitle(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuelink.attach.AttachIssueSelectIssueLinkTypeWizardPage.title")); //$NON-NLS-1$

		String objectNameString = attachedObject.getClass().getSimpleName();
		setDescription(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuelink.attach.AttachIssueSelectIssueLinkTypeWizardPage.description") + objectNameString); //$NON-NLS-1$
	}

	@Override
	public Control createPageContents(Composite parent) {
		XComposite mainComposite = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA);
		mainComposite.getGridLayout().numColumns = 1;

		Group manageRelationGroup = new Group(mainComposite, SWT.NONE);
		manageRelationGroup.setLayout(new GridLayout(1, false));
		manageRelationGroup.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuelink.attach.AttachIssueSelectIssueLinkTypeWizardPage.group.relation.text")); //$NON-NLS-1$
		manageRelationGroup.setLayoutData(new GridData(GridData.FILL_BOTH));

		XComposite manageComposite = new XComposite(manageRelationGroup, SWT.NONE);
		manageComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		manageComposite.getGridLayout().numColumns = 1;

		issueLinkTypeList = new ListComposite<IssueLinkType>(manageComposite, SWT.SINGLE);
		issueLinkTypeList.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((IssueLinkType)element).getName().getText();
			}
		});
		GridData gridData = new GridData(GridData.FILL_BOTH);
		issueLinkTypeList.setLayoutData(gridData);
		issueLinkTypeList.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				selectedIssueLinkType = issueLinkTypeList.getSelectedElement();
			}
		});

		Job job = new Job(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuelink.attach.AttachIssueSelectIssueLinkTypeWizardPage.job.loadingIssueLinkType.text")) { //$NON-NLS-1$
			@Override
			protected IStatus run(final ProgressMonitor monitor) throws Exception {
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						IssueLinkTypeDAO issueLinkTypeDAO = IssueLinkTypeDAO.sharedInstance();
						Collection<IssueLinkType> issueLinkTypes = issueLinkTypeDAO.getIssueLinkTypes(
								attachedObject.getClass(), 
								new String[] {FetchPlan.DEFAULT, IssueLinkType.FETCH_GROUP_NAME}, 
								NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
								new SubProgressMonitor(monitor, 10));

						if (issueLinkTypes.size() > 0) {
							issueLinkTypeList.setInput(issueLinkTypes);
							issueLinkTypeList.setSelection(0);
							selectedIssueLinkType = issueLinkTypeList.getSelectedElement();
							issueLinkTypeList.setFocus();
							getContainer().updateButtons();

							if(issueLinkTypes.size() == 1 && JDOHelper.getObjectId(selectedIssueLinkType).equals(IssueLinkType.ISSUE_LINK_TYPE_ID_RELATED)) {
								if (getNextPage() instanceof SelectIssueWizardPage) {
									SelectIssueWizardPage selectPage = (SelectIssueWizardPage)getNextPage();
									selectPage.setIssueLinkType(selectedIssueLinkType);
								}
								getContainer().showPage(getNextPage());
							}
						}
					}
				});
				return Status.OK_STATUS;
			}
		};
		job.schedule();

		return mainComposite;
	}

	@Override
	public boolean canFlipToNextPage() {
		return selectedIssueLinkType != null;
	}

	public IssueLinkType getSelectedIssueLinkType() {
		return selectedIssueLinkType;
	}

//	private IssueLinkAdder issueLinkAdder;
//	public void setIssueLinkAdder(final IssueLinkAdder issueLinkAdder) {
//	this.issueLinkAdder = issueLinkAdder;

//	if (issueLinkAdder != null) {
//	Job job = new Job("Loading issue link types") {
//	@Override
//	protected IStatus run(ProgressMonitor monitor) throws Exception {
//	final Collection<IssueLinkType> issueLinkTypes = IssueLinkTypeDAO.sharedInstance().getIssueLinkTypes(
//	issueLinkAdder.getIssueLinkHandlerFactory().getLinkedObjectClass(), 
//	new String[] {IssueLinkType.FETCH_GROUP_NAME, FetchPlan.DEFAULT}, 
//	NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
//	monitor);

//	Display.getDefault().asyncExec(new Runnable() {
//	@Override
//	public void run() {
//	issueLinkTypeList.removeAll();
//	issueLinkTypeList.addElements(issueLinkTypes);
//	}
//	});

//	return Status.OK_STATUS;
//	}
//	};
//	job.setPriority(Job.SHORT);
//	job.schedule();
//	}
//	}
}