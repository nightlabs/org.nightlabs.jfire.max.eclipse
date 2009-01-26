package org.nightlabs.jfire.issuetracking.ui.projectphase;

import javax.jdo.FetchPlan;

import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableContentProvider;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.issue.project.ProjectPhase;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;

/**
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 */
public class ProjectPhaseTable 
extends AbstractTableComposite<ProjectPhase> 
{
	/**
	 * The fetch groups of issue data.
	 */
	public static final String[] FETCH_GROUPS = new String[] {
		FetchPlan.DEFAULT,
		ProjectPhase.FETCH_GROUP_NAME};
	
	public ProjectPhaseTable(Composite parent, int style)
	{
		super(parent, style);
	}
	
	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {
		TableColumn tc;
		TableLayout layout = new TableLayout();

		tc = new TableColumn(table, SWT.LEFT);
		tc.setMoveable(true);
		tc.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.projectphase.ProjectPhaseTable.tableColumn.id.text")); //$NON-NLS-1$
		
		tc = new TableColumn(table, SWT.LEFT);
		tc.setMoveable(true);
		tc.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.projectphase.ProjectPhaseTable.tableColumn.phaseName.text")); //$NON-NLS-1$
		
		tc = new TableColumn(table, SWT.LEFT);
		tc.setMoveable(true);
		tc.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.projectphase.ProjectPhaseTable.tableColumn.description.text")); //$NON-NLS-1$
		
		tc = new TableColumn(table, SWT.LEFT);
		tc.setMoveable(true);
		tc.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.projectphase.ProjectPhaseTable.tableColumn.status.text")); //$NON-NLS-1$
		
		table.setLayout(new WeightedTableLayout(new int[] {5, 30, 30, 30}));
	}

	class ProjectPhaseLabelProvider
	extends TableLabelProvider
	{
		public String getColumnText(Object element, int columnIndex) 
		{
			if (element instanceof ProjectPhase) {
				ProjectPhase projectPhase = (ProjectPhase) element;
				switch (columnIndex) 
				{
				case(0):
					return projectPhase.getProjectPhaseID();
				case(1):
					return projectPhase.getName().getText();
				case(2):
					return projectPhase.getDescription().getText();
				case(3):
					return projectPhase.isActive() ? Messages.getString("org.nightlabs.jfire.issuetracking.ui.projectphase.ProjectPhaseTable.tableColumnText.active.text") : Messages.getString("org.nightlabs.jfire.issuetracking.ui.projectphase.ProjectPhaseTable.tableColumnText.inActive.text"); //$NON-NLS-1$ //$NON-NLS-2$
				default:
					return ""; //$NON-NLS-1$
				}
			}
			return null;
		}		
	}

	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setContentProvider(new TableContentProvider());
		tableViewer.setLabelProvider(new ProjectPhaseLabelProvider());
	}
}
