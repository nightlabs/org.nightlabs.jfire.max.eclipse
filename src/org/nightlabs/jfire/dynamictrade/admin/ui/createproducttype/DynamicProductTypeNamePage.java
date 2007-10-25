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

package org.nightlabs.jfire.dynamictrade.admin.ui.createproducttype;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.language.I18nTextEditorTable;
import org.nightlabs.base.ui.language.II18nTextEditor;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.wizard.DynamicPathWizardPage;
import org.nightlabs.i18n.I18nTextBuffer;
import org.nightlabs.jfire.dynamictrade.admin.ui.DynamicTradeAdminPlugin;
import org.nightlabs.jfire.dynamictrade.admin.ui.resource.Messages;
import org.nightlabs.jfire.dynamictrade.store.DynamicProductType;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class DynamicProductTypeNamePage extends DynamicPathWizardPage
{
	private ProductTypeID parentDynamicProductTypeID;
	private Combo inheritanceNatureCombo;

	private I18nTextBuffer dynamicProductTypeNameBuffer;
	private II18nTextEditor dynamicProductTypeNameEditor;

	public DynamicProductTypeNamePage(ProductTypeID parentDynamicProductTypeID)
	{
		super(DynamicProductTypeNamePage.class.getName(),
				Messages.getString("org.nightlabs.jfire.dynamictrade.admin.ui.createproducttype.DynamicProductTypeNamePage.title"), //$NON-NLS-1$
				SharedImages.getWizardPageImageDescriptor(DynamicTradeAdminPlugin.getDefault(), DynamicProductTypeNamePage.class));
		this.setDescription(Messages.getString("org.nightlabs.jfire.dynamictrade.admin.ui.createproducttype.DynamicProductTypeNamePage.description")); //$NON-NLS-1$
		this.parentDynamicProductTypeID = parentDynamicProductTypeID;
	}
 
	@Override
	public Control createPageContents(Composite parent)
	{
		XComposite page = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);

		XComposite comp0 = new XComposite(page, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		comp0.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		comp0.getGridLayout().numColumns = 2;
		inheritanceNatureCombo = new Combo(comp0, SWT.READ_ONLY);
		inheritanceNatureCombo.add(Messages.getString("org.nightlabs.jfire.dynamictrade.admin.ui.createproducttype.DynamicProductTypeNamePage.inheritanceNatureCombo.item_node")); //$NON-NLS-1$
		inheritanceNatureCombo.add(Messages.getString("org.nightlabs.jfire.dynamictrade.admin.ui.createproducttype.DynamicProductTypeNamePage.inheritanceNatureCombo.item_leaf")); //$NON-NLS-1$
		inheritanceNatureCombo.select(0);
		inheritanceNatureCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				inheritanceNatureCombo_selectionChanged();
			}
		});
		inheritanceNatureCombo_selectionChanged();

		new Label(page, SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.dynamictrade.admin.ui.createproducttype.DynamicProductTypeNamePage.nameLabel.text")); //$NON-NLS-1$
		dynamicProductTypeNameBuffer = new I18nTextBuffer();
		dynamicProductTypeNameEditor = new I18nTextEditorTable(page);
		dynamicProductTypeNameEditor.setI18nText(dynamicProductTypeNameBuffer);
		dynamicProductTypeNameEditor.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0)
			{
				getWizard().getContainer().updateButtons();
			}
		});

		return page;
	}

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

	private byte packageNature = ProductType.PACKAGE_NATURE_OUTER; // it's always this, right?!

	public byte getPackageNature()
	{
		return packageNature;
	}

	/**
	 * @return Returns the dynamicProductTypeNameEditor.
	 */
	public II18nTextEditor getDynamicProductTypeNameEditor()
	{
		return dynamicProductTypeNameEditor;
	}
	/**
	 * @return Returns the dynamicProductTypeNameBuffer.
	 */
	public I18nTextBuffer getDynamicProductTypeNameBuffer()
	{
		return dynamicProductTypeNameBuffer;
	}

	@Override
	public boolean isPageComplete()
	{
		return !dynamicProductTypeNameBuffer.isEmpty();
	}
}
