/* *****************************************************************************
 * JFire - it's hot - Free ERP System - http://jfire.org                       *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://opensource.org/licenses/lgpl-license.php                         *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.jfire.trade.ui.articlecontainer.detail;


import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.ArticlePrice;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.l10n.NumberFormatter;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 * @author Daniel Mazurek - daniel at nightlabs dot de
 */
public class FooterComposite extends XComposite
{
	private static final Logger logger = Logger.getLogger(FooterComposite.class);
	
	private GeneralEditorComposite generalEditorComposite;
//	private ArticleContainer articleContainer;

	public FooterComposite(Composite parent, GeneralEditorComposite generalEditorComposite)
	{
		super(parent, SWT.BORDER, LayoutMode.TIGHT_WRAPPER);		
		this.generalEditorComposite = generalEditorComposite;
//		this.articleContainer = articleContainer;
		
		setBackground(DEFAULT_BG_COLOR);	
		
//		setLayout(new GridLayout(2, false));
//		spacerLabel = new Label(this, SWT.NONE);
//		spacerLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		spacerLabel.setBackground(bgColor);

		setLayout(new GridLayout(1, true));
		label = new Label(this, SWT.RIGHT);		
		label.setBackground(bgColor);
//		label.setText("                                                                    ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_CENTER | GridData.FILL_HORIZONTAL));

//		refresh();
	}

	public static final Color DEFAULT_BG_COLOR = new Color(null, 255, 255, 255);
	private Color bgColor = DEFAULT_BG_COLOR;
	public void setBgColor(Color c) {
		bgColor = c;
	}
		
//	private Label spacerLabel;
	private Label label;
	public void setFooterText(String text) 
	{
		if (label.isDisposed())
			return;

		label.setBackground(bgColor);
		label.setText(text);
		layout(true, true);
//		spacerLabel.setBackground(bgColor);
	}
		
	/**
	 * @return Returns the articleContainer.
	 */
	public ArticleContainer getArticleContainer()
	{
		return generalEditorComposite.getArticleContainer();
	}
	/**
	 * @return Returns the generalEditorComposite.
	 */
	public GeneralEditorComposite getGeneralEditorComposite()
	{
		return generalEditorComposite;
	}
	
	/**
	 * updates / refreshes the content if a refresh is needed
	 * the default implementation shows the total price of all articles in the articleContainer
	 */
//	public abstract void refresh();
	public void refresh() 
	{
		long priceAmount = 0;
		Currency currency = null;

		for (Article article : generalEditorComposite.getArticles()) { 
			ArticlePrice articlePrice = article.getPrice();
			priceAmount += articlePrice.getAmount();
			if (currency == null)
				currency = article.getPrice().getCurrency();
//			if (currency != null && !currency.equals(article.getPrice().getCurrency())) {
//				logger.error("There are articles with different currencies in the articleContainer "+
//						getArticleContainer().getOrganisationID() +", "+
//						getArticleContainer().getCustomerID() +", "+
//						getArticleContainer().getVendorID());
//				logger.error("The articleID is "+article.getArticleID());
//				setFooterText(TradePlugin.getResourceString("FooterComposite.errorMessage.differentCurrencies"));
//				return;
//			}				
		}
		if (currency != null) {
			String price = NumberFormatter.formatCurrency(priceAmount , currency);		
			setFooterText(String.format(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.FooterComposite.text"), price));							 //$NON-NLS-1$
		} 
		else {
			logger.info("currency == null!"); //$NON-NLS-1$
			setFooterText(""); //$NON-NLS-1$
		}
	}
}
