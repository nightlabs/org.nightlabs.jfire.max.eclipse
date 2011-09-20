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

package org.nightlabs.jfire.trade.admin.ui.moneyflow.edit;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.statushandlers.StatusManager;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.base.ui.wizard.DynamicPathWizardPage;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.book.mappingbased.MoneyFlowMapping;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.dao.ProductTypeDAO;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.admin.ui.TradeAdminPlugin;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
import org.nightlabs.jfire.trade.ui.store.ProductTypeTree;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class SelectProductTypeAndPackagePage extends DynamicPathWizardPage {

	private ProductTypeID productTypeID;
	private Label labelPackageTypeDescription;
	private Combo comboPackageType;
	private String[] packageTypes;
	private String[] packageTypeDescriptions;
//	private ProductTypePackageTree productTypePackageTree;
	private ProductTypeTree productTypeTree;
	
	/**
	 * @param title Page title
	 * @param productTypePK If not null will be fix and not selectable.
	 */
	public SelectProductTypeAndPackagePage(ProductTypeID productTypeID) {
		super(SelectProductTypeAndPackagePage.class.getName(), Messages.getString("org.nightlabs.jfire.trade.admin.ui.moneyflow.edit.SelectProductTypeAndPackagePage.title"));		 //$NON-NLS-1$
		this.productTypeID = productTypeID;
	}


	/**
	 * @see org.nightlabs.base.ui.wizard.DynamicPathWizardPage#createPageContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control createPageContents(Composite parent) {
		XComposite wrapper = new XComposite(parent, SWT.NONE);
		SelectionListener selectionListener = new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				((DynamicPathWizard)getWizard()).updateDialog();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		};
		wrapper.getGridLayout().numColumns = 2;
		
		comboPackageType = new Combo(wrapper, SWT.NONE | SWT.READ_ONLY);
		GridData comboData = new GridData(SWT.CENTER, SWT.BEGINNING, false, false);
		comboPackageType.setLayoutData(comboData);
		
		labelPackageTypeDescription = new Label(wrapper, SWT.WRAP);
		labelPackageTypeDescription.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_BEGINNING));
		
		packageTypes = new String[] {MoneyFlowMapping.PACKAGE_TYPE_PACKAGE, MoneyFlowMapping.PACKAGE_TYPE_INNER};
		packageTypeDescriptions = new String[] {
			Messages.getString("org.nightlabs.jfire.trade.admin.ui.moneyflow.edit.SelectProductTypeAndPackagePage.packageTypeDescription_outer"),  //$NON-NLS-1$
			Messages.getString("org.nightlabs.jfire.trade.admin.ui.moneyflow.edit.SelectProductTypeAndPackagePage.packageTypeDescription_inner") //$NON-NLS-1$
		};
		comboPackageType.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				if (comboPackageType.getSelectionIndex() < 0)
					return;
				labelPackageTypeDescription.setText(packageTypeDescriptions[comboPackageType.getSelectionIndex()]);
			}
		});
		comboPackageType.add(Messages.getString("org.nightlabs.jfire.trade.admin.ui.moneyflow.edit.SelectProductTypeAndPackagePage.comboPackageType.item_packageOuter")); //$NON-NLS-1$
		comboPackageType.add(Messages.getString("org.nightlabs.jfire.trade.admin.ui.moneyflow.edit.SelectProductTypeAndPackagePage.comboPackageType.item_packageInner")); //$NON-NLS-1$
		comboPackageType.select(0);
		labelPackageTypeDescription.setText(packageTypeDescriptions[0]);
		comboPackageType.addSelectionListener(selectionListener);

		productTypeTree = new ProductTypeTree(wrapper);
		productTypeTree.getGridData().horizontalSpan = 2;
		productTypeTree.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				try{
					getContainer().updateButtons();
				}catch(Exception e){
					// NPE is possible while loading is not done yet
					StatusManager.getManager().handle(
							new Status(Status.WARNING, TradeAdminPlugin.PLUGIN_ID, e.getMessage(), e), StatusManager.LOG);
				}
			}
		});

		// The money-flow-mappings can declare extended product types, since the booking process takes data inheritance into account.
		// Additionally, a delegate might contain mappings which don't apply at all for a certain booking process - hence it can be shared
		// among different modules providing different implementations of product-type.
		// Therefore, we need to use a tree (or other composite) which allows for the selection of all product types which make sense
		// in the current environment (everything that can be packaged/traded by the jfire-module which uses this wizard-page) or maybe even
		// really *all* that exist. Really all is probably too much => either a filter composite should be shown where the user can specify that
		// he wants to see all, or we simply only show what makes sense here (i.e. pass a Set of root-product-types).
//		productTypeTree.setProductTypeID(productTypeID); // TODO use another product-type-composite and pass a set of roots (or first all, before we extend this class to get them from the API-client).
		
		productTypeTree.setSelectedObjectID(productTypeID);
//		ProductType pt = ProductTypeDAO.sharedInstance().getProductType(productTypeID, new String[] {FetchPlan.DEFAULT, ProductType.FETCH_GROUP_NAME}, 
//				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
//		productTypeTree.getTreeViewer().expandToLevel(pt, AbstractTreeViewer.ALL_LEVELS);
		
		return wrapper;
	}
	
	public ProductTypeID getProductTypeID() {
		return productTypeID;
	}
	
	public String getPackageType() {
		return packageTypes[comboPackageType.getSelectionIndex()];
	}
	
	@Override
	public boolean isPageComplete() {
		return (comboPackageType.getSelectionIndex() >= 0 ) && productTypeTree.getFirstSelectedElement() != null;
	}
	
	@Override
	protected String getDefaultPageMessage() {
		return Messages.getString("org.nightlabs.jfire.trade.admin.ui.moneyflow.edit.SelectProductTypeAndPackagePage.description"); //$NON-NLS-1$
	}
	
	public ProductType getSelectedProductType() {
		ProductTypeID pTypeID = (ProductTypeID) JDOHelper.getObjectId(productTypeTree.getFirstSelectedElement());
		if (pTypeID == null)
			return null;

		ProductType pType = ProductTypeDAO.sharedInstance().getProductType(
				pTypeID,
				new String[] {
						FetchPlan.DEFAULT,
						ProductType.FETCH_GROUP_NAME
				},
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				new NullProgressMonitor()
		);
		return pType;
	}

}
