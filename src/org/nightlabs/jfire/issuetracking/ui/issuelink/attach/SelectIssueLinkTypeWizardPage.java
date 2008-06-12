package org.nightlabs.jfire.issuetracking.ui.issuelink.attach;

import java.util.Collection;

import javax.jdo.FetchPlan;

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
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

public class SelectIssueLinkTypeWizardPage 
extends WizardHopPage
{
	private SelectIssueWizardPage selectIssuePage;

	private ListComposite<IssueLinkType> issueLinkTypeList;

	//Used Objects
	private IssueLinkType selectedIssueLinkType;
	private Object attachedObject;

	public SelectIssueLinkTypeWizardPage(Object attachedObject) {
		super(SelectIssueLinkTypeWizardPage.class.getName(), "New Issue", SharedImages.getWizardPageImageDescriptor(IssueTrackingPlugin.getDefault(), SelectIssueLinkTypeWizardPage.class));
		this.attachedObject = attachedObject;

		setTitle("Create/Attach issue");

		String objectNameString = attachedObject.getClass().getSimpleName();
		setDescription("Create/Attach issue to " + objectNameString);
	}

	@Override
	public Control createPageContents(Composite parent) {
		XComposite mainComposite = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA);
		mainComposite.getGridLayout().numColumns = 1;

		Group manageRelationGroup = new Group(mainComposite, SWT.NONE);
		manageRelationGroup.setLayout(new GridLayout(1, false));
		manageRelationGroup.setText("Relation");
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

		Job job = new Job("Loading IssueLinkTypes...") {
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
	public boolean isPageComplete() {
		return selectedIssueLinkType != null;
	}

	public IssueLinkType getSelectedIssueLinkType() {
		return selectedIssueLinkType;
	}
}