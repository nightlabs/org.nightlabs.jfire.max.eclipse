package org.nightlabs.jfire.issuetracking.ui.project;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.custom.XCombo;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.issue.project.ProjectType;
import org.nightlabs.jfire.issue.project.ProjectTypeDAO;
import org.nightlabs.jfire.issue.project.id.ProjectTypeID;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.util.CollectionUtil;
import org.nightlabs.util.NLLocale;

public class ProjectTypeComboComposite 
extends XComposite
implements ISelectionProvider
{
	public ProjectTypeComboComposite(Composite parent, int style)
	{
		this(parent, style, getLocalOrganisationID(), false);
	}

	private static String getLocalOrganisationID()
	{
		try {
			return Login.getLogin().getOrganisationID();
		} catch (Exception x) {
			throw new RuntimeException(x);
		}
	}
	
	private XCombo projectTypeCombo;
	private ProjectType selectedProjectType;
	
	public ProjectTypeComboComposite(Composite parent, int style,String filterOrganisationID, boolean filterOrganisationIDInverse)
	{
		super(parent, style, LayoutMode.TIGHT_WRAPPER);
		
		projectTypeCombo = new XCombo(this, SWT.BORDER);
		projectTypeCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		projectTypeCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				int idx = projectTypeCombo.getSelectionIndex();
				if (idx < 0 || idx > projectTypeList.size() - 1)
					selectedProjectType = null;
				else
					selectedProjectType = projectTypeList.get(idx);

				fireSelectionChangedEvent();
			}
		});
		
		loadProjectTypes();
	}
	
	private static String[] FETCH_GROUP_PROJECT_TYPE = new String[] {
		FetchPlan.DEFAULT, 
		ProjectType.FETCH_GROUP_NAME}; 
	
	private List<ProjectType> projectTypeList = new ArrayList<ProjectType>();
	public void loadProjectTypes()
	{
		projectTypeCombo.removeAll();

		Job loadJob = new Job("Loading Project Types............") {
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				try {
					final Collection<ProjectType> _projectTypes = ProjectTypeDAO.sharedInstance().getProjectTypes(FETCH_GROUP_PROJECT_TYPE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
					CollectionUtil.addAllToCollection(_projectTypes.toArray(new ProjectType[0]), projectTypeList);
					Collections.sort(projectTypeList);
					
					Display.getDefault().asyncExec(new Runnable()
					{
						public void run()
						{
							if (isDisposed())
								return;

							projectTypeCombo.removeAll();
							
							for (ProjectType pt : projectTypeList) {
								projectTypeCombo.add(null, (pt.getName().getText(NLLocale.getDefault().getLanguage())));
							}
							
							setSelectedProjectType(ProjectType.PROJECT_TYPE_ID_DEFAULT);
							ProjectTypeComboComposite.this.getParent().layout(true);
						}
					});
				} catch (Exception x) {
					throw new RuntimeException(x);
				}

				return Status.OK_STATUS;
			}
		};
		
		loadJob.setPriority(Job.SHORT);
		loadJob.schedule();
	}
	
	private ListenerList selectionChangedListeners = new ListenerList();
	private void fireSelectionChangedEvent()
	{
		if (selectionChangedListeners.isEmpty())
			return;

		SelectionChangedEvent event = new SelectionChangedEvent(this, getSelection());

		for (Object l : selectionChangedListeners.getListeners()) {
			ISelectionChangedListener listener = (ISelectionChangedListener) l;
			listener.selectionChanged(event);
		}
	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionChangedListeners.add(listener);
	}

	@Override
	public ISelection getSelection() {
		if (selectedProjectType == null)
			return new StructuredSelection(new Object[0]);

		return new StructuredSelection(selectedProjectType);
	}

	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener)
	{
		selectionChangedListeners.remove(listener);
	}

	@Override
	public void setSelection(ISelection selection) {
		if (!(selection instanceof IStructuredSelection))
			throw new IllegalArgumentException("selection is not an instance of " + IStructuredSelection.class.getName() + " but " + (selection == null ? null : selection.getClass().getName())); //$NON-NLS-1$ //$NON-NLS-2$

		IStructuredSelection sel = (IStructuredSelection) selection;
		Object selObj = sel.getFirstElement();

		if (selObj instanceof ProjectType)
			setSelectedProjectType((ProjectType) selObj);
		else
			throw new IllegalArgumentException("selection.getFirstElement() is neither null, nor an instanceof " + ProjectType.class.getName()+ " or " + ProjectTypeID.class.getName()+ "! It is an instance of " + (selObj == null ? null : selObj.getClass().getName())); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	public void setSelectedProjectType(ProjectType projectType) {
		setSelectedProjectTypeID(projectType == null ? "" : projectType.getProjectTypeID());
	}

	public void setSelectedProjectType(ProjectTypeID projectTypeID) {
		setSelectedProjectTypeID(projectTypeID == null ? "" : projectTypeID.projectTypeID);
	}
	
	private void setSelectedProjectTypeID(String projectTypeID) {
		int idx = -1;
		int i = 0;
		for (ProjectType projectType : projectTypeList) {
			if (projectType.getProjectTypeID().equals(projectTypeID)) {
				idx = i;
				break;
			}
			++i;
		}

		if (idx < 0) {
			projectTypeCombo.deselectAll();
			selectedProjectType = null;
		}
		else {
			projectTypeCombo.select(idx);
			selectedProjectType = projectTypeList.get(idx);
		}
	}
	
	public ProjectType getSelectedProjectType() {
		return selectedProjectType;
	}
	
	public void addProjectType(ProjectType projectType, int index) {
		projectTypeList.add(index, projectType);
		projectTypeCombo.add(null, projectType.getName().getText(), index);
	}
}