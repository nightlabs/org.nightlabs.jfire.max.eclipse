package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.reverse;

import javax.jdo.FetchPlan;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.store.reverse.AlreadyReversedArticleReverseProductError;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.dao.ArticleDAO;
import org.nightlabs.jfire.trade.id.ArticleID;
import org.nightlabs.jfire.trade.id.OfferID;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditor;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditorInput;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class OpenAlreadyReversedOfferDialog 
//extends TrayDialog 
extends MessageDialog
{
	private AlreadyReversedArticleReverseProductError error;
	
	/**
	 * @param shell
	 */
	public OpenAlreadyReversedOfferDialog(Shell shell, AlreadyReversedArticleReverseProductError error) {
		super(shell, 
				Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.reverse.OpenAlreadyReversedOfferDialog.dialog.title"),  //$NON-NLS-1$
				null, 
				Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.reverse.OpenAlreadyReversedOfferDialog.dialog.message"), //$NON-NLS-1$
				MessageDialog.WARNING,
				new String[] {Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.reverse.OpenAlreadyReversedOfferDialog.button.ok.text"), Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.reverse.OpenAlreadyReversedOfferDialog.button.cancel.text")}, //$NON-NLS-1$ //$NON-NLS-2$
				0);
		this.error = error;
	}

	@Override
	protected void buttonPressed(int buttonId)
	{
		if (buttonId == 0) {
			ArticleID reversingArticleID = error.getReversingArticleID();
			Article reversingArticle = ArticleDAO.sharedInstance().getArticle(reversingArticleID, 
					new String[] {FetchPlan.DEFAULT, Article.FETCH_GROUP_PRODUCT, 
					Article.FETCH_GROUP_OFFER, Article.FETCH_GROUP_OFFER_ID}, 
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
			OfferID reversingOfferID = reversingArticle.getOfferID();
			try {
				RCPUtil.openEditor(
						new ArticleContainerEditorInput(reversingOfferID), 
						ArticleContainerEditor.ID_EDITOR);
			} catch (PartInitException e) {
				throw new RuntimeException(e);
			}			
		}
		super.buttonPressed(buttonId);
	}

}
