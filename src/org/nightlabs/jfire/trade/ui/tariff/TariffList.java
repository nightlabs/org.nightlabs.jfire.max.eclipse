package org.nightlabs.jfire.trade.ui.tariff;

import java.text.Collator;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

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

public class TariffList extends AbstractTableComposite<Tariff> {
	public static interface TariffFilter {
		boolean includeTariff(Tariff tariff);
	}
	private TariffFilter tariffFilter;

	private boolean organisationVisible = false;

	private String filterOrganisationID;
	private boolean filterOrganisationIDInverse = false;
	
	private List<Tariff> tariffs;
	
	private LabelProvider labelProvider = new TableLabelProvider() {
		public String getColumnText(Object element, int columnIndex) {
			Tariff tariff = (Tariff) element;
			return tariff.getName().getText() + (organisationVisible ? (" (" + tariff.getOrganisationID() + ")") : "");
		}
	};

	private static String getLocalOrganisationID() {
		try {
			return Login.getLogin().getOrganisationID();
		} catch (Exception x) {
			throw new RuntimeException(x);
		}
	}

	public TariffList(Composite parent, int style, boolean multiSelect, TariffFilter tariffFilter) {
		this(parent, style, multiSelect, tariffFilter, getLocalOrganisationID(), false);
	}

	public TariffList(Composite parent, int style, boolean multiSelect, TariffFilter tariffFilter, String filterOrganisationID,
			boolean filterOrganisationIDInverse) {
		super(parent, style, false);

		this.tariffFilter = tariffFilter;
		this.filterOrganisationID = filterOrganisationID;
		this.filterOrganisationIDInverse = filterOrganisationIDInverse;
		
		getTable().setHeaderVisible(false);
		
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

	public void loadTariffs(final Comparator<Tariff> _tariffComparator) {
		setLoadingMessage("Loading tariffs...");
		new Job(Messages.getString("org.nightlabs.jfire.trade.ui.tariff.TariffList.loadTariffsJob.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				try {
					// TODO create a TariffProvider in order to use the Cache
					//					AccountingManager accountingManager = AccountingManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
					//					final List<Tariff> _tariffs = new ArrayList<Tariff>(
					//							accountingManager.getTariffs(FETCH_GROUPS_TARIFFS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT));
					tariffs = TariffDAO.sharedInstance().getTariffs(filterOrganisationID, filterOrganisationIDInverse, FETCH_GROUPS_TARIFF,
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);

					if (tariffFilter != null) {
						for (Iterator<Tariff> it = tariffs.iterator(); it.hasNext();) {
							Tariff tariff = it.next();
							if (!tariffFilter.includeTariff(tariff))
								it.remove();
						}
					}
					
					Comparator<Tariff> tariffComparator = _tariffComparator;
					if (tariffComparator == null) {
						tariffComparator = new Comparator<Tariff>() {
							public int compare(Tariff o1, Tariff o2) {
								String s1 = o1.getName().getText(Locale.getDefault().getLanguage());
								String s2 = o2.getName().getText(Locale.getDefault().getLanguage());
								return Collator.getInstance().compare(s1, s2);
							}
						};
					}						
					Collections.sort(tariffs, tariffComparator);

					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							if (isDisposed())
								return;

							setInput(tariffs);
							
//							for (Tariff tariff : _tariffs) {
//								tariffList.add(tariff.getName().getText(Locale.getDefault().getLanguage())
//										+ (organisationVisible ? (" (" + tariff.getOrganisationID() + ")") : "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//							}
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
