package org.nightlabs.jfire.trade.admin.ui.editor;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.PartInitException;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jfire.base.ui.editlock.EditLockCallback;
import org.nightlabs.jfire.base.ui.editlock.EditLockCarrier;
import org.nightlabs.jfire.base.ui.editlock.EditLockHandle;
import org.nightlabs.jfire.base.ui.editlock.EditLockMan;
import org.nightlabs.jfire.base.ui.editlock.InactivityAction;
import org.nightlabs.jfire.editlock.EditLock;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.dao.ProductTypeDAO;
import org.nightlabs.jfire.trade.JFireTradeEAR;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;

/**
 * The Base Class of all {@link ProductType} AdminEditors. It automatically extracts the correct
 * title and checks for {@link EditLock}.
 * 
 * <p> Note: This Editor should only be subclassed if used for ProductType configuration. The
 * 			{@link EditLock}s should only be used if necessary.
 * </p>
 * 
 * @author Marius Heinzmann [marius[at]NightLabs[dot]de]
 * @author Tobias Langner <!-- tobias[dot]langner[at]nightlabs[dot]de -->
 */
public abstract class AbstractProductTypeAdminEditor
	extends EntityEditor
{
	private EditLockHandle editLockHandle;
	
	private EditLockCallback editLockCallback = new EditLockCallback() {
		@Override
		public InactivityAction getEditLockAction(EditLockCarrier editLockCarrier) {
			if (!isDirty())
				return InactivityAction.DIALOG_ABOUT_TO_EXPIRE;
			else
				return InactivityAction.DIALOG_BLOCKING_DUE_TO_INACTIVITY;
		}

		@Override
		public void doDiscardAndRelease() {
			close(false);
		}

		@Override
		public void doSaveAndRelease() {
			doSave(new NullProgressMonitor());
			close(false);
		}
	};
	
	@Override
	public String getTitle()
	{
		if(getEditorInput() == null)
			return super.getTitle();

		Job loadTitleJob = new Job(Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypeAdminEditor.loadTitleJob.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				final String title = ProductTypeDAO.sharedInstance().getProductType(
						((ProductTypeEditorInput)getEditorInput()).getJDOObjectID(),
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
	
	private IPropertyListener dirtyStateListener = new IPropertyListener() {
		public void propertyChanged(Object source, int propID) {
			if (PROP_DIRTY == propID) {
				editLockHandle.refresh();
			}
		}
	};

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);
		editLockHandle = EditLockMan.sharedInstance().acquireEditLockAsynchronously(
				JFireTradeEAR.EDIT_LOCK_TYPE_ID_PRODUCT_TYPE, ((ProductTypeEditorInput)input).getJDOObjectID(),
				Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypeAdminEditor.editLock.description"), editLockCallback //$NON-NLS-1$
		);
		
		addPropertyListener(dirtyStateListener);
	}

	@Override
	public void dispose() {
		super.dispose();
		
		removePropertyListener(dirtyStateListener);
		
		if (editLockHandle != null)
			editLockHandle.release();
	}
}
