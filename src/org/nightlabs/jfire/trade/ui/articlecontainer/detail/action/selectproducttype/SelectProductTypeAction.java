package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.selectproducttype;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.jdo.JDOHelper;

import org.nightlabs.base.ui.notification.SelectionManager;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleSelection;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.ArticleEditAction;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.notification.NotificationEvent;

public class SelectProductTypeAction extends ArticleEditAction
{
	private Set<ProductType> productTypesOfSelectedArticle = Collections.emptySet();

	private String origText = null;
	private String origToolTipText = null;

	@Override
	public boolean calculateVisible() {
		return true;
	}

	@Override
	public boolean calculateEnabled(Set<ArticleSelection> articleSelections)
	{
		if (origText == null)
			origText = getText();

		if (origToolTipText == null)
			origToolTipText = getToolTipText();

		Set<ProductType> productTypes = new HashSet<ProductType>();
		for (ArticleSelection articleSelection : articleSelections) {
			for (Article article : articleSelection.getSelectedArticles()) {
				productTypes.add(article.getProductType());
			}
		}
		productTypes.remove(null); // in case an article has no product type assigned, we remove it

		productTypesOfSelectedArticle = productTypes;
		if (productTypes.size() == 1) {
			ProductType productType = productTypes.iterator().next();
			setText(String.format(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.selectproducttype.SelectProductTypeAction.text"), productType.getName().getText())); //$NON-NLS-1$
			setToolTipText(String.format(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.selectproducttype.SelectProductTypeAction.toolTipText"), productType.getName().getText())); //$NON-NLS-1$
			return true;
		}
		else {
			setText(origText);
			setToolTipText(origToolTipText);
			return false;
		}
	}

	@Override
	public void run() {
		Set<ProductType> productTypes = productTypesOfSelectedArticle;
		ProductType productType;
		if (productTypes.isEmpty())
			productType = null;
		else
			productType = productTypes.iterator().next();

		SelectionManager.sharedInstance().notify(
				new NotificationEvent(this, TradePlugin.ZONE_SALE, JDOHelper.getObjectId(productType), ProductType.class)
		);
	}

}
