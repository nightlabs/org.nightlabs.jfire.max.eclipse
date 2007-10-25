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

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.annotation.Implement;
import org.nightlabs.base.ui.composite.FadeableComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.language.I18nTextEditorTable;
import org.nightlabs.base.ui.language.II18nTextEditor;
import org.nightlabs.base.ui.wizard.DynamicPathWizardPage;
import org.nightlabs.i18n.I18nTextBuffer;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.simpletrade.admin.ui.resource.Messages;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.ui.store.ProductTypeDAO;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class ProductTypeNamePage 
extends DynamicPathWizardPage
{
	private Combo inheritanceNatureCombo;
	private Combo packageNatureCombo;
	private I18nTextBuffer productTypeNameBuffer;
	private II18nTextEditor productTypeNameEditor;
//	private Button createPriceConfigCheckBox;
	private ProductTypeID parentProductTypeID;
	private ProductType parentProductType;
	private Label packageNatureDescription;
	
	/**
	 * @param pageName
	 */
	public ProductTypeNamePage(ProductTypeID parentProductTypeID)
	{
		super(ProductTypeNamePage.class.getName(), Messages.getString("org.nightlabs.jfire.simpletrade.admin.ui.producttype.create.ProductTypeNamePage.title")); //$NON-NLS-1$
		this.setDescription(Messages.getString("org.nightlabs.jfire.simpletrade.admin.ui.producttype.create.ProductTypeNamePage.description")); //$NON-NLS-1$
//		this.parentProductType = parentProductType;
		this.parentProductTypeID = parentProductTypeID;
	}
 
	/**
	 * @see org.nightlabs.base.ui.wizard.DynamicPathWizardPage#createPageContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control createPageContents(Composite parent)
	{
		final FadeableComposite page = new FadeableComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		XComposite comp0 = new XComposite(page, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		comp0.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		comp0.getGridLayout().numColumns = 2;
		inheritanceNatureCombo = new Combo(comp0, SWT.READ_ONLY);
		inheritanceNatureCombo.add( Messages.getString("org.nightlabs.jfire.simpletrade.admin.ui.producttype.create.ProductTypeNamePage.inheritanceNatureCombo.item_node")); //$NON-NLS-1$
		inheritanceNatureCombo.add(Messages.getString("org.nightlabs.jfire.simpletrade.admin.ui.producttype.create.ProductTypeNamePage.inheritanceNatureCombo.item_leaf")); //$NON-NLS-1$
		inheritanceNatureCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				inheritanceNatureCombo_selectionChanged();
			}
		});

		packageNatureCombo = new Combo(comp0, SWT.READ_ONLY);
		packageNatureCombo.add(Messages.getString("org.nightlabs.jfire.simpletrade.admin.ui.producttype.create.ProductTypeNamePage.packageNatureCombo.item_inner")); //$NON-NLS-1$
		packageNatureCombo.add(Messages.getString("org.nightlabs.jfire.simpletrade.admin.ui.producttype.create.ProductTypeNamePage.packageNatureCombo.item_outer")); //$NON-NLS-1$

		packageNatureCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				packageNatureCombo_selectionChanged();
			}
		});

		packageNatureDescription = new Label(page, SWT.WRAP);
		packageNatureDescription.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		new Label(page, SWT.NONE);
		
		new Label(page, SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.simpletrade.admin.ui.producttype.create.ProductTypeNamePage.nameLabel.text")); //$NON-NLS-1$
		productTypeNameBuffer = new I18nTextBuffer();
		productTypeNameEditor = new I18nTextEditorTable(page);
		productTypeNameEditor.setI18nText(productTypeNameBuffer);
		productTypeNameEditor.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0)
			{
				getWizard().getContainer().updateButtons();
			}
		});

		// standard selection = category
		inheritanceNatureCombo.select(0);
		inheritanceNatureCombo_selectionChanged();
		
		page.setFaded(true);
		packageNatureDescription.setText(Messages.getString("org.nightlabs.jfire.simpletrade.admin.ui.producttype.create.ProductTypeNamePage.packageNatureDescription.text_loading")); //$NON-NLS-1$

		Job job = new Job(Messages.getString("org.nightlabs.jfire.simpletrade.admin.ui.producttype.create.ProductTypeNamePage.loadProductTypeJob.name")) { //$NON-NLS-1$
			@Override
			@Implement
			protected IStatus run(ProgressMonitor monitor)
			throws Exception
			{
				if (parentProductTypeID != null)
					parentProductType = ProductTypeDAO.sharedInstance().getProductType(parentProductTypeID, FETCH_GROUPS_PARENT_PRODUCT_TYPE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);

				Display.getDefault().asyncExec(new Runnable()
				{
					public void run()
					{
						page.setFaded(false);

						if (parentProductType == null)
							packageNatureCombo.select(1);
						else {
							if (parentProductType.getPackageNature() == ProductType.PACKAGE_NATURE_INNER)
								packageNatureCombo.select(0);
							else
								packageNatureCombo.select(1);
						}

						packageNatureCombo_selectionChanged();
					}
				});

				return Status.OK_STATUS;
			}
		};
		job.setPriority(org.eclipse.core.runtime.jobs.Job.SHORT);
		job.schedule();

		return page;
	}

	public static final String[] FETCH_GROUPS_PARENT_PRODUCT_TYPE = {
		FetchPlan.DEFAULT
	};

	private byte inheritanceNature;

	private void inheritanceNatureCombo_selectionChanged()
	{
		switch (inheritanceNatureCombo.getSelectionIndex()) {
			case 0:
				inheritanceNature = ProductType.INHERITANCE_NATURE_BRANCH;
				break;
			case 1:
				inheritanceNature = ProductType.INHERITANCE_NATURE_LEAF;
				break;
			default:
				throw new IllegalStateException("Unknown inheritanceNatureCombo.selectionIndex!"); //$NON-NLS-1$
		}
	}

	public byte getInheritanceNature()
	{
		return inheritanceNature;
	}

	private byte packageNature;

	private void packageNatureCombo_selectionChanged()
	{
		switch (packageNatureCombo.getSelectionIndex()) {
			case 0:
				packageNature = ProductType.PACKAGE_NATURE_INNER;
				setPackageNatureDescription();
				break;
			case 1:
				packageNature = ProductType.PACKAGE_NATURE_OUTER;
				setPackageNatureDescription();
				break;
			default:
				throw new IllegalStateException("Unknown packageNatureCombo.selectionIndex!"); //$NON-NLS-1$
		}
	}

	public byte getPackageNature()
	{
		return packageNature;
	}

	/**
	 * @return Returns the productTypeNameEditor.
	 */
	public II18nTextEditor getProductTypeNameEditor()
	{
		return productTypeNameEditor;
	}
	/**
	 * @return Returns the productTypeNameBuffer.
	 */
	public I18nTextBuffer getProductTypeNameBuffer()
	{
		return productTypeNameBuffer;
	}

//	public boolean isCreatePriceConfig()
//	{
//		return createPriceConfigCheckBox.getSelection();
//	}

	@Override
	public boolean isPageComplete()
	{
		return !productTypeNameBuffer.isEmpty();
	}

	private void setPackageNatureDescription() 
	{
		if (packageNatureCombo.getSelectionIndex() == 0)
			packageNatureDescription.setText(Messages.getString("org.nightlabs.jfire.simpletrade.admin.ui.producttype.create.ProductTypeNamePage.packageNatureDescription.text_inner")); //$NON-NLS-1$
		if (packageNatureCombo.getSelectionIndex() == 1)		
			packageNatureDescription.setText(Messages.getString("org.nightlabs.jfire.simpletrade.admin.ui.producttype.create.ProductTypeNamePage.packageNatureDescription.text_outer")); //$NON-NLS-1$

		getShell().layout(true, true);
	}
}
