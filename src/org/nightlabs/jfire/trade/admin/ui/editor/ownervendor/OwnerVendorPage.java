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

package org.nightlabs.jfire.trade.admin.ui.editor.ownervendor;

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
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.dao.ProductTypeDAO;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.OrganisationLegalEntity;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
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
	private boolean ownerInherited = false;
	private boolean vendorInherited = false;
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
		super(OwnerVendorPage.class.getName(), Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.ownervendor.OwnerVendorPage.page.title"));  //$NON-NLS-1$
		this.setDescription(Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.ownervendor.OwnerVendorPage.page.description")); //$NON-NLS-1$
		this.parentProductTypeID = parentProductTypeID;
		inilialize();
	}

	protected void inilialize() {

		Job job = new Job(Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.ownervendor.OwnerVendorPage.loadingProduct")) {  //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor)
			throws Exception
			{
				if (parentProductTypeID != null)
					parentProductType = ProductTypeDAO.sharedInstance().getProductType(parentProductTypeID, FETCH_GROUPS_PARENT_PRODUCT_TYPE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);

				Display.getDefault().asyncExec(new Runnable()
				{
					public void run()
					{
						if (parentProductType != null)
						{
							originEntityOwner = parentProductType.getOwner();
							originEntityVendor = parentProductType.getVendor();
							ownerInherited = true;
							vendorInherited = true;

						}
					}
				});

				return Status.OK_STATUS;
			}
		};
		job.setPriority(org.eclipse.core.runtime.jobs.Job.SHORT);
		job.schedule();	
	}

	public LegalEntityEditComposite getOwnerEditComposite() {
		return ownerEditComposite;
	}

	public LegalEntityEditComposite getVendorEditComposite() {
		return vendorEditComposite;
	}

	@Override
	public boolean isPageComplete() {	
		if(!vendorInherited) 
			if (originEntityVendor == null)
				return false;

		if(!ownerInherited) 
			if (originEntityOwner == null)
				return false;				

		return true;
	}

	public void configureProductType(ProductType productType) 
	{
		if (originEntityVendor != null)
			productType.setVendor(originEntityOwner);      

		if (originEntityVendor != null)
			productType.setOwner(originEntityOwner);

		productType.getFieldMetaData(ProductType.FieldName.vendor).setValueInherited(vendorInherited);
		productType.getFieldMetaData(ProductType.FieldName.owner).setValueInherited(ownerInherited);
	}


	/**
	 * @see org.nightlabs.base.ui.wizard.DynamicPathWizardPage#createPageContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control createPageContents(Composite parent)
	{
		final FadeableComposite page = new FadeableComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		XComposite comp0 = new XComposite(page, SWT.NONE, LayoutMode.ORDINARY_WRAPPER);
		comp0.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		comp0.getGridLayout().numColumns = 3;

		Label labelOwner = new Label(comp0, SWT.NONE);
		labelOwner.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.ownervendor.OwnerVendorPage.labelOwner.text"));  //$NON-NLS-1$

		this.ownerEditComposite = new LegalEntityEditComposite(comp0, SWT.NONE);
		this.ownerEditComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		ownerEditComposite.addLegalEntityValueChangedListener(
				new ILegalEntityValueChangedListener()
				{
					public void legalEntityValueChanged()
					{
						inheritButtonOwner.setSelection(false);
						ownerInherited = inheritButtonOwner.getSelection();
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

				ownerInherited = inheritButtonOwner.getSelection();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

//		new Label(page, SWT.NONE);

//		XComposite comp1 = new XComposite(page, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
//		comp1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		comp1.getGridLayout().numColumns = 3;

		Label labelVendor = new Label(comp0, SWT.NONE);
		labelVendor.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.ownervendor.OwnerVendorPage.labelVendor.text"));  //$NON-NLS-1$

		this.vendorEditComposite = new LegalEntityEditComposite(comp0, SWT.NONE);
		this.vendorEditComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		vendorEditComposite.addLegalEntityValueChangedListener(
				new ILegalEntityValueChangedListener()
				{
					public void legalEntityValueChanged()
					{

						String VendorOrgId = vendorEditComposite.getLegalEntity().getOrganisationID();
						if (!VendorOrgId.equals(SecurityReflector.getUserDescriptor().getOrganisationID()) && (vendorEditComposite.getLegalEntity() instanceof OrganisationLegalEntity))
						{
							MessageDialog.openError(RCPUtil.getActiveShell(),Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.ownervendor.OwnerVendorPage.errorDialog.title"), Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.ownervendor.OwnerVendorPage.errorDialog.message")); //$NON-NLS-1$ //$NON-NLS-2$
							//	revert to original value						
							vendorEditComposite.setLegalEntity(originEntityVendor);
						}
						else
						{			
							inheritButtonVendor.setSelection(false);
							vendorInherited = inheritButtonVendor.getSelection();
							// if value has changed
							originEntityVendor = getVendorEntity();
						}

					}
				});

		inheritButtonVendor = new InheritanceToggleButton(comp0, null);
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
				vendorInherited = inheritButtonVendor.getSelection();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		if (parentProductType != null)
		{
			getOwnerEditComposite().setLegalEntity(parentProductType.getOwner());
			getVendorEditComposite().setLegalEntity(parentProductType.getVendor());
		}

		return page;
	}

	public LegalEntity getOwnerEntity()
	{
		return getOwnerEditComposite().getLegalEntity();
	}

	public LegalEntity getVendorEntity()
	{
		return getVendorEditComposite().getLegalEntity();
	}


}
