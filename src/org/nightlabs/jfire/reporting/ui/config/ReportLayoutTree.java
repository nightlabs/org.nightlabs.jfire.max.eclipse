/* *****************************************************************************
 * JFire - it's hot - Free ERP System - http://jfire.org                       *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.jfire.reporting.ui.config;

import javax.jdo.JDOHelper;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.table.CheckboxCellEditorHelper;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.base.ui.jdo.tree.ActiveJDOObjectTreeComposite;
import org.nightlabs.jfire.base.ui.jdo.tree.ActiveJDOObjectTreeController;
import org.nightlabs.jfire.base.ui.jdo.tree.JDOObjectTreeContentProvider;
import org.nightlabs.jfire.base.ui.jdo.tree.JDOTreeNodesChangedEvent;
import org.nightlabs.jfire.base.ui.jdo.tree.JDOTreeNodesChangedEventHandler;
import org.nightlabs.jfire.reporting.config.ReportLayoutAvailEntry;
import org.nightlabs.jfire.reporting.config.ReportLayoutConfigModule;
import org.nightlabs.jfire.reporting.layout.ReportCategory;
import org.nightlabs.jfire.reporting.layout.ReportLayout;
import org.nightlabs.jfire.reporting.layout.ReportRegistryItem;
import org.nightlabs.jfire.reporting.layout.id.ReportRegistryItemID;
import org.nightlabs.jfire.reporting.ui.ReportingPlugin;
import org.nightlabs.jfire.reporting.ui.layout.ActiveReportRegistryItemTreeController;
import org.nightlabs.jfire.reporting.ui.layout.ReportRegistryItemNode;
import org.nightlabs.jfire.reporting.ui.layout.ReportRegistryItemTree;
import org.nightlabs.jfire.reporting.ui.resource.Messages;
import org.nightlabs.util.NLLocale;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class ReportLayoutTree
extends ActiveJDOObjectTreeComposite<ReportRegistryItemID, ReportRegistryItem, ReportRegistryItemNode>
implements ICellModifier
{
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(ReportLayoutTree.class);
	
	private static class LabelProvider extends TableLabelProvider {

		private ICellModifier cellModifier;
		
		public LabelProvider(ICellModifier cellModifier) {
			super();
			this.cellModifier = cellModifier;
		}
		

		@Override
		public Image getColumnImage(Object element, int columnIndex)
		{
			if (element instanceof ReportRegistryItemNode) {
				ReportRegistryItemNode node = (ReportRegistryItemNode)element;
				if (columnIndex != 0) {
					if (node.getJdoObject() instanceof ReportLayout) {
						if (columnIndex == 1)
							return CheckboxCellEditorHelper.getCellEditorImage(cellModifier, element, PROPERTY_AVAILABLE);
						else
							return CheckboxCellEditorHelper.getCellEditorImage(cellModifier, element, PROPERTY_DEFAULT);
					}
					else
						return null;
				}
			
				ReportRegistryItem item = node.getJdoObject();
				if (item.getClass().equals(ReportCategory.class)) {
					if (((ReportCategory)node.getJdoObject()).isInternal())
						return SharedImages.getSharedImage(ReportingPlugin.getDefault(), ReportRegistryItemTree.class, "category-internal"); //$NON-NLS-1$
					else
						return SharedImages.getSharedImage(ReportingPlugin.getDefault(), ReportRegistryItemTree.class, "category-normal"); //$NON-NLS-1$
				}
				else if (item.getClass().equals(ReportLayout.class))
					return SharedImages.getSharedImage(ReportingPlugin.getDefault(), ReportRegistryItemTree.class, "layout"); //$NON-NLS-1$
			}
			return super.getColumnImage(element, columnIndex);
		}

		/*
		 *  (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText(Object element, int columnIndex)
		{
			if (element instanceof ReportRegistryItemNode) {
				ReportRegistryItem item = ((ReportRegistryItemNode)element).getJdoObject();
				switch(columnIndex) {
					case 0: return item.getName().getText(NLLocale.getDefault().getLanguage());
					case 1:
					case 2:
						return ""; //$NON-NLS-1$
				}
			}
			return ""; //$NON-NLS-1$
		}
	}
	
	private ReportLayoutConfigPreferencePage preferencePage;
	
	/**
	 * @param parent
	 */
	public ReportLayoutTree(Composite parent, ReportLayoutConfigPreferencePage preferencePage) {
		super(parent, DEFAULT_STYLE_SINGLE | SWT.FULL_SELECTION, true, true, true);
		this.preferencePage = preferencePage;
	}

	/**
	 * @param parent
	 */
	public ReportLayoutTree(Composite parent, ReportLayoutConfigPreferencePage preferencePage, int treeStyle) {
		super(parent, treeStyle, true, true, true);
		this.preferencePage = preferencePage;
	}
	
	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.tree.AbstractTreeComposite#setTreeProvider(org.eclipse.jface.viewers.TreeViewer)
	 */
	@Override
	public void setTreeProvider(TreeViewer treeViewer) {
		treeViewer.setContentProvider(new JDOObjectTreeContentProvider<ReportRegistryItemID, ReportRegistryItem, ReportRegistryItemNode>() {

			@Override
			public boolean hasJDOObjectChildren(ReportRegistryItem jdoObject) {
				return jdoObject instanceof ReportCategory;
			}
		});
		treeViewer.setLabelProvider(new LabelProvider(this));
		
		treeViewer.setCellModifier(this);
		treeViewer.setColumnProperties(
				new String[] {
						null,
						PROPERTY_AVAILABLE,
						PROPERTY_DEFAULT
				}
			);
		treeViewer.setCellEditors(
				new CellEditor[] {
						null,
						new CheckboxCellEditor(treeViewer.getTree()),
						new CheckboxCellEditor(treeViewer.getTree())
				}
			);
	}
	
	private static final String PROPERTY_AVAILABLE = "available"; //$NON-NLS-1$
	private static final String PROPERTY_DEFAULT = "default"; //$NON-NLS-1$
	
	private ActiveReportRegistryItemTreeController itemTreeController = new ActiveReportRegistryItemTreeController() {
		@Override
		protected void onJDOObjectsChanged(JDOTreeNodesChangedEvent<ReportRegistryItemID, ReportRegistryItemNode> changedEvent) {
			JDOTreeNodesChangedEventHandler.handle(getTreeViewer(), changedEvent);
		}
	};

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.tree.AbstractTreeComposite#createTreeColumns(org.eclipse.swt.widgets.Tree)
	 */
	@Override
	public void createTreeColumns(Tree tree) {
		TreeColumn column = new TreeColumn(getTree(), SWT.LEFT);
		column.setText(Messages.getString("org.nightlabs.jfire.reporting.ui.config.ReportLayoutTree.reportLayoutOrCategoryColumn.text")); //$NON-NLS-1$
		column.setResizable(true);
		// TODO: Set better width
		column.setWidth(310);
		
		TreeColumn availCol = new TreeColumn(getTree(), SWT.CENTER);
		availCol.setText(Messages.getString("org.nightlabs.jfire.reporting.ui.config.ReportLayoutTree.availColumn.text")); //$NON-NLS-1$
		availCol.setResizable(true);
		// TODO: Set better width
		availCol.setWidth(35);
		
		TreeColumn defaultCol = new TreeColumn(getTree(), SWT.CENTER);
		defaultCol.setText(Messages.getString("org.nightlabs.jfire.reporting.ui.config.ReportLayoutTree.defaultColumn.text")); //$NON-NLS-1$
		defaultCol.setResizable(true);
		// TODO: Set better width
		defaultCol.setWidth(35);
		
	}

	private ReportRegistryItem getReportRegistryItem(Object element) {
		return ((ReportRegistryItemNode)element).getJdoObject();
	}
	
	public boolean canModify(Object element, String property) {
		logger.debug("getValue() called with "+property+" = "+element); //$NON-NLS-1$ //$NON-NLS-2$
		ReportRegistryItem item = getReportRegistryItem(element);
		if (item instanceof ReportLayout)
			return true;
		return false;
	}

	public Object getValue(Object element, String property) {
		logger.debug("getValue() called with "+property+" = "+element); //$NON-NLS-1$ //$NON-NLS-2$
		ReportRegistryItem item = getReportRegistryItem(element);
		if (item == null || configModule == null)
			return new Boolean(false);
		
		ReportLayoutAvailEntry entry = configModule.getAvailEntry(item.getReportRegistryItemType());
		if (entry == null)
			return new Boolean(false);
		
		if (PROPERTY_AVAILABLE.equals(property))
			return new Boolean(entry.getAvailableReportLayoutKeys().contains(JDOHelper.getObjectId(item).toString()));
		if (PROPERTY_DEFAULT.equals(property)) {
			return new Boolean(
					(entry.getDefaultReportLayoutKey() == null) ?
					false:
					entry.getDefaultReportLayoutKey().equals(JDOHelper.getObjectId(item).toString())
				);
		}
		
		return new Boolean(false);
	}

	public void modify(Object element, String property, Object value) {		
		logger.debug("modify() called with "+property+" = "+value); //$NON-NLS-1$ //$NON-NLS-2$
		ReportRegistryItem item = getReportRegistryItem(((TreeItem)element).getData());
		if (item == null || configModule == null)
			return;
		
		ReportLayoutAvailEntry entry = configModule.getAvailEntry(item.getReportRegistryItemType());
		
		if (PROPERTY_AVAILABLE.equals(property)) {
			boolean isAvail = ((Boolean)value).booleanValue();
			if (isAvail)
				entry.getAvailableReportLayoutKeys().add(JDOHelper.getObjectId(item).toString());
			else
				entry.getAvailableReportLayoutKeys().remove(JDOHelper.getObjectId(item).toString());
		}
		else if (PROPERTY_DEFAULT.equals(property)) {
			boolean isDefault = ((Boolean)value).booleanValue();
			if (isDefault)
				entry.setDefaultReportLayoutKey(JDOHelper.getObjectId(item).toString());
			else
				entry.setDefaultReportLayoutKey(null);
		}
		getTreeViewer().refresh(((TreeItem)element).getData(), true);
		if (preferencePage != null)
			preferencePage.setChanged(true);
	}
	
	
	private ReportLayoutConfigModule configModule;
	
	public void setConfigModule(ReportLayoutConfigModule configModule) {
		this.configModule = configModule;
		boolean doExpand = getTreeViewer().getInput() == null;
		getTreeViewer().setInput(itemTreeController);
		getTreeViewer().addTreeListener(new ITreeViewerListener() {
			public void treeCollapsed(TreeExpansionEvent event) {
			}

			public void treeExpanded(TreeExpansionEvent event) {
				event.getElement();
			}
				
		});
		if (doExpand)
			getTreeViewer().expandToLevel(3);
	}

	
	
	@Override
	protected ActiveJDOObjectTreeController<ReportRegistryItemID, ReportRegistryItem, ReportRegistryItemNode> getJDOObjectTreeController() {
		return itemTreeController;
	}


}
