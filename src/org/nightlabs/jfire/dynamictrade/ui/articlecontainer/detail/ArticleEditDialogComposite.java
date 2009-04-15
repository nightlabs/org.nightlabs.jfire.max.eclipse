package org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail;

import javax.jdo.JDOHelper;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.i18n.I18nText;
import org.nightlabs.jfire.accounting.Price;
import org.nightlabs.jfire.accounting.Tariff;
import org.nightlabs.jfire.accounting.gridpriceconfig.PriceCell;
import org.nightlabs.jfire.accounting.id.TariffID;
import org.nightlabs.jfire.base.JFireEjbFactory;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.dynamictrade.DynamicProductInfo;
import org.nightlabs.jfire.dynamictrade.DynamicTradeManager;
import org.nightlabs.jfire.dynamictrade.store.DynamicProduct;
import org.nightlabs.jfire.store.Unit;
import org.nightlabs.jfire.store.id.UnitID;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.id.ArticleID;
import org.nightlabs.l10n.NumberFormatter;
import org.nightlabs.script.ScriptParser;

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
				ScriptParser script = new ScriptParser(getProductName());
				String err = script.validateContent();
				if(err !=null)
				{
					// shows the error message !!!
					storedText = getProductNameTextBox().getText();
					getProductNameTextBox().setText(err);
					getProductNameTextBox().setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
					getProductNameTextBox().addFocusListener(  new FocusListener(){
						@Override
						public void focusGained(FocusEvent arg0) {
							getProductNameTextBox().setText(getStoredText());
							getProductNameTextBox().setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
							getProductNameTextBox().removeFocusListener(this);

						}				
						@Override
						public void focusLost(FocusEvent arg0) {
							// TODO Auto-generated method stub

						}

					});
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
			DynamicTradeManager m = JFireEjbFactory.getBean(DynamicTradeManager.class, Login.getLogin().getInitialContextProperties());
			m.modifyArticle(articleID, quantity, unitID, tariffID, productName, singlePrice, false, null, 1);
			return true;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
