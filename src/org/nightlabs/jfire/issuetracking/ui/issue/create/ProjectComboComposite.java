package org.nightlabs.jfire.issuetracking.ui.issue.create;

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
import org.nightlabs.jfire.issue.project.Project;
import org.nightlabs.jfire.issue.project.ProjectDAO;
import org.nightlabs.jfire.issue.project.id.ProjectID;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.util.CollectionUtil;
import org.nightlabs.util.NLLocale;

public class ProjectComboComposite 
extends XComposite
implements ISelectionProvider
{
	public ProjectComboComposite(Composite parent, int style)
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
	
	private XCombo projectCombo;
	private Project selectedProject;
	
	public ProjectComboComposite(Composite parent, int style,String filterOrganisationID, boolean filterOrganisationIDInverse)
	{
		super(parent, style, LayoutMode.TIGHT_WRAPPER);
		
		projectCombo = new XCombo(this, SWT.BORDER);
		projectCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		projectCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				int idx = projectCombo.getSelectionIndex();
				if (idx < 0 || idx > projectList.size() - 1)
					selectedProject = null;
				else
					selectedProject = projectList.get(idx);

				fireSelectionChangedEvent();
			}
		});
		loadProjects();
	}
	
	private static String[] FETCH_GROUP_PROJECT = new String[] {FetchPlan.DEFAULT, 
		Project.FETCH_GROUP_NAME, 
		Project.FETCH_GROUP_PARENT_PROJECT, 
		Project.FETCH_GROUP_SUBPROJECTS, 
		Project.FETCH_GROUP_DESCRIPTION};
	
	private List<Project> projectList = new ArrayList<Project>();
	public void loadProjects()
	{
		projectCombo.removeAll();

		new Job("Loading Projects............") {
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				try {
					final Collection<Project> _projects = ProjectDAO.sharedInstance().getRootProjects(getLocalOrganisationID(), FETCH_GROUP_PROJECT, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
					
					List<Project> tempProjectList = new ArrayList<Project>();
					CollectionUtil.addAllToCollection(_projects.toArray(new Project[0]), tempProjectList);
					Collections.sort(tempProjectList);
					for (Project project : tempProjectList) {
						generateSub(project);
					}
					
					Display.getDefault().asyncExec(new Runnable()
					{
						public void run()
						{
							if (isDisposed())
								return;

							projectCombo.removeAll();
							
							for (Project pj : projectList) {
								StringBuffer sb = new StringBuffer("");
								for (int i = 0; i < pj.getLevel(); i++) 
									sb.append("»");
								projectCombo.add(null, (sb.toString().equals("")?"": sb.append(" ")) +  pj.getName().getText(NLLocale.getDefault().getLanguage()));
							}
							
							ProjectComboComposite.this.getParent().layout(true);
							
							try {
								setSelectedProject(ProjectID.create(Login.getLogin().getOrganisationID(), -1));
							} catch (Exception e) {
								throw new RuntimeException(e);
							}
						}
					});
				} catch (Exception x) {
					throw new RuntimeException(x);
				}

				return Status.OK_STATUS;
			}
		}.schedule();
	}
	
	private int level = 0;
	private void generateSub(Project project) {
		projectList.add(project);
		
		Collection<Project> sp = ProjectDAO.sharedInstance().getProjectsByParentProjectID(project.getObjectId(), FETCH_GROUP_PROJECT, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
		
		List<Project> tempProjectList = new ArrayList<Project>();
		CollectionUtil.addAllToCollection(sp.toArray(new Project[0]), tempProjectList);
		Collections.sort(tempProjectList);
		
		if (sp.size() > 0) {
			level++;
			for (Project p : tempProjectList) {
				p.setLevel(level);
				generateSub(p);
			}
			level--;
		}
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
		if (selectedProject == null)
			return new StructuredSelection(new Object[0]);

		return new StructuredSelection(selectedProject);
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

		if (selObj == null)
			setSelectedProjectID(-1);
		if (selObj instanceof Project)
			setSelectedProject((Project) selObj);
		else if (selObj instanceof ProjectID)
			setSelectedProject((ProjectID) selObj);
		else
			throw new IllegalArgumentException("selection.getFirstElement() is neither null, nor an instanceof " + Project.class.getName()+ " or " + ProjectID.class.getName()+ "! It is an instance of " + (selObj == null ? null : selObj.getClass().getName())); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	public void setSelectedProject(Project project) {
		setSelectedProjectID(project == null ? null : project.getProjectID());
	}

	public void setSelectedProject(ProjectID projectID) {
		setSelectedProjectID(projectID == null ? null : projectID.projectID);
	}
	
	private void setSelectedProjectID(long projectID) {
		int idx = -1;
		int i = 0;
		for (Project project : projectList) {
			if (project.getProjectID() == projectID) {
				idx = i;
				break;
			}
			++i;
		}

		if (idx < 0) {
			projectCombo.deselectAll();
			selectedProject = null;
		}
		else {
			projectCombo.select(idx);
			selectedProject = projectList.get(idx);
		}
	}
	
	public Project getSelectedProject() {
		return selectedProject;
	}
	
	public void addProject(Project project, int index) {
		projectList.add(index, project);
		projectCombo.add(null, project.getName().getText(), index);
	}
}
