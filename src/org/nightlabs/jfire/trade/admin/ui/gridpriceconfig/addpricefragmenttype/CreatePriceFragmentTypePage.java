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

package org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.addpricefragmenttype;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.base.ui.wizard.DynamicPathWizardPage;
import org.nightlabs.i18n.I18nText;
import org.nightlabs.i18n.I18nTextBuffer;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.ObjectIDUtil;
import org.nightlabs.jfire.accounting.PriceFragmentType;
import org.nightlabs.jfire.accounting.dao.PriceFragmentTypeDAO;
import org.nightlabs.jfire.base.login.ui.Login;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class CreatePriceFragmentTypePage extends DynamicPathWizardPage
{
	private Text priceFragmentTypeID;
	private I18nTextBuffer priceFragmentTypeNameBuffer = new I18nTextBuffer();
	private I18nTextEditor priceFragmentTypeNameEditor;
	private Button checkBoxIsContainedInPriceFragmentTypeTotal;

	public CreatePriceFragmentTypePage()
	{
		super(CreatePriceFragmentTypePage.class.getName(), Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.addpricefragmenttype.CreatePriceFragmentTypePage.title")); //$NON-NLS-1$
		setDescription(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.addpricefragmenttype.CreatePriceFragmentTypePage.description")); //$NON-NLS-1$
	}

	@Override
	public Control createPageContents(Composite parent)
	{
		XComposite page = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);

		new Label(page, SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.addpricefragmenttype.CreatePriceFragmentTypePage.priceFragmentTypeIDLabel.text")); //$NON-NLS-1$
		priceFragmentTypeID = new Text(page, SWT.BORDER);
		priceFragmentTypeID.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		priceFragmentTypeID.addModifyListener(updateDialogModifyListener);
		new Label(page, SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.addpricefragmenttype.CreatePriceFragmentTypePage.nameLabel.text")); //$NON-NLS-1$
		priceFragmentTypeNameEditor = new I18nTextEditor(page);
		priceFragmentTypeNameEditor.setI18nText(priceFragmentTypeNameBuffer);
		priceFragmentTypeNameEditor.addModifyListener(updateDialogModifyListener);

		checkBoxIsContainedInPriceFragmentTypeTotal = new Button(page, SWT.CHECK);
		checkBoxIsContainedInPriceFragmentTypeTotal.setText("Is contained in \"total\" price fragment type.");
		checkBoxIsContainedInPriceFragmentTypeTotal.setToolTipText("If checked, the price fragments of the new price fragment type will be a part of the total price. This is important for money flow configurations.");

		final Display display = getShell().getDisplay();
		Job loadJob = new Job("Loading data") {
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				final PriceFragmentType pftTotal = PriceFragmentTypeDAO.sharedInstance().getPriceFragmentType(
						PriceFragmentType.PRICE_FRAGMENT_TYPE_ID_TOTAL,
						null,
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						monitor
				);

				display.asyncExec(new Runnable() {
					public void run() {
						priceFragmentTypeTotal = pftTotal;
						getContainer().updateButtons();
					}
				});

				return Status.OK_STATUS;
			}
		};
		loadJob.setPriority(Job.INTERACTIVE);
		loadJob.schedule();

		return page;
	}

	private PriceFragmentType priceFragmentTypeTotal;

	private ModifyListener updateDialogModifyListener = new ModifyListener() {
		@Override
		public void modifyText(ModifyEvent event)
		{
//			((DynamicPathWizard)getWizard()).updateDialog();
			getContainer().updateButtons();
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

	@Override
	public boolean isPageComplete()
	{
		if (priceFragmentTypeNameEditor == null)
			return false;

		return priceFragmentTypeTotal != null && !"".equals(priceFragmentTypeNameEditor.getEditText()); //$NON-NLS-1$
	}

	public PriceFragmentType createPriceFragmentType()
	{
		I18nTextBuffer priceFragmentTypeNameBuffer = getPriceFragmentTypeNameBuffer();

		String priceFragmentTypeID = getPriceFragmentTypeID().getText();
		if ("".equals(priceFragmentTypeID)) //$NON-NLS-1$
			priceFragmentTypeID = ObjectIDUtil.makeValidIDString(
					priceFragmentTypeNameBuffer.getText(I18nText.DEFAULT_LANGUAGEID), true
			);

		PriceFragmentType priceFragmentType;
		try {
			priceFragmentType = new PriceFragmentType(
					Login.getLogin().getOrganisationID(),
					priceFragmentTypeID
			);
		} catch (LoginException e) {
			throw new RuntimeException(e);
		}

		priceFragmentType.getName().copyFrom(priceFragmentTypeNameBuffer);

		if (checkBoxIsContainedInPriceFragmentTypeTotal.getSelection()) {
			priceFragmentType.setContainerPriceFragmentType(
					priceFragmentTypeTotal
			);
		}

		return priceFragmentType;
	}
}
