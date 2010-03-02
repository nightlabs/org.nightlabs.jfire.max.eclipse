package org.nightlabs.jfire.issuetracking.ui.issue.editor.issueMarker;
// TODO this package should be called issuemarker (small "m")!!!
// TODO this class should be in the package org.nightlabs.jfire.issuetracking.ui.issuemarker, because it has IMHO nothing to do with an editor.

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import javax.jdo.JDOHelper;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.issue.issuemarker.IssueMarker;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;

/**
 * An {@link AbstractTableComposite} to deal with displaying {@link IssueMarker}s.
 *
 * @author Khaireel Mohamed - khaireel at nightlabs dot de
 */
public class IssueMarkerTable extends AbstractTableComposite<IssueMarker> {   //<IssueMarkerTableItem> {
	/**
	 * Creates a new instance of an IssueMarkerTable.
	 */
	public IssueMarkerTable(Composite parent) { this(parent, SWT.NONE, DEFAULT_STYLE_SINGLE_BORDER); }

	/**
	 * Creates a new instance of an IssueMarkerTable.
	 */
	public IssueMarkerTable(Composite parent, int style, int viewerStyle) {
		super(parent, style, true, viewerStyle);
		setHeaderVisible(false);

		addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent event) { disposeAllImages(); }
		});
	}


	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#createTableColumns(org.eclipse.jface.viewers.TableViewer, org.eclipse.swt.widgets.Table)
	 */
	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {
		TableColumn tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setMoveable(true);
		tableColumn.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.issueMarker.IssueMarkerTable.column.markerName")); //$NON-NLS-1$

		tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setMoveable(true);
		tableColumn.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.issueMarker.IssueMarkerTable.column.description")); //$NON-NLS-1$

		WeightedTableLayout layout = new WeightedTableLayout(new int[]{20, 70});
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



	// ------------------------------------------------------------------------------------------------------
	// Cleanly control the images, so that we dont have to keep creating new ones, even after it was
	// removed from the table entry, and the re-added again later.
	private Map<String, Image> imageKey2Image = new HashMap<String, Image>();

	/**
	 * Disposes all images.
	 */
	private void disposeAllImages() {
		for (Image image : imageKey2Image.values())
			image.dispose();

		imageKey2Image.clear();
	}


	// -----------------------------------------------------------------------------------------------------|
	/**
	 * Provides the contents for the IssueMarkerTable.
	 */
	private class IssMrkrLabelProvider extends TableLabelProvider {
		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			if (element != null && element instanceof IssueMarker && columnIndex == 0) {
				IssueMarker issueMarker = (IssueMarker)element;
				String imageKey = JDOHelper.getObjectId(issueMarker).toString();

				Image icon = imageKey2Image.get(imageKey);
				if (icon == null) {
					byte[] iconByte = issueMarker.getIcon16x16Data();
					if (iconByte != null) {
						ByteArrayInputStream in = new ByteArrayInputStream( iconByte );
						icon = new Image(getDisplay(), in);

						imageKey2Image.put(imageKey, icon);
					}
				}

				return icon;
			}

			return super.getColumnImage(element, columnIndex);
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			// Note: @colunmIndex 0 -- Name of the IssueMarker, and its related icon.
			//       @colunmIndex 1 -- Description of the IssueMarker.
			if (element != null && element instanceof IssueMarker) {
				IssueMarker issueMarker = (IssueMarker)element;

				if (columnIndex == 0) return issueMarker.getName().getText();
				if (columnIndex == 1) return issueMarker.getDescription().getText();
			}

			return ""; //$NON-NLS-1$
		}
	}

}
