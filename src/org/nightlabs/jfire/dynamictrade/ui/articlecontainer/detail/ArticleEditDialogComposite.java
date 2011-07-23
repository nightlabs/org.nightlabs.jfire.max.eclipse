package org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail;

import javax.jdo.JDOHelper;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.message.MessageType;
import org.nightlabs.i18n.I18nText;
import org.nightlabs.jfire.accounting.Price;
import org.nightlabs.jfire.accounting.Tariff;
import org.nightlabs.jfire.accounting.gridpriceconfig.PriceCell;
import org.nightlabs.jfire.accounting.id.TariffID;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.base.login.ui.Login;
import org.nightlabs.jfire.dynamictrade.DynamicProductInfo;
import org.nightlabs.jfire.dynamictrade.DynamicTradeManagerRemote;
import org.nightlabs.jfire.dynamictrade.store.DynamicProduct;
import org.nightlabs.jfire.store.Unit;
import org.nightlabs.jfire.store.id.UnitID;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.id.ArticleID;
import org.nightlabs.l10n.NumberFormatter;
import org.nightlabs.script.JSHTMLExecuter;

public class ArticleEditDialogComposite
extends ArticleBaseComposite
{
	private String storedText;

	public String getStoredText() {
		return storedText;
	}

	public ArticleEditDialogComposite(Composite parent,
			ArticleContainer articleContainer, Article article)
	{
		super(parent, articleContainer, article);
		DynamicProduct product = (DynamicProduct) article.getProduct();
		// if it s a dynamic recurring product then shows the script message
		if (product == null)
			setScriptable(true);
		createUI();
	}

	@Override
	protected void fireCompositeContentChangeEvent()
	{
		// nothing to do
	}

	public boolean submit()
	{
		if (!isEditable())
			return true; // If the Composite is not editable we do nothing here.

		try {
			ArticleID articleID = (ArticleID) JDOHelper.getObjectId(this.article);
			Long quantity = null;
			UnitID unitID = null;
			TariffID tariffID = null;
			I18nText productName = null;
			Price singlePrice = null;

			DynamicProduct product = (DynamicProduct) this.article.getProduct();
			DynamicProductInfo dynamicProductInfo;
			if (product != null)
				dynamicProductInfo = product;
			else
			{
				dynamicProductInfo = (DynamicProductInfo) this.article;
				JSHTMLExecuter script = new JSHTMLExecuter(getProductName());
				String err = script.validateContent();
				if(err !=null)
				{
					showTextNameMessage(err,MessageType.ERROR);
					return false;
				}

			}

			Unit u = this.unitCombo.getSelectedElement();
			if (!u.equals(dynamicProductInfo.getUnit()))
				unitID = (UnitID) JDOHelper.getObjectId(u);

			double q = NumberFormatter.parseFloat(this.quantity.getText());
			if (Math.abs(dynamicProductInfo.getQuantityAsDouble() - q) > 0.0001)
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
			DynamicTradeManagerRemote m = JFireEjb3Factory.getRemoteBean(DynamicTradeManagerRemote.class, Login.getLogin().getInitialContextProperties());
			m.modifyArticle(articleID, quantity, unitID, tariffID, productName, singlePrice, false, null, 1);
			return true;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
