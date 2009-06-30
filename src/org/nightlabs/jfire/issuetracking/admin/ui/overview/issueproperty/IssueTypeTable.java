package org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty;

import javax.jdo.FetchPlan;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableContentProvider;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.issue.IssueType;
import org.nightlabs.jfire.issue.dao.IssueTypeDAO;
import org.nightlabs.jfire.issuetracking.admin.ui.resource.Messages;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * This composite lists all {@link IssueType}s of an issue type in a table.
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 */
public class IssueTypeTable
extends AbstractTableComposite<IssueType>
{
	/**
	 * The fetch groups of issue data.
	 */
	public static final String[] FETCH_GROUPS = new String[] {
		FetchPlan.DEFAULT,
		IssueType.FETCH_GROUP_NAME};

	public IssueTypeTable(Composite parent, int style)
	{
		super(parent, style);
		setInput(IssueTypeDAO.sharedInstance().getAllIssueTypes(IssueTypeTable.FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor()));
	}

//	@Override
//	protected ActiveJDOObjectController<IssueTypeID, IssueType> createActiveJDOObjectController() {
//		return new ActiveJDOObjectController<IssueTypeID, IssueType>() {
//
//			@Override
//			protected Class<? extends IssueType> getJDOObjectClass() {
//				return IssueType.class;
//			}
//
//			@Override
//			protected Collection<IssueType> retrieveJDOObjects(
//					Set<IssueTypeID> objectIDs, ProgressMonitor monitor) {
//
//				return IssueTypeDAO.sharedInstance().getIssueTypes(objectIDs, IssueTypeTable.FETCH_GROUPS,
//						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
//						new NullProgressMonitor());
//			}
//
//			@Override
//			protected Collection<IssueType> retrieveJDOObjects(
//					ProgressMonitor monitor) {
//				return IssueTypeDAO.sharedInstance().getIssueTypes(IssueTypeTable.FETCH_GROUPS,
//						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
//						new NullProgressMonitor());
//			}
//
//			@Override
//			protected void sortJDOObjects(List<IssueType> objects) {
//			}
//		};
//	}

	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {
		TableColumn tc;
		TableLayout layout = new TableLayout();

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty.IssueTypeTable.tableColumn.name.text")); //$NON-NLS-1$
		layout.addColumnData(new ColumnWeightData(30));

		table.setLayout(layout);
		table.setHeaderVisible(false);

		table.setLinesVisible(false);
	}

//	@Override
//	protected ITableLabelProvider createLabelProvider() {
//		return new IssueTypeLabelProvider();
//	}

	class IssueTypeLabelProvider
	extends TableLabelProvider
	{
		public String getColumnText(Object element, int columnIndex)
		{
			if (element instanceof IssueType) {
				IssueType issueType = (IssueType) element;
				switch (columnIndex)
				{
				case(0):
					return issueType.getName().getText();
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
		tableViewer.setLabelProvider(new IssueTypeLabelProvider());
	}
}
