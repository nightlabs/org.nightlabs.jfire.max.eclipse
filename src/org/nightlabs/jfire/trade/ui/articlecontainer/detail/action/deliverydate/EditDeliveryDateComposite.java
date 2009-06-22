package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.deliverydate;

import java.util.Date;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.DateTimeControl;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jfire.trade.ArticleDeliveryDateSet;
import org.nightlabs.jfire.trade.id.ArticleID;
import org.nightlabs.l10n.DateFormatter;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class EditDeliveryDateComposite
extends XComposite
{
	private ArticleDeliveryDateTable articleDeliveryDateTable;
	private ArticleDeliveryDateSet articleDeliveryDateSet;
	private DateTimeControl dateTimeControl;

	public EditDeliveryDateComposite(Composite parent, int style) {
		super(parent, style);
		articleDeliveryDateTable = new ArticleDeliveryDateTable(this, SWT.NONE);
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
				for (Map.Entry<ArticleID, Date> entry : articleDeliveryDateSet.getArticleID2DeliveryDate().entrySet()){
					entry.setValue(date);
				}
				updateText();
				articleDeliveryDateTable.refresh(true);
			}
		});
	}

	public void setArticleDeliveryDateSet(ArticleDeliveryDateSet articleDeliveryDateSet){
		this.articleDeliveryDateSet = articleDeliveryDateSet;
		articleDeliveryDateTable.setArticleDeliveryDateSet(articleDeliveryDateSet);
		updateText();
	}

	private void updateText()
	{
		if (articleDeliveryDateSet != null && !articleDeliveryDateSet.getArticleID2DeliveryDate().isEmpty()) {
			Date date = articleDeliveryDateSet.getArticleID2DeliveryDate().entrySet().iterator().next().getValue();
			dateTimeControl.setDate(date);
		}
	}

}
