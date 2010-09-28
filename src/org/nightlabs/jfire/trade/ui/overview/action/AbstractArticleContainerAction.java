package org.nightlabs.jfire.trade.ui.overview.action;

import javax.jdo.JDOHelper;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.nightlabs.base.ui.action.WorkbenchPartSelectionAction;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.dao.ArticleContainerDAO;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * A base Action for actions that should be invoked on an {@link ArticleContainer}.
 * An instance of this action will try to extract <b>one</b> {@link ArticleContainerID}
 * from the workbenchs selection and make this id available to subclasses. 
 *  
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public abstract class AbstractArticleContainerAction
extends WorkbenchPartSelectionAction
{
	public AbstractArticleContainerAction() {
		super();
	}

	private ArticleContainerID articleContainerID;
	protected ArticleContainerID getArticleContainerID() {
		return articleContainerID;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Extracts <b>one</b> {@link ArticleContainerID} from the selection, if possible.
	 * </p>
	 * @see org.nightlabs.base.ui.action.WorkbenchPartSelectionAction#setSelection(org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void setSelection(ISelection selection)
	{
		super.setSelection(selection);
		if (!getSelection().isEmpty() && getSelection() instanceof StructuredSelection) {
			StructuredSelection sel = (StructuredSelection) getSelection();
			Object objectID = JDOHelper.getObjectId(sel.getFirstElement());
			if (objectID instanceof ArticleContainerID) {
//				articleContainerID = (ArticleContainerID) objectID;
				setArticleContainerID((ArticleContainerID) objectID);
			}
		}
		else if (getSelection().isEmpty()) {
			articleContainerID = null;
		}
	}
	
	/**
	 * Set the {@link ArticleContainerID} this action should operate on.
	 *  
	 * @param objectID The {@link ArticleContainerID} to set.
	 */
	public void setArticleContainerID(ObjectID objectID) {
		articleContainerID = (ArticleContainerID)objectID;
	}
	
	
	public ArticleContainer getArticleContainer(String[] fetchGroups)
	{
		if(articleContainerID != null)
		{
			return ArticleContainerDAO.sharedInstance().getArticleContainer(
					getArticleContainerID(), fetchGroups,
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
		}
		else
			return null;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * The default implementation only returns <code>true</code> if 
	 * the acrticleContainerID is set.
	 * </p>
	 * 
	 * @see org.nightlabs.base.ui.action.IUpdateActionOrContributionItem#calculateEnabled()
	 */
	public boolean calculateEnabled() {
		return articleContainerID != null;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The default implementation only returns <code>true</code> if 
	 * the acrticleContainerID is set.
	 * </p>
	 * 
	 * @see org.nightlabs.base.ui.action.IUpdateActionOrContributionItem#calculateVisible()
	 */
	public boolean calculateVisible() {
		return articleContainerID != null;
	}
}
