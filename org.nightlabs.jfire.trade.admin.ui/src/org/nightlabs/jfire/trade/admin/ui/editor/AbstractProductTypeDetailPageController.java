package org.nightlabs.jfire.trade.admin.ui.editor;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageController;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.gridpriceconfig.FormulaPriceConfig;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.base.login.ui.Login;
import org.nightlabs.jfire.store.NestedProductTypeLocal;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.ProductTypeLocal;
import org.nightlabs.jfire.store.StoreManagerRemote;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * A base {@link EntityEditorPageController} for ProductType detail pages.
 * It provides default fetch-groups and delegates its work to the methods:
 * {@link #retrieveProductType(ProgressMonitor)} and {@link #storeProductType(ProductType, ProgressMonitor)}.
 * Additionally it manages the retrieval and storage of the sale-access-control properties
 * of the given ProductType directly with the {@link StoreManager}.
 *
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public abstract class AbstractProductTypeDetailPageController<ProductTypeType extends ProductType>
extends AbstractProductTypePageController<ProductTypeType>
implements IProductTypeDetailPageController<ProductTypeType>
{
	/**
	 * Create a new {@link AbstractProductTypeDetailPageController}.
	 * @param editor The editor.
	 */
	public AbstractProductTypeDetailPageController(EntityEditor editor) {
		super(editor);
	}

	/**
	 * Create a new {@link AbstractProductTypeDetailPageController}.
	 * @param editor The editor.
	 * @param startBackgroundLoading Whether to start background loading.
	 */
	public AbstractProductTypeDetailPageController(EntityEditor editor,
			boolean startBackgroundLoading) {
		super(editor, startBackgroundLoading);
	}

	/**
	 * The default fetch-groups to detach a ProductType for a ProductType detail page.
	 * This can be used or should be extended by subclasses.
	 */
	public static final String[] FETCH_GROUPS_DEFAULT = new String[] {
		FetchPlan.DEFAULT,
		ProductType.FETCH_GROUP_NAME,
		ProductType.FETCH_GROUP_FIELD_METADATA_MAP,
		ProductType.FETCH_GROUP_PRODUCT_TYPE_LOCAL,
		ProductTypeLocal.FETCH_GROUP_NESTED_PRODUCT_TYPE_LOCALS,
		ProductTypeLocal.FETCH_GROUP_FIELD_METADATA_MAP,
		NestedProductTypeLocal.FETCH_GROUP_INNER_PRODUCT_TYPE,
		NestedProductTypeLocal.FETCH_GROUP_PACKAGE_PRODUCT_TYPE,
		ProductType.FETCH_GROUP_INNER_PRICE_CONFIG,
		ProductType.FETCH_GROUP_PACKAGE_PRICE_CONFIG,
		FormulaPriceConfig.FETCH_GROUP_PACKAGING_RESULT_PRICE_CONFIGS,
		ProductType.FETCH_GROUP_OWNER,
		ProductType.FETCH_GROUP_VENDOR,
		LegalEntity.FETCH_GROUP_PERSON,
		ProductType.FETCH_GROUP_EXTENDED_PRODUCT_TYPE,
		ProductType.FETCH_GROUP_EXTENDED_PRODUCT_TYPE_ID,
		ProductType.FETCH_GROUP_PRODUCT_TYPE_LOCAL,
		ProductTypeLocal.FETCH_GROUP_LOCAL_ACCOUNTANT_DELEGATE,
		ProductType.FETCH_GROUP_DELIVERY_CONFIGURATION
	};

	@Override
	protected ProductTypeType retrieveEntity(ProgressMonitor monitor) {
		monitor.beginTask(Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypeDetailPageController.loadProductTypeDetailMonitor.task.name"), 3); //$NON-NLS-1$
		monitor.worked(1);
		ProductTypeType productType = retrieveProductType(new SubProgressMonitor(monitor, 2));
		saleAccessStatus = null;
		monitor.done();
		return productType;
	}

	@Override
	protected ProductTypeType storeEntity(ProductTypeType productType, ProgressMonitor monitor) {
		monitor.beginTask(Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypeDetailPageController.storeProductTypeDetailMonitor.task.name"), 5); //$NON-NLS-1$
		monitor.worked(1);

		// It is important that the two following lines are executed in this order.
		// This way, the sale access is first modified, then the ProductType is stored *and* *retrieved*.
		// This way the second call returns the correct newProductType and not one
		// which is already outdated when storeSaleAccessControl(...) has been called. Marco.
		storeSaleAccessControl(new SubProgressMonitor(monitor, 2));
		ProductTypeType newProductType = storeProductType(productType, new SubProgressMonitor(monitor, 2));

		monitor.done();
		return newProductType;
	}

	/**
	 * Stores the given saleStatus properties to the actual ProductType using the {@link StoreManager}.
	 * @param saleStatus The status to apply.
	 */
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

			StoreManagerRemote storeManager = JFireEjb3Factory.getRemoteBean(StoreManagerRemote.class, Login.getLogin().getInitialContextProperties());

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

	protected void storeSaleAccessControl(ProgressMonitor monitor)
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

	/**
	 * Retrieve the ProductType using the appropriate DAO for the actual ProductType-type.
	 * @param monitor The monitor to use
	 * @return The ProductType of this controller.
	 */
	protected abstract ProductTypeType retrieveProductType(ProgressMonitor monitor);
	/**
	 * Store the given ProductType using the appropriate DAO/Bean for the actual ProductType-type.
	 * @param productType The ProductType to store.
	 * @param monitor The monitor to use.
	 * @return The stored and newly-detached ProductType.
	 */
	protected abstract ProductTypeType storeProductType(ProductTypeType productType, ProgressMonitor monitor);
}

