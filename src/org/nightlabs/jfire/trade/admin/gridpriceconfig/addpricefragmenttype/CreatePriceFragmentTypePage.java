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

package org.nightlabs.jfire.trade.admin.gridpriceconfig.addpricefragmenttype;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.base.ui.wizard.DynamicPathWizardPage;
import org.nightlabs.i18n.I18nTextBuffer;
import org.nightlabs.jfire.trade.admin.resource.Messages;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class CreatePriceFragmentTypePage extends DynamicPathWizardPage
{
	private Text priceFragmentTypeID;
	private I18nTextBuffer priceFragmentTypeNameBuffer = new I18nTextBuffer();
	private I18nTextEditor priceFragmentTypeNameEditor;

	public CreatePriceFragmentTypePage()
	{
		super(CreatePriceFragmentTypePage.class.getName(), Messages.getString("org.nightlabs.jfire.trade.admin.gridpriceconfig.addpricefragmenttype.CreatePriceFragmentTypePage.title")); //$NON-NLS-1$
		setDescription(Messages.getString("org.nightlabs.jfire.trade.admin.gridpriceconfig.addpricefragmenttype.CreatePriceFragmentTypePage.description")); //$NON-NLS-1$
	}

	/**
	 * @see org.nightlabs.base.ui.wizard.DynamicPathWizardPage#createPageContents(org.eclipse.swt.widgets.Composite)
	 */
	public Control createPageContents(Composite parent)
	{
		XComposite page = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);

		new Label(page, SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.trade.admin.gridpriceconfig.addpricefragmenttype.CreatePriceFragmentTypePage.priceFragmentTypeIDLabel.text")); //$NON-NLS-1$
		priceFragmentTypeID = new Text(page, SWT.BORDER);
		priceFragmentTypeID.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		priceFragmentTypeID.addModifyListener(updateDialogModifyListener);
		new Label(page, SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.trade.admin.gridpriceconfig.addpricefragmenttype.CreatePriceFragmentTypePage.nameLabel.text")); //$NON-NLS-1$
		priceFragmentTypeNameEditor = new I18nTextEditor(page);
		priceFragmentTypeNameEditor.setI18nText(priceFragmentTypeNameBuffer);
		priceFragmentTypeNameEditor.addModifyListener(updateDialogModifyListener);
		return page;
	}
	
	private ModifyListener updateDialogModifyListener = new ModifyListener() {
		public void modifyText(ModifyEvent event)
		{
			((DynamicPathWizard)getWizard()).updateDialog();
		}
	};

	/**
	 * @return Returns the priceFragmentTypeID.
	 */
	public Text getPriceFragmentTypeID()
	{
		return priceFragmentTypeID;
	}
	/**
	 * @return Returns the priceFragmentTypeNameBuffer.
	 */
	public I18nTextBuffer getPriceFragmentTypeNameBuffer()
	{
		return priceFragmentTypeNameBuffer;
	}

	/**
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	public boolean isPageComplete()
	{
		if (priceFragmentTypeNameEditor == null)
			return false;

		return !"".equals(priceFragmentTypeNameEditor.getEditText()); //$NON-NLS-1$
	}
}
