/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.layout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.annotation.Implement;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableContentProvider;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.reporting.dao.ReportRegistryItemDAO;
import org.nightlabs.jfire.reporting.layout.ReportCategory;
import org.nightlabs.jfire.reporting.layout.ReportLayout;
import org.nightlabs.jfire.reporting.layout.ReportRegistryItem;
import org.nightlabs.jfire.reporting.layout.id.ReportRegistryItemID;
import org.nightlabs.jfire.reporting.ui.ReportingPlugin;
import org.nightlabs.jfire.reporting.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;

/**
 * A table that displays ReportRegistyItems.
 * It has one column showing the items name.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class ReportRegistryItemTable extends AbstractTableComposite<ReportRegistryItem> {
	
	protected class LabelProvider extends TableLabelProvider {
		@Implement
		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof ReportRegistryItem) {
				return ((ReportRegistryItem) element).getName().getText();
			}
			return String.valueOf(element);
		}
		
		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			if (element.getClass().equals(ReportCategory.class)) {
				if (((ReportCategory) element).isInternal())
					return SharedImages.getSharedImage(ReportingPlugin.getDefault(), ReportRegistryItemTable.class, "category-internal"); //$NON-NLS-1$
				else
					return SharedImages.getSharedImage(ReportingPlugin.getDefault(), ReportRegistryItemTable.class, "category-normal"); //$NON-NLS-1$
			}
			else if (element.getClass().equals(ReportLayout.class))
				return SharedImages.getSharedImage(ReportingPlugin.getDefault(), ReportRegistryItemTable.class, "layout"); //$NON-NLS-1$
			return null;
		}
	}
	

	/**
	 * @param parent
	 * @param style
	 */
	public ReportRegistryItemTable(Composite parent, int style) {
		super(parent, style);
	}

	/**
	 * @param parent
	 * @param style
	 * @param initTable
	 */
	public ReportRegistryItemTable(Composite parent, int style,
			boolean initTable) {
		super(parent, style, initTable);
	}

	
	/**
	 * @param parent
	 * @param style
	 * @param initTable
	 * @param viewerStyle
	 */
	public ReportRegistryItemTable(Composite parent, int style,
			boolean initTable, int viewerStyle) {
		super(parent, style, initTable, viewerStyle);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#createTableColumns(org.eclipse.jface.viewers.TableViewer, org.eclipse.swt.widgets.Table)
	 */
	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {
		TableColumn col = new TableColumn(table, SWT.LEFT);
		col.setText(Messages.getString("org.nightlabs.jfire.reporting.ui.layout.ReportRegistryItemTable.reportRegistryItemNameColumn.text")); //$NON-NLS-1$
		TableLayout l = new TableLayout();
		l.addColumnData(new ColumnWeightData(1));
		table.setLayout(l);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#setTableProvider(org.eclipse.jface.viewers.TableViewer)
	 */
	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setContentProvider(new TableContentProvider());
		tableViewer.setLabelProvider(new LabelProvider());
	}

	/**
	 * Loads the {@link ReportRegistryItem}s for the given ids in a Job an
	 * displays then when done.
	 * 
	 * @see #setReportRegistryItemIDs(Collection, ReportRegistryItemID)
	 * @param reportRegistryItemIDs The report registry item ids to display.
	 * @param fetchGroups The fetch-groups the items should be detached with.
	 */
	public void setReportRegistryItemIDs(final Collection<ReportRegistryItemID> reportRegistryItemIDs, String[] fetchGroups) {
		setReportRegistryItemIDs(reportRegistryItemIDs, null, fetchGroups);
	}

		
	
	/**
	 * Loads the {@link ReportRegistryItem}s for the given ids in a Job an
	 * displays then when done.
	 * 
	 * @param reportRegistryItemIDs The report registry item ids to display.
	 * @param defaultRegistryItemID The item that should be preselected.
	 * @param fetchGroups The fetch-groups the items should be detached with.
	 */
	public void setReportRegistryItemIDs(
			final Collection<ReportRegistryItemID> reportRegistryItemIDs,
			final ReportRegistryItemID defaultRegistryItemID,
			final String[] fetchGroups
	) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				setInput(Messages.getString("org.nightlabs.jfire.reporting.ui.layout.ReportRegistryItemTable.loadingText")); //$NON-NLS-1$
			}
		});
		Job loadJob = new Job(Messages.getString("org.nightlabs.jfire.reporting.ui.layout.ReportRegistryItemTable.loadReportRegistryItemsJob.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				final Collection<ReportRegistryItem> items = ReportRegistryItemDAO.sharedInstance().getReportRegistryItems(
						new HashSet<ReportRegistryItemID>(reportRegistryItemIDs),
						fetchGroups,
						monitor
					);
				ReportRegistryItem defaultRegistryItem = null;
				if (defaultRegistryItemID != null) {
					defaultRegistryItem = ReportRegistryItemDAO.sharedInstance().getReportRegistryItem(
							defaultRegistryItemID,
							fetchGroups,
							monitor
						);
				}
				final ReportRegistryItem finalDefaultItem = defaultRegistryItem;
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						setInput(items);
						if (finalDefaultItem != null) {
							final java.util.List<ReportRegistryItem> sel = new ArrayList<ReportRegistryItem>(1);
							sel.add(finalDefaultItem);
							setSelection(sel, true);
						}
					}
				});
				
				return Status.OK_STATUS;
			}
		};
		loadJob.schedule();
	}

}
