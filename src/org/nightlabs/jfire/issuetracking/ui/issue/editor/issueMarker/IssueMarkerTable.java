package org.nightlabs.jfire.issuetracking.ui.issue.editor.issueMarker;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.resource.SharedImages.ImageDimension;
import org.nightlabs.base.ui.resource.SharedImages.ImageFormat;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.issue.issueMarker.IssueMarker;
import org.nightlabs.jfire.issuetracking.ui.IssueTrackingPlugin;

//      ,-_|\
//     /     \  ]Egoiste in
//     @_,-._/       ]N[ightLabs
//  ======= v =====================================================================================
//  "Science without religion is lame, religion without science is blind." -- A. E. (1879 - 1955).
/**
 * A table to deal with IssueMarkerTableItems.
 *
 * @author Khaireel Mohamed - khaireel at nightlabs dot de
 */
public class IssueMarkerTable extends AbstractTableComposite<IssueMarker> {   //<IssueMarkerTableItem> {
	/**
	 * Creates a new instance of a IssueMarkerTable.
	 */
	public IssueMarkerTable(Composite parent) { this(parent, SWT.NONE, DEFAULT_STYLE_SINGLE_BORDER); }

	/**
	 * Creates a new instance of a IssueMarkerTable.
	 */
	public IssueMarkerTable(Composite parent, int style, int viewerStyle) {
		super(parent, style, true, viewerStyle);
		setHeaderVisible(false);
	}

	/**
	 * Creates a new instance of a IssueMarkerTable.
	 */
	public IssueMarkerTable(Composite parent, int style) { super(parent, style); }


	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#createTableColumns(org.eclipse.jface.viewers.TableViewer, org.eclipse.swt.widgets.Table)
	 */
	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {
		TableColumn tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setMoveable(true);
		tableColumn.setText("Name");

		tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setMoveable(true);
		tableColumn.setText("Description");

		WeightedTableLayout layout = new WeightedTableLayout(new int[]{30, 70});
		table.setLayout(layout);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#setTableProvider(org.eclipse.jface.viewers.TableViewer)
	 */
	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setLabelProvider(new IssMrkrLabelProvider());
		tableViewer.setContentProvider(new ArrayContentProvider());
	}



	/**
	 * Provides the contents for the IssueMarkerTable.
	 */
	private class IssMrkrLabelProvider extends TableLabelProvider {
		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			if (element != null && element instanceof IssueMarker && columnIndex == 0) {
				System.out.println("---------------- Aha! MOST convenient! It's asking for the image @colunmIndex: " + columnIndex);

				// FIXME Ensure this works for the general case!!
				IssueMarker issueMarker = (IssueMarker)element;
				String suffix = "Email";
				if (issueMarker.getName().getText().contains("Telephone")) suffix = "Telephone";
				if (issueMarker.getName().getText().contains("Suspended")) suffix = "Suspended";

				return SharedImages.getSharedImage(IssueTrackingPlugin.getDefault(), IssueMarkerSection.class, suffix, ImageDimension._16x16, ImageFormat.gif);
			}

			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			// Note: @colunmIndex 0 -- Name of the IssueMarker, and its related icon.
			//       @colunmIndex 1 -- Description of the IssueMarker.
			if (element != null && element instanceof IssueMarker) {
				IssueMarker issueMarker = (IssueMarker)element;

				if (columnIndex == 0) return issueMarker.getName().getText();
				if (columnIndex == 1) return issueMarker.getDescription().getText();
			}

			return "";
		}
	}

}
