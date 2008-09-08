package org.nightlabs.jfire.issuetracking.ui.issue.create;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.issue.project.Project;
import org.nightlabs.jfire.issue.project.ProjectDAO;
import org.nightlabs.progress.NullProgressMonitor;
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
	
	private Combo projectCombo;
	
	public ProjectComboComposite(Composite parent, int style,String filterOrganisationID, boolean filterOrganisationIDInverse)
	{
		super(parent, style, LayoutMode.TIGHT_WRAPPER);
		
		projectCombo = new Combo(this, SWT.BORDER);
		projectCombo.setLayoutData(new GridData(GridData.FILL_BOTH));
		projectCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				fireSelectionChangedEvent();
			}
		});
	}
	
	protected void fireSelectionChangedEvent()
	{
		selectedProject = null;
		selection = null;

		Object[] listeners = selectionChangedListeners.getListeners();
		if (listeners.length == 0)
			return;

		SelectionChangedEvent event = new SelectionChangedEvent(this, getSelection());
		for (int i = 0; i < listeners.length; i++) {
			ISelectionChangedListener listener = (ISelectionChangedListener) listeners[i];
			listener.selectionChanged(event);
		}
	}
	
	private ListenerList selectionChangedListeners = new ListenerList();
	
	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionChangedListeners.add(listener);
	}

	private IStructuredSelection selection = null;

	@Override
	public ISelection getSelection() {
		if (selection == null)
			selection = new StructuredSelection(getSelectedProject());
		return selection;
	}

	private Set<Project> projectSet = new HashSet<Project>();
	public void loadProjects()
	{
		projects.clear();
		projectCombo.removeAll();
		projectCombo.add("Loading Projects............");

		new Job("Loading Projects............") {
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				try {
					final Collection<Project> _projects = ProjectDAO.sharedInstance().getRootProjects(getLocalOrganisationID());
					for (Project project : _projects) {
						projectSet.add(project);
						generateSub(project);
					}
					
					Display.getDefault().asyncExec(new Runnable()
					{
						public void run()
						{
							if (isDisposed())
								return;

							projectCombo.removeAll();
							
							for (Project project : projectSet) {
								projectCombo.add(project.getName().getText(NLLocale.getDefault().getLanguage()));
							}
							
							ProjectComboComposite.this.getParent().layout(true);
							fireSelectionChangedEvent();
						}
					});
				} catch (Exception x) {
					throw new RuntimeException(x);
				}

				return Status.OK_STATUS;
			}
		}.schedule();
	}
	
	private void generateSub(Project project) {
		boolean hasMoreChilds = false;
		
		Collection<Project> sp = ProjectDAO.sharedInstance().getProjectsByParentProjectID(project.getOrganisationID(), project.getProjectID());
		if (sp != null && sp.size() > 0) {
			hasMoreChilds = true;
			projectSet.addAll(sp);
		}
		
		if (hasMoreChilds) {
			for (Project p : sp) {
				generateSub(p);
			}
		}
	}
	
	private List<Project> projects = new ArrayList<Project>(0);
	private Project selectedProject = null;
	
	public Project getSelectedProject()
	{
		return null;
	}
	
	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		selectionChangedListeners.remove(listener);
	}

	@Override
	public void setSelection(ISelection arg0) {
		throw new UnsupportedOperationException("NYI");
	}

}
