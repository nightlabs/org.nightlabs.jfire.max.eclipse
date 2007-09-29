package org.nightlabs.jfire.trade.admin.ui.editor;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jfire.base.ui.editlock.EditLockCallback;
import org.nightlabs.jfire.base.ui.editlock.EditLockMan;
import org.nightlabs.jfire.editlock.EditLock;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.JFireTradeEAR;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
import org.nightlabs.jfire.trade.store.ProductTypeDAO;
import org.nightlabs.progress.ProgressMonitor;

/**
 * The Base Class of all {@link ProductType} AdminEditors. It automatically extracts the correct 
 * title and checks for {@link EditLock}.
 * 
 * <p> Note: This Editor should only be subclassed if used for ProductType configuration. The 
 * 			{@link EditLock}s should only be used if necessary.
 * </p>		
 *   
 * @author Marius Heinzmann [marius<at>NightLabs<dot>de]
 */
public abstract class AbstractProductTypeAdminEditor 
	extends EntityEditor 
{	
	@Override
	public String getTitle()
	{
		if(getEditorInput() == null)
			return super.getTitle();

		Job loadTitleJob = new Job(Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypeAdminEditor.loadTitleJob.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				final String title = ProductTypeDAO.sharedInstance().getProductType(
						(ProductTypeID)((ProductTypeEditorInput)getEditorInput()).getJDOObjectID(),
						new String[] { FetchPlan.DEFAULT, ProductType.FETCH_GROUP_NAME },
						1, monitor).getName().getText();
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						setPartName(title);
					}
				});
				return Status.OK_STATUS;
			}
		};
		loadTitleJob.schedule();
		return super.getTitle();
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);
		EditLockMan.sharedInstance().acquireEditLockAsynchronously(
				JFireTradeEAR.EDIT_LOCK_TYPE_ID_PRODUCT_TYPE, ((ProductTypeEditorInput)input).getJDOObjectID(), 
				Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypeAdminEditor.editLock.description"), (EditLockCallback) null //$NON-NLS-1$
				);
	}

	@Override
	public void dispose() {
		super.dispose();
		EditLockMan.sharedInstance().releaseEditLock(
				((ProductTypeEditorInput)getEditorInput()).getJDOObjectID());
	}
}
