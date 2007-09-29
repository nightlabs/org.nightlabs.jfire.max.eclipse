/**
 * 
 */
package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.openrelated;

import java.util.Set;

import org.eclipse.ui.PartInitException;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jdo.ObjectIDUtil;
import org.nightlabs.jfire.accounting.id.InvoiceID;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.GeneralEditor;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.invoice.GeneralEditorInputInvoice;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.util.Util;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class OpenRelatedInvoiceAction extends OpenRelatedAction {

	@Override
	protected boolean calculateEnabledWithArticles(Set<Article> articles) {
		setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.openrelated.OpenRelatedInvoiceAction.action.text.disabled")); //$NON-NLS-1$
		InvoiceID invoiceID = getCommonInvoiceID(articles);
		if (invoiceID != null) {
			setText(
					String.format(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.openrelated.OpenRelatedInvoiceAction.action.text.enabled"), //$NON-NLS-1$
					invoiceID.invoiceIDPrefix, ObjectIDUtil.longObjectIDFieldToString(invoiceID.invoiceID)
				)
			);
		}
		return invoiceID != null && !(getActiveGeneralEditorInput() instanceof GeneralEditorInputInvoice);
	}
	
	/**
	 * Extracts the InvoiceID common to all given articles or <code>null</code>.
	 * @param articles The articles to check.
	 */
	protected InvoiceID getCommonInvoiceID(Set<Article> articles) {
		InvoiceID invoiceID = null;
		boolean first = true;
		for (Article article : articles) {
			if (first) {
				invoiceID = article.getInvoiceID();
				first = false;
				continue;
			}
			if (!Util.equals(invoiceID, article.getInvoiceID()))
				return null;
		}
		return invoiceID;
	}
	
	@Override
	public void run() {
		InvoiceID invoiceID = getCommonInvoiceID(getArticles());
		if (invoiceID == null)
			return;
		try {
			RCPUtil.openEditor(new GeneralEditorInputInvoice(invoiceID), GeneralEditor.ID_EDITOR);
		} catch (PartInitException e) {
			throw new RuntimeException(e);
		}
	}
	
}
