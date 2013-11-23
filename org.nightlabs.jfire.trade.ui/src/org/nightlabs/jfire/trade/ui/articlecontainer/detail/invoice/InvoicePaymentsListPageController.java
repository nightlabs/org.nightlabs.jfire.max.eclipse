package org.nightlabs.jfire.trade.ui.articlecontainer.detail.invoice;

import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageController;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditorInput;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;

/**
 * the Editor page which lists all the payments of an Invoice.
 * 
 * @author Fitas Amine - fitas at NightLabs dot de
 */
public class InvoicePaymentsListPageController extends EntityEditorPageController {

	private ArticleContainerID articleContainerID;

	public InvoicePaymentsListPageController(EntityEditor editor) {
		super(editor);	
		this.articleContainerID = ((ArticleContainerEditorInput) editor.getEditorInput()).getArticleContainerID();
	}


	public void doLoad(ProgressMonitor monitor) {
		monitor.beginTask(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.invoice.InvoicePaymentsListPageController.LoadingJobText"), 100);  //$NON-NLS-1$
		this.articleContainerID = ((ArticleContainerEditorInput)getEntityEditor().getEditorInput()).getArticleContainerID();
		monitor.done();
		setLoaded(true); // must be done before fireModifyEvent!
		fireModifyEvent(null, null);
	}

	@Override
	public boolean doSave(ProgressMonitor monitor) {
		return false;
	}

	public ArticleContainerID getArticleContainerID() {
		return articleContainerID;
	}
}