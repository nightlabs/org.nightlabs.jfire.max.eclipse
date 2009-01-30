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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.JDOHelper;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.notification.IDirtyStateManager;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.JFireEjbFactory;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.StoreManager;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
import org.nightlabs.util.CollectionUtil;
import org.nightlabs.util.NLLocale;

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
	public static final String STATUS_CONTROL_ID_PUBLISHED = "published";
	public static final String STATUS_CONTROL_ID_CONFIRMED = "confirmed";
	public static final String STATUS_CONTROL_ID_SALEABLE = "saleable";
	public static final String STATUS_CONTROL_ID_CLOSED = "closed";

	private Label productTypeLabel;

	private ProductType productType;

	private Map<String, Control> statusControlID2statusControl = new HashMap<String, Control>();

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
		super(parent, style, LayoutMode.TIGHT_WRAPPER);
//		super(parent, style);

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

		XComposite statusComp = new XComposite(this, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		statusComp.getGridData().grabExcessVerticalSpace = false;
		createStatusControlsForStatusComposite(statusComp, CollectionUtil.array2ArrayList(new String[] {
				STATUS_CONTROL_ID_PUBLISHED,
				STATUS_CONTROL_ID_CONFIRMED,
				STATUS_CONTROL_ID_SALEABLE,
				STATUS_CONTROL_ID_CLOSED
		}));
		statusComp.getGridLayout().numColumns = statusComp.getChildren().length;

//		JDOLifecycleManager.sharedInstance().addNotificationListener(ProductType.class, productTypeChangedListener);
//		addDisposeListener(new DisposeListener(){
//			public void widgetDisposed(DisposeEvent e)
//			{
//				JDOLifecycleManager.sharedInstance().removeNotificationListener(ProductType.class, productTypeChangedListener);
//			}
//		});

		setProductType(null);
	}

	protected void createStatusControlsForStatusComposite(Composite parent, List<String> statusControlIDs)
	{
		for (String statusControlID : statusControlIDs) {
			Control control = createStatusControl(parent, statusControlID);
			addStatusControl(statusControlID, control);
		}
	}

	protected Control getStatusControl(String statusControlID)
	{
		return statusControlID2statusControl.get(statusControlID);
	}

	protected void addStatusControl(String statusControlID, Control control)
	{
		if (control != null)
			statusControlID2statusControl.put(statusControlID, control);
	}

	protected Control createStatusControl(Composite parent, String statusControlID)
	{
		if (STATUS_CONTROL_ID_PUBLISHED.equals(statusControlID)) {
			publishedCheckBox = new Button(parent, SWT.CHECK);
			publishedCheckBox.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.producttype.SaleAccessControlComposite.publishedCheckBox.text")); //$NON-NLS-1$
			publishedCheckBox.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					publishedCheckBoxChanged();
				}
			});
			return publishedCheckBox;
		}
		else if (STATUS_CONTROL_ID_CONFIRMED.equals(statusControlID)) {
			confirmedCheckBox = new Button(parent, SWT.CHECK);
			confirmedCheckBox.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.producttype.SaleAccessControlComposite.confirmedCheckBox.text")); //$NON-NLS-1$
			confirmedCheckBox.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					confirmedCheckBoxChanged();
				}
			});
			return confirmedCheckBox;
		}
		else if (STATUS_CONTROL_ID_SALEABLE.equals(statusControlID)) {
			saleableCheckBox = new Button(parent, SWT.CHECK);
			saleableCheckBox.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.producttype.SaleAccessControlComposite.saleableCheckBox.text")); //$NON-NLS-1$
			saleableCheckBox.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					saleableCheckBoxChanged(saleableCheckBox.getSelection());
				}
			});
			return saleableCheckBox;
		}
		else if (STATUS_CONTROL_ID_CLOSED.equals(statusControlID)) {
			closedCheckBox = new Button(parent, SWT.CHECK);
			closedCheckBox.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.producttype.SaleAccessControlComposite.closedCheckBox.text")); //$NON-NLS-1$
			closedCheckBox.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					closedCheckBoxChanged();
				}
			});
			return closedCheckBox;
		}
		else
			return null;
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
							RCPUtil.getActiveShell(),
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
							RCPUtil.getActiveShell(),
							Messages.getString("org.nightlabs.jfire.trade.admin.ui.producttype.SaleAccessControlComposite.confirmDialog.title"), //$NON-NLS-1$
							Messages.getString("org.nightlabs.jfire.trade.admin.ui.producttype.SaleAccessControlComposite.confirmDialog.message")); //$NON-NLS-1$
				}

				if (!flag)
					confirmedCheckBox.setSelection(false);
				else {
					if (!saleableCheckBox.isEnabled()) {
						saleableCheckBox.setSelection(true);
						saleableCheckBoxChanged(saleableCheckBox.getSelection());
					}
				}
			}
			else {
				if (productType.isConfirmed()) {
					MessageDialog.openError(
							RCPUtil.getActiveShell(),
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

	/**
	 * Set the saleable state. Call this method from a subclass to set the saleable state.
	 *
	 * @param saleable whether or not to make it saleable.
	 */
	protected final void setSaleable(boolean saleable)
	{
		if (saleable == saleableCheckBox.getSelection())
			return;

		saleableCheckBox.setSelection(saleable);
		saleableCheckBoxChanged(saleableCheckBox.getSelection());
	}

	protected void saleableCheckBoxChanged(boolean checked)
	{
		try {
			if (checked) {
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

	protected IDirtyStateManager getDirtyStateManager() {
		return dirtyStateManager;
	}

	protected void closedCheckBoxChanged()
	{
		try {
			if (closedCheckBox.getSelection()) {
				boolean flag = saleAccessControlHelper.canClose(false);

				if (flag) {
					flag = MessageDialog.openConfirm(
							RCPUtil.getActiveShell(),
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

// I think this is set by the surrounding composite/editor
//	protected NotificationListener productTypeChangedListener = new NotificationAdapterJob(Messages.getString("org.nightlabs.jfire.trade.admin.ui.producttype.SaleAccessControlComposite.loadProductTypesJob.name")) { //$NON-NLS-1$
//		public void notify(NotificationEvent notificationEvent)
//		{
//			DirtyObjectID dirtyObjectID = (DirtyObjectID) notificationEvent.getFirstSubject();
//			ProductTypeID productTypeID = (ProductTypeID) dirtyObjectID.getObjectID();
//			if (productTypeID == null || !productTypeID.equals(JDOHelper.getObjectId(productType)))
//				return; // We have none or another object open and are not interested in this change.
//
//			try {
//				setProductTypeID(productTypeID);
//			} catch (ModuleException e) {
//				throw new RuntimeException(e);
//			}
//		}
//	};

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

//	/**
//	 * @deprecated should not be used anymore, call {@link SaleAccessControlComposite#setProductType(ProductType)} instead
//	 *
//	 * @param productTypeID the {@link ProductTypeID} of the {@link ProductType} to load
//	 * @throws ModuleException if something during loading the productType went wrong
//	 */
//	@Deprecated
//	public void setProductTypeID(ProductTypeID productTypeID)
//	throws ModuleException
//	{
//		try {
//			if (productTypeID == null) {
//				Display.getDefault().asyncExec(new Runnable() {
//					public void run() {
//						setProductType(null);
//					}
//				});
//				return;
//			}
//
//			StoreManager sm = JFireEjbFactory.getBean(StoreManager.class, Login.getLogin().getInitialContextProperties());
//			final ProductType productType = sm.getProductType(
//					productTypeID, fetchGroupsProductType, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
//
//			if (Thread.currentThread() == Display.getDefault().getThread())
//				setProductType(productType);
//			else {
//				Display.getDefault().asyncExec(new Runnable() {
//					public void run() {
//						setProductType(productType);
//					}
//				});
//			}
//		} catch (Exception x) {
//			throw new ModuleException(x);
//		}
//	}

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
				productTypeLabel.setText(productType.getName().getText(NLLocale.getDefault().getLanguage()));

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

			StoreManager storeManager = JFireEjbFactory.getBean(StoreManager.class, Login.getLogin().getInitialContextProperties());

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

//	public Composite getStatusComposite() {
//		return statusComposite;
//	}
}
