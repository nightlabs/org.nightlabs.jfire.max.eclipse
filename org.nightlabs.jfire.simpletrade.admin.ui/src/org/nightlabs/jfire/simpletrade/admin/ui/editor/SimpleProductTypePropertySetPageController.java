package org.nightlabs.jfire.simpletrade.admin.ui.editor;

import javax.jdo.FetchPlan;

import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.prop.PropertySet;
import org.nightlabs.jfire.simpletrade.admin.ui.resource.Messages;
import org.nightlabs.jfire.simpletrade.dao.SimpleProductTypeDAO;
import org.nightlabs.jfire.simpletrade.store.SimpleProductType;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypePageController;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class SimpleProductTypePropertySetPageController
extends AbstractProductTypePageController<SimpleProductType>
{
	private static final String[] FETCH_GROUPS = new String[] {FetchPlan.DEFAULT,
		SimpleProductType.FETCH_GROUP_PROPERTY_SET, PropertySet.FETCH_GROUP_DATA_FIELDS,
		PropertySet.FETCH_GROUP_FULL_DATA, ProductType.FETCH_GROUP_FIELD_METADATA_MAP};

	/**
	 * @param editor
	 */
	public SimpleProductTypePropertySetPageController(EntityEditor editor) {
		super(editor);
	}

	/**
	 * @param editor
	 * @param startBackgroundLoading
	 */
	public SimpleProductTypePropertySetPageController(EntityEditor editor,
			boolean startBackgroundLoading) {
		super(editor, startBackgroundLoading);
	}

	@Override
	protected String[] getEntityFetchGroups() {
		return FETCH_GROUPS;
	}

	@Override
	protected SimpleProductType retrieveEntity(ProgressMonitor monitor) {
		monitor.beginTask(Messages.getString("org.nightlabs.jfire.simpletrade.admin.ui.editor.SimpleProductTypePropertySetPageController.loadProductTypeMonitor.task.name"), 3); //$NON-NLS-1$
		monitor.worked(1);
		SimpleProductType productType = SimpleProductTypeDAO.sharedInstance().getSimpleProductType(
				getProductTypeID(), getEntityFetchGroups(),
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new SubProgressMonitor(monitor, 2));
		monitor.worked(1);
		return productType;
	}

	@Override
	public SimpleProductType getExtendedProductType(ProgressMonitor monitor , ProductTypeID extendedProductTypeID)
	{
		return  SimpleProductTypeDAO.sharedInstance().getSimpleProductType(extendedProductTypeID,
				getEntityFetchGroups(),
				getEntityMaxFetchDepth(),
				monitor);

	}

	@Override
	protected SimpleProductType storeEntity(SimpleProductType controllerObject, ProgressMonitor monitor) {
		return SimpleProductTypeDAO.sharedInstance().storeJDOObject(controllerObject, true, getEntityFetchGroups(), getEntityMaxFetchDepth(), monitor);
	}

	public PropertySet getPropertySet() {
		SimpleProductType spt = getControllerObject();
		if (spt == null) // This check seems not necessary (never had an NPE so far), but at least theoretically, this might be null (until the data is loaded). Marco.
			return null;

		return spt.getPropertySet();
	}

}
