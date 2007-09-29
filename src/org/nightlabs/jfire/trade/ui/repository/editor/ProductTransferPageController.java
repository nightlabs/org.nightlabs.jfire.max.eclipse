package org.nightlabs.jfire.trade.ui.repository.editor;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageController;
import org.nightlabs.base.ui.progress.ProgressMonitorWrapper;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.store.ProductTransfer;
import org.nightlabs.jfire.store.dao.ProductTransferDAO;
import org.nightlabs.jfire.store.query.ProductTransferIDQuery;
import org.nightlabs.jfire.trade.ui.repository.transfer.ProductTransferTable;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.progress.SubProgressMonitor;

class ProductTransferPageController
extends EntityEditorPageController
{
	private ProductTransferIDQuery productTransferQuery = new ProductTransferIDQuery();
	private List<ProductTransfer> productTransferList = null;

	public ProductTransferPageController(EntityEditor editor)
	{
		super(editor);
		productTransferQuery.setCurrentAnchorID(getCurrentRepositoryID());
		productTransferQuery.setToExclude(30); // load initially only the last 30 results

		// TODO add listener for new transfers (needs to check, whether the new transfers match the criteria before displaying them!)
	}
	@Override
	public void dispose()
	{
		// TODO remove listener for new transfers
		super.dispose();
	}

	public void doLoad(IProgressMonitor monitor)
	{
		monitor.beginTask(Messages.getString("org.nightlabs.jfire.trade.ui.repository.editor.ProductTransferPageController.loadingProductTransfersJobMonitor.task.name"), 100); //$NON-NLS-1$

		List<ProductTransfer> productTransfers = ProductTransferDAO.sharedInstance().getProductTransfers(
				productTransferQuery,
				ProductTransferTable.FETCH_GROUPS_PRODUCT_TRANSFER,
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				new SubProgressMonitor(new ProgressMonitorWrapper(monitor), 100));

		this.productTransferList = productTransfers;
		monitor.done();
		fireModifyEvent(null, productTransfers);
	}

	public void doSave(IProgressMonitor monitor)
	{
		// nothing to do
	}

	/**
	 * This method must be called on the UI thread!
	 */
	public void fireProductTransferQueryChange()
	{
		propertyChangeSupport.firePropertyChange(PROPERTY_PRODUCT_TRANSFER_QUERY, null, productTransferQuery);

		Job job = new Job(Messages.getString("org.nightlabs.jfire.trade.ui.repository.editor.ProductTransferPageController.loadingProductTransfersJob.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				doLoad(monitor);
				return Status.OK_STATUS;
			}
		};
		job.setPriority(Job.SHORT);
		job.schedule();
	}

	public AnchorID getCurrentRepositoryID()
	{
		return ((RepositoryEditorInput)getEntityEditor().getEditorInput()).getJDOObjectID();
	}

	public ProductTransferIDQuery getProductTransferQuery()
	{
		return productTransferQuery;
	}

	public List<ProductTransfer> getProductTransferList()
	{
		return productTransferList;
	}

	public static final String PROPERTY_PRODUCT_TRANSFER_QUERY = "productTransferQuery"; //$NON-NLS-1$

	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	/**
	 * Add a {@link PropertyChangeListener} which will be triggered on the UI thread. Currently,
	 * the only property available is {@link #PROPERTY_PRODUCT_TRANSFER_QUERY} which
	 * references the object returned by {@link #getProductTransferQuery()}.
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

}
