/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.parameter.guifactory.jfireobjects;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.workstation.Workstation;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class SelectedWorkstationsTable extends AbstractTableComposite<Workstation> {

	public class LabelProvider extends TableLabelProvider {
		@Override
		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof Workstation) {
				Workstation workstation = (Workstation) element;
				return String.format(
					"%s (%s)", //$NON-NLS-1$
					workstation.getWorkstationID(), workstation.getDescription()
				);
			}
			return ""; //$NON-NLS-1$
		}
	}
	
	private Collection<Workstation> workstations = new ArrayList<Workstation>();
	
	/**
	 * @param parent
	 * @param style
	 */
	public SelectedWorkstationsTable(Composite parent, int style) {
		super(parent, style);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#createTableColumns(org.eclipse.jface.viewers.TableViewer, org.eclipse.swt.widgets.Table)
	 */
	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {
		// TODO: FIXME: Fix Column layout on windows (column will be rendered only as wide as its header-text)
//		TableLayout l = new TableLayout();
//		new TableColumn(table, SWT.LEFT).setText("Selected user");
//		l.addColumnData(new ColumnWeightData(10));
//		table.setLayout(l);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#setTableProvider(org.eclipse.jface.viewers.TableViewer)
	 */
	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setLabelProvider(new LabelProvider());
		tableViewer.setInput(workstations);
	}
	
	public Collection<Workstation> getSelectedWorkstations() {
		return new ArrayList<Workstation>(workstations);
	}
	
	public void addWorkstation(Workstation workstation) {
		if (!workstations.contains(workstation)) {
			workstations.add(workstation);
			setInput(workstations);
		}
	}
	
	public void removeWorkstation(Workstation workstation) {
		workstations.remove(workstation);
		setInput(workstations);
	}

	public void removeSelectedWorkstations() {
		Collection<Workstation> sel = getSelectedElements();
		for (Workstation workstation : sel) {
			workstations.remove(workstation);
		}
		setInput(workstations);
	}
}
