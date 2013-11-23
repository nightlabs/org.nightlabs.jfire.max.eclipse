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

package org.nightlabs.jfire.trade.admin.ui.tariff;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.jdo.FetchPlan;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.nightlabs.ModuleException;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableContentProvider;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.AccountingManagerRemote;
import org.nightlabs.jfire.accounting.Tariff;
import org.nightlabs.jfire.accounting.dao.TariffDAO;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.base.login.ui.Login;
import org.nightlabs.jfire.base.ui.JFireBasePlugin;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.util.NLLocale;

/**
 * @author Tobias Langner <!-- tobias[dot]langner[at]nightlabs[dot]de -->
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class TariffListComposite extends AbstractTableComposite<TariffCarrier> {
	private class TariffNameEditingSupport extends EditingSupport {
		private TextCellEditor editor;

		public TariffNameEditingSupport() {
			super(getTableViewer());
			editor = new TextCellEditor(getTable());
		}

		@Override
		protected boolean canEdit(final Object element) {
			return true;
		}

		@Override
		protected CellEditor getCellEditor(final Object element) {
			return editor;
		}

		@Override
		protected Object getValue(final Object element) {
			return ((TariffCarrier) element).getTariff().getName().getText(getLanguageID());
		}

		@Override
		protected void setValue(final Object element, final Object value) {
			((TariffCarrier) element).getTariff().getName().setText(getLanguageID(), (String) value);
			((TariffCarrier) element).setDirty(true);
			getTableViewer().refresh(true);
		}
	};

	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(TariffListComposite.class);

	public static final String COLUMN_NAME = "name"; //$NON-NLS-1$

	private List<TariffCarrier> tariffCarriers;

	public static final String[] FETCH_GROUPS_TARIFF = { FetchPlan.DEFAULT, Tariff.FETCH_GROUP_NAME };

	private static String getLocalOrganisationID() {
		try {
			return Login.getLogin().getOrganisationID();
		} catch (final Exception x) {
			throw new RuntimeException(x);
		}
	}

	public TariffListComposite(final Composite parent, final int style) {
		super(parent, style, false);

		//		WORKAROUND
		JFireBasePlugin.class.getName();

		final Table t = getTable();

		//		t.setHeaderVisible(true);
		t.setLinesVisible(true);

		final GridData tgd = new GridData(GridData.FILL_BOTH);
		tgd.horizontalSpan = 1;
		tgd.verticalSpan = 1;

		t.setLayoutData(tgd);
		t.setLayout(new WeightedTableLayout(new int[] { 1 }));

		initTable();

		tariffCarriers = getTariffCarriers();
		setInput(tariffCarriers);
	}

	protected List<TariffCarrier> getTariffCarriers() {
		final Collection<Tariff> tariffCollection = TariffDAO.sharedInstance().getTariffs(getLocalOrganisationID(), false, FETCH_GROUPS_TARIFF,
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor()); // TODO use a job for non-blocking UI!

		final List<TariffCarrier> tariffCarriers = new ArrayList<TariffCarrier>();
		for (final Tariff tariff : tariffCollection)
			tariffCarriers.add(new TariffCarrier(tariff));

		Collections.sort(tariffCarriers, new Comparator<TariffCarrier>() {
			public int compare(final TariffCarrier o1, final TariffCarrier o2) {
				return new Integer(o1.getTariff().getTariffIndex()).compareTo(o2.getTariff().getTariffIndex());
			}
		});
		return tariffCarriers;
	}

	public void moveSelectedTariffOneUp() {
		moveSelectedTariff(true);
	}

	public void moveSelectedTariffOneDown() {
		moveSelectedTariff(false);
	}

	private void moveSelectedTariff(final boolean up) {
		final TariffCarrier selectedCarrier = getFirstSelectedElement();

		if (selectedCarrier == null)
			return;

		final int index = tariffCarriers.indexOf(selectedCarrier);
		if (up && index > 0)
			Collections.swap(tariffCarriers, index, index - 1);
		else if (index < tariffCarriers.size() - 1)
			Collections.swap(tariffCarriers, index, index + 1);

		refresh();
	}

	private String languageID = NLLocale.getDefault().getLanguage();

	/**
	 * @return Returns the languageID.
	 */
	public String getLanguageID() {
		return languageID;
	}

	/**
	 * @param languageID The languageID to set.
	 */
	public void setLanguageID(final String languageID) {
		this.languageID = languageID;
		refresh();
	}

	public void submit() throws ModuleException {
		try {
			AccountingManagerRemote accountingManager = null;
			try {

				int tariffIndex = 0;
				for (final TariffCarrier tc : tariffCarriers) {
					//				if (csc.isDirty()) { TODO
					if (accountingManager == null)
						accountingManager = JFireEjb3Factory.getRemoteBean(AccountingManagerRemote.class, Login.getLogin().getInitialContextProperties());

					Tariff tariff = tc.getTariff();
					tariff.setTariffIndex(tariffIndex++);

					tariff = accountingManager.storeTariff(tc.getTariff(), true, new String[] { FetchPlan.ALL }, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT); // TODO Fetch Groups!!!
					tc.setTariff(tariff);
					tc.setDirty(false);
					//				}
				}
			} finally {
				if (accountingManager != null)
					try {
						// TODO Method does not exists !!! Daniel
//						accountingManager.remove();
					} catch (final Exception x) {
						logger.error("removing bean failed!", x);} //$NON-NLS-1$
			}
		} catch (final Exception x) {
			throw new ModuleException(x);
		}
	}

	@Override
	protected void createTableColumns(final TableViewer tableViewer, final Table table) {
		// Add the columns to the table
		final TableViewerColumn col = new TableViewerColumn(tableViewer, SWT.LEFT);
		col.getColumn().setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.tariff.TariffListComposite.tariffNameTableColumn.text")); //$NON-NLS-1$
		col.setEditingSupport(new TariffNameEditingSupport());
	}

	@Override
	protected void setTableProvider(final TableViewer tableViewer) {
		tableViewer.setContentProvider(new TableContentProvider());
		tableViewer.setLabelProvider(new TableLabelProvider() {
			public String getColumnText(final Object element, final int columnIndex) {
				if (element instanceof TariffCarrier) {
					final TariffCarrier tariffCarrier = (TariffCarrier) element;
					return tariffCarrier.getTariff().getName().getText(getLanguageID());
				}
				return ""; //$NON-NLS-1$
			}
		});
	}

	public void createTariff() {
		final Tariff tariff = new Tariff(null);
		final TariffCarrier tc = new TariffCarrier(tariff);
		tc.setDirty(true);
		tariffCarriers.add(tc);
		refresh();
	}
}
