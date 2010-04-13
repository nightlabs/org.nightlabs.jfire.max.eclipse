package org.nightlabs.jfire.asterisk.ui.config;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.nightlabs.base.ui.labelprovider.ColumnSpanLabelProvider;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.notification.IDirtyStateManager;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.jfire.asterisk.AsteriskServer;
import org.nightlabs.jfire.asterisk.config.AsteriskConfigModule;
import org.nightlabs.jfire.asterisk.ui.ContactAsteriskPlugin;
import org.nightlabs.jfire.asterisk.ui.resource.Messages;
import org.nightlabs.util.Util;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 * @author Marco หงุ่ยตระกูล-Schulze - marco at nightlabs dot de
 */
public class CallFilePropertyCfModTable
extends AbstractTableComposite<String>
{
	private static final Logger logger = Logger.getLogger(CallFilePropertyCfModTable.class);

	private static final String KEY_COLUMN_ID = "property-key"; //$NON-NLS-1$
	private static final String DEFAULT_VALUE_COLUMN_ID = "default-property-value"; //$NON-NLS-1$
	private static final String OVERRIDDEN_COLUMN_ID = "overridden"; //$NON-NLS-1$
	private static final String PROPERTY_VALUE_COLUMN_ID = "property-value"; //$NON-NLS-1$

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
//		emulatedNativeCheckBoxTableLabelProvider = new EmulatedNativeCheckBoxTableLabelProvider(getTableViewer()) {
//			@Override
//			public String getColumnText(Object element, int columnIndex) {
//				throw new UnsupportedOperationException("This method should never be called!"); //$NON-NLS-1$
//			}
//		};
		getTableViewer().setSorter(new ViewerSorter());
//		hookContextMenu();
		createContextMenu(getTableViewer().getControl());
	}

	// ------------------------------------------------------------------------------------- ++ ------------------------------->>
	// Note: @Kai
	// Since 2010.04.13, we now have the super class ContextMenuReadyXComposite (which the AbstractTableComposite now extends) 
	// to efficiently manage (priority-ordered) context-menus when needed (thru the method integratePriorityOrderedContextMenu()),
	// which has been streamlined to handle 3 types of contextMenuContributions: 
	//   (i) IContributionItem, (ii) IAction, and (iii) IViewActionDelegate.
	// ------------------------------------------------------------------------------------- ++ ------------------------------->>
//	private void hookContextMenu() {
//		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
//		menuMgr.setRemoveAllWhenShown(true);
//		menuMgr.addMenuListener(new IMenuListener() {
//			public void menuAboutToShow(IMenuManager manager) {
//				CallFilePropertyCfModTable.this.fillContextMenu(manager);
//			}
//		});
//		Menu menu = menuMgr.createContextMenu(getTableViewer().getControl());
//		getTableViewer().getControl().setMenu(menu);
////		if (getSite() != null)
////			getSite().registerContextMenu(menuMgr, getTableViewer());
//	}
//
//	/**
//	 * Contains instances of both, {@link IContributionItem} and {@link IAction}
//	 */
//	private List<Object> contextMenuContributions;
//
//	@Override
//	public void addContextMenuContribution(IContributionItem contributionItem)
//	{
//		if (contextMenuContributions == null)
//			contextMenuContributions = new LinkedList<Object>();
//
//		contextMenuContributions.add(contributionItem);
//	}
//
//	@Override
//	public void addContextMenuContribution(IAction action)
//	{
//		if (contextMenuContributions == null)
//			contextMenuContributions = new LinkedList<Object>();
//
//		contextMenuContributions.add(action);
//	}
//
//	@Override
//	private void fillContextMenu(IMenuManager manager) {
//		if (contextMenuContributions != null) {
//			for (Object contextMenuContribution : contextMenuContributions) {
//				if (contextMenuContribution instanceof IContributionItem)
//					manager.add((IContributionItem)contextMenuContribution);
//				else if (contextMenuContribution instanceof IAction)
//					manager.add((IAction)contextMenuContribution);
//				else
//					throw new IllegalStateException("How the hell got an instance of " + (contextMenuContribution == null ? "null" : contextMenuContribution.getClass()) + " in the contextMenuContributions list?!"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//			}
//		}
//
//		// Other plug-ins can contribute their actions here
//		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
//	}
	// ------------------------------------------------------------------------------------- ++ ------------------------------->>

	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {
		TableColumn tc;

		tc = new TableColumn(table, SWT.LEFT); // @column 0
		tc.setMoveable(true);
		tc.setText(Messages.getString("org.nightlabs.jfire.asterisk.ui.config.CallFilePropertyCfModTable.columnHeader[key].text")); //$NON-NLS-1$

		tc = new TableColumn(table, SWT.LEFT); // @column 1
		tc.setMoveable(true);
		tc.setText(Messages.getString("org.nightlabs.jfire.asterisk.ui.config.CallFilePropertyCfModTable.columnHeader[asteriskServerValue].text")); //$NON-NLS-1$

		tc = new TableColumn(table, SWT.LEFT); // @column 2
		tc.setMoveable(true);
		tc.setText(Messages.getString("org.nightlabs.jfire.asterisk.ui.config.CallFilePropertyCfModTable.columnHeader[overridden].text")); //$NON-NLS-1$

		tc = new TableColumn(table, SWT.LEFT); // @column 3
		tc.setMoveable(true);
		tc.setText(Messages.getString("org.nightlabs.jfire.asterisk.ui.config.CallFilePropertyCfModTable.columnHeader[value].text")); //$NON-NLS-1$


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
		super.setInput(null);

		if (Display.getCurrent() != getDisplay())
			throw new IllegalStateException("Thread mismatch! This method must be called on the SWT UI thread!");

		this.asteriskConfigModule = configModule;
//		setAsteriskServer(asteriskConfigModule.getAsteriskServer());
		setAsteriskServer(asteriskServer);
	}

	public void setAsteriskServer(AsteriskServer asteriskServer) {
		super.setInput(null);

		if (Display.getCurrent() != getDisplay())
			throw new IllegalStateException("Thread mismatch! This method must be called on the SWT UI thread!");

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
		throw new IllegalArgumentException("This table should not be set with the input directly, use setConfigModule instead!"); //$NON-NLS-1$
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

//	private EmulatedNativeCheckBoxTableLabelProvider emulatedNativeCheckBoxTableLabelProvider;

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
				if (asteriskServer == null)
					return null;
				else
					return asteriskServer.getCallFileProperties().get(key);
			case 2:
				return null; //overriden column
			case 3:
				if (asteriskConfigModule == null)
					return null;
				else
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

				if (Boolean.valueOf(asteriskConfigModule.getOverrideCallFilePropertyKeys().contains(key))) {
					return SharedImages.getSharedImage(
							ContactAsteriskPlugin.getDefault(),
							CallFilePropertyCfModTable.CallFilePropertyLabelProvider.class,
							"override-checked" // We use this only here, thus we don't need a constant.
					);
				} else {
					return SharedImages.getSharedImage(
							ContactAsteriskPlugin.getDefault(),
							CallFilePropertyCfModTable.CallFilePropertyLabelProvider.class,
							"override-unchecked"
					);
				}
// Added 2009-12-01: Unfortunately, the below code doesn't work properly and often renders a bad image. I therefore
// had to switch to the above code using static pictures :-( Marco.
//
// However, the above code would cause the check-box to look the same on all systems, but
// it is nicer to use sth. that looks like a native check-box. Hence, we
// delegate to our emulated-native-check-box-drawing-tool ;-)
// Marco.
//				return emulatedNativeCheckBoxTableLabelProvider.getCheckBoxImage(
//						asteriskConfigModule.getOverrideCallFilePropertyKeys().contains(key)
//				);
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
				return value == null ? "" : value; //$NON-NLS-1$
			}
			return null;
		}

		@Override
		public void modify(Object element, String property, Object value) {
			if (element == null) {
				logger.warn("element is null!!! property='" + property + "', value='" + value + "'", new IllegalStateException("element == null"));
				// I think this happens, when the input is set while the data is edited. IMHO this happens when asynchronous load operations are still running
				// while the user already clicks into the cell editor. The best way to handle this is not yet clear to me. For the moment, this warn-log
				// and a silent return seems to be the best. Marco.
				return;
			}

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