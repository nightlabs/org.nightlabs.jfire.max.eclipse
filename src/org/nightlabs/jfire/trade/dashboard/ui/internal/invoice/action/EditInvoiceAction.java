package org.nightlabs.jfire.trade.dashboard.ui.internal.invoice.action;

import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jfire.base.dashboard.ui.action.AbstractDashboardTableAction;
import org.nightlabs.jfire.trade.dashboard.ui.internal.invoice.InvoiceTableItem;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditorUtil;
import org.nightlabs.jfire.trade.ui.overview.action.AbstractEditArticleContainerAction;

/**
 * @author abieber
 *
 */
public class EditInvoiceAction extends AbstractDashboardTableAction<InvoiceTableItem> {

	public EditInvoiceAction() {
		setId(EditInvoiceAction.class.getName());
		setText("Edit invoice");
		setImageDescriptor(SharedImages.getSharedImageDescriptor(
				TradePlugin.getDefault(), AbstractEditArticleContainerAction.class));
	}
	
	@Override
	public void run() {
		InvoiceTableItem tableItem = getFirstSelectedTableItem();
		if (tableItem != null) {
			ArticleContainerEditorUtil.openArticleContainerInTradePespective(tableItem.getInvoiceID());
		}
	}
	
	@Override
	public boolean calculateEnabled() {
		return getFirstSelectedTableItem() != null;
	}

}
