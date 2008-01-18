package org.nightlabs.jfire.trade.ui.overview.action;

import javax.jdo.JDOHelper;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.nightlabs.base.ui.action.WorkbenchPartSelectionAction;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.trade.id.ArticleContainerID;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
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

	@Override
	public void setSelection(ISelection selection) 
	{
		super.setSelection(selection);
		if (!getSelection().isEmpty() && getSelection() instanceof StructuredSelection) {
			StructuredSelection sel = (StructuredSelection) getSelection();
			Object objectID = JDOHelper.getObjectId(sel.getFirstElement());
			if (objectID instanceof ArticleContainerID) {
				articleContainerID = (ArticleContainerID) objectID;
			}
		}
		else if (getSelection().isEmpty()) {
			articleContainerID = null;
		}
	}

	public void setArticleContainerID(ObjectID objectID) {
		articleContainerID = (ArticleContainerID)objectID;
	}
	
	public boolean calculateEnabled() {		
		return articleContainerID != null;
	}

	public boolean calculateVisible() {
		return articleContainerID != null;
	}
	
}
