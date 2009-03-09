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

package org.nightlabs.jfire.simpletrade.admin.ui.producttype.create;

import java.lang.reflect.InvocationTargetException;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.ModuleException;
import org.nightlabs.base.ui.editor.Editor2PerspectiveRegistry;
import org.nightlabs.base.ui.progress.ProgressMonitorWrapper;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.ObjectIDUtil;
import org.nightlabs.jfire.accounting.gridpriceconfig.StablePriceConfig;
import org.nightlabs.jfire.accounting.priceconfig.PriceConfig;
import org.nightlabs.jfire.base.JFireEjbFactory;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.simpletrade.SimpleTradeManager;
import org.nightlabs.jfire.simpletrade.admin.ui.editor.SimpleProductTypeEditor;
import org.nightlabs.jfire.simpletrade.admin.ui.gridpriceconfig.ChooseSimpleTradePriceConfigPage;
import org.nightlabs.jfire.simpletrade.admin.ui.resource.Messages;
import org.nightlabs.jfire.simpletrade.store.SimpleProductType;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.StoreManager;
import org.nightlabs.jfire.store.dao.ProductTypeDAO;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.admin.ui.editor.ProductTypeEditorInput;
import org.nightlabs.jfire.trade.admin.ui.editor.ownervendor.OwnerVendorPage;
import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.AbstractChooseGridPriceConfigPage;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class CreateProductTypeWizard extends DynamicPathWizard
{
//	private ProductTypeTreeNode selectedNode;
	private ProductTypeID parentProductTypeID;
	private boolean priceConfigSelectionEnabled = true;

	private ProductTypeNamePage productTypeNamePage;
	private OwnerVendorPage ownerVendorPage;

	private AbstractChooseGridPriceConfigPage selectPriceConfigPage;

	private StoreManager _storeManager = null;

	public static final String[] FETCH_GROUPS_PARENT_PRODUCT_TYPE = {
		FetchPlan.DEFAULT,
		ProductType.FETCH_GROUP_INNER_PRICE_CONFIG,
		ProductType.FETCH_GROUP_OWNER,
		ProductType.FETCH_GROUP_VENDOR,
		ProductType.FETCH_GROUP_DELIVERY_CONFIGURATION
	};

	protected StoreManager getStoreManager()
	throws ModuleException
	{
		try {
			if (_storeManager == null)
				_storeManager = JFireEjbFactory.getBean(StoreManager.class, Login.getLogin().getInitialContextProperties());
			return _storeManager;
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new ModuleException(e);
		}
	}

	private SimpleTradeManager _simpleTradeManager = null;
	protected SimpleTradeManager getSimpleTradeManager()
	throws ModuleException
	{
		try {
			if (_simpleTradeManager == null)
				_simpleTradeManager = JFireEjbFactory.getBean(SimpleTradeManager.class, Login.getLogin().getInitialContextProperties());
			return _simpleTradeManager;
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new ModuleException(e);
		}
	}

	public CreateProductTypeWizard(ProductTypeID parentProductTypeID)
	{
		this.parentProductTypeID = parentProductTypeID;
		setWindowTitle(Messages.getString("org.nightlabs.jfire.simpletrade.admin.ui.producttype.create.CreateProductTypeWizard.windowTitle")); //$NON-NLS-1$
	}

	@Override
	public void addPages()
	{
		assert parentProductTypeID != null;
		
		productTypeNamePage = new ProductTypeNamePage(parentProductTypeID);
		addPage(productTypeNamePage);


		selectPriceConfigPage = new ChooseSimpleTradePriceConfigPage(parentProductTypeID);
		addDynamicWizardPage(selectPriceConfigPage);

		ownerVendorPage = new OwnerVendorPage(parentProductTypeID);
		addDynamicWizardPage(ownerVendorPage);
		//ownerVendorPage.se
	}

	public ProductTypeNamePage getProductTypeNamePage()
	{
		return productTypeNamePage;
	}


	@Override
	public boolean performFinish()
	{
		try {
			getContainer().run(false, false, new IRunnableWithProgress() {

				public void run(IProgressMonitor _monitor) throws InvocationTargetException, InterruptedException {
					try {
						ProgressMonitorWrapper monitor = new ProgressMonitorWrapper(_monitor);
						monitor.beginTask(Messages.getString("org.nightlabs.jfire.simpletrade.admin.ui.producttype.create.CreateProductTypeWizard.createSimpleProductTypeMonitor.task.name"), 3); //$NON-NLS-1$

						ProductType parentProductType = ProductTypeDAO.sharedInstance().getProductType(
								parentProductTypeID, FETCH_GROUPS_PARENT_PRODUCT_TYPE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
								new SubProgressMonitor(monitor, 1));

						SimpleProductType newProductType = new SimpleProductType(
								Login.getLogin().getOrganisationID(),
								ObjectIDUtil.makeValidIDString(getProductTypeNamePage().getProductTypeNameBuffer().getText()) + '_' + ProductType.createProductTypeID(),
								parentProductType,
								getProductTypeNamePage().getInheritanceNature(),
								getProductTypeNamePage().getPackageNature());
						getProductTypeNamePage().getProductTypeNameBuffer().copyTo(newProductType.getName());

						if (ProductType.PACKAGE_NATURE_OUTER == getProductTypeNamePage().getPackageNature()) {
							// If the new ProductType is packageable, we need to create a package result price config
							newProductType.setPackagePriceConfig(
									new StablePriceConfig(
											IDGenerator.getOrganisationID(),
											PriceConfig.createPriceConfigID()));
						}

						selectPriceConfigPage.configureProductType(newProductType);

						// TODO: Add Wizardhop for PropertySet inherit/createnew
						monitor.worked(1);
//						SubProgressMonitor subMonitor = new SubProgressMonitor(monitor, 1);
//						StructLocal struct = StructLocalDAO.sharedInstance().getStructLocal(
//								SimpleProductType.class, Struct.DEFAULT_SCOPE, StructLocal.DEFAULT_SCOPE, subMonitor);
//						newProductType.getPropertySet().setStructLocalAttributes(struct);
						if(ownerVendorPage.isPageComplete())
							ownerVendorPage.configureProductType(newProductType);

						newProductType = getSimpleTradeManager().storeProductType(newProductType, true, null, 1);
						//						newProductType = getSimpleTradeManager().storeProductType(
//						newProductType, true,
//						ProductTypeTreeNode.FETCH_GROUPS_SIMPLE_PRODUCT_TYPE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
						monitor.worked(1);
//						if (selectedNode.isChildrenLoaded()) {
//						new ProductTypeTreeNode(selectedNode, newProductType);
//						selectedNode.refreshLocal();
//						}

						final ProductTypeID newProductTypeId = newProductType.getObjectId();
						Display.getDefault().asyncExec(new Runnable() {
							public void run()
							{
								try {
									ProductTypeEditorInput editorInput = new ProductTypeEditorInput(newProductTypeId);
//									RCPUtil.openEditor(editorInput, SimpleProductTypeEditor.ID_EDITOR);
									
									Editor2PerspectiveRegistry.sharedInstance().openEditor(editorInput, SimpleProductTypeEditor.ID_EDITOR);
								} catch (Exception e) {
									throw new RuntimeException(e);
								}
							}
						});
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	public void setPriceConfigSelectionEnabled(boolean enabled)
	{
		if (priceConfigSelectionEnabled == enabled)
			return;

		if (enabled) {
			addDynamicWizardPage(selectPriceConfigPage);
			updateDialog();
		}
		else {
			removeDynamicWizardPage(selectPriceConfigPage);
		}

		priceConfigSelectionEnabled = enabled;
	}

	public void setParentProductTypeID(ProductTypeID parentProductTypeID) {
		this.parentProductTypeID = parentProductTypeID;
	}
	
//	/**
//	* @return Returns the selectedNode.
//	*/
//	public ProductTypeTreeNode getSelectedNode()
//	{
//	return selectedNode;
//	}
}
