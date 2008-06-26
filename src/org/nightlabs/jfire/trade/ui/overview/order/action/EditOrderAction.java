package org.nightlabs.jfire.trade.ui.overview.order.action;

import org.eclipse.ui.IEditorInput;
import org.nightlabs.jfire.trade.id.OrderID;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditor;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.order.GeneralEditorInputOrder;
import org.nightlabs.jfire.trade.ui.overview.action.AbstractEditArticleContainerAction;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class EditOrderAction
extends AbstractEditArticleContainerAction
{

	public EditOrderAction() {
	}

	public String getEditorID() {
		return ArticleContainerEditor.ID_EDITOR;
	}

	public IEditorInput getEditorInput() {
		OrderID orderID = (OrderID) getArticleContainerID();
		return new GeneralEditorInputOrder(orderID);
	}

}
