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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.annotation.Implement;
import org.nightlabs.base.ui.composite.FadeableComposite;
import org.nightlabs.base.ui.composite.InheritanceToggleButton;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.base.ui.wizard.DynamicPathWizardPage;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.prop.PropertySet;
import org.nightlabs.jfire.security.SecurityReflector;
import org.nightlabs.jfire.simpletrade.admin.ui.resource.Messages;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.dao.ProductTypeDAO;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.OrganisationLegalEntity;
import org.nightlabs.jfire.trade.admin.ui.editor.ownervendor.ILegalEntityValueChangedListener;
import org.nightlabs.jfire.trade.admin.ui.editor.ownervendor.LegalEntityEditComposite;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Fitas - fitas at nightlabs dot de
 */
public class OwnerVendorPage
extends DynamicPathWizardPage
{
	private ProductTypeID parentProductTypeID;
	private ProductType parentProductType;
	private LegalEntityEditComposite ownerEditComposite = null;
	private LegalEntityEditComposite vendorEditComposite = null;
	private InheritanceToggleButton inheritButtonOwner = null;
	private InheritanceToggleButton inheritButtonVendor = null;

	private LegalEntity originEntityOwner = null;
	private LegalEntity originEntityVendor = null;


	public static final String[] FETCH_GROUPS_PARENT_PRODUCT_TYPE = {
		FetchPlan.DEFAULT,
		ProductType.FETCH_GROUP_OWNER,
		ProductType.FETCH_GROUP_VENDOR,
		LegalEntity.FETCH_GROUP_PERSON,
		PropertySet.FETCH_GROUP_FULL_DATA
	};

	/**
	 * @param pageName
	 */
	public OwnerVendorPage(ProductTypeID parentProductTypeID)
	{
		super(ProductTypeNamePage.class.getName(), "Owner && Vendor"); //$NON-NLS-1$
		this.setDescription("Please Define the Owner and Vendor"); //$NON-NLS-1$
//		this.parentProductType = parentProductType;
		this.parentProductTypeID = parentProductTypeID;
	}

	public LegalEntityEditComposite getOwnerEditComposite() {
		return ownerEditComposite;
	}

	public LegalEntityEditComposite getVendorEditComposite() {
		return vendorEditComposite;
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
		comp0.getGridLayout().numColumns = 3;

		Label labelOwner = new Label(comp0, SWT.NONE);
		labelOwner.setText(Messages.getString("org.nightlabs.jfire.simpletrade.admin.ui.producttype.create.OwnerVendorPage.labelOwner.text")); //$NON-NLS-1$
//		labelOwner.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		this.ownerEditComposite = new LegalEntityEditComposite(comp0, SWT.NONE);
		this.ownerEditComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		ownerEditComposite.addLegalEntityValueChangedListener(
				new ILegalEntityValueChangedListener()
				{
					public void legalEntityValueChanged()
					{
						inheritButtonOwner.setSelection(false);
						// if value has changed
						originEntityOwner = getOwnerEntity();
					}
				});

		inheritButtonOwner = new InheritanceToggleButton(comp0,null);
//		inheritButtonOwner.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		inheritButtonOwner.setSelection(true);
		inheritButtonOwner.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {

				if(inheritButtonOwner.getSelection())
					getOwnerEditComposite().setLegalEntity(parentProductType.getOwner());
				else
				{
					if(originEntityOwner != null)
						getOwnerEditComposite().setLegalEntity(originEntityOwner);
				}

			}
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		new Label(page, SWT.NONE);

		XComposite comp1 = new XComposite(page, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		comp1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		comp1.getGridLayout().numColumns = 3;

		Label labelVendor = new Label(comp1, SWT.NONE);
		labelVendor.setText(Messages.getString("org.nightlabs.jfire.simpletrade.admin.ui.producttype.create.OwnerVendorPage.labelVendor.text")); //$NON-NLS-1$
//		labelVendor.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		this.vendorEditComposite = new LegalEntityEditComposite(comp1, SWT.NONE);
		this.vendorEditComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		vendorEditComposite.addLegalEntityValueChangedListener(
				new ILegalEntityValueChangedListener()
				{
					public void legalEntityValueChanged()
					{

						String VendorOrgId = vendorEditComposite.getLegalEntity().getOrganisationID();
						if (!VendorOrgId.equals(SecurityReflector.getUserDescriptor().getOrganisationID()) && (vendorEditComposite.getLegalEntity() instanceof OrganisationLegalEntity))
						{
							MessageDialog.openError(RCPUtil.getActiveShell(),"can't assign a Foreign OrganisationLegalEntity", "you Cannot assign a foreign OrganisationLegalEntity as vendor of a ProductType!");
							//	revert to original value						
							vendorEditComposite.setLegalEntity(originEntityVendor);
						}
						else
						{			
							inheritButtonVendor.setSelection(false);
							// if value has changed
							originEntityVendor = getVendorEntity();
						}

					}
				});

		inheritButtonVendor = new InheritanceToggleButton(comp1,null);
//		inheritButtonVendor.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		inheritButtonVendor.setSelection(true);
		inheritButtonVendor.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {

				if(inheritButtonVendor.getSelection())
					getVendorEditComposite().setLegalEntity(parentProductType.getVendor());
				else
				{
					if(originEntityVendor != null)
						getVendorEditComposite().setLegalEntity(originEntityVendor);
				}
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		page.setFaded(true);

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
						if (parentProductType != null)
						{
							getOwnerEditComposite().setLegalEntity(parentProductType.getOwner());
							getVendorEditComposite().setLegalEntity(parentProductType.getVendor());
						}
					}
				});

				return Status.OK_STATUS;
			}
		};
		job.setPriority(org.eclipse.core.runtime.jobs.Job.SHORT);
		job.schedule();

		return page;
	}

	protected LegalEntity getOwnerEntity()
	{
		return getOwnerEditComposite().getLegalEntity();
	}

	protected LegalEntity getVendorEntity()
	{
		return getVendorEditComposite().getLegalEntity();
	}

}
