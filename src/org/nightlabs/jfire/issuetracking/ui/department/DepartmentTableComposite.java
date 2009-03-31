package org.nightlabs.jfire.issuetracking.ui.department;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.nightlabs.jfire.base.ui.jdo.ActiveJDOObjectController;
import org.nightlabs.jfire.base.ui.jdo.ActiveJDOObjectTableComposite;
import org.nightlabs.jfire.issue.project.Department;
import org.nightlabs.jfire.issue.project.id.DepartmentID;

/**
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 *
 */
public class DepartmentTableComposite 
extends ActiveJDOObjectTableComposite<DepartmentID, Department> 
{

	public DepartmentTableComposite(Composite parent, int style)
	{
		super(parent, style);
		load();
	}

	@Override
	protected ActiveJDOObjectController<DepartmentID, Department> createActiveJDOObjectController() {
		return new ActiveDepartmentController();
	}

	@Override
	protected ITableLabelProvider createLabelProvider() {
		return null;
	}

	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {
		TableLayout tableLayout = new TableLayout();
		TableViewerColumn c;

		c = new TableViewerColumn(tableViewer, SWT.LEFT);
		c.getColumn().setText("Name");
		c.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element)
			{
				return ((Department)element).getName().getText();
			}
		});
		tableLayout.addColumnData(new ColumnPixelData(100));

		table.setLayout(tableLayout);

		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent arg0)
			{
			}
		});

	}

}