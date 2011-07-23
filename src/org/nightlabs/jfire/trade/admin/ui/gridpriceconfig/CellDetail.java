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
 *     http://opensource.org/licenses/lgpl-license.php                         *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.jfire.trade.admin.ui.gridpriceconfig;

import java.util.Iterator;

import javax.security.auth.login.LoginException;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jfire.accounting.gridpriceconfig.IFormulaPriceConfig;
import org.nightlabs.jfire.base.login.ui.Login;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.cellreference.InsertCellReferenceAction;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
import org.nightlabs.jseditor.ui.IJSEditor;
import org.nightlabs.jseditor.ui.JSEditorFactory;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class CellDetail extends XComposite
{
	private static final Logger logger = Logger.getLogger(CellDetail.class);

	private TabFolder cellDetailFolder;
	private TabItem tabItemCell;
	private TabItem tabItemFallback;

	private IJSEditor cellDetailText;
	public IJSEditor getCellDetailText() {
		return cellDetailText;
	}

	private IJSEditor cellDetailFallbackText;
	public IJSEditor getCellDetailFallbackText() {
		return cellDetailFallbackText;
	}
//	private Text cellDetailText;
//	public Text getCellDetailText() {
//	return cellDetailText;
//	}

//	private Text cellDetailFallbackText;
//	public Text getCellDetailFallbackText() {
//	return cellDetailFallbackText;
//	}

	private PriceConfigGrid priceConfigGrid;

	protected boolean editable = false;

	private PriceConfigComposite priceConfigComposite = null;
	public CellDetail(Composite parent, int style, PriceConfigGrid priceConfigGrid, PriceConfigComposite priceConfigComposite)
	{
		this(parent, style, priceConfigGrid);
		this.priceConfigComposite = priceConfigComposite;
		
		makeActions();
	}
	
	
	public PriceConfigComposite getPriceConfigComposite() {
		return priceConfigComposite;
	}


	public void setPriceConfigComposite(PriceConfigComposite priceConfigComposite) {
		this.priceConfigComposite = priceConfigComposite;
	}

	/**
	 * @param parent
	 * @param style
	 */
	public CellDetail(Composite parent, int style, PriceConfigGrid priceConfigGrid)
	{
		super(parent, style, LayoutMode.TIGHT_WRAPPER);
		this.priceConfigGrid = priceConfigGrid;

		cellDetailFolder = new TabFolder(this, SWT.BORDER);
		cellDetailFolder.setLayoutData(new GridData(GridData.FILL_BOTH));

		tabItemCell = new TabItem(cellDetailFolder, SWT.NONE);
		tabItemCell.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.CellDetail.cellTabItem.text")); //$NON-NLS-1$

		tabItemFallback = new TabItem(cellDetailFolder, SWT.NONE);
		tabItemFallback.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.CellDetail.fallbackTabItem.text")); //$NON-NLS-1$

		cellDetailText = JSEditorFactory.createJSEditor(cellDetailFolder);
		cellDetailText.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		cellDetailText.addFocusListener(cellDetailTextFocusListener);
		detailTextHookContextMenu(cellDetailText);
		cellDetailText.setEnabled(false);
		tabItemCell.setControl(cellDetailText.getControl());

		cellDetailFallbackText = JSEditorFactory.createJSEditor(cellDetailFolder);
		cellDetailFallbackText.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		cellDetailFallbackText.addFocusListener(cellDetailFallbackTextFocusListener);
		fallbackTextHookContextMenu(cellDetailFallbackText);
		cellDetailFallbackText.setEnabled(false);
		tabItemFallback.setControl(cellDetailFallbackText.getControl());

//		cellDetailText = new Text(cellDetailFolder, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
//		cellDetailText.setLayoutData(new GridData(GridData.FILL_BOTH));
//		cellDetailText.addFocusListener(cellDetailTextFocusListener);
//		cellDetailText.setEnabled(false);
//		tabItemCell.setControl(cellDetailText);

//		cellDetailFallbackText = new Text(cellDetailFolder, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
//		cellDetailFallbackText.setLayoutData(new GridData(GridData.FILL_BOTH));
//		cellDetailFallbackText.addFocusListener(cellDetailFallbackTextFocusListener);
//		cellDetailFallbackText.setEnabled(false);
//		tabItemFallback.setControl(cellDetailFallbackText);

		priceConfigGrid.addSelectionChangedListener(priceConfigGridSelectionChangedListener);
	}

	private ISelectionChangedListener priceConfigGridSelectionChangedListener = new ISelectionChangedListener() {
		public void selectionChanged(SelectionChangedEvent event)
		{
			if (priceConfigGrid.getProductTypeSelector().getSelectedProductTypeItem(false) == null) {
				cellDetailText.setDocumentText(""); //$NON-NLS-1$
				cellDetailText.setEnabled(false);
				cellDetailFallbackText.setDocumentText(""); //$NON-NLS-1$
				cellDetailFallbackText.setEnabled(false);
				return;
			}

			PriceConfigGridCell cursorCell = ((PriceConfigGridSelection)event.getSelection()).getCursorCell();

			boolean enabled = cursorCell == null ? false : cursorCell.getFormulaPriceConfig() != null;
			cellDetailFallbackText.setEnabled(enabled);
			cellDetailText.setEnabled(enabled);

			// Formulas are only editable, if the
			ProductType pt = priceConfigGrid.getProductTypeSelector().getSelectedProductTypeItem(true).getProductType();
			editable = false;
			if (pt != null) {
				try {
					editable = Login.getLogin().getOrganisationID().equals(pt.getOrganisationID());
				} catch (LoginException e) {
					throw new RuntimeException(e);
				}

				if (editable) {
					ProductTypeSelector.Item item = priceConfigGrid.getProductTypeSelector().getSelectedProductTypeItem(true);
					editable &= item.isInnerVirtual() || item.getProductType().isPackageInner();
				}

				if (editable)
					editable = editable && pt.getPriceConfigInPackage(priceConfigGrid.getProductTypeSelector().getPackageProductType().getPrimaryKey()) instanceof IFormulaPriceConfig;
			}

			if (logger.isDebugEnabled())
				logger.debug("priceConfigGridSelectionChangedListener.selectionChanged: editable=" + editable); //$NON-NLS-1$

			String formula = cursorCell == null ? null : cursorCell.getFormula();
			cellDetailText.setDocumentText(formula == null ? "" : formula); //$NON-NLS-1$
			cellDetailText.setEnabled(enabled && editable);
			formula = cursorCell == null ? null : cursorCell.getFallbackFormula();
			cellDetailFallbackText.setDocumentText(formula == null ? "" : formula); //$NON-NLS-1$
			cellDetailFallbackText.setEnabled(enabled && editable);

			if ("".equals(cellDetailText.getDocumentText()) && !"".equals(cellDetailFallbackText.getDocumentText())) //$NON-NLS-1$ //$NON-NLS-2$
				cellDetailFolder.setSelection(new TabItem[]{tabItemFallback});
			else
				cellDetailFolder.setSelection(new TabItem[]{tabItemCell});
		}
	};

	private FocusListener cellDetailFallbackTextFocusListener = new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent event) {
			PriceConfigGridCell cursorCell = priceConfigGrid.getCursorCell();

			if (cursorCell == null)
				return;

			if (!editable)
				return;

			if (!cellDetailFallbackText.isEnabled())
				return;

			String formula = cellDetailFallbackText.getDocumentText();
			cursorCell.setFallbackFormula(formula);
		}
	};

	private FocusListener cellDetailTextFocusListener = new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent event) {
			PriceConfigGridCell cursorCell = priceConfigGrid.getCursorCell();

			if (cursorCell == null)
				return;

			if (!editable)
				return;

			if (!cellDetailText.isEnabled())
				return;

			String formula = cellDetailText.getDocumentText();
			cursorCell.setFormula(formula);

			for (Iterator it = priceConfigGrid.getSelectedCells().iterator(); it.hasNext(); ) {
				PriceConfigGridCell cell = (PriceConfigGridCell) it.next();
				cell.setFormula(formula);
			}
		}
	};

	/******************************
	 * Action
	 ******************************/
	private InsertCellReferenceAction cellRefAction;
	private InsertCellReferenceAction cellRefFallbackAction;
	
//	private BuildScriptAction buildScriptAction;
	private void detailTextHookContextMenu(IJSEditor editor) {
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				manager.add(cellRefAction);
//				manager.add(buildScriptAction);
				manager.add(new Separator());
			}
		});
		editor.createContextMenu(menuMgr);
	}
	
	private void fallbackTextHookContextMenu(IJSEditor editor) {
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				manager.add(cellRefFallbackAction);
//				manager.add(buildScriptAction);
				manager.add(new Separator());
			}
		});
		editor.createContextMenu(menuMgr);
	}

	private void makeActions() {
		//Insert Cell Reference Menu Item
		if(cellRefAction == null){
			cellRefAction = new InsertCellReferenceAction(priceConfigComposite, cellDetailText);
			cellRefAction.setEnabled(true);
		}//if
		
		if(cellRefFallbackAction == null){
			cellRefFallbackAction = new InsertCellReferenceAction(priceConfigComposite, cellDetailFallbackText);
			cellRefFallbackAction.setEnabled(true);
		}//if

//		//Insert Build Script Menu Item
//		if(buildScriptAction == null){
//			buildScriptAction = new BuildScriptAction(this, cellDetailFallbackText.getSourceViewer());
//			buildScriptAction.setText("Build Script Wizard");
//			buildScriptAction.setToolTipText("Build Script Wizard");
//			buildScriptAction.setEnabled(true);
//		}
	}
}
