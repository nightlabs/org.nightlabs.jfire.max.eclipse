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

package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.reverse;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableContentProvider;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.base.ui.wizard.DynamicPathWizardPage;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.ObjectIDUtil;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.base.login.ui.Login;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.TradeManagerRemote;
import org.nightlabs.jfire.trade.id.OfferID;
import org.nightlabs.jfire.trade.id.OrderID;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.l10n.DateFormatter;
import org.nightlabs.progress.ProgressMonitor;

public class SelectOfferPage extends DynamicPathWizardPage
{
	public static final int ACTION_CREATE = 1;
	public static final int ACTION_SELECT = 2;

	private Button createOfferRadio;
	private Button selectOfferRadio;

	private AbstractTableComposite<Offer> offerTable;
	private List<Object> offers = new ArrayList<Object>(0); // holds either a String or instances of Offer
	private Offer selectedOffer = null;
	private OrderID orderID;

	public SelectOfferPage(OrderID orderID)
	{
		super(SelectOfferPage.class.getName(), Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.reverse.SelectOfferPage.title")); //$NON-NLS-1$
		setDescription(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.reverse.SelectOfferPage.description")); //$NON-NLS-1$
		this.orderID = orderID;
	}

	@Override
	public Control createPageContents(Composite parent)
	{
		XComposite page = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);

		createOfferRadio = new Button(page, SWT.RADIO);
		createOfferRadio.setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.reverse.SelectOfferPage.createOfferRadio.text")); //$NON-NLS-1$
		createOfferRadio.setSelection(true);
		selectOfferRadio = new Button(page, SWT.RADIO);
		selectOfferRadio.setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.reverse.SelectOfferPage.selectOfferRadio.text")); //$NON-NLS-1$
		selectOfferRadio.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				offerTable.setEnabled(selectOfferRadio.getSelection());
				((DynamicPathWizard)getWizard()).updateDialog();
			}
		});

		offerTable = new AbstractTableComposite<Offer>(page, SWT.NONE, true, page.getBorderStyle() | SWT.FULL_SELECTION | SWT.SINGLE) {
			@Override
			protected void createTableColumns(TableViewer tableViewer, Table table)
			{
				new TableColumn(table, SWT.RIGHT).setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.reverse.SelectOfferPage.offerIDTableColumn.text")); //$NON-NLS-1$
				new TableColumn(table, SWT.LEFT).setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.reverse.SelectOfferPage.createDateTableColumn.text")); //$NON-NLS-1$
				new TableColumn(table, SWT.LEFT).setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.reverse.SelectOfferPage.createUserTableColumn.text")); //$NON-NLS-1$
				table.setLayout(new WeightedTableLayout(new int[] {33, 33, 33}));
			}
			@Override
			protected void setTableProvider(TableViewer tableViewer)
			{
				tableViewer.setContentProvider(new TableContentProvider());
				tableViewer.setLabelProvider(new TableLabelProvider() {
					public String getColumnText(Object element, int columnIndex)
					{
						if (element instanceof String) {
							if (columnIndex == 0)
								return (String)element;
							else
								return ""; //$NON-NLS-1$
						}

						Offer offer = (Offer) element;
						switch (columnIndex) {
							case 0:
								return offer.getOfferIDPrefix() + '/' + ObjectIDUtil.longObjectIDFieldToString(offer.getOfferID());
							case 1:
								return DateFormatter.formatDateShortTimeHMS(offer.getCreateDT(), true);
							case 2:
								return offer.getCreateUser().getName() + " (" + offer.getCreateUser().getCompleteUserID() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
							default:
								return ""; //$NON-NLS-1$
						}
					}
				});
			}
		};
		offerTable.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event)
			{
				IStructuredSelection sel = (IStructuredSelection) event.getSelection();
				if (sel.isEmpty())
					selectedOffer = null;
				else {
					Object selectedObject = sel.getFirstElement();
					if (selectedObject instanceof Offer)
						selectedOffer = (Offer) selectedObject;
					else
						selectedOffer = null;
				}
				((DynamicPathWizard)getWizard()).updateDialog();
			}
		});
		offers.add(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.reverse.SelectOfferPage.loadOffersPseudoEntry")); //$NON-NLS-1$
		offerTable.setInput(offers);
		offerTable.setEnabled(selectOfferRadio.getSelection());

		new Job(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.reverse.SelectOfferPage.loadOffersJob.name")) { //$NON-NLS-1$
			@Override
			public IStatus run(ProgressMonitor monitor)
			{
				loadDataInJob();
				return Status.OK_STATUS;
			}
		}.schedule();

		return page;
	}

//	protected ReverseWizard getReverseWizard()
//	{
//		return (ReverseWizard) getWizard();
//	}

	private void loadDataInJob()
	{
		try {
			TradeManagerRemote tradeManager = JFireEjb3Factory.getRemoteBean(TradeManagerRemote.class, Login.getLogin().getInitialContextProperties());
			final List<Offer> l = tradeManager.getNonFinalizedNonEndedOffers(
//					getReverseWizard().getOrderID(),
					orderID,
					FETCH_GROUPS_OFFERS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);

			Display.getDefault().asyncExec(new Runnable() {
				public void run()
				{
					offers.clear();
					offers.addAll(l);
					if (!offerTable.isDisposed())
						offerTable.refresh();
				}
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static final String[] FETCH_GROUPS_OFFERS = new String[] {
		FetchPlan.DEFAULT,
		Offer.FETCH_GROUP_CREATE_USER
	};

	public int getAction()
	{
		int action = 0;

		if (createOfferRadio.getSelection())
			action = ACTION_CREATE;
		else if (selectOfferRadio.getSelection())
			action = ACTION_SELECT;

		if (action == 0)
			throw new IllegalStateException("action == 0!!!"); //$NON-NLS-1$

		return action;
	}

	public Offer getSelectedOffer()
	{
		return selectedOffer;
	}
	public OfferID getSelectedOfferID()
	{
		if (selectedOffer == null)
			return null;

		return (OfferID) JDOHelper.getObjectId(selectedOffer);
	}

	@Override
	public boolean isPageComplete()
	{
		return ACTION_CREATE == getAction() || (ACTION_SELECT == getAction() && getSelectedOffer() != null);
	}
}
