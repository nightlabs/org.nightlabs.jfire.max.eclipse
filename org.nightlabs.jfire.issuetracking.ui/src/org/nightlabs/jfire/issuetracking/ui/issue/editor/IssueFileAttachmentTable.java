package org.nightlabs.jfire.issuetracking.ui.issue.editor;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.issue.IssueFileAttachment;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;

/**
 * An {@link AbstractTableComposite} to deal with displaying {@link IssueFileAttachment}s.
 * The table currently displays two main information about the attached file:
 *   [At column 0] -- the filename; and
 *   [At column 1] -- the filesize in bytes.
 *
 * @author Khaireel Mohamed - khaireel at nightlabs dot de
 */
public class IssueFileAttachmentTable
extends AbstractTableComposite<IssueFileAttachment>
{
	/**
	 * Creates a new instance of an IssueFileAttachmentTable.
	 */
	public IssueFileAttachmentTable(Composite parent) { this(parent, SWT.NONE, DEFAULT_STYLE_SINGLE_BORDER); }

	/**
	 * Creates a new instance of an IssueFileAttachmentTable.
	 */
	public IssueFileAttachmentTable(Composite parent, int style, int viewerStyle) {
		super(parent, style, true, viewerStyle);
//		setHeaderVisible(false);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#createTableColumns(org.eclipse.jface.viewers.TableViewer, org.eclipse.swt.widgets.Table)
	 */
	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {
		TableColumn tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setMoveable(true);
		tableColumn.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueFileAttachmentTable.column.filename.text")); //$NON-NLS-1$

		tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setMoveable(true);
		tableColumn.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueFileAttachmentTable.column.size.text")); //$NON-NLS-1$

		WeightedTableLayout layout = new WeightedTableLayout(new int[]{70, 30});
		table.setLayout(layout);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#setTableProvider(org.eclipse.jface.viewers.TableViewer)
	 */
	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setLabelProvider(new IssueFileAttachmentTableLabelProvider());
		tableViewer.setContentProvider(new ArrayContentProvider());
	}


	// -----------------------------------------------------------------------------------------------------|
	/**
	 * Provides the contents for the {@link IssueFileAttachmentTable}.
	 */
	private class IssueFileAttachmentTableLabelProvider extends TableLabelProvider {
		@Override
		public String getColumnText(Object element, int columnIndex) {
			// Note: @colunmIndex 0 -- Filename.
			//       @colunmIndex 1 -- Filesize.
			if (element != null && element instanceof IssueFileAttachment) {
				IssueFileAttachment issueFileAttachment = (IssueFileAttachment)element;

				if (columnIndex == 0) return issueFileAttachment.getFileName();
				if (columnIndex == 1) return IssueFileAttachmentUtil.getFileSizeString(issueFileAttachment); //$NON-NLS-1$
			}

			return ""; //$NON-NLS-1$
		}
	}

}
