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
 *     http://opensource.org/licenses/lgpl-license.php                         *
 ******************************************************************************/
package org.nightlabs.jfire.trade.ui.account.editor;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageController;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.QueryCollection;
import org.nightlabs.jfire.accounting.MoneyTransfer;
import org.nightlabs.jfire.accounting.dao.MoneyTransferDAO;
import org.nightlabs.jfire.accounting.query.MoneyTransferQuery;
import org.nightlabs.jfire.trade.ui.account.transfer.MoneyTransferTable;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun <!-- chairatk [AT] nightlabs [DOT] de -->
 */
public class MoneyTransferPageController extends EntityEditorPageController
{
	private MoneyTransferQuery moneyTransferQuery = new MoneyTransferQuery();
	private QueryCollection<MoneyTransferQuery> queryWrapper =
		new QueryCollection<MoneyTransferQuery>(MoneyTransfer.class, moneyTransferQuery);
	
	private List<MoneyTransfer> moneyTransferList = null;
	
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(MoneyTransferPageController.class);

	public MoneyTransferPageController(EntityEditor editor)
	{
		super(editor);
		if (editor.getEditorInput() instanceof AccountEditorInput) {
			AnchorID accountID = ((AccountEditorInput ) editor.getEditorInput()).getJDOObjectID();
			moneyTransferQuery.setCurrentAnchorID(accountID);
		}
	}

	@Override
	public void dispose()
	{
		// TODO remove listener for new transfers
		super.dispose();
	}

	public void doLoad(ProgressMonitor monitor)
	{
		monitor.beginTask(Messages.getString("org.nightlabs.jfire.trade.ui.account.editor.MoneyTransferPageController.loadMoneyTransfersJob.name"), 100); //$NON-NLS-1$

		List<MoneyTransfer> moneyTransfers = MoneyTransferDAO.sharedInstance().getMoneyTransfers(
			queryWrapper,
			MoneyTransferTable.FETCH_GROUPS,
			NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
			new SubProgressMonitor(monitor, 100));

		this.moneyTransferList = moneyTransfers;
		monitor.done();
		setLoaded(true); // must be done before fireModifyEvent!
		fireModifyEvent(null, moneyTransfers);
	}

	public boolean doSave(ProgressMonitor monitor)
	{
		return true;
	}

	/**
	 * This method must be called on the UI thread!
	 */
	public void fireMoneyTransferQueryChange()
	{
		propertyChangeSupport.firePropertyChange(PROPERTY_MONEY_TRANSFER_QUERY, null, queryWrapper);

		Job job = new Job(Messages.getString("org.nightlabs.jfire.trade.ui.account.editor.MoneyTransferPageController.loadMoneyTransfersMonitor.task.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor)
			{
				doLoad(monitor);
				return Status.OK_STATUS;
			}
		};
		job.setPriority(Job.SHORT);
		job.schedule();
	}

	public MoneyTransferQuery getMoneyTransferQuery()
	{
		return moneyTransferQuery;
	}

	public List<MoneyTransfer> getMoneyTransferList()
	{
		return moneyTransferList;
	}

	public AnchorID getCurrentAnchorID()
	{
		return ((AccountEditorInput)getEntityEditor().getEditorInput()).getJDOObjectID();
	}
	
	public static final String PROPERTY_MONEY_TRANSFER_QUERY = "moneyTransferQuery"; //$NON-NLS-1$

	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	/**
	 * Add a {@link PropertyChangeListener} which will be triggered on the UI thread. Currently,
	 * the only property available is {@link #PROPERTY_MONEY_TRANSFER_QUERY} which
	 * references the object returned by {@link #getQueryWrapper()}.
	 *
	 * @param listener The listener to be added.
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		propertyChangeSupport.addPropertyChangeListener(listener);
	}
	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener)
	{
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}
	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		propertyChangeSupport.removePropertyChangeListener(listener);
	}
	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener)
	{
		propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
	}

	/**
	 * @return the queryWrapper
	 */
	public QueryCollection<MoneyTransferQuery> getQueryWrapper()
	{
		return queryWrapper;
	}

	/**
	 * @return
	 * @see org.nightlabs.jdo.query.QueryCollection#getFromInclude()
	 */
	public long getFromInclude()
	{
		return queryWrapper.getFromInclude();
	}

	/**
	 * @return
	 * @see org.nightlabs.jdo.query.QueryCollection#getToExclude()
	 */
	public long getToExclude()
	{
		return queryWrapper.getToExclude();
	}

	/**
	 * @param fromInclude
	 * @see org.nightlabs.jdo.query.QueryCollection#setFromInclude(long)
	 */
	public void setFromInclude(long fromInclude)
	{
		queryWrapper.setFromInclude(fromInclude);
	}

	/**
	 * @param toExclude
	 * @see org.nightlabs.jdo.query.QueryCollection#setToExclude(long)
	 */
	public void setToExclude(long toExclude)
	{
		queryWrapper.setToExclude(toExclude);
	}
}
