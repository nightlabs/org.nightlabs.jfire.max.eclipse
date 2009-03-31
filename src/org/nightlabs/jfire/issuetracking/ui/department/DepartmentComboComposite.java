package org.nightlabs.jfire.issuetracking.ui.department;

import java.util.ArrayList;
import java.util.Collection;
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
import org.nightlabs.jfire.issue.project.Department;
import org.nightlabs.jfire.issue.project.DepartmentDAO;
import org.nightlabs.progress.NullProgressMonitor;

public class DepartmentComboComposite 
extends XComposite
implements ISelectionProvider
{
	public DepartmentComboComposite(Composite parent, int style)
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

	private XCombo departmentCombo;
	private Department selectedDepartment;

	public DepartmentComboComposite(Composite parent, int style,String filterOrganisationID, boolean filterOrganisationIDInverse)
	{
		super(parent, style, LayoutMode.TIGHT_WRAPPER);

		departmentCombo = new XCombo(this, SWT.BORDER | SWT.READ_ONLY);
		departmentCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		departmentCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				int idx = departmentCombo.getSelectionIndex();
				if (idx < 0 || idx > departmentList.size() - 1)
					selectedDepartment = null;
				else
					selectedDepartment = departmentList.get(idx);

				fireSelectionChangedEvent();
			}
		});

		loadDepartments();
	}

	private static String[] FETCH_GROUP_DEPARTMENT = new String[] {
		FetchPlan.DEFAULT, 
		Department.FETCH_GROUP_NAME};

	private List<Department> departmentList = new ArrayList<Department>();
	public void loadDepartments()
	{
		departmentCombo.removeAll();

		Job loadJob = new Job("Loading departments") {
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				try {
					final Collection<Department> departments = DepartmentDAO.sharedInstance().getDepartments(FETCH_GROUP_DEPARTMENT, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
					Display.getDefault().asyncExec(new Runnable()
					{
						public void run()
						{
							if (isDisposed())
								return;

							departmentCombo.removeAll();

							for (Department department : departments) {
								departmentCombo.add(null, department.getName().getText());
							}

							DepartmentComboComposite.this.getParent().layout(true);
							departmentCombo.select(0);
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
		if (selectedDepartment == null)
			return new StructuredSelection(new Object[0]);

		return new StructuredSelection(selectedDepartment);
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
	}
}