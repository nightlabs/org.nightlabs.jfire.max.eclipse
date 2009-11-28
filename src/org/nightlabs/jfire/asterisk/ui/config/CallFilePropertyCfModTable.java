package org.nightlabs.jfire.asterisk.ui.config;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.nightlabs.base.ui.labelprovider.ColumnSpanLabelProvider;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.notification.IDirtyStateManager;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.EmulatedNativeCheckBoxTableLabelProvider;
import org.nightlabs.jfire.asterisk.AsteriskServer;
import org.nightlabs.jfire.asterisk.config.AsteriskConfigModule;
import org.nightlabs.util.Util;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 * @author Marco หงุ่ยตระกูล-Schulze - marco at nightlabs dot de
 */
public class CallFilePropertyCfModTable
extends AbstractTableComposite<String>
{
	public static final String KEY_COLUMN_ID = "property-key";
	public static final String DEFAULT_VALUE_COLUMN_ID = "default-property-value";
	public static final String OVERRIDDEN_COLUMN_ID = "overridden";
	public static final String PROPERTY_VALUE_COLUMN_ID = "property-value";

	private IDirtyStateManager dirtyStateManager;

	private AsteriskConfigModule asteriskConfigModule;
	private AsteriskServer asteriskServer;

	/**
	 * @param parent
	 * @param style
	 */
	public CallFilePropertyCfModTable(Composite parent, IDirtyStateManager dirtyStateManager) {
		this(parent, SWT.NONE, DEFAULT_STYLE_MULTI_BORDER, dirtyStateManager);
	}

	/**
	 * @param parent
	 * @param style
	 * @param initTable
	 */
	public CallFilePropertyCfModTable(Composite parent, int style, int viewerStyle, IDirtyStateManager dirtyStateManager) {
		super(parent, style, true, viewerStyle);
		this.dirtyStateManager = dirtyStateManager;
		getTable().setHeaderVisible(true);
		emulatedNativeCheckBoxTableLabelProvider = new EmulatedNativeCheckBoxTableLabelProvider(getTableViewer()) {
			@Override
			public String getColumnText(Object element, int columnIndex) {
				throw new UnsupportedOperationException("This method should never be called!");
			}
		};
		getTableViewer().setSorter(new ViewerSorter());
		hookContextMenu();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				CallFilePropertyCfModTable.this.fillContextMenu(manager);
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
		tc.setText("Key");

		tc = new TableColumn(table, SWT.LEFT); // @column 1
		tc.setMoveable(true);
		tc.setText("Default Value");

		tc = new TableColumn(table, SWT.LEFT); // @column 2
		tc.setMoveable(true);
		tc.setText("Overridden");

		tc = new TableColumn(table, SWT.LEFT); // @column 3
		tc.setMoveable(true);
		tc.setText("Value");


		WeightedTableLayout layout = new WeightedTableLayout(new int[]{
				20,
				35,
				10,
				35});
		table.setLayout(layout);

		tableViewer.setColumnProperties(new String[] {KEY_COLUMN_ID,
				DEFAULT_VALUE_COLUMN_ID,
				OVERRIDDEN_COLUMN_ID,
				PROPERTY_VALUE_COLUMN_ID});

		tableViewer.setCellEditors(new CellEditor[] {
				null,
				null,
				new CheckboxCellEditor(table),
				new TextCellEditor(table)}
		);
		tableViewer.setCellModifier(new CallFilePropertyCellModifier());
	}

	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setLabelProvider(new CallFilePropertyLabelProvider(tableViewer));
	}

	public void setConfigModule(AsteriskConfigModule configModule) {
		this.asteriskConfigModule = configModule;
//		setAsteriskServer(asteriskConfigModule.getAsteriskServer());
		setAsteriskServer(asteriskServer);
	}

	public void setAsteriskServer(AsteriskServer asteriskServer) {
		this.asteriskServer = asteriskServer;
		if (asteriskServer == null || asteriskConfigModule == null) {
			super.setInput(null);
			return;
		}

		keys = collectKeys();
		super.setInput(keys);
	}

	private Set<String> keys;

	private Set<String> collectKeys()
	{
		Set<String> asteriskServerKeys = asteriskServer.getCallFileProperties().keySet();
		Set<String> configModuleKeys = asteriskConfigModule.getCallFileProperties().keySet();
		Set<String> overrideKeys = asteriskConfigModule.getOverrideCallFilePropertyKeys();
		Set<String> keys = new HashSet<String>();
		keys.addAll(asteriskServerKeys);
		keys.addAll(configModuleKeys);
		keys.addAll(overrideKeys); // in case a key is only in the overrides-set, we should still show it.
		return keys;
	}

	private void updateKeys()
	{
		if (keys == null) // no input loaded yet
			return;

		Set<String> newKeys = collectKeys();
		keys.retainAll(newKeys);
		keys.addAll(newKeys);
	}

	@Override
	public void refresh() {
		updateKeys();
		super.refresh();
	}

	@Override
	public void refresh(boolean updateLabels) {
		updateKeys();
		super.refresh(updateLabels);
	}

	@Override
	public void setInput(Object input) {
		throw new IllegalArgumentException("This table should not be set with the input directly, use setConfigModule instead!");
	}

//	protected class CallFilePropertyContentProvider implements IStructuredContentProvider {
//		@Override
//		public Object[] getElements(Object inputElement) {
//			if (inputElement instanceof AsteriskServer) {
//				AsteriskServer asteriskServer = (AsteriskServer) inputElement;
//				return CollectionUtil.collection2TypedArray(
//						(Collection)(asteriskServer.getCallFileProperties().entrySet()),
//						Map.Entry.class,
//						false);
//				Collection<Map.Entry<String, String>> c = asteriskServer.getCallFileProperties().entrySet();
//				@SuppressWarnings("unchecked")
//				Map.Entry<String, String>[] a = c.toArray(new Map.Entry[c.size()]);
//				return a;
//			}
//			return null;
//		}
//
//		@Override
//		public void dispose() {
//
//		}
//
//		@Override
//		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
//
//		}
//	}

	private EmulatedNativeCheckBoxTableLabelProvider emulatedNativeCheckBoxTableLabelProvider;

	private class CallFilePropertyLabelProvider
	extends ColumnSpanLabelProvider
	{
//		private final Image CHECKED = ContactAsteriskPlugin.getImageDescriptor(
//		"icons/editor/asteriskserver/preference/checked.gif").createImage();
//		private final Image UNCHECKED = ContactAsteriskPlugin.getImageDescriptor(
//		"icons/editor/asteriskserver/preference/unchecked.gif").createImage();

		public CallFilePropertyLabelProvider(ColumnViewer columnViewer) {
			super(columnViewer);
		}

		@Override
		public String getColumnText(Object element, int columnIndex)
		{
			String key = (String)element;

			switch (columnIndex) {
			case 0:
				return key;
			case 1:
				return asteriskServer.getCallFileProperties().get(key);
			case 2:
				return null; //overriden column
			case 3:
				return asteriskConfigModule.getCallFileProperties().get(key);
			default: return "";//$NON-NLS-1$
			}
		}

		@Override
		protected int[][] getColumnSpan(Object element) {
			return null;
		}

		/**
		 * Eclipse 3.3 doesn't provide the button for CheckboxCellEditor, so I have to handle it this way.
		 * See - http://www.eclipsezone.com/eclipse/forums/t110249.html
		 * http://tom-eclipse-dev.blogspot.com/2007/01/tableviewers-and-nativelooking.html
		 */
		@Override
		protected Image getColumnImage(Object element, int spanColIndex) {
			String key = (String)element;
			if (spanColIndex == 2) {
// @Yo: First, you should not use GIF files, but only PNG, because PNGs have a real alpha channel and look better.
// Second, our SharedImages utility automatically determines the correct path and thus minimizes the risk of
// inconsistent naming (e.g. due to refactorings). I changed the code to use the SharedImages class:
//
//				if (Boolean.valueOf(asteriskConfigModule.getOverrideCallFilePropertyKeys().contains(entry.getKey()))) {
//					return SharedImages.getSharedImage(
//							ContactAsteriskPlugin.getDefault(),
//							CallFilePropertyCfModTable.CallFilePropertyLabelProvider.class,
//							"override-checked" // We use this only here, thus we don't need a constant.
//					);
//				} else {
//					return SharedImages.getSharedImage(
//							ContactAsteriskPlugin.getDefault(),
//							CallFilePropertyCfModTable.CallFilePropertyLabelProvider.class,
//							"override-unchecked"
//					);
//				}
//
// However, the above code would cause the check-box to look the same on all systems, but
// it is nicer to use sth. that looks like a native check-box. Hence, we
// delegate to our emulated-native-check-box-drawing-tool ;-)
// Marco.
				return emulatedNativeCheckBoxTableLabelProvider.getCheckBoxImage(
						asteriskConfigModule.getOverrideCallFilePropertyKeys().contains(key)
				);
			}
			return null;

		}
	}

	private class CallFilePropertyCellModifier implements ICellModifier {
		@Override
		public boolean canModify(Object element, String property) {
			return (
					property.equals(OVERRIDDEN_COLUMN_ID) ||
					property.equals(PROPERTY_VALUE_COLUMN_ID)
			);
		}

		@Override
		public Object getValue(Object element, String property) {
			String key = (String)element;
			if(property.equals(OVERRIDDEN_COLUMN_ID)){
				return Boolean.valueOf(asteriskConfigModule.getOverrideCallFilePropertyKeys().contains(key));
			}
			else if(property.equals(PROPERTY_VALUE_COLUMN_ID)){
				String value = asteriskConfigModule.getCallFileProperties().get(key);
				return value == null ? "" : value;
			}
			return null;
		}

		@Override
		public void modify(Object element, String property, Object value) {
			if (!(property.equals(OVERRIDDEN_COLUMN_ID) ||
					property.equals(PROPERTY_VALUE_COLUMN_ID))){
				return;
			}

			TableItem tableItem = (TableItem)element;
			String key = (String)tableItem.getData();

			if (property.equals(OVERRIDDEN_COLUMN_ID)) {
				boolean modified;
				if (Boolean.TRUE.equals(value))
					modified = asteriskConfigModule.addOverrideCallFilePropertyKey(key);
				else
					modified = asteriskConfigModule.removeOverrideCallFilePropertyKey(key);

				if (!modified)
					return; // no change => no mark dirty and no refresh
			}
			else if (property.equals(PROPERTY_VALUE_COLUMN_ID)) {
				String oldValue = asteriskConfigModule.getCallFileProperties().get(key);
				String newValue = (String) value;
				if (newValue != null && newValue.isEmpty())
					newValue = null; // we remove empty elements from the config-module

				if (Util.equals(newValue, oldValue))
					return; // no change => no mark dirty and no refresh

				asteriskConfigModule.setCallFileProperty(key, newValue);
			}

//			getTableViewer().update(entry, new String[] { property });
			getTableViewer().refresh();
			dirtyStateManager.markDirty();
		}
	}
}