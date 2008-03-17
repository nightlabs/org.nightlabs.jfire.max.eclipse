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

package org.nightlabs.jfire.voucher.admin.ui.createvouchertype;

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
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.voucher.admin.ui.VoucherAdminPlugin;
import org.nightlabs.jfire.voucher.admin.ui.resource.Messages;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class VoucherTypeNamePage extends DynamicPathWizardPage
{
	@SuppressWarnings("unused")
	private ProductTypeID parentVoucherTypeID;
	private Combo inheritanceNatureCombo;

	private I18nTextBuffer voucherTypeNameBuffer;
	private II18nTextEditor voucherTypeNameEditor;

	public VoucherTypeNamePage(ProductTypeID parentVoucherTypeID)
	{
		super(VoucherTypeNamePage.class.getName(), Messages.getString("org.nightlabs.jfire.voucher.admin.ui.createvouchertype.VoucherTypeNamePage.title"), //$NON-NLS-1$
				SharedImages.getWizardPageImageDescriptor(VoucherAdminPlugin.getDefault(), VoucherTypeNamePage.class));
		this.setDescription(Messages.getString("org.nightlabs.jfire.voucher.admin.ui.createvouchertype.VoucherTypeNamePage.description")); //$NON-NLS-1$
		this.parentVoucherTypeID = parentVoucherTypeID;
	}

	@Override
	public Control createPageContents(Composite parent)
	{
		XComposite page = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);

		XComposite comp0 = new XComposite(page, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		comp0.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		comp0.getGridLayout().numColumns = 2;
		inheritanceNatureCombo = new Combo(comp0, SWT.READ_ONLY);
		inheritanceNatureCombo.add(Messages.getString("org.nightlabs.jfire.voucher.admin.ui.createvouchertype.VoucherTypeNamePage.inheritanceNatureCombo.item_node")); //$NON-NLS-1$
		inheritanceNatureCombo.add(Messages.getString("org.nightlabs.jfire.voucher.admin.ui.createvouchertype.VoucherTypeNamePage.inheritanceNatureCombo.item_leaf")); //$NON-NLS-1$
		inheritanceNatureCombo.select(0);
		inheritanceNatureCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				inheritanceNatureCombo_selectionChanged();
			}
		});
		inheritanceNatureCombo_selectionChanged();

		new Label(page, SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.voucher.admin.ui.createvouchertype.VoucherTypeNamePage.nameLabel.text")); //$NON-NLS-1$
		voucherTypeNameBuffer = new I18nTextBuffer();
		voucherTypeNameEditor = new I18nTextEditorTable(page);
		voucherTypeNameEditor.setI18nText(voucherTypeNameBuffer);
		voucherTypeNameEditor.addModifyListener(new ModifyListener() {
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
	 * @return Returns the voucherTypeNameEditor.
	 */
	public II18nTextEditor getVoucherTypeNameEditor()
	{
		return voucherTypeNameEditor;
	}
	/**
	 * @return Returns the voucherTypeNameBuffer.
	 */
	public I18nTextBuffer getVoucherTypeNameBuffer()
	{
		return voucherTypeNameBuffer;
	}

	@Override
	public boolean isPageComplete()
	{
//		return !"".equals(voucherTypeNameEditor.getEditText());
		return !voucherTypeNameBuffer.isEmpty();
	}
}
