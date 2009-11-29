package org.nightlabs.jfire.asterisk.ui.asteriskserver;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.nightlabs.base.ui.labelprovider.ColumnSpanLabelProvider;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.jfire.asterisk.AsteriskServer;
import org.nightlabs.jfire.asterisk.ui.resource.Messages;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 */
public class CallFilePropertyTable
extends AbstractTableComposite<Map.Entry<String, String>>
{
	public static final String KEY_COLUMN_ID = "key"; //$NON-NLS-1$
	public static final String VALUE_COLUMN_ID = "value"; //$NON-NLS-1$

	/**
	 * @param parent
	 * @param style
	 */
	public CallFilePropertyTable(Composite parent) {
		this(parent, SWT.NONE, DEFAULT_STYLE_MULTI_BORDER);
	}

	/**
	 * @param parent
	 * @param style
	 * @param initTable
	 */
	public CallFilePropertyTable(Composite parent, int style, int viewerStyle) {
		super(parent, style, true, viewerStyle);
		getTable().setHeaderVisible(true);
		getTableViewer().setSorter(new ViewerSorter());
		hookContextMenu();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				CallFilePropertyTable.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(getTableViewer().getControl());
		getTableViewer().getControl().setMenu(menu);
//		if (getSite() != null)
//			getSite().registerContextMenu(menuMgr, getTableViewer());
	}

	/**
	 * Contains instances of both, {@link IContributionItem} and {@link IAction}
	 */
	private List<Object> contextMenuContributions;

	public void addContextMenuContribution(IContributionItem contributionItem)
	{
		if (contextMenuContributions == null)
			contextMenuContributions = new LinkedList<Object>();

		contextMenuContributions.add(contributionItem);
	}

	public void addContextMenuContribution(IAction action)
	{
		if (contextMenuContributions == null)
			contextMenuContributions = new LinkedList<Object>();

		contextMenuContributions.add(action);
	}

	private void fillContextMenu(IMenuManager manager) {
		if (contextMenuContributions != null) {
			for (Object contextMenuContribution : contextMenuContributions) {
				if (contextMenuContribution instanceof IContributionItem)
					manager.add((IContributionItem)contextMenuContribution);
				else if (contextMenuContribution instanceof IAction)
					manager.add((IAction)contextMenuContribution);
				else
					throw new IllegalStateException("How the hell got an instance of " + (contextMenuContribution == null ? "null" : contextMenuContribution.getClass()) + " in the contextMenuContributions list?!"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		}

		// Other plug-ins can contribute their actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {
		TableColumn tc;

		tc = new TableColumn(table, SWT.LEFT); // @column 0
		tc.setMoveable(true);
		tc.setText(Messages.getString("org.nightlabs.jfire.asterisk.ui.asteriskserver.CallFilePropertyTable.columnHeader[key].text")); //$NON-NLS-1$

		tc = new TableColumn(table, SWT.LEFT); // @column 1
		tc.setMoveable(true);
		tc.setText(Messages.getString("org.nightlabs.jfire.asterisk.ui.asteriskserver.CallFilePropertyTable.columnHeader[value].text")); //$NON-NLS-1$

		WeightedTableLayout layout = new WeightedTableLayout(new int[]{
				30,
				70});
		table.setLayout(layout);

		tableViewer.setColumnProperties(new String[] {KEY_COLUMN_ID, VALUE_COLUMN_ID});
		tableViewer.setCellEditors(new CellEditor[] {
				null,
				new TextCellEditor(table)}
		);
		tableViewer.setCellModifier(new CallFilePropertyCellModifier());
	}

	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setContentProvider(new CallFilePropertyContentProvider());
		tableViewer.setLabelProvider(new CallFilePropertyLabelProvider(tableViewer));
	}

	@Override
	public void setInput(Object input) {
		if (!(input instanceof AsteriskServer))
			throw new IllegalArgumentException("input must be an instance of AsteriskServer, but is: " + input); //$NON-NLS-1$
		setAsteriskServer((AsteriskServer)input);
	}

	private AsteriskServer asteriskServer;
	private void setAsteriskServer(AsteriskServer asteriskServer) {
		this.asteriskServer = asteriskServer;
		super.setInput(asteriskServer);
	}

	protected class CallFilePropertyContentProvider implements IStructuredContentProvider {
		@Override
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof AsteriskServer) {
				AsteriskServer asteriskServer = (AsteriskServer) inputElement;
				Collection<Map.Entry<String, String>> c = asteriskServer.getCallFileProperties().entrySet();
				@SuppressWarnings("unchecked")
				Map.Entry<String, String>[] a = c.toArray(new Map.Entry[c.size()]);
				return a;
			}
			return null;
		}

		@Override
		public void dispose() {

		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

		}
	}

	private class CallFilePropertyLabelProvider
	extends ColumnSpanLabelProvider
	{
		public CallFilePropertyLabelProvider(ColumnViewer columnViewer) {
			super(columnViewer);
		}

		@Override
		public String getColumnText(Object element, int columnIndex)
		{
			@SuppressWarnings("unchecked")
			Map.Entry<String, String> entry = (Map.Entry<String, String>)element;

			switch (columnIndex) {
			case 0:
				return entry.getKey();
			case 1:
				return entry.getValue();
			default: return "";//$NON-NLS-1$
			}
		}

		@Override
		protected int[][] getColumnSpan(Object element) {
			return null;
		}
	}

	private class CallFilePropertyCellModifier implements ICellModifier {
		@Override
		public boolean canModify(Object element, String property) {
			return property.equals(VALUE_COLUMN_ID);
		}

		@Override
		public Object getValue(Object element, String property) {
			if(property.equals(VALUE_COLUMN_ID)){
				@SuppressWarnings("unchecked")
				Map.Entry<String, String> me = (Map.Entry<String, String>)element;
				return me.getValue();
			}
			return null;
		}

		@Override
		public void modify(Object element, String property, Object value) {
			if (!property.equals(VALUE_COLUMN_ID)){
				return;
			}

			TableItem tableItem = (TableItem)element;
			@SuppressWarnings("unchecked")
			Map.Entry<String, String> entry = (Map.Entry<String, String>)tableItem.getData();
			if (value.equals(entry.getValue()))
				return; // modify only if really changed (e.g. no mark-dirty, if the value is unchanged)
//			entry.setValue((String)value);

			//Don't know what's wrong with it, These lines fix the NPE of the element. (I think because of the Map.Entry.equals' method)
			asteriskServer.setCallFileProperty(entry.getKey(), (String)value);
//			setAsteriskServer(asteriskServer); // This line calls setInput and thus all selection and other properties get lost.

//			getTableViewer().update(entry, new String[] { property });
			getTableViewer().refresh();
			fireModification();
		}
	}

	private ListenerList modifyListeners = new ListenerList();
	public void addCallFilePropertyModifyListener(CallFilePropertyModifyListener listener) {
		modifyListeners.add(listener);
	}
	public void removeCallFilePropertyModifyListener(CallFilePropertyModifyListener listener) {
		modifyListeners.remove(listener);
	}
	private void fireModification()
	{
		CallFilePropertyModifyEvent event =  new CallFilePropertyModifyEvent(this);
		for( int i=0; i< modifyListeners.size(); i++){
			CallFilePropertyModifyListener modifylistener = (CallFilePropertyModifyListener)modifyListeners.getListeners()[i];
			modifylistener.modifyValue(event);
		}
	}
}