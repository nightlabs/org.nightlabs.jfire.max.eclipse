package org.nightlabs.jfire.issuetracking.ui.issuelink.create;

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
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.wizard.DynamicPathWizardPage;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.issue.IssueLinkType;
import org.nightlabs.jfire.issue.dao.IssueLinkTypeDAO;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkAdder;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class SelectIssueLinkTypeWizardPage
extends DynamicPathWizardPage
{
	private ListComposite<IssueLinkType> issueLinkTypeList;

	private IssueLinkType selectedIssueLinkType;

	public SelectIssueLinkTypeWizardPage() {
		super(SelectIssueLinkTypeWizardPage.class.getName(), Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuelink.create.SelectIssueLinkTypeWizardPage.title")); //$NON-NLS-1$
		setDescription(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuelink.create.SelectIssueLinkTypeWizardPage.description")); //$NON-NLS-1$
	}

	public void setIssueLinkAdder(final IssueLinkAdder issueLinkAdder) {
		if (issueLinkAdder != null) {
			Job job = new Job(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuelink.create.SelectIssueLinkTypeWizardPage.job.loadingIssueLinkTypes.text")) { //$NON-NLS-1$
				@Override
				protected IStatus run(ProgressMonitor monitor) throws Exception {
					final Collection<IssueLinkType> issueLinkTypes = IssueLinkTypeDAO.sharedInstance().getIssueLinkTypes(
							issueLinkAdder.getIssueLinkHandlerFactory().getLinkedObjectClass(),
							new String[] {IssueLinkType.FETCH_GROUP_NAME, FetchPlan.DEFAULT},
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
							monitor);

					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							selectedIssueLinkType = null;
							issueLinkTypeList.removeAll();
							issueLinkTypeList.addElements(issueLinkTypes);
//							if (!issueLinkTypes.isEmpty()) {
							if (issueLinkTypes.size() == 1) { // Only auto-select, if there's exactly one - otherwise the user must choose.
								issueLinkTypeList.selectElementByIndex(0);
								selectedIssueLinkType = issueLinkTypeList.getSelectedElement();
							}
							getContainer().updateButtons();
						}
					});

					return Status.OK_STATUS;
				}
			};
			job.setPriority(Job.SHORT);
			job.schedule();
		}
	}

	@Override
	public Control createPageContents(Composite parent) {
		XComposite mainComposite = new XComposite(parent, SWT.NONE);
		mainComposite.getGridLayout().numColumns = 1;

		Group manageRelationGroup = new Group(mainComposite, SWT.NONE);
		manageRelationGroup.setLayout(new GridLayout(1, false));
		manageRelationGroup.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuelink.create.SelectIssueLinkTypeWizardPage.group.predefinedRelation.text")); //$NON-NLS-1$
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
				getContainer().updateButtons();
			}
		});

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