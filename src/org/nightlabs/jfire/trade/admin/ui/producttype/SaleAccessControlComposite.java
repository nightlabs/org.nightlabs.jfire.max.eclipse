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

package org.nightlabs.jfire.trade.admin.ui.producttype;

import java.util.Locale;

import javax.jdo.JDOHelper;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.ModuleException;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.notification.IDirtyStateManager;
import org.nightlabs.base.ui.notification.NotificationAdapterJob;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleManager;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.jdo.notification.DirtyObjectID;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.StoreManager;
import org.nightlabs.jfire.store.StoreManagerUtil;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;

/**
 * With this composite the user can control the sale status of every
 * product type. Currently, this is only the published and saleable
 * flags, but on the long run, access rights can be managed via Authority
 * assignment and manipulation of the Authority.
 *
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class SaleAccessControlComposite extends XComposite
{
	private Label productTypeLabel;

	private ProductType productType;

	private Button publishedCheckBox;
	private Button confirmedCheckBox;
	private Button saleableCheckBox;
	private Button closedCheckBox;

	private SaleAccessControlHelper saleAccessControlHelper;

	private boolean showProductTypeLabel = true;
	
	private IDirtyStateManager dirtyStateManager;
	
	/**
	 * @param parent SWT parent composite (into which this composite will be added as child).
	 * @param style SWT style
	 * @param fetchGroupsProductType Can be null.
	 * @param saleAccessControlHelper Must not be null.
	 */	
	public SaleAccessControlComposite(Composite parent, int style, 
			SaleAccessControlHelper saleAccessControlHelper) 
	{
		this(parent, style, saleAccessControlHelper, true, null);
	}
	
	/**
	 * @param parent SWT parent composite (into which this composite will be added as child).
	 * @param style SWT style
	 * @param fetchGroupsProductType Can be null.
	 * @param _saleAccessControlHelper Must not be null.
	 * @param showProductTypeLabel determines if the the name of the productType should be displayed
	 * or not
	 */
	public SaleAccessControlComposite(Composite parent, int style, 
			SaleAccessControlHelper _saleAccessControlHelper, boolean showProductTypeLabel,
			IDirtyStateManager dirtyStateManager)
	{
//		super(parent, style, LayoutMode.TIGHT_WRAPPER);
		super(parent, style);

		this.dirtyStateManager = dirtyStateManager;
		this.showProductTypeLabel = showProductTypeLabel;
		
//		Set fetchGroups = _saleAccessControlHelper.getFetchGroupsProductType();
//		if (fetchGroups.isEmpty())
//			this.fetchGroupsProductType = FETCH_GROUPS_PRODUCT_TYPE_MIN;
//		else {
//			fetchGroups.addAll(Utils.array2ArrayList(FETCH_GROUPS_PRODUCT_TYPE_MIN));
//			this.fetchGroupsProductType = (String[]) Utils.collection2TypedArray(fetchGroups, String.class);
//		}

		if (_saleAccessControlHelper == null)
			throw new NullPointerException("saleAccessControlHelper"); //$NON-NLS-1$

		this.saleAccessControlHelper = _saleAccessControlHelper;

		if (showProductTypeLabel) {
			productTypeLabel = new Label(this, SWT.BORDER);
			productTypeLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));			
		}

		XComposite statusComp = new XComposite(this, SWT.NONE);
		statusComp.getGridLayout().numColumns = 4;
		statusComp.getGridData().grabExcessVerticalSpace = false;

		publishedCheckBox = new Button(statusComp, SWT.CHECK);
		publishedCheckBox.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.producttype.SaleAccessControlComposite.publishedCheckBox.text")); //$NON-NLS-1$
		publishedCheckBox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				publishedCheckBoxChanged();
			}
		});

		confirmedCheckBox = new Button(statusComp, SWT.CHECK);
		confirmedCheckBox.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.producttype.SaleAccessControlComposite.confirmedCheckBox.text")); //$NON-NLS-1$
		confirmedCheckBox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				confirmedCheckBoxChanged();
			}
		});

		saleableCheckBox = new Button(statusComp, SWT.CHECK);
		saleableCheckBox.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.producttype.SaleAccessControlComposite.saleableCheckBox.text")); //$NON-NLS-1$
		saleableCheckBox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				saleableCheckBoxChanged();
			}
		});

		closedCheckBox = new Button(statusComp, SWT.CHECK);
		closedCheckBox.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.producttype.SaleAccessControlComposite.closedCheckBox.text")); //$NON-NLS-1$
		closedCheckBox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				closedCheckBoxChanged();
			}
		});

		JDOLifecycleManager.sharedInstance().addNotificationListener(ProductType.class, productTypeChangedListener);
		addDisposeListener(new DisposeListener(){
			public void widgetDisposed(DisposeEvent e)
			{
				JDOLifecycleManager.sharedInstance().removeNotificationListener(ProductType.class, productTypeChangedListener);
			}
		});

		setProductType(null);
	}

	protected void updateControlsEnabled()
	{
		if (productType == null)
			return;

		saleableCheckBox.setEnabled(confirmedCheckBox.getSelection());
		closedCheckBox.setEnabled(productType.isConfirmed());
	}	
	
	protected void publishedCheckBoxChanged()
	{
		try {
			if (publishedCheckBox.getSelection()) {
				boolean flag = saleAccessControlHelper.canPublish(false);

				if (flag) {
					flag = MessageDialog.openConfirm(
							RCPUtil.getActiveWorkbenchShell(),
							Messages.getString("org.nightlabs.jfire.trade.admin.ui.producttype.SaleAccessControlComposite.publishDialog.title"), //$NON-NLS-1$
							Messages.getString("org.nightlabs.jfire.trade.admin.ui.producttype.SaleAccessControlComposite.publishDialog.message")); //$NON-NLS-1$
				}

				if (!flag)
					publishedCheckBox.setSelection(false);
			}
			else {
				if (productType.isPublished()) {
					MessageDialog.openError(
							RCPUtil.getActiveWorkbenchShell(),
							Messages.getString("org.nightlabs.jfire.trade.admin.ui.producttype.SaleAccessControlComposite.publishDialog.undo.title"), //$NON-NLS-1$
							Messages.getString("org.nightlabs.jfire.trade.admin.ui.producttype.SaleAccessControlComposite.publishDialog.undo.message")); //$NON-NLS-1$
					publishedCheckBox.setSelection(true);
				}
			}
		} catch (Throwable t) {
			publishedCheckBox.setSelection(!publishedCheckBox.getSelection());
			throw new RuntimeException(t);
		}
		updateControlsEnabled();
		calculateChanged();
		
		if (dirtyStateManager != null) {
			if (isChanged())
				dirtyStateManager.markDirty();				
		}		
	}

	protected void confirmedCheckBoxChanged()
	{
		try {
			if (confirmedCheckBox.getSelection()) {
				boolean flag = saleAccessControlHelper.canConfirm(false);

				if (flag) {
					flag = MessageDialog.openConfirm(
							RCPUtil.getActiveWorkbenchShell(),
							Messages.getString("org.nightlabs.jfire.trade.admin.ui.producttype.SaleAccessControlComposite.confirmDialog.title"), //$NON-NLS-1$
							Messages.getString("org.nightlabs.jfire.trade.admin.ui.producttype.SaleAccessControlComposite.confirmDialog.message")); //$NON-NLS-1$
				}

				if (!flag)
					confirmedCheckBox.setSelection(false);
				else {
					if (!saleableCheckBox.isEnabled()) {
						saleableCheckBox.setSelection(true);
						saleableCheckBoxChanged();
					}
				}
			}
			else {
				if (productType.isConfirmed()) {
					MessageDialog.openError(
							RCPUtil.getActiveWorkbenchShell(),
							Messages.getString("org.nightlabs.jfire.trade.admin.ui.producttype.SaleAccessControlComposite.undoConfirmDialog.title"), //$NON-NLS-1$
							Messages.getString("org.nightlabs.jfire.trade.admin.ui.producttype.SaleAccessControlComposite.undoConfirmDialog.message")); //$NON-NLS-1$
					confirmedCheckBox.setSelection(true);
				}
				else
					saleableCheckBox.setSelection(false);
			}
		} catch (Throwable t) {
			confirmedCheckBox.setSelection(!confirmedCheckBox.getSelection());
			throw new RuntimeException(t);
		}
		updateControlsEnabled();
		calculateChanged();
		
		if (dirtyStateManager != null) {
			if (isChanged())
				dirtyStateManager.markDirty();				
		}				
	}

	protected void saleableCheckBoxChanged()
	{
		try {
			if (saleableCheckBox.getSelection()) {
				boolean flag = saleAccessControlHelper.canSetSaleable(false, saleableCheckBox.getSelection());

				if (!flag)
					saleableCheckBox.setSelection(!saleableCheckBox.getSelection());
			}
			else {
				// nothing
			}
		} catch (Throwable t) {
			saleableCheckBox.setSelection(!saleableCheckBox.getSelection());
			throw new RuntimeException(t);
		}
		updateControlsEnabled();
		calculateChanged();
		
		if (dirtyStateManager != null) {
			if (isChanged())
				dirtyStateManager.markDirty();				
		}		
	}

	protected void closedCheckBoxChanged()
	{
		try {
			if (closedCheckBox.getSelection()) {
				boolean flag = saleAccessControlHelper.canClose(false);

				if (flag) {
					flag = MessageDialog.openConfirm(
							RCPUtil.getActiveWorkbenchShell(),
							Messages.getString("org.nightlabs.jfire.trade.admin.ui.producttype.SaleAccessControlComposite.closeDialog.title"), //$NON-NLS-1$
							Messages.getString("org.nightlabs.jfire.trade.admin.ui.producttype.SaleAccessControlComposite.closeDialog.message"));					 //$NON-NLS-1$
				}
				
				if (!flag)
					closedCheckBox.setSelection(false);
				
				saleableCheckBox.setSelection(false);	
			}
			else {
				// nothing
				saleableCheckBox.setSelection(true);	
			}
		} catch (Throwable t) {
			closedCheckBox.setSelection(!closedCheckBox.getSelection());
			throw new RuntimeException(t);
		}
		updateControlsEnabled();
		calculateChanged();
		
		if (dirtyStateManager != null) {
			if (isChanged())
				dirtyStateManager.markDirty();				
		}
	}

	private boolean published = false;
	public boolean isPublished() {
		return published;
	}
	
	private boolean confirmed = false;
	public boolean isConfirmed() {
		return confirmed;
	}
	
	private boolean saleable = false;
	public boolean isSaleable() {
		return saleable;
	}
	
	private boolean closed = false;
	public boolean isClosed() {
		return closed;
	}
	
	private void calculateChanged()
	{
		changed =
				(publishedCheckBox.getSelection() != productType.isPublished()) ||
				(confirmedCheckBox.getSelection() != productType.isConfirmed()) ||
				(saleableCheckBox.getSelection() != productType.isSaleable()) ||
				(closedCheckBox.getSelection() != productType.isClosed());
		
		published = publishedCheckBox.getSelection();
		confirmed = confirmedCheckBox.getSelection();
		saleable = saleableCheckBox.getSelection();
		closed = closedCheckBox.getSelection();
	}

	protected NotificationListener productTypeChangedListener = new NotificationAdapterJob(Messages.getString("org.nightlabs.jfire.trade.admin.ui.producttype.SaleAccessControlComposite.loadProductTypesJob.name")) { //$NON-NLS-1$
		public void notify(NotificationEvent notificationEvent)
		{
			DirtyObjectID dirtyObjectID = (DirtyObjectID) notificationEvent.getFirstSubject();
			ProductTypeID productTypeID = (ProductTypeID) dirtyObjectID.getObjectID();
			if (productTypeID == null || !productTypeID.equals(JDOHelper.getObjectId(productType)))
				return; // We have none or another object open and are not interested in this change.

			try {
				setProductTypeID(productTypeID);
			} catch (ModuleException e) {
				throw new RuntimeException(e);
			}
		}
	};

	public SaleAccessControlHelper getSaleAccessControlHelper()
	{
		return saleAccessControlHelper;
	}

	private String[] fetchGroupsProductType;

	public String[] getFetchGroupsProductType()
	{
		return fetchGroupsProductType;
	}

//	protected static String[] FETCH_GROUPS_PRODUCT_TYPE_MIN = new String[] {
//		FetchPlan.DEFAULT,
//		ProductType.FETCH_GROUP_NAME};

	/**
	 * @deprecated should not be used anymore, call {@link SaleAccessControlComposite#setProductType(ProductType)} instead
	 * 
	 * @param productTypeID the {@link ProductTypeID} of the {@link ProductType} to load
	 * @throws ModuleException if something during loading the productType went wrong
	 */
	@Deprecated
	public void setProductTypeID(ProductTypeID productTypeID)
	throws ModuleException
	{
		try {
			if (productTypeID == null) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						setProductType(null);
					}
				});
				return;
			}

			StoreManager sm = StoreManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
			final ProductType productType = sm.getProductType(
					productTypeID, fetchGroupsProductType, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);

			if (Thread.currentThread() == Display.getDefault().getThread())
				setProductType(productType);
			else {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						setProductType(productType);
					}
				});
			}
		} catch (Exception x) {
			throw new ModuleException(x);
		}
	}

	/** 
	 * @param productType The selected ProductType.
	 */
	public void setProductType(ProductType productType)
	{
		if (isDisposed())
			return;

		saleAccessControlHelper.setProductType(productType);
		productType = saleAccessControlHelper.getProductType();
		this.productType = productType;

		if (productType == null) {
			setEnabled(false);
			if (showProductTypeLabel)
				productTypeLabel.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.producttype.SaleAccessControlComposite.productTypeLabel.text_nothingSelected")); //$NON-NLS-1$
			publishedCheckBox.setSelection(false);
			confirmedCheckBox.setSelection(false);
			saleableCheckBox.setSelection(false);
			closedCheckBox.setSelection(false);
		}
		else {
			if (showProductTypeLabel)			
				productTypeLabel.setText(productType.getName().getText(Locale.getDefault().getLanguage()));

			publishedCheckBox.setSelection(productType.isPublished());
			confirmedCheckBox.setSelection(productType.isConfirmed());
			saleableCheckBox.setSelection(productType.isSaleable());
			closedCheckBox.setSelection(productType.isClosed());
			setEnabled(true);
			updateControlsEnabled();
		}
		changed = false;
		published = publishedCheckBox.getSelection();
		confirmed = confirmedCheckBox.getSelection();
		saleable = saleableCheckBox.getSelection();
		closed = closedCheckBox.getSelection();
	}

	private boolean changed = false;

	public boolean isChanged()
	{
		return changed;
	}

	/**
	 * @deprecated should not be called any more, but productType should be saved 
	 * somewhere else e.g. by a controller
	 *  
	 * Submit all the settings to the server.
	 */
	@Deprecated
	public void submit()
	{
		if (productType == null)
			return;

		if (!isChanged())
			return;

		try {

			ProductTypeID productTypeID = (ProductTypeID) JDOHelper.getObjectId(productType);

			boolean make_published = published && !productType.isPublished();
			boolean make_confirmed = confirmed && !productType.isConfirmed();
			boolean make_saleable_true = saleable && !productType.isSaleable();
			boolean make_saleable_false = !saleable && productType.isSaleable();;
			boolean make_closed = closed && !productType.isClosed();

//			setEnabled(false);

			StoreManager storeManager = StoreManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();

			if (make_published)
				storeManager.setProductTypeStatus_published(productTypeID, false, null, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);

			if (make_confirmed)
				storeManager.setProductTypeStatus_confirmed(productTypeID, false, null, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);

			if (make_saleable_true || make_saleable_false)
				storeManager.setProductTypeStatus_saleable(productTypeID, make_saleable_true, false, null, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);

			if (make_closed)
				storeManager.setProductTypeStatus_closed(productTypeID, false, null, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
