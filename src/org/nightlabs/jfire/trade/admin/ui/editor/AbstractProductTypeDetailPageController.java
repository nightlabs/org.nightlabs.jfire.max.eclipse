package org.nightlabs.jfire.trade.admin.ui.editor;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.jdo.cache.Cache;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.store.NestedProductTypeLocal;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.ProductTypeLocal;
import org.nightlabs.jfire.store.StoreManager;
import org.nightlabs.jfire.store.StoreManagerUtil;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
import org.nightlabs.util.Util;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public abstract class AbstractProductTypeDetailPageController 
extends AbstractProductTypePageController 
implements IProductTypeDetailPageController
{
	/**
	 * @param editor
	 */
	public AbstractProductTypeDetailPageController(EntityEditor editor) {
		super(editor);
	}

	/**
	 * @param editor
	 * @param startBackgroundLoading
	 */
	public AbstractProductTypeDetailPageController(EntityEditor editor,
			boolean startBackgroundLoading) {
		super(editor, startBackgroundLoading);
	}

	public static final String[] FETCH_GROUPS_DEFAULT = new String[] {
		FetchPlan.DEFAULT, 
		ProductType.FETCH_GROUP_NAME,
		ProductType.FETCH_GROUP_FIELD_METADATA_MAP,
		ProductType.FETCH_GROUP_PRODUCT_TYPE_LOCAL,
		ProductTypeLocal.FETCH_GROUP_NESTED_PRODUCT_TYPE_LOCALS,
		ProductTypeLocal.FETCH_GROUP_FIELD_METADATA_MAP,
		NestedProductTypeLocal.FETCH_GROUP_THIS_PACKAGED_PRODUCT_TYPE,
		ProductType.FETCH_GROUP_INNER_PRICE_CONFIG,
		ProductType.FETCH_GROUP_PACKAGE_PRICE_CONFIG,
		ProductType.FETCH_GROUP_OWNER,
		ProductType.FETCH_GROUP_VENDOR,
		LegalEntity.FETCH_GROUP_PERSON,
		ProductType.FETCH_GROUP_EXTENDED_PRODUCT_TYPE,
		ProductType.FETCH_GROUP_EXTENDED_PRODUCT_TYPE_ID,
		ProductType.FETCH_GROUP_PRODUCT_TYPE_LOCAL,
		ProductTypeLocal.FETCH_GROUP_LOCAL_ACCOUNTANT_DELEGATE,
		ProductType.FETCH_GROUP_DELIVERY_CONFIGURATION
	};

	public void doLoad(IProgressMonitor monitor) 
	{
		monitor.beginTask(Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypeDetailPageController.loadProductTypeDetailMonitor.task.name"), 2); //$NON-NLS-1$
		monitor.worked(1);
		ProductType productType = retrieveProductType(monitor);
		ProductType clonedProductType = Util.cloneSerializable(productType); 
		setProductType(clonedProductType);
		monitor.worked(1);
	}

	public void doSave(IProgressMonitor monitor) 
	{
		for (IFormPage page : getPages()) {
			if (page instanceof AbstractProductTypeDetailPage) {
				monitor.beginTask(Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypeDetailPageController.storeProductTypeDetailMonitor.task.name"), 2); //$NON-NLS-1$
				monitor.worked(1);
				ProductType oldObject = getProductType();
				final AbstractProductTypeDetailPage detailPage = (AbstractProductTypeDetailPage) page;
				storeProductType(page, monitor);
				storeSaleAccessControl(monitor);
				Cache.sharedInstance().removeByObjectID(JDOHelper.getObjectId(oldObject), false);
				setProductType(Util.cloneSerializable(retrieveProductType(monitor)));
				fireModifyEvent(oldObject, getProductType());
				monitor.worked(1);				
			}			
		}		
	}

	protected void storeSaleAccessControlProperties(ProductTypeSaleAccessStatus saleStatus) 
	{
		try {
			ProductType productType = getProductType();
			ProductTypeID productTypeID = (ProductTypeID) JDOHelper.getObjectId(productType);
			if (saleStatus == null)
				return; // Not changed, nothing to save
			boolean make_published = saleStatus.isPublished() && !productType.isPublished();
			boolean make_confirmed = saleStatus.isConfirmed() && !productType.isConfirmed();
			boolean make_saleable_true = saleStatus.isSaleable() && !productType.isSaleable();
			boolean make_saleable_false = !saleStatus.isSaleable() && productType.isSaleable();;
			boolean make_closed = saleStatus.isClosed() && !productType.isClosed();
			
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

	protected void storeSaleAccessControl(IProgressMonitor monitor) 
	{
		storeSaleAccessControlProperties(saleAccessStatus);
	}	
	
	private ProductTypeSaleAccessStatus saleAccessStatus;
	
	public ProductTypeSaleAccessStatus getProductTypeSaleAccessStatus() {
		if (saleAccessStatus == null && getProductType() != null) {
			return new ProductTypeSaleAccessStatus(getProductType());
		}
		return saleAccessStatus;
	}
	
	public void setProductTypeSaleAccessStatus(ProductTypeSaleAccessStatus saleAccessStatus) {
		this.saleAccessStatus = saleAccessStatus;
	}
	
	protected abstract ProductType retrieveProductType(IProgressMonitor monitor);
	protected abstract void storeProductType(IFormPage page, IProgressMonitor monitor);
}

