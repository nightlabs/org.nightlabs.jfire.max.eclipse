package org.nightlabs.jfire.trade.ui.tariff;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableContentProvider;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Tariff;
import org.nightlabs.jfire.accounting.dao.TariffDAO;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.util.NLLocale;

public class TariffList
extends AbstractTableComposite<Tariff>
{
	public static Comparator<Tariff> tariffComparator = new Comparator<Tariff>() {
		public int compare(Tariff o1, Tariff o2) {
			String s1 = o1.getName().getText(NLLocale.getDefault().getLanguage());
			String s2 = o2.getName().getText(NLLocale.getDefault().getLanguage());
			return Collator.getInstance().compare(s1, s2);
		}
	};

	public static interface TariffFilter {
		boolean includeTariff(Tariff tariff);
	}
	private TariffFilter tariffFilter;

	private boolean organisationVisible = false;

	private String filterOrganisationID;
	private boolean filterOrganisationIDInverse = false;

	private volatile List<Tariff> tariffs;

	private LabelProvider labelProvider = new TableLabelProvider() {
		public String getColumnText(Object element, int columnIndex) {
			Tariff tariff = (Tariff) element;
			return tariff.getName().getText() + (organisationVisible ? (" (" + tariff.getOrganisationID() + ")") : ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	};

	private static String getLocalOrganisationID() {
		try {
			return Login.getLogin().getOrganisationID();
		} catch (Exception x) {
			throw new RuntimeException(x);
		}
	}

	/**
	 * @deprecated Pass the {@link TariffFilter} to {@link #loadTariffs(Comparator, org.nightlabs.jfire.trade.ui.tariff.TariffList.TariffFilter)} and use one of the non-deprecated constructors.
	 */
	@Deprecated
	public TariffList(Composite parent, int style, boolean multiSelect, TariffFilter tariffFilter) {
		this(parent, style, multiSelect, tariffFilter, getLocalOrganisationID(), false);
	}

	public TariffList(Composite parent, int style, boolean multiSelect) {
		this(parent, style, multiSelect, (TariffFilter)null);
	}

	public TariffList(Composite parent, int style, boolean multiSelect, String filterOrganisationID, boolean filterOrganisationIDInverse) {
		this(parent, style, multiSelect, (TariffFilter)null, filterOrganisationID, filterOrganisationIDInverse);
	}

	/**
	 * @deprecated Pass the {@link TariffFilter} to {@link #loadTariffs(Comparator, org.nightlabs.jfire.trade.ui.tariff.TariffList.TariffFilter)} and use one of the non-deprecated constructors.
	 */
	@Deprecated
	public TariffList(Composite parent, int style, boolean multiSelect, TariffFilter tariffFilter, String filterOrganisationID, boolean filterOrganisationIDInverse) {
		super(parent, style, false);

		this.tariffFilter = tariffFilter;
		this.filterOrganisationID = filterOrganisationID;
		this.filterOrganisationIDInverse = filterOrganisationIDInverse;

		getTable().setHeaderVisible(false);
		getTable().setLinesVisible(false);

		initTable();
	}

	public void setOrganisationVisible(boolean displayOrganisation) {
		this.organisationVisible = displayOrganisation;
	}

	protected static final String[] FETCH_GROUPS_TARIFF = { FetchPlan.DEFAULT, Tariff.FETCH_GROUP_NAME };

	public String getFilterOrganisationID() {
		return filterOrganisationID;
	}

	public void setFilterOrganisationID(String filterOrganisationID) {
		this.filterOrganisationID = filterOrganisationID;
	}

	public boolean isFilterOrganisationIDInverse() {
		return filterOrganisationIDInverse;
	}

	public void setFilterOrganisationIDInverse(boolean filterOrganisationIDInverse) {
		this.filterOrganisationIDInverse = filterOrganisationIDInverse;
	}

	/**
	 * @deprecated Use {@link #loadTariffs(Comparator, org.nightlabs.jfire.trade.ui.tariff.TariffList.TariffFilter)} instead (you can pass <code>null</code> as filter).
	 */
	@Deprecated
	public void loadTariffs(final Comparator<Tariff> _tariffComparator) {
		loadTariffs(_tariffComparator, this.tariffFilter);
	}

//	/**
//	 * Load the tariffs. When this composite is created, it is initially empty. In order to make it display
//	 * the tariffs, this method needs to be called on the UI thread. It spawns a {@link Job} which loads the data,
//	 * orders it with the given {@link Comparator} and displays it. If no <code>Comparator</code> is given, it
//	 * sorts according to the tariff's name (obtained via {@link Tariff#getName()} and using the default {@link Locale}).
//	 * <p>
//	 * If not all {@link Tariff}s should be displayed, you can pass a {@link TariffFilter} to the constructor.
//	 * </p>
//	 *
//	 * @param _tariffComparator The comparator to be used for sorting the {@link Tariff}s or <code>null</code> to use the built-in
//	 *		default (sorting by the tariff's name).
//	 * @param tariffFilter the filter to be used or <code>null</code> if the tariffs should not be filtered.
//	 */
//	public void loadTariffs(final Comparator<Tariff> _tariffComparator, final TariffFilter tariffFilter) {
//		this.tariffFilter = tariffFilter;
//
//		setLoadingMessage(Messages.getString("org.nightlabs.jfire.trade.ui.tariff.TariffList.message.loadingTariffs")); //$NON-NLS-1$
//		new Job(Messages.getString("org.nightlabs.jfire.trade.ui.tariff.TariffList.loadTariffsJob.name")) { //$NON-NLS-1$
//			@Override
//			protected IStatus run(ProgressMonitor monitor) throws Exception {
//				try {
//					final List<Tariff> _tariffs = TariffDAO.sharedInstance().getTariffs(filterOrganisationID, filterOrganisationIDInverse, FETCH_GROUPS_TARIFF,
//							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
//
//					if (tariffFilter != null) {
//						for (Iterator<Tariff> it = _tariffs.iterator(); it.hasNext();) {
//							Tariff tariff = it.next();
//							if (!tariffFilter.includeTariff(tariff))
//								it.remove();
//						}
//					}
//
//					Comparator<Tariff> tariffComparator = _tariffComparator;
//					if (tariffComparator == null) {
//						tariffComparator = TariffList.tariffComparator;
//					}
//					Collections.sort(_tariffs, tariffComparator);
//
//					Display.getDefault().asyncExec(new Runnable() {
//						public void run() {
//							if (isDisposed())
//								return;
//
//							tariffs = _tariffs;
//							setInput(_tariffs);
//							if (!selectedTariffs.isEmpty()) {
//								setSelectedElements(selectedTariffs);
//							} else {
//								setSelection(StructuredSelection.EMPTY);
//							}
//
//							TariffList.this.getParent().layout(true);
//						}
//					});
//				} catch (Exception x) {
//					throw new RuntimeException(x);
//				}
//
//				return Status.OK_STATUS;
//			}
//		}.schedule();
//	}

	public void loadTariffs(final Comparator<Tariff> _tariffComparator, final TariffFilter tariffFilter) {
		loadTariffs(_tariffComparator, tariffFilter, null);
	}

	/**
	 * Load the tariffs. When this composite is created, it is initially empty. In order to make it display
	 * the tariffs, this method needs to be called on the UI thread. It spawns a {@link Job} which loads the data,
	 * orders it with the given {@link Comparator} and displays it. If no <code>Comparator</code> is given, it
	 * sorts according to the tariff's name (obtained via {@link Tariff#getName()} and using the default {@link Locale}).
	 * <p>
	 * If not all {@link Tariff}s should be displayed, you can pass a {@link TariffFilter} to the constructor.
	 * </p>
	 *
	 * @param _tariffComparator The comparator to be used for sorting the {@link Tariff}s or <code>null</code> to use the built-in
	 *		default (sorting by the tariff's name).
	 * @param tariffFilter the filter to be used or <code>null</code> if the tariffs should not be filtered.
	 */
	public void loadTariffs(final Comparator<Tariff> _tariffComparator, final TariffFilter tariffFilter, final String preselectedTariffName) {
		this.tariffFilter = tariffFilter;

		setLoadingMessage(Messages.getString("org.nightlabs.jfire.trade.ui.tariff.TariffList.message.loadingTariffs")); //$NON-NLS-1$
		new Job(Messages.getString("org.nightlabs.jfire.trade.ui.tariff.TariffList.loadTariffsJob.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				try {
					final List<Tariff> _tariffs = TariffDAO.sharedInstance().getTariffs(filterOrganisationID, filterOrganisationIDInverse, FETCH_GROUPS_TARIFF,
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);

					if (tariffFilter != null) {
						for (Iterator<Tariff> it = _tariffs.iterator(); it.hasNext();) {
							Tariff tariff = it.next();
							if (!tariffFilter.includeTariff(tariff))
								it.remove();
						}
					}

					Comparator<Tariff> tariffComparator = _tariffComparator;
					if (tariffComparator == null) {
						tariffComparator = TariffList.tariffComparator;
					}
					Collections.sort(_tariffs, tariffComparator);

					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							if (isDisposed())
								return;

							tariffs = _tariffs;
							setInput(_tariffs);
							if (preselectedTariffName != null) {
								for (Tariff tariff : tariffs) {
									if (tariff.getName().getText().equals(preselectedTariffName)) {
										selectedTariffs.add(tariff);
									}
								}
							}
							if (!selectedTariffs.isEmpty()) {
								setSelectedElements(new ArrayList<Tariff>(selectedTariffs), true);
							}

							TariffList.this.getParent().layout(true);
						}
					});
				} catch (Exception x) {
					throw new RuntimeException(x);
				}

				return Status.OK_STATUS;
			}
		}.schedule();
	}

	public Tariff getSelectedTariff() {
		return getFirstSelectedElement();
	}

	public Collection<Tariff> getSelectedTariffs() {
		return getSelectedElements();
	}

	/**
	 * Keeps track of selected tariffs already before they are loaded from the server. This way, it is not necessary to wait or pass a callback, if the
	 * API client wants to select tariffs.
	 */
	private Set<Tariff> selectedTariffs = new HashSet<Tariff>();

	@Override
	public void setSelectedElements(Collection<Tariff> elements)
	{
		if (Display.getCurrent() == null)
			throw new IllegalStateException("Wrong thread! This method must be called on the SWT UI thread!"); //$NON-NLS-1$

		this.selectedTariffs.clear();
		this.selectedTariffs.addAll(elements);
		if (this.tariffs != null)
			super.setSelectedElements(elements);
	}

	public void setSelectedElements(Collection<Tariff> elements, boolean reveal)
	{
		if (Display.getCurrent() == null)
			throw new IllegalStateException("Wrong thread! This method must be called on the SWT UI thread!"); //$NON-NLS-1$

		this.selectedTariffs.clear();
		this.selectedTariffs.addAll(elements);
		if (this.tariffs != null)
			setSelection(new ArrayList<Tariff>(elements), reveal);
	}

	public void moveSelectedTariffOneUp() {
		int selected = tariffs.indexOf(getSelectedTariff());
		if (selected <= 0)
			return;

		Collections.swap(tariffs, selected, selected - 1);
		refresh();
	}

	public void moveSelectedTariffOneDown() {
		int selected = tariffs.indexOf(getSelectedTariff());
		if (selected >= tariffs.size()-1)
			return;

		Collections.swap(tariffs, selected, selected + 1);
		refresh();
	}

	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {
		table.setLayout(new WeightedTableLayout(new int[] {1}));
		new TableColumn(table, SWT.LEFT);
	}

	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setLabelProvider(labelProvider);
		tableViewer.setContentProvider(new TableContentProvider());
	}

	public List<Tariff> getOrderedTariffs() {
		return tariffs;
	}
}
