package org.nightlabs.jfire.issuetracking.ui.issuehistory;

import java.io.ByteArrayInputStream;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import javax.jdo.JDOHelper;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
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
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleEvent;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleListener;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleManager;
import org.nightlabs.jfire.base.ui.jdo.notification.JDOLifecycleAdapterJob;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.history.IssueHistoryItem;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;
import org.nightlabs.jfire.jdo.notification.IJDOLifecycleListenerFilter;
import org.nightlabs.jfire.jdo.notification.JDOLifecycleState;
import org.nightlabs.jfire.jdo.notification.SimpleLifecycleListenerFilter;

/**
 * This composite lists all {@link IssueHistoryItem}s of an issue in a table.
 *
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 * @author Khaireel Mohamed - khaireel at nightlabs dot de
 */
public class IssueHistoryTable extends AbstractTableComposite<IssueHistoryItem> {
	/**
	 * Creates a new instance of an IssueHistoryTable.
	 */
	public IssueHistoryTable(Composite parent, int style) {
		super(parent, style);

		getTableViewer().addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent e) {

			}
		});

		JDOLifecycleManager.sharedInstance().addLifecycleListener(myLifecycleListener);
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent event) {
				JDOLifecycleManager.sharedInstance().removeLifecycleListener(myLifecycleListener);
			}
		});

		getTableViewer().setComparator(new ViewerComparator() {
			@Override
			public void sort(Viewer viewer, Object[] elements) {
				Arrays.sort(elements, new Comparator<Object>() {
					public int compare(Object object1, Object object2) {
						return -((IssueHistoryItem)object1).getCreateTimestamp().compareTo(((IssueHistoryItem)object2).getCreateTimestamp());
					}
				});
			}
		});


		// Since 29 May 2009.
		addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent event) { disposeAllImages(); }
		});
	}

	private JDOLifecycleListener myLifecycleListener = new JDOLifecycleAdapterJob(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuehistory.IssueHistoryTable.lifeCycleListener.loading.text")) { //$NON-NLS-1$
		private IJDOLifecycleListenerFilter filter = new SimpleLifecycleListenerFilter(
				Issue.class,
				true,
				JDOLifecycleState.NEW);

		public IJDOLifecycleListenerFilter getJDOLifecycleListenerFilter() {
			return filter;
		}

		public void notify(JDOLifecycleEvent event) {}
	};

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#createTableColumns(org.eclipse.jface.viewers.TableViewer, org.eclipse.swt.widgets.Table)
	 */
	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {
		TableColumn tc;

		tc = new TableColumn(table, SWT.LEFT);
		tc.setMoveable(true);
		tc.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuehistory.IssueHistoryTable.tableColumn.date.text")); //$NON-NLS-1$

		tc = new TableColumn(table, SWT.LEFT);
		tc.setMoveable(true);
		tc.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuehistory.IssueHistoryTable.tableColumn.userName.text")); //$NON-NLS-1$

		tc = new TableColumn(table, SWT.LEFT);
		tc.setMoveable(true);
		tc.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuehistory.IssueHistoryTable.tableColumn.action.text")); //$NON-NLS-1$

		WeightedTableLayout layout = new WeightedTableLayout(new int[]{15, 10, 75});
		table.setLayout(layout);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#setTableProvider(org.eclipse.jface.viewers.TableViewer)
	 */
	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setContentProvider(new ArrayContentProvider()); //(new TableContentProvider());
		tableViewer.setLabelProvider(new IssueHistoryListLabelProvider());
	}

	private static DateFormat dateTimeFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);




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


	// -------------------------------------------------------------------------------------------------------------------------|
	class IssueHistoryListLabelProvider extends TableLabelProvider {
		@Override
		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof IssueHistoryItem) {
				IssueHistoryItem issueHistoryItem = (IssueHistoryItem) element;
				switch (columnIndex) {
				case(0): return dateTimeFormat.format(issueHistoryItem.getCreateTimestamp());
				case(1): return issueHistoryItem.getUser().getName();
				case(2): return issueHistoryItem.getDescription(); //.getChange();
				}
			}
			return ""; //$NON-NLS-1$
		}

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			if (element != null && element instanceof IssueHistoryItem && columnIndex == 2) {
				IssueHistoryItem issueHistoryItem = (IssueHistoryItem)element;
				String imageKey = JDOHelper.getObjectId(issueHistoryItem).toString();

				Image icon = imageKey2Image.get(imageKey);
				if (icon == null) {
					byte[] iconByte = issueHistoryItem.getIcon16x16Data();
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
	}




	// -------------------------------------------------------------------------------------------------------------------------|
	public void setLoadingStatus() {
		super.setInput(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuehistory.IssueHistoryTable.tableColumnText.loading.text")); //$NON-NLS-1$
	}



	//    TO CHECK: Do we really need these? Kai
	// --> CHECKED: No we dont. Kai.
	//	private IssueID issueID;
	//	public void setIssueHistoryItems(IssueID issueID, Collection<IssueHistoryItem> issueHistoryItems) {
	//		if (issueID == null)
	//			throw new IllegalArgumentException("issueID == null"); //$NON-NLS-1$
	//
	//		this.issueID = issueID;
	//		super.setInput(issueHistoryItems);
	//	}
	//
	//	@Override
	//	public void setInput(Object input) {
	//		throw new UnsupportedOperationException("Use setIssueHistories(...) or setLoadingStatus(...) instead!"); //$NON-NLS-1$
	//	}

}

