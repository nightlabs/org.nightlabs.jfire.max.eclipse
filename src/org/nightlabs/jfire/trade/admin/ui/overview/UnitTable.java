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
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.accounting.id.CurrencyID;
import org.nightlabs.jfire.base.jdo.JDOObjectsChangedEvent;
import org.nightlabs.jfire.base.ui.jdo.ActiveJDOObjectController;
import org.nightlabs.jfire.base.ui.jdo.ActiveJDOObjectTableComposite;
import org.nightlabs.jfire.store.Unit;
import org.nightlabs.jfire.store.dao.UnitDAO;
import org.nightlabs.jfire.store.id.UnitID;
import org.nightlabs.jfire.trade.admin.ui.overview.CurrencyTable.CurrencyTypeLabelProvider;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;

public class UnitTable 
extends ActiveJDOObjectTableComposite<UnitID, Unit>
{
	public static final String[] FETCH_GROUP = new String[] {
		FetchPlan.DEFAULT, Unit.FETCH_GROUP_NAME, Unit.FETCH_GROUP_SYMBOL
	};
	
	public UnitTable(Composite parent, int style) {
		super(parent, style);
		load();
	}
	
	@Override
	protected ActiveJDOObjectController<UnitID, Unit> createActiveJDOObjectController() {
		
		return new UnitController();
	}

	@Override
	protected ITableLabelProvider createLabelProvider() {
		return new UnitTableLabelProvider();
	}

	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {
		TableColumn unitSymbol;
		TableColumn unitName;
		unitSymbol = new TableColumn(table, SWT.LEFT);
		unitName = new TableColumn(table, SWT.LEFT);
		unitSymbol.setText("Symbol");
		unitName.setText("Name");
		table.setLayout(new WeightedTableLayout(new int[]{1,1}));
		table.setLinesVisible(false);
	}
	
	private class UnitController extends ActiveJDOObjectController<UnitID, Unit> {

		@Override
		protected Class<? extends Unit> getJDOObjectClass() {
			return Unit.class;
		}

		@Override
		protected Collection<Unit> retrieveJDOObjects(Set<UnitID> objectIDs, ProgressMonitor monitor) {
			return UnitDAO.sharedInstance().getUnits(objectIDs, FETCH_GROUP, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
		}

		@Override
		protected Collection<Unit> retrieveJDOObjects(ProgressMonitor monitor) {
			return UnitDAO.sharedInstance().getUnits(FETCH_GROUP, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
		}

		@Override
		protected void sortJDOObjects(List<Unit> objects) {
			Collections.sort(objects, new Comparator<Unit>() {
				@Override
				public int compare(Unit o1, Unit o2) {
					return o1.getUnitID().compareTo(o2.getUnitID());
				}
			});
		}

		@Override
		protected void onJDOObjectsChanged(JDOObjectsChangedEvent<UnitID, Unit> event) {

		}
	}
	
	class UnitTableLabelProvider
	extends TableLabelProvider
	{
		public String getColumnText(Object element, int columnIndex)
		{
			if (element instanceof Unit) {
				Unit unit = (Unit) element;
				switch (columnIndex)
				{
				case(0):

					return unit.getSymbol().getText();
				case(1):

					return unit.getName().getText();
				default:
					return ""; //$NON-NLS-1$
				}
			}
			return null;
		}
	}
}