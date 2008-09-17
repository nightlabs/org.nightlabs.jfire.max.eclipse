package org.nightlabs.jfire.trade.ui.overview.invoice.action;

import org.eclipse.ui.IEditorInput;
import org.nightlabs.jfire.accounting.id.InvoiceID;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditor;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditorInput;
import org.nightlabs.jfire.trade.ui.overview.action.AbstractEditArticleContainerAction;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class EditInvoiceAction
//extends OverviewEditAction
extends AbstractEditArticleContainerAction
{
	public EditInvoiceAction() {
		super();
	}

	public String getEditorID() {
		return ArticleContainerEditor.ID_EDITOR;
	}

	public IEditorInput getEditorInput() {
		InvoiceID invoiceID = (InvoiceID) getArticleContainerID();
		return new ArticleContainerEditorInput(invoiceID);
	}
	
}
