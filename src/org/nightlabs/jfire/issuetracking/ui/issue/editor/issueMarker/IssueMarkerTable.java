package org.nightlabs.jfire.issuetracking.ui.issue.editor.issueMarker;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.issue.issueMarker.IssueMarker;

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
			if (element != null && element instanceof IssueMarker) {
				System.out.println("---------------- Aha! MOST convenient! It's asking for the image @colunmIndex: " + columnIndex);

//				IssueMarker issueMarker = (IssueMarker)element;
//				return issueMarker.getIcon16x16Data().;
			}
//			if (columnIndex == 0) {
//				if (element instanceof IssueLinkTableItem) {
//					IssueLinkTableItem issueLinkTableItem = (IssueLinkTableItem) element;
//					IssueLinkHandler<ObjectID, Object> handler = getIssueLinkHandler(issueLinkTableItem.getLinkedObjectID());
//					IssueLink issueLink = issueLinkTableItem.getIssueLink();
//					if (issueLink == null)
//						return null; // TODO we should return an image symbolising that currently data is loaded. issueLinkTableItem.getIssueLink() is only null, if there is currently a Jab running in the background loading data for a newly created IssueLink.
//
//					Object linkedObject = issueLink2LinkedObjectMap.get(issueLink);
//
//					return handler.getLinkedObjectImage(issueLink, linkedObject);
//				}
//			}
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
