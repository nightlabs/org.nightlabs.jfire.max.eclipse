/**
 *
 */
package org.nightlabs.jfire.reporting.admin.ui.category.editor;

import javax.jdo.FetchPlan;

import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.inheritance.Inheritable;
import org.nightlabs.jfire.base.admin.ui.editor.authority.AuthorityPageControllerHelper;
import org.nightlabs.jfire.base.admin.ui.editor.authority.InheritedSecuringAuthorityResolver;
import org.nightlabs.jfire.base.ui.entity.editor.ActiveEntityEditorPageController;
import org.nightlabs.jfire.reporting.admin.ui.layout.editor.IReportRegistryItemEditorInput;
import org.nightlabs.jfire.reporting.dao.ReportRegistryItemDAO;
import org.nightlabs.jfire.reporting.layout.ReportRegistryItem;
import org.nightlabs.jfire.security.id.AuthorityID;
import org.nightlabs.progress.ProgressMonitor;

/**
 * {@link IEntityEditorPageController} to be used for authority pages for a {@link ReportRegistryItem}.
 *
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class AuthorityPageController extends ActiveEntityEditorPageController<ReportRegistryItem> {

	private final AuthorityPageControllerHelper pageControllerHelper = new AuthorityPageControllerHelper() {
		@Override
		protected InheritedSecuringAuthorityResolver createInheritedSecuringAuthorityResolver() {
			return new InheritedSecuringAuthorityResolver() {
				@Override
				public AuthorityID getInheritedSecuringAuthorityID(ProgressMonitor monitor) {
					if (getControllerObject().getParentCategory() != null)
						return getControllerObject().getParentCategory().getSecuringAuthorityID();
					return null;
				}
//				@Override
//				public boolean isInitiallyInherited(ProgressMonitor monitor) {
//					return getControllerObject().getFieldMetaData(FieldName.securingAuthorityID).isValueInherited();
//				}
				@Override
				public Inheritable retrieveSecuredObjectInheritable(ProgressMonitor monitor) {
					return loadRegistryItem(monitor);
				}
			};
		}
	};

	/**
	 * @param editor
	 */
	public AuthorityPageController(EntityEditor editor) {
		super(editor);
	}

	/**
	 * @param editor
	 * @param startBackgroundLoading
	 */
	public AuthorityPageController(EntityEditor editor,
			boolean startBackgroundLoading) {
		super(editor, startBackgroundLoading);
	}

	public AuthorityPageControllerHelper getAuthorityPageControllerHelper() {
		return pageControllerHelper;
	}

	@Override
	protected String[] getEntityFetchGroups() {
		return new String[] {
				FetchPlan.DEFAULT, ReportRegistryItem.FETCH_GROUP_PARENT_CATEGORY,
				ReportRegistryItem.FETCH_GROUP_FIELD_META_DATA
			};
	}

	private ReportRegistryItem loadRegistryItem(ProgressMonitor monitor) {
		IReportRegistryItemEditorInput input = (IReportRegistryItemEditorInput) getEntityEditor().getEditorInput();
		return ReportRegistryItemDAO.sharedInstance().getReportRegistryItem(input.getReportRegistryItemID(), getEntityFetchGroups(), monitor);
	}

	@Override
	protected ReportRegistryItem retrieveEntity(ProgressMonitor monitor) {
		ReportRegistryItem item = loadRegistryItem(monitor);
		pageControllerHelper.load(item, monitor);
		return item;
	}

	@Override
	protected ReportRegistryItem storeEntity(ReportRegistryItem controllerObject, ProgressMonitor monitor) {
		// Storing data is delegated to the helper, too.
		pageControllerHelper.store(monitor);
		return getControllerObject();
	}
}
