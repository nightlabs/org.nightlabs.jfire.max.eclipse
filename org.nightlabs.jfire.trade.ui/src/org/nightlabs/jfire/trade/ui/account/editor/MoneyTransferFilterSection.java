package org.nightlabs.jfire.trade.ui.account.editor;

/* *****************************************************************************
 * JFire - it's hot - Free ERP System - http://jfire.org                       *
 * Copyright (C) 2004-2006 NightLabs - http://NightLabs.org                    *
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
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 ******************************************************************************/
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.editor.RestorableSectionPart;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jdo.query.QueryCollection;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.resource.Messages;

/**
 * An editor page section to display manual money transfer components.
 * @author Chairat Kongarayawetchakun - chairatk[at]nightlabs[dot]de
 */
public class MoneyTransferFilterSection
extends RestorableSectionPart
{
	private MoneyTransferFilterComposite moneyTransferSearchComposite;
	private MoneyTransferPageController controller;
	private Button refreshButton;
	
	public MoneyTransferFilterSection(FormPage page, Composite parent, MoneyTransferPageController _controller) {
		super(parent, page.getEditor().getToolkit(), ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		this.controller = _controller;
		getSection().setText(Messages.getString("org.nightlabs.jfire.trade.ui.account.editor.MoneyTransferFilterSection.section.text.filterMoneyTransfer")); //$NON-NLS-1$
		getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		getSection().setLayout(new GridLayout());

		this.controller.addPropertyChangeListener(MoneyTransferPageController.PROPERTY_MONEY_TRANSFER_QUERY, new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt)
			{
				if (ignoreMoneyTransferQueryChanged)
					return;

				moneyTransferQueryChanged((QueryCollection<?>) evt.getNewValue());
			}
		});

		XComposite client = new XComposite(getSection(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		client.getGridLayout().numColumns = 2; // TODO when adding more UI, we might want to switch to 1 and use wrapper-composites

		moneyTransferSearchComposite = new MoneyTransferFilterComposite(client, SWT.NONE);
		moneyTransferSearchComposite.getGridData().grabExcessHorizontalSpace = true;
		moneyTransferSearchComposite.getGridData().widthHint = parent.getBounds().width; //TODO Fix me : The problem about its width increases automatically!!!
		
		refreshButton = new Button(client, SWT.NONE);
		refreshButton.setImage(SharedImages.getSharedImage(TradePlugin.getDefault(), MoneyTransferFilterSection.class, "refreshButton")); //$NON-NLS-1$
		refreshButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.account.editor.MoneyTransferFilterSection.button.refresh")); //$NON-NLS-1$
		refreshButton.setToolTipText(Messages.getString("org.nightlabs.jfire.trade.ui.account.editor.MoneyTransferFilterSection.button.refresh.tooltip")); //$NON-NLS-1$
		refreshButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				// Was wrong, as it should have define the minimum transfer amount and not limit the results 
//				controller.getMoneyTransferQuery().set(moneyTransferSearchComposite.getTransferAmountEntry().getSpinnerComposite().getValue().longValue());
				if (moneyTransferSearchComposite.getCreateDTMin().isActive())
					controller.getMoneyTransferQuery().setTimestampFromIncl(moneyTransferSearchComposite.getCreateDTMin().getDate());
				else 
					controller.getMoneyTransferQuery().setTimestampFromIncl(null);
				if (moneyTransferSearchComposite.getCreateDTMax().isActive())
					controller.getMoneyTransferQuery().setTimestampToIncl(moneyTransferSearchComposite.getCreateDTMax().getDate());
				else
					controller.getMoneyTransferQuery().setTimestampToIncl(null);
				// For this page only transfers with the current account as one of the anchors 
//				controller.getMoneyTransferQuery().setToAnchorID(moneyTransferSearchComposite.getAccountChooserComposite().getSelectedAccount());

				fireMoneyTransferQueryChanged();
			}
		});
		
		getSection().setClient(client);
	}
	
	private boolean ignoreMoneyTransferQueryChanged = false;
	/**
	 * must be called on UI thread!
	 */
	private void fireMoneyTransferQueryChanged()
	{
		ignoreMoneyTransferQueryChanged = true;
		try {
			controller.fireMoneyTransferQueryChange();
		} finally {
			ignoreMoneyTransferQueryChanged = false;
		}
	}
	
	/**
	 * This method is called on the UI thread whenever the moneyTransferQuery has changed.
	 * It is not called, if the change originated from here (i.e. {@link #fireMoneyTransferQueryChanged()} in
	 * this object).
	 */
	private void moneyTransferQueryChanged(QueryCollection<?> queryCollection)
	{
		//do nothing
	}
}

