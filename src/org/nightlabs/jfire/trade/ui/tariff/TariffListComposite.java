package org.nightlabs.jfire.trade.ui.tariff;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Status;
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
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Tariff;
import org.nightlabs.jfire.accounting.dao.TariffDAO;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;

public class TariffListComposite
extends XComposite
implements ISelectionProvider
{
	public static interface TariffFilter {
		boolean includeTariff(Tariff tariff);
	}

	private List<Tariff> tariffs = new ArrayList<Tariff>(0);
	private org.eclipse.swt.widgets.List tariffList;
	private TariffFilter tariffFilter;

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

	public TariffListComposite(Composite parent, int style, boolean multiSelect, TariffFilter tariffFilter)
	{
		this(parent, style, multiSelect, tariffFilter, getLocalOrganisationID(), false);
	}

	public TariffListComposite(Composite parent, int style, boolean multiSelect, TariffFilter tariffFilter, String filterOrganisationID, boolean filterOrganisationIDInverse)
	{
		super(parent, style, LayoutMode.TIGHT_WRAPPER);

		this.tariffFilter = tariffFilter;
		this.filterOrganisationID = filterOrganisationID;
		this.filterOrganisationIDInverse = filterOrganisationIDInverse;
		tariffList = new org.eclipse.swt.widgets.List(this, SWT.BORDER | (!multiSelect ? 0 : SWT.MULTI));
		tariffList.setLayoutData(new GridData(GridData.FILL_BOTH));
		tariffList.addSelectionListener(new SelectionAdapter() {
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

	protected static final String[] FETCH_GROUPS_TARIFF = { FetchPlan.DEFAULT, Tariff.FETCH_GROUP_NAME };

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

	public void loadTariffs()
	{
		tariffs.clear();
		tariffList.removeAll();
		tariffList.add(Messages.getString("org.nightlabs.jfire.trade.ui.tariff.TariffListComposite.pseudoEntry_loading")); //$NON-NLS-1$

		new Job(Messages.getString("org.nightlabs.jfire.trade.ui.tariff.TariffListComposite.loadTariffsJob.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception
			{
				try {
					// TODO create a TariffProvider in order to use the Cache
//					AccountingManager accountingManager = AccountingManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
//					final List<Tariff> _tariffs = new ArrayList<Tariff>(
//							accountingManager.getTariffs(FETCH_GROUPS_TARIFFS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT));
					final List<Tariff> _tariffs = TariffDAO.sharedInstance().getTariffs(filterOrganisationID, filterOrganisationIDInverse, FETCH_GROUPS_TARIFF, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);

					if (tariffFilter != null) {
						for (Iterator<Tariff> it = _tariffs.iterator(); it.hasNext();) {
							Tariff tariff = it.next();
							if (!tariffFilter.includeTariff(tariff))
								it.remove();
						}
					}

					Collections.sort(_tariffs, new Comparator<Tariff>() {
						public int compare(Tariff o1, Tariff o2)
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

							tariffList.removeAll();
							tariffs = _tariffs;
							for (Tariff tariff : _tariffs) {
								tariffList.add(tariff.getName().getText(Locale.getDefault().getLanguage()) + (organisationVisible ? (" (" + tariff.getOrganisationID() + ")") : "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
							}
							TariffListComposite.this.getParent().layout(true);
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

	private Tariff selectedTariff = null;
	private List<Tariff> selectedTariffs = null;
	private IStructuredSelection selection = null;

	private ListenerList selectionChangedListeners = new ListenerList();

	public void addSelectionChangedListener(ISelectionChangedListener listener)
	{
		selectionChangedListeners.add(listener);
	}

	public Tariff getSelectedTariff()
	{
		if (selectedTariff == null) {
			List<Tariff> st = getSelectedTariffs();
			if (st.isEmpty())
				return null;
			else
				selectedTariff = st.get(0);
		}
		return selectedTariff;
	}

	public List<Tariff> getSelectedTariffs()
	{
		if (selectedTariffs == null) {
			int[] selectionIndices = tariffList.getSelectionIndices();
			selectedTariffs = new ArrayList<Tariff>(selectionIndices.length);
			if (!tariffs.isEmpty()) {
				for (int i = 0; i < selectionIndices.length; i++) {
					int selIdx = selectionIndices[i];
					selectedTariffs.add(tariffs.get(selIdx));
				}
			}
		}
		return selectedTariffs;
	}

	protected void fireSelectionChangedEvent()
	{
		selectedTariff = null;
		selectedTariffs = null;
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
			selection = new StructuredSelection(getSelectedTariffs());

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
