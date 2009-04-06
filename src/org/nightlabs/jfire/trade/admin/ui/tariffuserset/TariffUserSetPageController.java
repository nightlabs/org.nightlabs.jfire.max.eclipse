package org.nightlabs.jfire.trade.admin.ui.tariffuserset;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.inheritance.FieldMetaData;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.tariffuserset.TariffUserSet;
import org.nightlabs.jfire.base.jdo.IJDOObjectDAO;
import org.nightlabs.jfire.entityuserset.id.EntityUserSetID;
import org.nightlabs.jfire.entityuserset.ui.AbstractInheritedEntityUserSetResolver;
import org.nightlabs.jfire.entityuserset.ui.InheritedEntityUserSetResolver;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.dao.ProductTypeDAO;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypePageController;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * @author Daniel Mazurek - Daniel.Mazurek [dot] nightlabs [dot] de
 *
 */
public class TariffUserSetPageController<ProductTypeType extends ProductType> 
extends AbstractProductTypePageController<ProductType>
{
	private TariffUserSetPageControllerHelper tariffUserSetPageControllerHelper;
	private IJDOObjectDAO<ProductTypeType> productTypeDAO;
	
	/**
	 * @param editor
	 * @param startBackgroundLoading
	 * @param productTypeDAO
	 */
	public TariffUserSetPageController(EntityEditor editor, boolean startBackgroundLoading, IJDOObjectDAO<ProductTypeType> productTypeDAO) {
		super(editor, startBackgroundLoading);
		this.productTypeDAO = productTypeDAO;
	}

	/**
	 * @param editor
	 * @param productTypeDAO
	 */
	public TariffUserSetPageController(EntityEditor editor, IJDOObjectDAO<ProductTypeType> productTypeDAO) {
		super(editor);
		this.productTypeDAO = productTypeDAO;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.entity.editor.ActiveEntityEditorPageController#getEntityFetchGroups()
	 */
	@Override
	protected String[] getEntityFetchGroups() {
		return new String[] {FetchPlan.DEFAULT, ProductType.FETCH_GROUP_TARIFF_USER_SET, 
				ProductType.FETCH_GROUP_EXTENDED_PRODUCT_TYPE_ID, ProductType.FETCH_GROUP_FIELD_METADATA_MAP};
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.entity.editor.ActiveEntityEditorPageController#retrieveEntity(org.nightlabs.progress.ProgressMonitor)
	 */
	@Override
	protected ProductType retrieveEntity(ProgressMonitor monitor) 
	{
		ProductType pt = ProductTypeDAO.sharedInstance().getProductType(getProductTypeID(), getEntityFetchGroups(), 
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
		TariffUserSet tariffUserSet = pt.getTariffUserSet();
		EntityUserSetID entityUserSetID = (EntityUserSetID) JDOHelper.getObjectId(tariffUserSet);
		getTariffUserSetPageControllerHelper().load(entityUserSetID, null, new SubProgressMonitor(monitor, 50));
		return pt;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.entity.editor.ActiveEntityEditorPageController#storeEntity(java.lang.Object, org.nightlabs.progress.ProgressMonitor)
	 */
	@Override
	protected ProductType storeEntity(ProductType controllerObject, ProgressMonitor monitor) {
		getTariffUserSetPageControllerHelper().store(new SubProgressMonitor(monitor, 70));
		ProductTypeType pt = (ProductTypeType) controllerObject;
		pt.setTariffUserSet((TariffUserSet) getTariffUserSetPageControllerHelper().getEntityUserSet());
		FieldMetaData metaData = pt.getFieldMetaData(ProductType.FieldName.tariffUserSet, true);
		metaData.setValueInherited(false);
//		metaData.setWritable(false);
		pt = productTypeDAO.storeJDOObject(pt, true, getEntityFetchGroups(), 
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new SubProgressMonitor(monitor, 30));
		return pt;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypePageController#getExtendedProductType(org.nightlabs.progress.ProgressMonitor, org.nightlabs.jfire.store.id.ProductTypeID)
	 */
	@Override
	public ProductType getExtendedProductType(ProgressMonitor monitor, ProductTypeID extendedProductTypeID) {
		return ProductTypeDAO.sharedInstance().getProductType(extendedProductTypeID, getEntityFetchGroups(), 
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
	}
	
	public TariffUserSetPageControllerHelper getTariffUserSetPageControllerHelper() {
		if (tariffUserSetPageControllerHelper == null) {
			tariffUserSetPageControllerHelper = new TariffUserSetPageControllerHelper(){
				@Override
				protected InheritedEntityUserSetResolver createInheritedEntityUserSetResolver() {
					return new AbstractInheritedEntityUserSetResolver(){
						@Override
						public EntityUserSetID getInheritedEntityUserSetID(ProgressMonitor monitor) {
							ProductType extendedProductType = getExtendedProductType(monitor, getProductType().getExtendedProductTypeID());
							if (extendedProductType != null) {
								return (EntityUserSetID) JDOHelper.getObjectId(extendedProductType.getTariffUserSet());	
							}
							return null;
						}
					};
				}
			};
		}
		return tariffUserSetPageControllerHelper;
	}
}
