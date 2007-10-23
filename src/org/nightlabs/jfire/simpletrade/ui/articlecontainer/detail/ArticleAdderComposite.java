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

package org.nightlabs.jfire.simpletrade.ui.articlecontainer.detail;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.ComboComposite;
import org.nightlabs.base.ui.composite.FadeableComposite;
import org.nightlabs.base.ui.composite.QuantitySelector;
import org.nightlabs.base.ui.job.FadeableCompositeJob;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Price;
import org.nightlabs.jfire.accounting.Tariff;
import org.nightlabs.jfire.accounting.TariffOrderConfigModule;
import org.nightlabs.jfire.accounting.gridpriceconfig.TariffPricePair;
import org.nightlabs.jfire.accounting.id.TariffID;
import org.nightlabs.jfire.base.ui.config.ConfigUtil;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.simpletrade.SimpleTradeManager;
import org.nightlabs.jfire.simpletrade.SimpleTradeManagerUtil;
import org.nightlabs.jfire.simpletrade.ui.resource.Messages;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.FetchGroupsTrade;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.Segment;
import org.nightlabs.jfire.trade.id.OfferID;
import org.nightlabs.jfire.trade.id.SegmentID;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.SegmentEdit;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.SegmentEditFactory;
import org.nightlabs.l10n.NumberFormatter;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class ArticleAdderComposite extends FadeableComposite
{
	private ArticleAdder articleAdder;
	private Label productTypeNameLabel;

	private ComboComposite<TariffPricePair> tariffCombo;
	private QuantitySelector quantitySelector;

	public ArticleAdderComposite(Composite parent, ArticleAdder articleAdder, Collection<TariffPricePair> tariffPricePairsCollection)
	{
		super(parent, SWT.NONE); // , XComposite.LAYOUT_MODE_TIGHT_WRAPPER);
		this.articleAdder = articleAdder;
		this.setLayoutData(new GridData(GridData.FILL_BOTH));

		productTypeNameLabel = new Label(this, SWT.NONE);
		productTypeNameLabel.setLayoutData(new GridData());
		productTypeNameLabel.setText(
				articleAdder.getProductType().getName().getText(Locale.getDefault().getLanguage()));

		tariffCombo = new ComboComposite<TariffPricePair>(this, SWT.NONE, new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof TariffPricePair) {
					Tariff tariff = ((TariffPricePair)element).getTariff();
					Price price = ((TariffPricePair)element).getPrice();
					return (tariff.getName().getText()	+ " - " +	NumberFormatter.formatCurrency(price.getAmount(), price.getCurrency()));  //$NON-NLS-1$
				} else
					return element.toString();
			}
		});
		((Combo)tariffCombo.getControl()).setLayoutData(new GridData());		
		List<TariffPricePair> tariffPricePairs = new LinkedList<TariffPricePair>(tariffPricePairsCollection);
		
		// TODO This should be done in a job :)
		String[] fetchGroups = new String[] { TariffOrderConfigModule.FETCH_GROUP_TARIFF_ORDER_CONFIG_MODULE , FetchPlan.DEFAULT };
		TariffOrderConfigModule cfMod = (TariffOrderConfigModule) ConfigUtil.getUserCfMod(TariffOrderConfigModule.class,
				fetchGroups, -1, new NullProgressMonitor());
		
		final Comparator<Tariff> tariffComparator = cfMod.getTariffComparator();
		
		Collections.sort(tariffPricePairs, new Comparator<TariffPricePair>() {
			public int compare(TariffPricePair o1, TariffPricePair o2) {
				return tariffComparator.compare(o1.getTariff(), o2.getTariff());
			}
		});
		
		tariffCombo.setInput(tariffPricePairs);
		
		if (tariffPricePairs.size() > 0)
			tariffCombo.setSelection(0); // TODO later on we need to store a priority in the server

		quantitySelector = new QuantitySelector(this) {
			protected void quantitySelected(int qty)
			{
				qtySelected(qty);
			}
			protected void relayout()
			{
				ArticleAdderComposite.this.layout(true, true);
			}
		};

		this.getGridLayout().numColumns = this.getChildren().length - 1;
		((GridData)productTypeNameLabel.getLayoutData()).horizontalSpan = this.getGridLayout().numColumns;
	}

	private void qtySelected(final int qty)
	{
		final Tariff tariff = tariffCombo.getSelectedElement().getTariff();
		final TariffID tariffID = (TariffID) JDOHelper.getObjectId(tariff);
		FadeableCompositeJob addJob = new FadeableCompositeJob(Messages.getString("org.nightlabs.jfire.simpletrade.ui.articlecontainer.detail.ArticleAdderComposite.addArticlesJob.name"), this, this) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor, Object source) throws Exception {
				SegmentEdit segmentEdit = articleAdder.getSegmentEdit();

				OfferID offerID = null;
				if (segmentEdit.getArticleContainer() instanceof Offer)
					offerID = (OfferID) JDOHelper.getObjectId(segmentEdit.getArticleContainer());

				Segment segment = segmentEdit.getArticleSegmentGroup().getSegment();
				SegmentID segmentID = (SegmentID) JDOHelper.getObjectId(segment);
				ProductType productType = articleAdder.getProductType();
				ProductTypeID productTypeID = (ProductTypeID) JDOHelper.getObjectId(productType);

				String segmentContext = articleAdder.getSegmentEdit().getSegmentContext();
				String fetchGroupTrade_article;
				if (SegmentEditFactory.SEGMENTCONTEXT_ORDER.equals(segmentContext)) {
					fetchGroupTrade_article = FetchGroupsTrade.FETCH_GROUP_ARTICLE_IN_ORDER_EDITOR;
				}
				else if (SegmentEditFactory.SEGMENTCONTEXT_OFFER.equals(segmentContext)) {
					fetchGroupTrade_article = FetchGroupsTrade.FETCH_GROUP_ARTICLE_IN_OFFER_EDITOR;
				}
				else
					throw new IllegalStateException("Why is this ArticleAdder in an unknown segment context? segmentContext=" + segmentContext); //$NON-NLS-1$

				SimpleTradeManager stm = SimpleTradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
//				stm.createArticles(segmentID, productTypeID, qty, tariffID, null, 1); // TODO here should be a boolean get = false!

				Collection<Article> articles = stm.createArticles(
						segmentID, offerID, productTypeID, qty, tariffID, true, false,
						new String[] {
								fetchGroupTrade_article,
								FetchPlan.DEFAULT}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);

				segmentEdit.getClientArticleSegmentGroups().addArticles(articles);
//				segmentEdit.addArticles(abx);
				return Status.OK_STATUS;
			}
		};
		addJob.setPriority(Job.SHORT);
//		addJob.setUser(true);
		addJob.schedule();
	};

}
