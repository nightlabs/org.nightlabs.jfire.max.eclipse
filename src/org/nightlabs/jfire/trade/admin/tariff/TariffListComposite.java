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

package org.nightlabs.jfire.trade.admin.tariff;

import java.util.Iterator;
import java.util.Locale;

import javax.jdo.FetchPlan;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.ModuleException;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.AccountingManager;
import org.nightlabs.jfire.accounting.AccountingManagerUtil;
import org.nightlabs.jfire.accounting.Tariff;
import org.nightlabs.jfire.base.ui.JFireBasePlugin;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.trade.admin.resource.Messages;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class TariffListComposite
extends XComposite
{
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(TariffListComposite.class);

	private TableViewer viewer;
	private TariffTableContentProvider contentProvider;
	private TariffTableLabelProvider labelProvider;

	public static final String COLUMN_NAME = "name"; //$NON-NLS-1$

	private static String getLocalOrganisationID()
	{
		try {
			return Login.getLogin().getOrganisationID();
		} catch (Exception x) {
			throw new RuntimeException(x);
		}
	}

	public TariffListComposite(Composite parent, int style)
	{
		this(parent, style, getLocalOrganisationID(), false);
	}
	public TariffListComposite(Composite parent, int style, String filterOrganisationID, boolean filterOrganisationIDInverse)
	{
		super(parent, style, LayoutMode.TIGHT_WRAPPER);

//		WORKAROUND
		JFireBasePlugin.class.getName();

		contentProvider = new TariffTableContentProvider(filterOrganisationID, filterOrganisationIDInverse);
		labelProvider = new TariffTableLabelProvider(this);
		viewer = new TableViewer(this, SWT.BORDER | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);
		viewer.setContentProvider(contentProvider);
		viewer.setLabelProvider(labelProvider);

		Table t = viewer.getTable();
//		t.setHeaderVisible(true);
		t.setLinesVisible(true);

		GridData tgd = new GridData(GridData.FILL_BOTH);
		tgd.horizontalSpan = 1;
		tgd.verticalSpan = 1;

		t.setLayoutData(tgd);
		t.setLayout(new WeightedTableLayout(new int[] {1}));

		// Add the columns to the table
		new TableColumn(t, SWT.LEFT).setText(Messages.getString("org.nightlabs.jfire.trade.admin.tariff.TariffListComposite.categorySetNameTableColumn.text")); //$NON-NLS-1$

		viewer.setColumnProperties(new String[]{COLUMN_NAME});
		viewer.setCellEditors(
				new CellEditor[] {new TextCellEditor(t)});
		viewer.setCellModifier(new TariffTableCellModifier(this));

		// This method MUST be called AFTER all columns are added - otherwise not all columns are shown!
		viewer.setInput(contentProvider);
	}

	public int getColumnIndex(String column)
	{
		int res = -1;
		Object[] cols = viewer.getColumnProperties();
		for (int i = 0; i < cols.length; ++i) {
			if (((String)cols[i]).equals(column)) {
				res = i;
				break;
			}
		}
		if (res < 0)
			throw new IllegalArgumentException("Column \""+column+"\" is not known!"); //$NON-NLS-1$ //$NON-NLS-2$
		return res;
	}

	private String languageID = Locale.getDefault().getLanguage();

	/**
	 * @return Returns the languageID.
	 */
	public String getLanguageID()
	{
		return languageID;
	}
	/**
	 * @param languageID The languageID to set.
	 */
	public void setLanguageID(String languageID)
	{
		this.languageID = languageID;
		refresh();
	}

	public void refresh()
	{
		viewer.setInput(contentProvider);
	}

	public void createTariff()
	{
		contentProvider.createTariff();
		refresh();
//		setSelectedCategorySetCarrierIndex(contentProvider.tariffCarriers.size() - 1);
	}

	public void submit()
	throws ModuleException
	{
		try {
			AccountingManager accountingManager = null;
			try {

				for (Iterator it = contentProvider.tariffCarriers.iterator(); it.hasNext(); ) {
					TariffCarrier tc = (TariffCarrier)it.next();
					//				if (csc.isDirty()) { TODO
					if (accountingManager == null)
						accountingManager = AccountingManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();

					Tariff tariff = accountingManager.storeTariff(tc.getTariff(), true, new String[]{FetchPlan.ALL}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT); // TODO Fetch Groups!!!
					tc.setTariff(tariff);
					tc.setDirty(false);
					//				}
				}
			} finally {
				if (accountingManager != null)
					try { accountingManager.remove(); } catch (Exception x) { logger.error("removing bean failed!", x); } //$NON-NLS-1$
			}
		} catch (Exception x) {
			throw new ModuleException(x);
		}
	}
	
	public TableViewer getTableViewer(){
		return viewer;
	}
}
