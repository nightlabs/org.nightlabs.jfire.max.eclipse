package org.nightlabs.jfire.trade.admin.ui.editor;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.PartInitException;
import org.nightlabs.jfire.base.ui.editlock.EditLockCallback;
import org.nightlabs.jfire.base.ui.editlock.EditLockCarrier;
import org.nightlabs.jfire.base.ui.editlock.EditLockHandle;
import org.nightlabs.jfire.base.ui.editlock.EditLockMan;
import org.nightlabs.jfire.base.ui.editlock.InactivityAction;
import org.nightlabs.jfire.base.ui.entity.editor.ActiveEntityEditor;
import org.nightlabs.jfire.base.ui.login.part.ICloseOnLogoutEditorPart;
import org.nightlabs.jfire.editlock.EditLock;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.dao.ProductTypeDAO;
import org.nightlabs.jfire.store.id.ProductTypeID;
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
 * @author Khaireel Mohamed - khaireel at nightlabs dot de
 */
public abstract class AbstractProductTypeAdminEditor extends ActiveEntityEditor //EntityEditor
implements ICloseOnLogoutEditorPart
{
	private final String managedBy = null;

	private EditLockHandle editLockHandle;

	private final EditLockCallback editLockCallback = new EditLockCallback() {
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

	private final IPropertyListener dirtyStateListener = new IPropertyListener() {
		public void propertyChanged(Object source, int propID) {
			if (PROP_DIRTY == propID) {
				editLockHandle.refresh();
			}
		}
	};

	@Override
	public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {
		super.init(site, input);
		editLockHandle = EditLockMan.sharedInstance().acquireEditLockAsynchronously(
				JFireTradeEAR.EDIT_LOCK_TYPE_ID_PRODUCT_TYPE, ((ProductTypeEditorInput)input).getJDOObjectID(),
				Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypeAdminEditor.editLock.description"), editLockCallback //$NON-NLS-1$
		);
//
//		addPropertyListener(dirtyStateListener);
//		Job loadPropertiesJob = new Job(Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypeAdminEditor.loadTitleJob.name")) { //$NON-NLS-1$
//			@Override
//			protected IStatus run(ProgressMonitor monitor) throws Exception {
//				final ProductType productType = ProductTypeDAO.sharedInstance().getProductType(
//						((ProductTypeEditorInput)getEditorInput()).getJDOObjectID(),
//						new String[] { FetchPlan.DEFAULT, ProductType.FETCH_GROUP_NAME, ProductType.FETCH_GROUP_PRODUCT_TYPE_LOCAL },
//						1, monitor);
//				Display.getDefault().asyncExec(new Runnable() {
//					public void run() {
//						String partTitle = productType.getName().getText();
//						managedBy = productType.getProductTypeLocal().getManagedBy();
//						if (managedBy != null) {
//							MessageDialog.openInformation(site.getShell(),
//									Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypeAdminEditor.dialog.title"), //$NON-NLS-1$
//									String.format(
//											Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypeAdminEditor.dialog.message"), //$NON-NLS-1$
//											partTitle, managedBy)
//									);
//							partTitle += String.format(Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypeAdminEditor.managedBy"), managedBy); //$NON-NLS-1$
//						}
//						setPartName(partTitle);
//						setTitle(partTitle);
//					}
//				});
//				return Status.OK_STATUS;
//			}
//		};
//		loadPropertiesJob.schedule();
	}

	@Override
	protected String getEditorTitleFromEntity(Object entity) {
		return entity instanceof ProductType ? ((ProductType)entity).getName().getText() : null;
	}

	@Override
	protected Object retrieveEntityForEditorTitle(ProgressMonitor monitor) {
		ProductTypeID productTypeID = ((ProductTypeEditorInput)getEditorInput()).getJDOObjectID();
		assert productTypeID != null;
		return ProductTypeDAO.sharedInstance().getProductType(productTypeID, new String[] { FetchPlan.DEFAULT, ProductType.FETCH_GROUP_NAME, ProductType.FETCH_GROUP_PRODUCT_TYPE_LOCAL }, 1, monitor);
	}


	@Override
	public void doSave(IProgressMonitor monitor) {
		if (managedBy != null)
			return;
		super.doSave(monitor);
	}

	@Override
	public void dispose() {
		super.dispose();

		removePropertyListener(dirtyStateListener);

		if (editLockHandle != null)
			editLockHandle.release();
	}

//	/**
//	 * Returns the Class of the ProductType this editor is used for.
//	 * @return the Class of the ProductType this editor is used for
//	 */
//	public abstract Class<? extends ProductType> getProductTypeClass();
}
