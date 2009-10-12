package org.nightlabs.jfire.trade.admin.ui.overview;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.accounting.dao.CurrencyDAO;
import org.nightlabs.jfire.accounting.id.CurrencyID;
import org.nightlabs.jfire.base.jdo.JDOObjectsChangedEvent;
import org.nightlabs.jfire.base.ui.jdo.ActiveJDOObjectController;
import org.nightlabs.jfire.base.ui.jdo.ActiveJDOObjectTableComposite;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;

/**
 *
 * @author vince
 *
 */
public class CurrencyTable extends ActiveJDOObjectTableComposite<CurrencyID, Currency>
{
	/**
	 * The fetch groups of issue data.
	 */
	public static final String[] FETCH_GROUPS = new String[] { FetchPlan.DEFAULT};

	private class CurrencyController extends ActiveJDOObjectController<CurrencyID, Currency> {

		@Override
		protected Class<? extends Currency> getJDOObjectClass() {
			return Currency.class;
		}

		@Override
		protected Collection<Currency> retrieveJDOObjects(Set<CurrencyID> objectIDs, ProgressMonitor monitor) {
			return CurrencyDAO.sharedInstance().getCurrencies(objectIDs, monitor);
		}

		@Override
		protected Collection<Currency> retrieveJDOObjects(ProgressMonitor monitor) {
			return CurrencyDAO.sharedInstance().getCurrencies(monitor);
		}

		@Override
		protected void sortJDOObjects(List<Currency> objects) {
			Collections.sort(objects, new Comparator<Currency>() {
				@Override
				public int compare(Currency o1, Currency o2) {
					return o1.getCurrencyID().compareTo(o2.getCurrencyID());
				}
			});
		}

		@Override
		protected void onJDOObjectsChanged(JDOObjectsChangedEvent<CurrencyID, Currency> event) {

		}
	}

	public CurrencyTable(Composite parent, int style) {
		super(parent, style);
		load();

	}
	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {
		TableColumn tcurrencyId;
		TableColumn tcurrencySymbol;
		tcurrencyId = new TableColumn(table, SWT.LEFT);
		tcurrencySymbol = new TableColumn(table, SWT.LEFT);
		tcurrencyId.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.overview.CurrencyTable.tablecolumn.currencyId.text")); //$NON-NLS-1$
		tcurrencySymbol.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.overview.CurrencyTable.tablecolumn.currencySymbol.text"));
		table.setLayout(new WeightedTableLayout(new int[]{1,1}));
		table.setHeaderVisible(false);
		table.setLinesVisible(false);

	}

	class CurrencyTypeLabelProvider
	extends TableLabelProvider
	{
		public String getColumnText(Object element, int columnIndex)
		{
			if (element instanceof Currency) {
				Currency currencyType = (Currency) element;
				switch (columnIndex)
				{
				case(0):

					return currencyType.getCurrencyID();
				case(1):

					return currencyType.getCurrencySymbol();
				default:
					return ""; //$NON-NLS-1$
				}
			}
			return null;
		}
	}



	@Override
	protected ActiveJDOObjectController<CurrencyID, Currency> createActiveJDOObjectController() {
		return new CurrencyController();
	}

	@Override
	protected ITableLabelProvider createLabelProvider() {
		return new CurrencyTypeLabelProvider();
	}

}
