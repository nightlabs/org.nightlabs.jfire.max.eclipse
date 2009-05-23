package org.nightlabs.jfire.issuetracking.ui.issue.editor.issueMarker;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collection;

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

//      ,-_|\
//     /     \  ]Egoiste in
//     @_,-._/       ]N[ightLabs
//  ======= v =====================================================================================
//  "Science without religion is lame, religion without science is blind." -- A. E. (1879 - 1955).
/**
 * A table, filtered, showing only contents that are available to be used as an IssueMarker.
 *
 * @author Khaireel Mohamed - khaireel at nightlabs dot de
 */
public class IssueMarkerWizardTable extends AbstractTableComposite<IssueMarker> {
	private Collection<IssueMarker> currentContentsOnSectionTable;
	/**
	 * Creates a new instance of an IssueMarkerWizardTable.
	 */
	public IssueMarkerWizardTable(Composite parent, Collection<IssueMarker> currentContentsOnSectionTable) {
		this(parent, SWT.NONE, DEFAULT_STYLE_SINGLE_BORDER);
		this.currentContentsOnSectionTable = currentContentsOnSectionTable;
	}

	/**
	 * Creates a new instance of an IssueMarkerWizardTable.
	 */
	public IssueMarkerWizardTable(Composite parent, int style, int viewerStyle) {
		super(parent, style, true, viewerStyle);
		setHeaderVisible(false);

		addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent event) { disposeAllImages(); }
		});
	}

	@Override
	public void setInput(Object input) {
		Collection<?> issueMarkers = (Collection<?>)input;

		Collection<IssueMarker> filteredInputs = new ArrayList<IssueMarker>();
		for (Object issueMarker : issueMarkers)
			if ( !currentContentsOnSectionTable.contains(issueMarker) ) {
				filteredInputs.add( (IssueMarker)issueMarker );
			}


		super.setInput(filteredInputs);

		if (!filteredInputs.isEmpty())
			select(0);
	}


	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#createTableColumns(org.eclipse.jface.viewers.TableViewer, org.eclipse.swt.widgets.Table)
	 */
	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {
		TableColumn tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setMoveable(true);
		tableColumn.setText("Marker name");

		tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setMoveable(true);
		tableColumn.setText("Description");

		WeightedTableLayout layout = new WeightedTableLayout(new int[]{20, 70});
		table.setLayout(layout);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#setTableProvider(org.eclipse.jface.viewers.TableViewer)
	 */
	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setLabelProvider(new IssMrkrWizLabelProvider());
		tableViewer.setContentProvider(new ArrayContentProvider());
	}


	// ------------------------------------------------------------------------------------------------------
	// Cleanly control the images, so that we dont have to keep creating new ones, even after it was
	// removed from the table entry, and the re-added again later.
	private Collection<Image> iconImages = new ArrayList<Image>();

	/**
	 * Disposes all images.
	 */
	private void disposeAllImages() {
		for (Image image : iconImages)
			image.dispose();

		iconImages.clear();
	}

	// ------------------------------------------------------------------------------------------------------
	/**
	 * Provides the contents for the IssueMarkerWizardTable.
	 */
	private class IssMrkrWizLabelProvider extends TableLabelProvider {
		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			if (element != null && element instanceof IssueMarker && columnIndex == 0) {
				IssueMarker issueMarker = (IssueMarker)element;
				ByteArrayInputStream in = new ByteArrayInputStream( issueMarker.getIcon16x16Data() );
				Image icon = new Image(getDisplay(), in);
				iconImages.add(icon);

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

			return "";
		}
	}
}
