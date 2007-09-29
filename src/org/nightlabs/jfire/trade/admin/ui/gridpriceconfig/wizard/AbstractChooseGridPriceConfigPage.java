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

package org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.composite.FadeableComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.resource.SharedImages.ImageDimension;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.base.ui.wizard.WizardHop;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.i18n.I18nTextBuffer;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.gridpriceconfig.GridPriceConfig;
import org.nightlabs.jfire.accounting.priceconfig.IInnerPriceConfig;
import org.nightlabs.jfire.accounting.priceconfig.IPriceConfig;
import org.nightlabs.jfire.accounting.priceconfig.PriceConfig;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.admin.ui.TradeAdminPlugin;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
import org.nightlabs.jfire.trade.ui.store.ProductTypeDAO;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public abstract class AbstractChooseGridPriceConfigPage 
extends WizardHopPage
{
	public static final String[] FETCH_GROUPS_PRICE_CONFIG = {
		FetchPlan.DEFAULT, PriceConfig.FETCH_GROUP_NAME
	};

	public static final String[] FETCH_GROUPS_PARENT_PRODUCT_TYPE = {
		FetchPlan.DEFAULT, ProductType.FETCH_GROUP_INNER_PRICE_CONFIG, GridPriceConfig.FETCH_GROUP_NAME
	};
	
	public static final int ACTION_INHERIT = 1;
	public static final int ACTION_LATER = 2;
	public static final int ACTION_CREATE = 3;
	public static final int ACTION_SELECT = 4;

	private FadeableComposite page;
	private Button inheritPriceConfigRadio;
	private Button assignPriceConfigLaterRadio;
	private Button createPriceConfigRadio;
	private I18nTextEditor newPriceConfigNameEditor;
	private I18nTextBuffer newPriceConfigNameBuffer = new I18nTextBuffer();
	private Button selectPriceConfigRadio;

	private List priceConfigs = new ArrayList();
	private org.eclipse.swt.widgets.List priceConfigList;
	private ProductTypeID parentProductTypeID;
	private ProductType parentProductType = null;
	private IInnerPriceConfig selectedPriceConfig = null;

	public AbstractChooseGridPriceConfigPage(ProductTypeID parentProductTypeID)
	{
		super(AbstractChooseGridPriceConfigPage.class.getName(), Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.AbstractChooseGridPriceConfigPage.title")); //$NON-NLS-1$
		this.parentProductTypeID = parentProductTypeID;
		this.setDescription(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.AbstractChooseGridPriceConfigPage.description")); //$NON-NLS-1$
		new WizardHop(this);
		setImageDescriptor(SharedImages.getSharedImageDescriptor(
				TradeAdminPlugin.getDefault(), AbstractChooseGridPriceConfigPage.class, "", ImageDimension._75x70)); //$NON-NLS-1$
	}
	
	private void setInheritPriceConfigRadio_InnerPriceConfigName(String name)
	{
		inheritPriceConfigRadio.setText(
				String.format(
						Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.AbstractChooseGridPriceConfigPage.inheritPriceConfigRadio.text"), //$NON-NLS-1$
						name)); 
				
	}

	@Override
	public Control createPageContents(Composite parent)
	{
		page = new FadeableComposite(parent, SWT.NONE, LayoutMode.ORDINARY_WRAPPER);

		inheritPriceConfigRadio = new Button(page, SWT.RADIO);
		setInheritPriceConfigRadio_InnerPriceConfigName(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.AbstractChooseGridPriceConfigPage.pseudoInheritedPriceConfig_loading")); //$NON-NLS-1$

		inheritPriceConfigRadio.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				setAction(ACTION_INHERIT);
			}
		});

		assignPriceConfigLaterRadio = new Button(page, SWT.RADIO);
		assignPriceConfigLaterRadio.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.AbstractChooseGridPriceConfigPage.assignPriceConfigLaterRadio.text")); //$NON-NLS-1$
		assignPriceConfigLaterRadio.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				setAction(ACTION_LATER);
			}
		});

		createPriceConfigRadio = new Button(page, SWT.RADIO);
		createPriceConfigRadio.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.AbstractChooseGridPriceConfigPage.createPriceConfigRadio.text")); //$NON-NLS-1$
		createPriceConfigRadio.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				setAction(ACTION_CREATE);
			}
		});
		newPriceConfigNameEditor = new I18nTextEditor(page, Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.AbstractChooseGridPriceConfigPage.newPriceConfigNameEditor.caption")); //$NON-NLS-1$
		newPriceConfigNameEditor.getGridData().horizontalIndent = 32;
		newPriceConfigNameEditor.setI18nText(newPriceConfigNameBuffer);
		newPriceConfigNameEditor.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e)
			{
				setAction(ACTION_CREATE);
			}
		});

		selectPriceConfigRadio = new Button(page, SWT.RADIO);
		selectPriceConfigRadio.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.AbstractChooseGridPriceConfigPage.selectPriceConfigRadio.text")); //$NON-NLS-1$
		selectPriceConfigRadio.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				setAction(ACTION_SELECT);
			}
		});

		priceConfigList = new org.eclipse.swt.widgets.List(page, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
		priceConfigList.setLayoutData(new GridData(GridData.FILL_BOTH));
		priceConfigList.add(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.AbstractChooseGridPriceConfigPage.pseudoPriceConfig_loading")); //$NON-NLS-1$
		priceConfigList.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				setAction(ACTION_SELECT);
			}
		});

		page.setFaded(true);

		Job job = new Job(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.AbstractChooseGridPriceConfigPage.loadPriceConfigsJob.name")) { //$NON-NLS-1$
			protected IStatus run(ProgressMonitor monitor) throws Exception
			{
				try {
					monitor.beginTask(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.AbstractChooseGridPriceConfigPage.loadPriceConfigsMonitor.task.name"), 2); //$NON-NLS-1$

					if (parentProductTypeID != null)
						parentProductType = ProductTypeDAO.sharedInstance().getProductType(
								parentProductTypeID, FETCH_GROUPS_PARENT_PRODUCT_TYPE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
								new SubProgressMonitor(monitor, 1));
					else
						monitor.worked(1);


					final List<? extends IInnerPriceConfig> pcs = retrievePriceConfigs(monitor);
					Collections.sort(pcs, new Comparator<IInnerPriceConfig>() {
						public int compare(IInnerPriceConfig fpc0, IInnerPriceConfig fpc1)
						{
							String languageID = Locale.getDefault().getLanguage();
							return fpc0.getName().getText(languageID).compareTo(fpc1.getName().getText(languageID));
						}
					});

					monitor.done();

					Display.getDefault().asyncExec(new Runnable() {
						public void run()
						{
							if (page.isDisposed())
								return;

							page.setFaded(false);

							inheritPriceConfigRadio.setEnabled(parentProductType != null);
							if (parentProductType == null)
								setAction(ACTION_LATER);
							else
								setAction(ACTION_INHERIT);

							if (parentProductType != null && parentProductType.getInnerPriceConfig() != null)
								setInheritPriceConfigRadio_InnerPriceConfigName(parentProductType.getInnerPriceConfig().getName().getText());
							else
								setInheritPriceConfigRadio_InnerPriceConfigName(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.AbstractChooseGridPriceConfigPage.pseudoInheritedPriceConfig_none")); //$NON-NLS-1$

							String languageID = Locale.getDefault().getLanguage();
							priceConfigs = pcs;
							priceConfigList.removeAll();
							for (Iterator it = priceConfigs.iterator(); it.hasNext(); ) {
								IPriceConfig pc = (IPriceConfig) it.next();
								priceConfigList.add(pc.getName().getText(languageID));
							}
						}
					});
				} catch (Exception e) {
					throw new RuntimeException(e);
				}

				return Status.OK_STATUS;
			}
		};
		job.setPriority(Job.SHORT);
		job.schedule();

		return page;
	}

	protected void setAction(int action)
	{
		this.action = action;

		createPriceConfigRadio.setSelection(false);
		inheritPriceConfigRadio.setSelection(false);
		assignPriceConfigLaterRadio.setSelection(false);
		selectPriceConfigRadio.setSelection(false);

		switch (action) {
			case ACTION_CREATE:
				createPriceConfigRadio.setSelection(true);
				selectedPriceConfig = null;
			break;
			case ACTION_INHERIT:
				inheritPriceConfigRadio.setSelection(true);
				// parentProductType can be null, because it's possible to use the wizard for the root-product-type
				selectedPriceConfig = parentProductType == null ? null : parentProductType.getInnerPriceConfig();
			break;
			case ACTION_LATER:
				assignPriceConfigLaterRadio.setSelection(true);
				selectedPriceConfig = null;
			break;
			case ACTION_SELECT:
				selectPriceConfigRadio.setSelection(true);
				int idx = priceConfigList.getSelectionIndex();
				if (idx < 0)
					selectedPriceConfig = null;
				else
					selectedPriceConfig = (IInnerPriceConfig) priceConfigs.get(idx);
			break;
			default:
				throw new IllegalArgumentException("Unknown action: " + action); //$NON-NLS-1$
		}

		((DynamicPathWizard)getWizard()).updateDialog();
	}

	public boolean isPageComplete()
	{
		if (inheritPriceConfigRadio == null)
			return false;

		return
			isShown() && (
				inheritPriceConfigRadio.getSelection() ||
				assignPriceConfigLaterRadio.getSelection() ||
				(createPriceConfigRadio.getSelection() && !newPriceConfigNameBuffer.isEmpty()) ||
				(selectPriceConfigRadio.getSelection() && selectedPriceConfig != null)
			);
	}

	public IInnerPriceConfig getSelectedPriceConfig()
	{
//		if (!selectPriceConfigRadio.getSelection())
//			return null;
		return selectedPriceConfig;
	}

//	public GridPriceConfig createPriceConfig()
//	{
//		if (ACTION_CREATE != getAction())
//			throw new IllegalStateException("Why is createPriceConfig() called? Action is not ACTION_CREATE!");
//
//		return _createPriceConfig();
//	}
//
//	protected abstract GridPriceConfig _createPriceConfig();

//	public int getAction()
//	{
//		int mode = 0;
//
//		if (inheritPriceConfigRadio.getSelection())
//			mode = ACTION_INHERIT;
//		else if (assignPriceConfigLaterRadio.getSelection())
//			mode = ACTION_LATER;
//		else if (createPriceConfigRadio.getSelection())
//			mode = ACTION_CREATE;
//		else if (selectPriceConfigRadio.getSelection())
//			mode = ACTION_SELECT;
//
//		if (mode == 0)
//			throw new IllegalStateException("mode == 0!!!");
//
//		return mode;
//	}

	private int action;

	public int getAction()
	{
		return action;
	}
	
	public I18nTextBuffer getNewPriceConfigNameBuffer() {
		return newPriceConfigNameBuffer;
	}
	
	protected abstract List<? extends IInnerPriceConfig> retrievePriceConfigs(ProgressMonitor monitor);

	@Override
	public void onShow()
	{
		super.onShow();
		getShell().layout(true, true);
		getContainer().updateButtons();
	}
	
//	public boolean isCreateNewPriceConfigChosen() {
//		return createPriceConfigRadio.getSelection();
//	}
//	
//	public boolean isUseInheritedPriceConfigChosen() {
//		return inheritPriceConfigRadio.getSelection();
//	}	
}
