package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.deliverydate;

import java.util.Collection;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.DateTimeControl;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.DeliveryDateMode;
import org.nightlabs.l10n.DateFormatter;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class EditDeliveryDateComposite
extends XComposite
{
	private ArticleDeliveryDateTable articleDeliveryDateTable;
	private Collection<Article> articles;
//	private ArticleDeliveryDateSet articleDeliveryDateSet;
	private DeliveryDateMode mode;
	private DateTimeControl dateTimeControl;

	public EditDeliveryDateComposite(Composite parent, int style, DeliveryDateMode mode) {
		super(parent, style);
		this.mode = mode;
		articleDeliveryDateTable = new ArticleDeliveryDateTable(this, SWT.NONE, mode);

		Composite wrapper = new XComposite(this, SWT.NONE, LayoutMode.ORDINARY_WRAPPER, LayoutDataMode.GRID_DATA_HORIZONTAL);
		dateTimeControl = new DateTimeControl(wrapper, false, SWT.NONE, DateFormatter.FLAGS_DATE_SHORT);
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
				for (Article article : articles) {
					switch (EditDeliveryDateComposite.this.mode) {
						case OFFER:
							article.setDeliveryDateOffer(date);
							break;
						case DELIVERY_NOTE:
							article.setDeliveryDateDeliveryNote(date);
							break;
					}
				}
				updateText();
				articleDeliveryDateTable.refresh(true);
			}
		});
	}

	public void setArticles(Collection<Article> articles){
		this.articles = articles;
		articleDeliveryDateTable.setInput(articles);
		updateText();
	}

	private void updateText()
	{
		if (articles != null && !articles.isEmpty()) {
			Article firstArticle = articles.iterator().next();
			Date date = null;
			switch (mode) {
				case OFFER:
					date = firstArticle.getDeliveryDateOffer();
					break;
				case DELIVERY_NOTE:
					date = firstArticle.getDeliveryDateDeliveryNote();
					break;
			}
			dateTimeControl.setDate(date);
		}
	}
//
//	public void setArticles(ArticleDeliveryDateSet articleDeliveryDateSet){
//		this.articleDeliveryDateSet = articleDeliveryDateSet;
//		articleDeliveryDateTable.setInput(articles);
//		updateText();
//	}
//
//	private void updateText()
//	{
//		if (articles != null && !articles.isEmpty()) {
//			Article firstArticle = articles.iterator().next();
//			Date date = null;
//			switch (mode) {
//				case OFFER:
//					date = firstArticle.getDeliveryDateOffer();
//					break;
//				case DELIVERY_NOTE:
//					date = firstArticle.getDeliveryDateDeliveryNote();
//					break;
//			}
//			dateTimeControl.setDate(date);
//		}
//	}

}
