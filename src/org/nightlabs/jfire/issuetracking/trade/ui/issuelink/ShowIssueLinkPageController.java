package org.nightlabs.jfire.issuetracking.trade.ui.issuelink;

import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageController;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditorInput;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun - chairat at nightlabs dot de
 *
 */
public class ShowIssueLinkPageController 
extends EntityEditorPageController 
{
	private ArticleContainerID articleContainerID;
	
	/**
	 * @param editor
	 */
	public ShowIssueLinkPageController(EntityEditor editor) {
		super(editor);
		this.articleContainerID = ((ArticleContainerEditorInput) editor.getEditorInput()).getArticleContainerID();
	}

	/**
	 * @param editor
	 * @param startBackgroundLoading
	 */
	public ShowIssueLinkPageController(EntityEditor editor, boolean startBackgroundLoading) {
		super(editor, startBackgroundLoading);
	}
	
	@Override
	public void doLoad(ProgressMonitor monitor) {
		
	}

	@Override
	public boolean doSave(ProgressMonitor monitor) {
		return false;
	}

}
