package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.deliverydate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.jdo.JDOHelper;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.DateTimeControl;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.deliverydate.ArticleDeliveryDateCarrier;
import org.nightlabs.jfire.trade.deliverydate.DeliveryDateMode;
import org.nightlabs.jfire.trade.id.ArticleID;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleTableProviderConstants;
import org.nightlabs.l10n.DateFormatter;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class EditDeliveryDateComposite
extends XComposite
{
	private ArticleDeliveryDateTable table;
	private Collection<ArticleDeliveryDateCarrier> articleDeliveryDateCarriers;
	private DateTimeControl dateTimeControl;
	private DeliveryDateMode mode;

	public EditDeliveryDateComposite(Composite parent, int style, DeliveryDateMode mode) {
		super(parent, style);
		this.mode = mode;
		table = new ArticleDeliveryDateTable(this, SWT.NONE, Article.class.getName(),
				ArticleTableProviderConstants.SCOPE_PRODUCT_TYPE, mode);

		Composite wrapper = new XComposite(this, SWT.NONE, LayoutMode.ORDINARY_WRAPPER, LayoutDataMode.GRID_DATA_HORIZONTAL);
		dateTimeControl = new DateTimeControl(wrapper, true, SWT.NONE, DateFormatter.FLAGS_DATE_SHORT);
		dateTimeControl.clearDate();
		dateTimeControl.setButtonText("Set the delivery date for all articles");
		dateTimeControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		dateTimeControl.addSelectionListener(new SelectionAdapter(){
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				Date date = dateTimeControl.getDate();
				for (ArticleDeliveryDateCarrier carrier : articleDeliveryDateCarriers){
					carrier.setDeliveryDate(date);
				}
				updateText();
				table.refresh(true);
			}
		});
	}

	public void setArticles(Collection<Article> articles, DeliveryDateMode mode)
	{
		Collection<ArticleID> articleIDs = new ArrayList<ArticleID>(articles.size());
		articleDeliveryDateCarriers = new ArrayList<ArticleDeliveryDateCarrier>(articles.size());
		for (Article article : articles) {
			ArticleID articleID = (ArticleID) JDOHelper.getObjectId(article);
			articleIDs.add(articleID);
			ArticleDeliveryDateCarrier articleDeliveryDateCarrier = new ArticleDeliveryDateCarrier(
					articleID, getDeliveryDate(article, mode), mode);
			articleDeliveryDateCarriers.add(articleDeliveryDateCarrier);
		}
		table.setArticleDeliveryDateCarriers(articleDeliveryDateCarriers);
		table.setElementIDs(articleIDs, ArticleTableProviderConstants.SCOPE_PRODUCT_TYPE, new NullProgressMonitor());

		updateText();
	}

	private Date getDeliveryDate(Article article, DeliveryDateMode mode) {
		if (mode == DeliveryDateMode.DELIVERY_NOTE){
			return article.getDeliveryDateDeliveryNote();
		}
		else
			return article.getDeliveryDateOffer();
	}

	public Collection<ArticleDeliveryDateCarrier> getArticleDeliveryDateCarriers() {
		return articleDeliveryDateCarriers;
	}

	private void updateText()
	{
		if (articleDeliveryDateCarriers != null && !articleDeliveryDateCarriers.isEmpty()) {
			Date date = articleDeliveryDateCarriers.iterator().next().getDeliveryDate();
			dateTimeControl.setDate(date);
		}
	}

}
