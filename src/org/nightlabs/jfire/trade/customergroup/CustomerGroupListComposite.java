package org.nightlabs.jfire.trade.customergroup;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

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
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.trade.CustomerGroup;
import org.nightlabs.jfire.trade.resource.Messages;

public class CustomerGroupListComposite
extends XComposite
implements ISelectionProvider
{
	public static interface CustomerGroupFilter {
		boolean includeCustomerGroup(CustomerGroup customerGroup);
	}

	private List<CustomerGroup> customerGroups = new ArrayList<CustomerGroup>(0);
	private org.eclipse.swt.widgets.List customerGroupList;
	private CustomerGroupFilter customerGroupFilter;

	private boolean organisationVisible = false;

	private String filterOrganisationID;
	private boolean filterOrganisationIDInverse = false;

	private static String getLocalOrganisationID()
	{
		try {
			return Login.getLogin().getOrganisationID();
		} catch (Exception x) {
			throw new RuntimeException(x);
		}
	}

	public CustomerGroupListComposite(Composite parent, int style, boolean multiSelect, CustomerGroupFilter customerGroupFilter)
	{
		this(parent, style, multiSelect, customerGroupFilter, getLocalOrganisationID(), false);
	}

	public CustomerGroupListComposite(Composite parent, int style, boolean multiSelect, CustomerGroupFilter customerGroupFilter, String filterOrganisationID, boolean filterOrganisationIDInverse)
	{
		super(parent, style, LayoutMode.TIGHT_WRAPPER);

		this.customerGroupFilter = customerGroupFilter;
		this.filterOrganisationID = filterOrganisationID;
		this.filterOrganisationIDInverse = filterOrganisationIDInverse;
		customerGroupList = new org.eclipse.swt.widgets.List(this, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | (!multiSelect ? 0 : SWT.MULTI));
		customerGroupList.setLayoutData(new GridData(GridData.FILL_BOTH));
		customerGroupList.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				fireSelectionChangedEvent();
			}
		});
	}

	public void setOrganisationVisible(boolean displayOrganisation)
	{
		this.organisationVisible = displayOrganisation;
	}

	protected static final String[] FETCH_GROUPS_TARIFF = { FetchPlan.DEFAULT, CustomerGroup.FETCH_GROUP_NAME };

	public String getFilterOrganisationID()
	{
		return filterOrganisationID;
	}
	public void setFilterOrganisationID(String filterOrganisationID)
	{
		this.filterOrganisationID = filterOrganisationID;
	}
	public boolean isFilterOrganisationIDInverse()
	{
		return filterOrganisationIDInverse;
	}
	public void setFilterOrganisationIDInverse(boolean filterOrganisationIDInverse)
	{
		this.filterOrganisationIDInverse = filterOrganisationIDInverse;
	}

	public void loadCustomerGroups()
	{
		customerGroups.clear();
		customerGroupList.removeAll();
		customerGroupList.add(Messages.getString("org.nightlabs.jfire.trade.customergroup.CustomerGroupListComposite.loadCustomerGroups.pseudoEntry_loading")); //$NON-NLS-1$

		new Job(Messages.getString("org.nightlabs.jfire.trade.customergroup.CustomerGroupListComposite.loadCustomerGroups.job.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				try {
					final List<CustomerGroup> _customerGroups = CustomerGroupDAO.sharedInstance().getCustomerGroups(filterOrganisationID, filterOrganisationIDInverse, FETCH_GROUPS_TARIFF, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);

					if (customerGroupFilter != null) {
						for (Iterator<CustomerGroup> it = _customerGroups.iterator(); it.hasNext();) {
							CustomerGroup customerGroup = it.next();
							if (!customerGroupFilter.includeCustomerGroup(customerGroup))
								it.remove();
						}
					}

					Collections.sort(_customerGroups, new Comparator<CustomerGroup>() {
						public int compare(CustomerGroup o1, CustomerGroup o2)
						{
							String s1 = o1.getName().getText(Locale.getDefault().getLanguage());
							String s2 = o2.getName().getText(Locale.getDefault().getLanguage());
							return Collator.getInstance().compare(s1, s2);
						}
					});

					Display.getDefault().asyncExec(new Runnable()
					{
						public void run()
						{
							if (isDisposed())
								return;

							customerGroupList.removeAll();
							customerGroups = _customerGroups;
							for (CustomerGroup customerGroup : _customerGroups) {
								customerGroupList.add(customerGroup.getName().getText(Locale.getDefault().getLanguage()) + (organisationVisible ? (" (" + customerGroup.getOrganisationID() + ")") : "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
							}
							CustomerGroupListComposite.this.getParent().layout(true);
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

	private CustomerGroup selectedCustomerGroup = null;
	private List<CustomerGroup> selectedCustomerGroups = null;
	private IStructuredSelection selection = null;

	private ListenerList selectionChangedListeners = new ListenerList();

	public void addSelectionChangedListener(ISelectionChangedListener listener)
	{
		selectionChangedListeners.add(listener);
	}

	public CustomerGroup getSelectedCustomerGroup()
	{
		if (selectedCustomerGroup == null) {
			List<CustomerGroup> st = getSelectedCustomerGroups();
			if (st.isEmpty())
				return null;
			else
				selectedCustomerGroup = st.get(0);
		}
		return selectedCustomerGroup;
	}

	public List<CustomerGroup> getSelectedCustomerGroups()
	{
		if (selectedCustomerGroups == null) {
			int[] selectionIndices = customerGroupList.getSelectionIndices();
			selectedCustomerGroups = new ArrayList<CustomerGroup>(selectionIndices.length);
			if (!customerGroups.isEmpty()) {
				for (int i = 0; i < selectionIndices.length; i++) {
					int selIdx = selectionIndices[i];
					selectedCustomerGroups.add(customerGroups.get(selIdx));
				}
			}
		}
		return selectedCustomerGroups;
	}

	protected void fireSelectionChangedEvent()
	{
		selectedCustomerGroup = null;
		selectedCustomerGroups = null;
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

	public ISelection getSelection()
	{
		if (selection == null)
			selection = new StructuredSelection(getSelectedCustomerGroups());

		return selection;
	}

	public void removeSelectionChangedListener(ISelectionChangedListener listener)
	{
		selectionChangedListeners.remove(listener);
	}

	public void setSelection(ISelection selection)
	{
		throw new UnsupportedOperationException("NYI"); //$NON-NLS-1$
	}
}
