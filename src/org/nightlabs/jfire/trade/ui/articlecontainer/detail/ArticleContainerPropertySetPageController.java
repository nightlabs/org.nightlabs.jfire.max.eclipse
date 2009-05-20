package org.nightlabs.jfire.trade.ui.articlecontainer.detail;

import javax.jdo.FetchPlan;

import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.entity.editor.ActiveEntityEditorPageController;
import org.nightlabs.jfire.prop.PropertySet;
import org.nightlabs.jfire.prop.dao.PropertySetDAO;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.dao.ArticleContainerDAO;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class ArticleContainerPropertySetPageController
extends ActiveEntityEditorPageController<ArticleContainer>
{
	public static final String[] FETCH_GROUPS = new String[] {FetchPlan.DEFAULT, ArticleContainer.FETCH_GROUP_PROPERTY_SET,
		PropertySet.FETCH_GROUP_DATA_FIELDS, PropertySet.FETCH_GROUP_FULL_DATA};

	private ArticleContainerID articleContainerID;

	/**
	 * @param editor
	 */
	public ArticleContainerPropertySetPageController(EntityEditor editor) {
		super(editor);
		this.articleContainerID = ((ArticleContainerEditorInput) editor.getEditorInput()).getArticleContainerID();
	}

	/**
	 * @param editor
	 * @param startBackgroundLoading
	 */
	public ArticleContainerPropertySetPageController(EntityEditor editor, boolean startBackgroundLoading) {
		super(editor, startBackgroundLoading);
		this.articleContainerID = ((ArticleContainerEditorInput) editor.getEditorInput()).getArticleContainerID();
	}

	protected PropertySet getPropertySet() {
		ArticleContainer ac = getControllerObject();
		if (ac == null)
			return null;

		return ac.getPropertySet();
	}

	@Override
	protected String[] getEntityFetchGroups() {
		return FETCH_GROUPS;
	}

	@Override
	protected ArticleContainer retrieveEntity(ProgressMonitor monitor) {
		return ArticleContainerDAO.sharedInstance().getArticleContainer(articleContainerID, FETCH_GROUPS,
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
	}

	@Override
	protected ArticleContainer storeEntity(ArticleContainer controllerObject, ProgressMonitor monitor)
	{
		PropertySetDAO.sharedInstance().storeJDOObject(getPropertySet(), false, null, 1, monitor);
		return null; // causes cache eviction and the retrieve method to be called by the framework.
	}

}
