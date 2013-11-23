package org.nightlabs.jfire.dynamictrade.admin.ui.editor;

import javax.jdo.FetchPlan;

import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.dynamictrade.admin.ui.resource.Messages;
import org.nightlabs.jfire.dynamictrade.dao.DynamicProductTypeDAO;
import org.nightlabs.jfire.dynamictrade.store.DynamicProductType;
import org.nightlabs.jfire.prop.PropertySet;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypePageController;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class DynamicProductTypePropertySetPageController
extends AbstractProductTypePageController<DynamicProductType>
{
	private static final String[] FETCH_GROUPS = new String[] {FetchPlan.DEFAULT,
		DynamicProductType.FETCH_GROUP_PROPERTY_SET, PropertySet.FETCH_GROUP_DATA_FIELDS,
		PropertySet.FETCH_GROUP_FULL_DATA, ProductType.FETCH_GROUP_FIELD_METADATA_MAP};

	/**
	 * @param editor
	 */
	public DynamicProductTypePropertySetPageController(EntityEditor editor) {
		super(editor);
	}

	/**
	 * @param editor
	 * @param startBackgroundLoading
	 */
	public DynamicProductTypePropertySetPageController(EntityEditor editor,
			boolean startBackgroundLoading) {
		super(editor, startBackgroundLoading);
	}

	@Override
	protected String[] getEntityFetchGroups() {
		return FETCH_GROUPS;
	}

	@Override
	protected DynamicProductType retrieveEntity(ProgressMonitor monitor) {
		monitor.beginTask(Messages.getString("org.nightlabs.jfire.dynamictrade.admin.ui.editor.DynamicProductTypePropertySetPageController.loadProductTypeMonitor.task.name"), 3); //$NON-NLS-1$
		monitor.worked(1);
		DynamicProductType productType = DynamicProductTypeDAO.sharedInstance().getDynamicProductType(
				getProductTypeID(), getEntityFetchGroups(),
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new SubProgressMonitor(monitor, 2));
		monitor.worked(1);
		return productType;
	}

	@Override
	public DynamicProductType getExtendedProductType(ProgressMonitor monitor , ProductTypeID extendedProductTypeID)
	{
		return  DynamicProductTypeDAO.sharedInstance().getDynamicProductType(extendedProductTypeID,
				getEntityFetchGroups(),
				getEntityMaxFetchDepth(),
				monitor);

	}

	@Override
	protected DynamicProductType storeEntity(DynamicProductType controllerObject, ProgressMonitor monitor) {
		return DynamicProductTypeDAO.sharedInstance().storeJDOObject(controllerObject, true, getEntityFetchGroups(), getEntityMaxFetchDepth(), monitor);
	}

	public PropertySet getPropertySet() {
		DynamicProductType spt = getControllerObject();
		if (spt == null) // This check seems not necessary (never had an NPE so far), but at least theoretically, this might be null (until the data is loaded). Marco.
			return null;

		return spt.getPropertySet();
	}

}
