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
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.l10n.IDateFormatter;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class EditDeliveryDateComposite
extends XComposite
{
	private final ArticleDeliveryDateTable table;
	private Collection<ArticleDeliveryDateCarrier> articleDeliveryDateCarriers;
	private final DateTimeControl dateTimeControl;
	private final DeliveryDateMode mode;

	public EditDeliveryDateComposite(final Composite parent, final int style, final DeliveryDateMode mode) {
		super(parent, style);
		this.mode = mode;
		table = new ArticleDeliveryDateTable(this, SWT.NONE, Article.class.getName(),
				ArticleTableProviderConstants.SCOPE_PRODUCT_TYPE, mode);

		final Composite wrapper = new XComposite(this, SWT.NONE, LayoutMode.ORDINARY_WRAPPER, LayoutDataMode.GRID_DATA_HORIZONTAL);
		dateTimeControl = new DateTimeControl(wrapper, true, SWT.NONE, IDateFormatter.FLAGS_DATE_SHORT);
		dateTimeControl.setDate(null);
		dateTimeControl.setButtonText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.deliverydate.EditDeliveryDateComposite.button.setDeliveryDate.text")); //$NON-NLS-1$
		dateTimeControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		dateTimeControl.addSelectionListener(new SelectionAdapter(){
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(final SelectionEvent e) {
				final Date date = dateTimeControl.getDate();
				for (final ArticleDeliveryDateCarrier carrier : articleDeliveryDateCarriers){
					carrier.setDeliveryDate(date);
				}
				updateText();
				table.refresh(true);
			}
		});
	}

	public void setArticles(final Collection<Article> articles, final DeliveryDateMode mode)
	{
		final Collection<ArticleID> articleIDs = new ArrayList<ArticleID>(articles.size());
		articleDeliveryDateCarriers = new ArrayList<ArticleDeliveryDateCarrier>(articles.size());
		for (final Article article : articles) {
			final ArticleID articleID = (ArticleID) JDOHelper.getObjectId(article);
			articleIDs.add(articleID);
			final ArticleDeliveryDateCarrier articleDeliveryDateCarrier = new ArticleDeliveryDateCarrier(
					articleID, getDeliveryDate(article, mode), mode);
			articleDeliveryDateCarriers.add(articleDeliveryDateCarrier);
		}
		table.setArticleDeliveryDateCarriers(articleDeliveryDateCarriers);
		table.setElementIDs(articleIDs, ArticleTableProviderConstants.SCOPE_PRODUCT_TYPE, new NullProgressMonitor());

		updateText();
	}

	private Date getDeliveryDate(final Article article, final DeliveryDateMode mode) {
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
			final Date date = articleDeliveryDateCarriers.iterator().next().getDeliveryDate();
			dateTimeControl.setDate(date);
		}
	}

}
