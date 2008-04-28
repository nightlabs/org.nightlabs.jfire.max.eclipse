package org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail;

import javax.jdo.JDOHelper;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.annotation.Implement;
import org.nightlabs.i18n.I18nText;
import org.nightlabs.jfire.accounting.Price;
import org.nightlabs.jfire.accounting.Tariff;
import org.nightlabs.jfire.accounting.gridpriceconfig.PriceCell;
import org.nightlabs.jfire.accounting.id.TariffID;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.dynamictrade.DynamicTradeManager;
import org.nightlabs.jfire.dynamictrade.DynamicTradeManagerUtil;
import org.nightlabs.jfire.dynamictrade.store.DynamicProduct;
import org.nightlabs.jfire.store.Unit;
import org.nightlabs.jfire.store.id.UnitID;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.id.ArticleID;
import org.nightlabs.l10n.NumberFormatter;

public class ArticleEditDialogComposite
extends ArticleBaseComposite
{

	public ArticleEditDialogComposite(Composite parent,
			ArticleContainer articleContainer, Article article)
	{
		super(parent, articleContainer, article);

		createUI();
	}

	@Override
	@Implement
	protected void fireCompositeContentChangeEvent()
	{
		// nothing to do
	}

	public void submit()
	{
		if (!isEditable())
			return; // If the Composite is not editable we do nothing here.
		
		try {			
			ArticleID articleID = (ArticleID) JDOHelper.getObjectId(this.article);
			Long quantity = null;
			UnitID unitID = null;
			TariffID tariffID = null;
			I18nText productName = null;
			Price singlePrice = null;

			DynamicProduct product = (DynamicProduct) this.article.getProduct();

			Unit u = this.unitCombo.getSelectedElement();
			if (!u.equals(product.getUnit()))
				unitID = (UnitID) JDOHelper.getObjectId(u);

			double q = NumberFormatter.parseFloat(this.quantity.getText());
			if (Math.abs(product.getQuantityAsDouble() - q) > 0.0001)
				quantity = new Long(u.toLong(q));

			Tariff t = this.tariffCombo.getSelectedElement();
			if (!t.equals(this.article.getTariff()))
				tariffID = (TariffID) JDOHelper.getObjectId(t);

			if (this.productNameModified)
				productName = this.productName;

			if (tariffID != null || this.inputPriceFragmentTypeModified) {
				PriceCell priceCell = this.resultPriceConfig.getPriceCell(createPriceCoordinate(), false);
				singlePrice = priceCell.getPrice(); // this price instance will not be persisted - only its values will be copied
			}

			// TODO use DAO
			DynamicTradeManager m = DynamicTradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
			m.modifyArticle(articleID, quantity, unitID, tariffID, productName, singlePrice, false, null, 1);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
